from google.protobuf import empty_pb2
from sqlalchemy.sql import select

from starlette.authentication import requires

from proto import user_pb2
from server.app.middleware import middleware
from server.app import model
from server.app.utils import sha_hash
from server.app.utils.db_utils import session_scope, prepare_db
from server.app.utils.web import route, prepare_app, Request, ProtobufResponse


@route('/', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def ping(request: Request):
    return ProtobufResponse(user_pb2.User(name=request.user.username, email='pufit@yandex.ru'))


@route('/register', methods=['POST'], request_type=user_pb2.RegisterRequest)
async def register(request: Request) -> ProtobufResponse:
    # todo: validation

    async with session_scope() as session:
        new_user = model.User(
            name=request.parsed.name,
            surname=request.parsed.surname,
            email=request.parsed.identity.email,
            password=sha_hash(request.parsed.identity.password)
        )

        session.add(new_user)

    return ProtobufResponse(empty_pb2.Empty())


@route('/login', methods=['POST'], request_type=user_pb2.AuthRequest)
async def login(request: Request) -> ProtobufResponse:
    async with session_scope() as session:
        query = (
            select(model.User.email)
            .filter_by(email=request.parsed.identity.email, password=sha_hash(request.parsed.identity.password))
        )

        result = await session.execute(query)
        user = result.scalars().first()

        if user is None:
            return ProtobufResponse(user_pb2.AuthResponse(), status_code=401)

        request.session['user'] = user
        return ProtobufResponse(user_pb2.AuthResponse(token=user))  # todo: empty response


app = prepare_app(debug=True, middleware=middleware, on_startup=[prepare_db])
