from aiohttp import web
from itsdangerous.serializer import Serializer

from server.app.config import Config
from server.app import model
from server.app.utils.db_utils import session_scope
from server.app.utils import sha_hash
from server.app import middleware
from proto import user_pb2

routes = web.RouteTableDef()
serializer = Serializer(Config.SECRET_KEY)


@routes.post('/auth')
@middleware.default
async def auth(request: user_pb2.UserIdentity, _):
    async with session_scope() as session:
        if user_id := await (
            session.query(model.User.id)
            .filter(
                model.User.email == request.email,
                model.User.password == sha_hash(request.password)
            )
        ):
            token = serializer.dumps({'id': user_id})

    # todo: response


def main():
    app = web.Application()
    app.add_routes(routes)
    web.run_app(app)


if __name__ == '__main__':
    main()
