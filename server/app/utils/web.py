import typing as tp
import functools
import logging
import logging.handlers

from google.protobuf.message import Message
from google.protobuf import empty_pb2
from google.protobuf.json_format import MessageToJson

from starlette.applications import Starlette
from starlette.requests import Request as StarletteRequest
from starlette.responses import Response
from starlette.routing import Route
from starlette_prometheus import metrics, PrometheusMiddleware


_routes = []
_signatures: tp.Dict[str, tp.Tuple[Message, Message]] = {}
_check_attrs: tp.Dict[str, str] = {}
_connections: tp.Dict[str, bool] = {}


class Request(StarletteRequest):
    parsed = None
    connection = None


class ProtobufResponse(Response):
    media_type = 'application/protobuf'

    def __init__(self, proto: Message, **kwargs):
        super().__init__(**kwargs)
        self.proto = proto

    async def __call__(self, scope, receive, send) -> None:

        content_type = b'application/json'
        for header, value in scope['headers']:
            if header == b'content-type':
                content_type = value

        if content_type != b'application/protobuf':
            self.headers['content-type'] = 'application/json'
            self.body = self.render(MessageToJson(self.proto, including_default_value_fields=True))

        else:
            self.body = self.proto.SerializeToString()

        await super().__call__(scope, receive, send)


def route(path: str, request_type: Message = empty_pb2.Empty, response_type: Message = empty_pb2.Empty, permission_check_attr: str = 'id', use_connection=True, **kwargs):
    def _decorator(func):

        @functools.wraps(func)
        async def _wrapped(request, **fn_kwargs):
            request.parsed = request.scope['_parsed']
            request.connection = request.scope.get('_connection', None)
            return await func(request, **fn_kwargs)

        _signatures[path] = (request_type, response_type)
        _check_attrs[path] = permission_check_attr
        _connections[path] = use_connection
        _routes.append(Route(path, _wrapped, **kwargs))

        return _wrapped

    return _decorator


def prepare_app(*args, **kwargs):
    logger = logging.getLogger()
    logger.setLevel(logging.DEBUG)
    app = Starlette(*args, routes=_routes, **kwargs)
    app.add_middleware(PrometheusMiddleware)
    app.add_route("/metrics/", metrics)
    return app


def get_signature(path: str):
    return _signatures.get(path, (empty_pb2.Empty, empty_pb2.Empty))


def get_check_attr(path: str):
    return _check_attrs.get(path, 'id')


def should_use_db_connection(path: str):
    return _connections.get(path, True)
