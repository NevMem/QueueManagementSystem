from google.protobuf import empty_pb2
from sqlalchemy.sql import select

from starlette.authentication import requires
from starlette.exceptions import HTTPException

from proto import user_pb2, permissions_pb2
from server.app.middleware import middleware
from server.app import model
from server.app.utils import sha_hash
from server.app.utils.db_utils import prepare_db
from server.app.utils.web import route, prepare_app, Request, ProtobufResponse


@route('/check_auth', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def check_auth(request: Request):
    return ProtobufResponse(user_pb2.User(name=request.user.name, email=request.user.email))


@route('/ping', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
async def ping(_: Request):
    return ProtobufResponse(empty_pb2.Empty())


@route('/get_user', methods=['POST'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def get_user(request: Request):
    query = (
        select(model.Permission)
        .where(
            model.Permission.user_id == request.user.id
        )
    )

    result = await request.connection.execute(query)
    permissions = result.scalars().all()

    response = user_pb2.User(
        id=str(request.user.id),
        name=request.user.name,
        surname=request.user.surname,
        email=request.user.email,
        permissions=[
            permissions_pb2.Permission(
                id=str(permission.id),
                organisation_id=str(permission.organisation_id),
                queue_id=str(permission.queue_id),
                permission_name=permission.permission_name,
            )
            for permission in permissions
        ]
    )

    return ProtobufResponse(response)


@route('/register', methods=['POST'], request_type=user_pb2.RegisterRequest)
async def register(request: Request) -> ProtobufResponse:
    # todo: validation

    query = select(model.User.email).where(model.User.email == request.parsed.identity.email)
    result = await request.connection.execute(query)

    if result.scalars().first():
        raise HTTPException(409)

    new_user = model.User(
        name=request.parsed.name,
        surname=request.parsed.surname,
        email=request.parsed.identity.email,
        password=sha_hash(request.parsed.identity.password)
    )

    request.connection.add(new_user)
    return ProtobufResponse(empty_pb2.Empty())


@route('/login', methods=['POST'], request_type=user_pb2.AuthRequest)
async def login(request: Request) -> ProtobufResponse:
    query = (
        select(model.User.email)
        .filter_by(email=request.parsed.identity.email, password=sha_hash(request.parsed.identity.password))
    )

    result = await request.connection.execute(query)
    user = result.scalars().first()

    if user is None:
        return ProtobufResponse(user_pb2.AuthResponse(), status_code=401)

    request.session['user'] = user
    return ProtobufResponse(user_pb2.AuthResponse(token=user))  # todo: empty response


@route('/client/check_unique_user', methods=['POST'], request_type=user_pb2.UserIdentity)
async def check_unique_user(request: Request):
    query = (
        select(model.User.email)
        .filter_by(email=request.parsed.email)
    )

    result = await request.connection.execute(query)
    user = result.scalars().first()

    if user is None:
        return ProtobufResponse(user_pb2.CheckUniqueUserResponse(is_unique=True))
    return ProtobufResponse(user_pb2.CheckUniqueUserResponse(is_unique=False))


app = prepare_app(debug=True, middleware=middleware, on_startup=[prepare_db])
