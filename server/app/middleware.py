import base64
import itsdangerous
import json
from google.protobuf.json_format import ParseDict
from itsdangerous.exc import BadTimeSignature, SignatureExpired
from sqlalchemy.sql import select
from sqlalchemy.orm import selectinload

from starlette.datastructures import MutableHeaders
from starlette.middleware import Middleware
from starlette.middleware.authentication import AuthenticationMiddleware, AuthenticationBackend, AuthCredentials
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.middleware.cors import CORSMiddleware
from starlette.requests import HTTPConnection
from starlette.types import ASGIApp, Message, Receive, Scope, Send

from server.app import model
from server.app.config import Config
from server.app.utils.db_utils import session_scope
from server.app.utils.web import get_signature


class BasicAuthBackend(AuthenticationBackend):
    async def authenticate(self, request):
        if 'user' not in request.session:
            return

        query = select(model.User).where(model.User.email == request.session['user']).options(selectinload(model.User.permissions))
        result = await request.scope['_connection'].execute(query)
        user = result.scalar()

        return AuthCredentials(["authenticated"]), user


class Proto2JsonMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        content_type = request.headers.get('Content-Type')
        signature = get_signature(request.url.path)

        if content_type == 'application/protobuf':
            request.scope['_parsed'] = signature[0]().ParseFromString(await request.body())
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
        session_header: str = "session",
        max_age: int = 14 * 24 * 60 * 60,  # 14 days, in seconds
    ) -> None:
        self.app = app
        self.signer = itsdangerous.TimestampSigner(str(secret_key))
        self.session_header = session_header
        self.max_age = max_age

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope["type"] not in ("http", "websocket"):
            await self.app(scope, receive, send)
            return

        connection = HTTPConnection(scope)

        if self.session_header in connection.headers:
            data = connection.headers[self.session_header].encode("utf-8")
            try:
                data = self.signer.unsign(data, max_age=self.max_age)
                scope["session"] = json.loads(base64.b64decode(data))
            except (BadTimeSignature, SignatureExpired):
                scope["session"] = {}
        else:
            scope["session"] = {}

        async def send_wrapper(message: Message) -> None:
            if message["type"] == "http.response.start":
                if scope["session"]:
                    _data = base64.b64encode(json.dumps(scope["session"]).encode("utf-8"))
                    _data = self.signer.sign(_data)
                    headers = MutableHeaders(scope=message)
                    headers.append('session', _data.decode('utf-8'))
            await send(message)

        await self.app(scope, receive, send_wrapper)


class DBSessionMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        async with session_scope() as session:
            request.scope['_connection'] = session
            return await call_next(request)


middleware = [
    Middleware(CORSMiddleware, allow_origins=['*'], allow_methods=['*']),
    Middleware(DBSessionMiddleware),
    Middleware(SessionMiddleware, secret_key=Config.SECRET_KEY),
    Middleware(AuthenticationMiddleware, backend=BasicAuthBackend()),
    Middleware(Proto2JsonMiddleware),
]
