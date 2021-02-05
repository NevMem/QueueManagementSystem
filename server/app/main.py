
from google.protobuf import empty_pb2
from sqlalchemy.sql import select

from starlette.authentication import requires
from starlette.exceptions import HTTPException

from proto import user_pb2, permissions_pb2, organization_pb2, queue_pb2
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


@route('/client/get_user', methods=['POST'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def get_user(request: Request):
    response = user_pb2.User(
        id=str(request.user.id),
        name=request.user.name,
        surname=request.user.surname,
        email=request.user.email,
        permissions=[
            permissions_pb2.Permission(
                id=str(permission.id),
                organisation_id=str(permission.organisation_id) if permission.organisation_id else None,
                queue_id=str(permission.queue_id) if permission.queue_id else None,
                permission_type=permission.permission_type,
            )
            for permission in request.user.permissions
        ]
    )

    return ProtobufResponse(response)


@route('/client/register', methods=['POST'], request_type=user_pb2.RegisterRequest)
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


@route('/client/login', methods=['POST'], request_type=user_pb2.UserIdentity)
async def login(request: Request) -> ProtobufResponse:
    query = (
        select(model.User.email)
        .where(
            model.User.email == request.parsed.email,
            model.User.password == sha_hash(request.parsed.password)
        )
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


@route('/admin/create_organisation', methods=['POST'], request_type=organization_pb2.OrganizationInfo)
@requires('authenticated')
async def create_organisation(request: Request):
    new_organisation = model.Organisation(
        name=request.parsed.name,
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='Owner',
    )

    new_organisation.admins.append(new_permission)
    request.connection.add(new_organisation)

    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/get_organisations_list', methods=['POST', 'GET'], request_type=organization_pb2.OrganizationInfo)
@requires('authenticated')
async def get_organisations_list(request: Request):
    query = (
        select(model.Organisation)
        .where(model.User.id == request.user.id)
        .join(model.Permission, model.Permission.organisation_id == model.Organisation.id)
        .join(model.User, model.User.id == model.Permission.user_id)
    )

    result = await request.connection.execute(query)
    organisations = result.scalars().all()

    result = []
    for organisation in organisations:
        result.append(organization_pb2.OrganizationInfo(
            id=str(organisation.id),
            name=organisation.name,
        ))

    return ProtobufResponse(organization_pb2.OrganisationList(organisations=result))


@route('/admin/create_queue', methods=['POST'], request_type=queue_pb2.Queue)
@requires('authenticated')
async def create_queue(request: Request):
    new_queue = model.Queue(
        name=request.parsed.name,
        image_url=request.parsed.image_url,
        organisation_id=request.parsed.organisation_id,
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='Owner',
    )

    new_queue.admins.append(new_permission)
    request.connection.add(new_queue)

    return ProtobufResponse(empty_pb2.Empty())


@route('/client/enter_queue', methods=['POST'], request_type=queue_pb2.Queue)
@requires('authenticated')
async def enter_queue(request: Request):
    new_queue_item = model.QueueItem(
        user_id=request.user.id,
        queue_id=request.parsed.id,
    )

    request.connection.add(new_queue_item)
    return ProtobufResponse(empty_pb2.Empty())


@route('/client/get_current_queue_info', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
async def get_current_queue_info(request: Request):
    return ProtobufResponse(queue_pb2.QueueUserInfo(
        user_count=2,
        user_queue_position=1,
        approximate_time=10,
    ))


app = prepare_app(debug=True, middleware=middleware, on_startup=[prepare_db])
