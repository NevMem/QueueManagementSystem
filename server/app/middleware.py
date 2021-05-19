import aioredis
import base64
import contextlib
import itsdangerous
import json
import logging
import typing as tp

from aiocache import caches, cached
from server.app.aiocache.aiocache.backends.redis import RedisCache

from google.protobuf.json_format import MessageToDict, ParseDict
from itsdangerous.exc import BadTimeSignature, SignatureExpired
from sqlalchemy.orm import selectinload
from sqlalchemy.sql import select

from starlette.datastructures import MutableHeaders
from starlette.middleware import Middleware
from starlette.middleware.authentication import AuthenticationMiddleware, AuthenticationBackend, AuthCredentials, UnauthenticatedUser
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import HTTPConnection
from starlette.types import ASGIApp, Message, Receive, Scope, Send

from proto import user_pb2
from server.app import model
from server.app.config import Config
from server.app.utils import isiterable
from server.app.utils.db_utils import session_scope
from server.app.utils.web import get_signature, get_check_attr, should_use_db_connection


async def _get_user(request, email: str) -> tp.Tuple[tp.List[str], tp.Optional[tp.Dict]]:
    query = (
        select(model.User)
        .where(model.User.email == email)
        .options(selectinload(model.User.permissions))
    )

    result = await request.scope['_connection'].execute(query)
    user: model.User = result.scalar()

    if user is None:
        return [], None

    credentials = {'authenticated', }

    target = getattr(request.scope['_parsed'], get_check_attr(request.url.path), None)

    if target:
        if isinstance(target, str):
            for permission in user.permissions:
                if str(permission.service_id) == target or permission.organization_id == target:
                    credentials |= permission.permissions_list

        elif isiterable(target):
            for permission in user.permissions:
                if str(permission.service_id) in target or permission.organization_id in target:
                    credentials |= permission.permissions_list

    return list(credentials), MessageToDict(user.to_protobuf())


async def redis_prepare():
    with contextlib.suppress(Exception):
        sentinel = await aioredis.create_sentinel(sentinels=[f'redis://{Config.REDIS_HOST}:{Config.REDIS_PORT}'], password=Config.REDIS_PASSWORD)
        cache = RedisCache(sentinel=sentinel, master=Config.REDIS_USER)
        caches._caches['redis'] = cache

        global _get_user
        _get_user = cached(namespace='users', alias='redis', ttl=60, key_builder=lambda f, request, email: f'user_{email}', timeout=0.5)(_get_user)


class BasicAuthBackend(AuthenticationBackend):
    logger = logging.getLogger('app')

    async def authenticate(self, request):
        if not request.session.get('user'):
            return AuthCredentials([]), UnauthenticatedUser()

        if '_connection' not in request.scope:
            return AuthCredentials(['authenticated']), user_pb2.User(email=request.session['user'])

        credentials, user = await _get_user(request, request.session['user'])

        if user is None:
            return AuthCredentials([]), UnauthenticatedUser()

        return AuthCredentials(credentials), ParseDict(user, user_pb2.User())


class Proto2JsonMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        content_type = request.headers.get('Content-Type')
        signature = get_signature(request.url.path)

        if content_type == 'application/protobuf':
            request.scope['_parsed'] = signature[0]()
            request.scope['_parsed'].ParseFromString(await request.body())
        else:
            body = await request.body()
            if not body:
                message = {}
            else:
                message = json.loads(body)

            request.scope['_parsed'] = ParseDict(message, signature[0](), ignore_unknown_fields=True)

        response = await call_next(request)
        return response


class SessionMiddleware:
    def __init__(
        self,
        app: ASGIApp,
        secret_key: str,
        session_header: str = 'session',
        max_age: int = 14 * 24 * 60 * 60,  # 14 days, in seconds
    ) -> None:
        self.app = app
        self.signer = itsdangerous.TimestampSigner(str(secret_key))
        self.session_header = session_header
        self.max_age = max_age

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope['type'] not in ('http', 'websocket'):
            await self.app(scope, receive, send)
            return

        connection = HTTPConnection(scope)

        if self.session_header in connection.headers:
            data = connection.headers[self.session_header].encode('utf-8')
            try:
                data = self.signer.unsign(data, max_age=self.max_age)
                scope['session'] = json.loads(base64.b64decode(data))
            except (BadTimeSignature, SignatureExpired):
                scope['session'] = {}
        else:
            scope['session'] = {}

        async def send_wrapper(message: Message) -> None:
            if message['type'] == 'http.response.start':
                if scope['session']:
                    _data = base64.b64encode(json.dumps(scope['session']).encode('utf-8'))
                    _data = self.signer.sign(_data)
                    headers = MutableHeaders(scope=message)
                    headers.append('session', _data.decode('utf-8'))
            await send(message)

        await self.app(scope, receive, send_wrapper)


class DBSessionMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        if should_use_db_connection(request.url.path):
            async with session_scope() as session:
                request.scope['_connection'] = session
                return await call_next(request)
        return await call_next(request)


middleware = [
    Middleware(DBSessionMiddleware),
    Middleware(SessionMiddleware, secret_key=Config.SECRET_KEY),
    Middleware(Proto2JsonMiddleware),
    Middleware(AuthenticationMiddleware, backend=BasicAuthBackend()),
]
