from aiohttp import web
from itsdangerous.serializer import Serializer
from Crypto.Hash import SHA256

from server.app.config import Config
from server.app import model
from server.app.utils.db_utils import session_scope
from proto import user_pb2

routes = web.RouteTableDef()
serializer = Serializer(Config.SECRET_KEY)


def sha_hash(s: str) -> str:
    return SHA256.new(s.encode()).hexdigest()


@routes.get('/ping')
async def ping(request: web.Request):
    return web.Response()


@routes.post('/auth')
async def auth(request: web.Request):
    user_identity = user_pb2.UserIdentity.ParseFromString(await request.read())

    async with session_scope() as session:
        if user_id := await (
            session.query(model.User.id)
            .filter(
                model.User.email == user_identity.email,
                model.User.password == sha_hash(user_identity.password)
            )
        ):
            token = serializer.dumps({'id': user_id})

    # todo: response, Proto/Json serializer

def main():
    app = web.Application()
    app.add_routes(routes)
    web.run_app(app)


if __name__ == '__main__':
    main()
