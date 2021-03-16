
from google.protobuf import empty_pb2
from sqlalchemy.sql import select, and_, delete
from sqlalchemy.orm import selectinload

from starlette.authentication import requires
from starlette.exceptions import HTTPException

from proto import user_pb2, permissions_pb2, organization_pb2, queue_pb2, service_pb2
from server.app.middleware import middleware
from server.app import model
from server.app.utils import sha_hash, generate_next_ticket
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
                organization_id=str(permission.organization_id) if permission.organization_id else None,
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


@route('/admin/create_organization', methods=['POST'], request_type=organization_pb2.OrganizationInfo)
@requires('authenticated')
async def create_organization(request: Request):
    new_organization = model.Organization(
        name=request.parsed.name,
        address=request.parsed.address,
        data=dict(**request.parsed.data)
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='Owner',
    )

    new_organization.admins.append(new_permission)
    request.connection.add(new_organization)

    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/get_organizations_list', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def get_organizations_list(request: Request):
    query = (
        select(model.Organization)
        .where(model.User.id == request.user.id)
        .join(model.Permission, model.Permission.organization_id == model.Organization.id)
        .join(model.User, model.User.id == model.Permission.user_id)
        .options(selectinload(model.Organization.services), selectinload(model.Organization.services, model.Service.queues))
    )

    result = await request.connection.execute(query)
    organizations = result.scalars().all()

    result = []
    for organization in organizations:
        result.append(
            organization_pb2.Organization(
                info=organization_pb2.OrganizationInfo(
                    id=str(organization.id),
                    name=organization.name,
                    address=organization.address,
                    data=organization.data
                ),
                services=[
                    service_pb2.Service(
                        info=service_pb2.ServiceInfo(
                            id=str(service.id),
                            name=service.name,
                            data=service.data
                        ),
                        queues=[
                            queue_pb2.Queue(
                                id=queue.id,
                                name=queue.name,
                                image_url=queue.image_url,
                            )
                            for queue in service.queues
                        ]
                    )
                    for service in organization.services
                ]
            )
        )

    return ProtobufResponse(organization_pb2.OrganizationList(organizations=result))


@route('/admin/create_service', methods=['POST'], request_type=service_pb2.ServiceInfo)
@requires('authenticated')
async def create_service(request: Request):
    new_service = model.Service(
        name=request.parsed.name,
        organization_id=request.parsed.organization_id,
        data=dict(request.parsed.data),
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='Owner',
    )

    new_service.admins.append(new_permission)
    request.connection.add(new_service)

    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/remove_service', methods=['POST'], request_type=service_pb2.ServiceInfo)
@requires('authenticated')
async def remove_service(request: Request):
    auth_query = (
        select(model.Permission)
        .where(and_(model.Permission.user_id == request.user.id,
                    model.Permission.service_id == request.parsed.id))
        .limit(1)
    )
    if len([await request.connection.execute(auth_query)]) == 0:
        raise HTTPException(403)

    delete_query = (
        delete(model.Service)
        .where(model.Service.id == request.parsed.id)
    )
    await request.connection.execute(delete_query)
    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/create_queue', methods=['POST'], request_type=queue_pb2.Queue)
@requires('authenticated')
async def create_queue(request: Request):
    new_queue = model.Queue(
        name=request.parsed.name,
        image_url=request.parsed.image_url,
        service_id=request.parsed.service_id,
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
    query = (
        select(model.QueueItem)
        .where(model.Queue.id == request.parsed.id)
        .join(model.Queue, model.Queue.id == model.QueueItem.queue_id)
        .order_by(model.QueueItem.enqueue_at.desc())
        .limit(1)
    )

    result = await request.connection.execute(query)
    last_queue_item = result.scalars().first()

    if last_queue_item is None:
        last_ticket_id = 'A00'
    else:
        last_ticket_id = last_queue_item.ticket_id

    query = (
        select(model.QueueItem)
        .where(model.QueueItem.user_id == request.user.id)
        .order_by(model.QueueItem.enqueue_at.desc())
        .limit(1)
    )

    result = await request.connection.execute(query)
    has_entered_queue = not result.scalars().first().processed
    if has_entered_queue:
        raise HTTPException(409)

    new_queue_item = model.QueueItem(
        user_id=request.user.id,
        queue_id=request.parsed.id,
        ticket_id=generate_next_ticket(last_ticket_id),
    )

    request.connection.add(new_queue_item)
    return ProtobufResponse(queue_pb2.QueueUserInfo(ticket_id=new_queue_item.ticket_id))


@route('/client/get_current_queue_info', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
async def get_current_queue_info(request: Request):
    # fixme: plug
    return ProtobufResponse(queue_pb2.QueueUserInfo(
        user_count=2,
        user_queue_position=1,
        approximate_time=10,
    ))


app = prepare_app(debug=True, middleware=middleware, on_startup=[prepare_db])
