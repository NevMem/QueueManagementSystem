import functools
import typing as tp
from aiohttp import web

from google.protobuf.message import Message
from google.protobuf.json_format import Parse, ParseDict


class Context:

    def __getattribute__(self, item):
        if item not in super().__getattribute__('__dict__'):
            return None
        return super().__getattribute__(item)


class AbstractMiddleware:

    async def process_request(self, request, context: Context):
        return request

    async def process_response(self, response, context: Context):
        return response

    async def process_finally(self):
        pass


class ProtobufMiddleware(AbstractMiddleware):

    # noinspection PyBroadException
    async def process_request(self, request: web.Request, context: Context) -> Message:
        data = await request.read()
        try:
            request = context.request_type().ParseFromString(data)
        except:
            request = Parse(data, context.request_type(), ignore_unknown_fields=True)
            context.is_rest = True

        return request

    async def process_response(self, response: Message, context: Context) -> web.Response:
        pass  # TODO: response


class MiddlewareApplier:

    def __init__(self, *middlewares: AbstractMiddleware):
        self.middlewares: tp.Tuple[AbstractMiddleware] = middlewares

    def __call__(self, func: tp.Callable):

        @functools.wraps(func)
        async def _wrapped(request, context: tp.Optional[Context] = None):
            if context is None:
                context = Context()

            # noinspection PyUnresolvedReferences
            annotations = func.__annotations__

            try:
                context.response_type = annotations.pop('returns')
            except KeyError:
                raise ValueError(f'Response type for function {func} not specified')

            try:
                context.request_type = next(iter(annotations.values()))
            except StopIteration:
                raise ValueError(f'Request type for function {func} not specified')

            middleware_processed = 0
            try:
                for middleware in self.middlewares:
                    middleware_processed += 1
                    request = await middleware.process_request(request, context)

                response = await func(request, context)

                for middleware in reversed(self.middlewares):
                    response = await middleware.process_response(response, context)
            finally:

                for middleware in reversed(self.middlewares):
                    await middleware.process_finally()

            return response

        return _wrapped


default = MiddlewareApplier(ProtobufMiddleware(), )
