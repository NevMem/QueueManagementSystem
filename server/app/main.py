
from google.protobuf import empty_pb2
from sqlalchemy.sql import select, and_, delete, update
from sqlalchemy.orm import selectinload
import qrcode
import qrcode.image.svg
import io
import json

from starlette.authentication import requires
from starlette.exceptions import HTTPException

from proto import user_pb2, permissions_pb2, organization_pb2, queue_pb2, service_pb2
from server.app.middleware import middleware
from server.app import model
from server.app.utils import sha_hash, generate_next_ticket
from server.app.utils.db_utils import prepare_db
from server.app.utils.web import route, prepare_app, Request, ProtobufResponse, Response


def organization_from_model(organization):
    return organization_pb2.Organization(
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
            )
            for service in organization.services
        ]
    )


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
                service_id=str(permission.service_id) if permission.service_id else None,
                permission_type=permission.permission_type,
            )
            for permission in request.user.permissions
        ],
        data=request.user.data
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


@route('/client/update_user', methods=['POST'], request_type=user_pb2.User)
@requires('authenticated')
async def update_user(request: Request) -> ProtobufResponse:
    query = (
        update(model.User)
        .where(model.User.id == request.user.id)
        .values({
            model.User.email: request.parsed.email,
            model.User.name: request.parsed.name,
            model.User.surname: request.parsed.surname,
            model.User.data: dict(**request.parsed.data)
        })
    )
    await request.connection.execute(query)
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
        .options(selectinload(model.Organization.services))
    )

    result = await request.connection.execute(query)
    organizations = result.scalars().all()

    result = []
    for organization in organizations:
        result.append(
            organization_from_model(organization)
        )

    return ProtobufResponse(organization_pb2.OrganizationList(organizations=result))


@route('/client/fetch_organization', methods=['POST', 'GET'], request_type=organization_pb2.OrganizationInfo)
async def fetch_organization(request: Request):
    query = (
        select(model.Organization)
        .where(model.Organization.id == request.parsed.id)
        .options(selectinload(model.Organization.services))
    )

    result = await request.connection.execute(query)
    organization = result.scalars().first()

    if organization is None:
        raise HTTPException(404)

    return ProtobufResponse(organization_from_model(organization))


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


@route('/admin/generate_qr', methods=['GET'])
async def generate_qr(request: Request):
    payload = {'organization': request.query_params['organization']}
    if 'organization' not in request.query_params:
        raise HTTPException(400)

    if 'service' in request.query_params:
        payload['service'] = request.query_params['service']
        query = (
            select(model.Service)
            .where(and_(
                model.Service.id == request.query_params['service'],
                model.Service.organization_id == request.query_params['organization']
                )
            ).limit(1)
        )
    else:
        query = (
            select(model.Organization)
            .where(model.Organization.id == request.query_params['organization'])
            .limit(1)
        )

    if len([await request.connection.execute(query)]) == 0:
        raise HTTPException(404)

    img = qrcode.make(json.dumps(payload), image_factory=qrcode.image.svg.SvgImage)
    resp = io.BytesIO()
    img.save(resp)
    return Response(content=resp.getvalue())


@route('/client/enter_queue', methods=['POST'], request_type=queue_pb2.Queue)
@requires('authenticated')
async def enter_queue(request: Request):
    query = (
        select(model.Ticket)
        .where(model.Service.id == request.parsed.id)
        .join(model.Service, model.Service.id == model.Ticket.service_id)
        .order_by(model.Ticket.enqueue_at.desc())
        .limit(1)
    )

    result = await request.connection.execute(query)
    last_queue_item = result.scalars().first()

    if last_queue_item is None:
        last_ticket_id = 'A00'
    else:
        last_ticket_id = last_queue_item.ticket_id

    query = (
        select(model.Ticket)
        .where(model.Ticket.user_id == request.user.id)
        .order_by(model.Ticket.enqueue_at.desc())
        .limit(1)
    )

    last_ticket = (await request.connection.execute(query)).scalars().first()
    if last_ticket and not last_ticket.processed:
        raise HTTPException(409)

    new_queue_item = model.Ticket(
        user_id=request.user.id,
        service_id=request.parsed.id,
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
