
from google.protobuf import empty_pb2
from sqlalchemy.sql import select, delete, update, exists
from sqlalchemy.orm import selectinload
from sqlalchemy import func

import io
import json
import qrcode
import qrcode.image.svg
import typing as tp

from starlette.authentication import requires
from starlette.exceptions import HTTPException

from proto import user_pb2, permissions_pb2, organization_pb2, queue_pb2, service_pb2, ticket_pb2
from server.app.middleware import middleware
from server.app import model
from server.app.utils import sha_hash, generate_next_ticket
from server.app.utils.db_utils import prepare_db
from server.app.utils.web import route, prepare_app, Request, ProtobufResponse, Response
from server.app.utils.protobuf import patch_enums


patch_enums()


def user_from_permission(permission: model.Permission) -> user_pb2.User:
    return user_pb2.User(
        name=permission.user.name,
        surname=permission.user.surname,
        email=permission.user.email,
        id=str(permission.user.id),
        permission_type=permission.permission_type,
    )


def permission_from_model(permission: model.Permission) -> permissions_pb2.Permission:
    return permissions_pb2.Permission(
        id=str(permission.id),
        organization_id=str(permission.organization_id) if permission.organization_id else None,
        service_id=str(permission.service_id) if permission.service_id else None,
        permission_type=permission.permission_type,
    )


def user_from_model(user: model.User, with_permissions=True) -> user_pb2.User:
    return user_pb2.User(
        name=user.name,
        surname=user.surname,
        email=user.email,
        id=str(user.id),
        data=user.data,
        permissions=[
            permission_from_model(permission)
            for permission in user.permissions
        ] if with_permissions else []
    )


def service_from_model(service: model.Service) -> service_pb2.Service:
    return service_pb2.Service(
        info=service_pb2.ServiceInfo(
            id=str(service.id),
            name=service.name,
            data=service.data
        ),
        admins=[
            user_from_permission(permission)
            for permission in service.admins
        ],
    )


def organization_from_model(organization: model.Organization, with_services: bool = True) -> organization_pb2.Organization:
    return organization_pb2.Organization(
        info=organization_pb2.OrganizationInfo(
            id=str(organization.id),
            name=organization.name,
            address=organization.address,
            data=organization.data
        ),
        services=[
            service_from_model(service)
            for service in organization.services
        ] if with_services else [],
        admins=[
            user_from_permission(permission)
            for permission in organization.admins
        ],
    )


def ticket_from_model(ticket: model.Ticket, with_user=False) -> ticket_pb2.Ticket:
    return ticket_pb2.Ticket(
        id=str(ticket.id),
        user_id=str(ticket.user_id),
        service_id=str(ticket.service_id),
        organization_id=ticket.service.organization_id,
        ticket_id=ticket.ticket_id,
        enqueue_at=ticket.enqueue_at.timestamp(),
        accepted_at=ticket.enqueue_at.timestamp(),
        state=ticket_pb2.Ticket.State[ticket.state],
        window=ticket.window,
        user=user_from_model(ticket.user) if with_user else user_pb2.User()
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
    return ProtobufResponse(user_from_model(request.user))


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
        permission_type='OWNER',
    )

    new_organization.admins.append(new_permission)
    request.connection.add(new_organization)

    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/update_organization', methods=['POST'], request_type=organization_pb2.OrganizationInfo)
@requires(['authenticated', 'update'])
async def update_organization(request: Request) -> empty_pb2.Empty:
    query = (
        update(model.Organization)
        .where(model.Organization.id == request.parsed.id)
        .values({
            model.Organization.name: request.parsed.name,
            model.Organization.address: request.parsed.address,
            model.Organization.data: dict(**request.parsed.data)
        })
    )

    await request.connection.execute(query)
    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/get_organizations_list', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def get_organizations_list(request: Request):
    query_organizations = (
        select(model.Organization)
        .where(model.User.id == request.user.id)
        .join(model.Permission, model.Permission.organization_id == model.Organization.id)
        .join(model.User, model.User.id == model.Permission.user_id)
        .options(
            selectinload(model.Organization.services),
            selectinload(model.Organization.admins),
            selectinload(model.Organization.admins, model.Permission.user),
            selectinload(model.Organization.services, model.Service.admins),
            selectinload(model.Organization.services, model.Service.admins, model.Permission.user)
        )
    )

    result = await request.connection.execute(query_organizations)
    organizations = result.scalars().all()

    response: tp.List[organization_pb2.Organization] = []
    for organization in organizations:
        response.append(
            organization_from_model(organization)
        )

    query_service = (
        select(model.Service)
        .where(
            model.User.id == request.user.id,
            model.Organization.id.notin_((organization.info.id for organization in response))
        )
        .join(model.Permission, model.Permission.service_id == model.Service.id)
        .join(model.User, model.User.id == model.Permission.user_id)
        .join(model.Organization, model.Organization.id == model.Service.organization_id)
        .options(
            selectinload(model.Service.organization),
            selectinload(model.Service.organization, model.Organization.admins),
            selectinload(model.Service.organization, model.Organization.admins, model.Permission.user),
            selectinload(model.Service.admins),
            selectinload(model.Service.admins, model.Permission.user),
        )
    )

    result = await request.connection.execute(query_service)
    services = result.scalars().all()

    for service in services:
        organization = organization_from_model(service.organization, with_services=False)
        organization.services.extend((service_from_model(service), ))
        response.append(organization)

    return ProtobufResponse(organization_pb2.OrganizationList(organizations=response))


@route('/client/fetch_organization', methods=['POST', 'GET'], request_type=organization_pb2.OrganizationInfo)
async def fetch_organization(request: Request):
    query = (
        select(model.Organization)
        .where(model.Organization.id == request.parsed.id)
        .options(
            selectinload(model.Organization.services),
            selectinload(model.Organization.admins),
            selectinload(model.Organization.admins, model.Permission.user),
            selectinload(model.Organization.admins, model.Permission.user),
            selectinload(model.Organization.services, model.Service.admins),
            selectinload(model.Organization.services, model.Service.admins, model.Permission.user)
        )
    )

    result = await request.connection.execute(query)
    organization = result.scalars().first()

    if organization is None:
        raise HTTPException(404)

    return ProtobufResponse(organization_from_model(organization))


@route('/admin/create_service', methods=['POST'], request_type=service_pb2.ServiceInfo, permission_check_attr='organization_id')
@requires(['authenticated', 'create_service'])
async def create_service(request: Request):
    query = (
        select(model.Service)
        .where(
            model.Service.organization_id == request.parsed.organization_id
        )
        .order_by(model.Service.index.desc())
    )
    result = await request.connection.execute(query)
    service = result.scalars().first()

    next_index = 0
    if service is not None:
        next_index = service.index + 1

    new_service = model.Service(
        name=request.parsed.name,
        organization_id=request.parsed.organization_id,
        data=dict(request.parsed.data),
        index=next_index,
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='OWNER',
    )

    new_service.admins.append(new_permission)
    request.connection.add(new_service)

    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/update_service', methods=['POST'], request_type=service_pb2.ServiceInfo)
@requires(['authenticated', 'update'])
async def update_service(request: Request) -> empty_pb2.Empty:
    query = (
        update(model.Service)
        .where(model.Service.id == request.parsed.id)
        .values({
            model.Service.name: request.parsed.name,
            model.Service.data: dict(**request.parsed.data)
        })
    )

    await request.connection.execute(query)
    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/remove_service', methods=['POST'], request_type=service_pb2.ServiceInfo)
@requires(['authenticated', 'delete'])
async def remove_service(request: Request):
    delete_query = (
        delete(model.Service)
        .where(model.Service.id == request.parsed.id)
    )

    await request.connection.execute(delete_query)
    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/generate_qr', methods=['GET'])
async def generate_qr(request: Request):
    if 'organization' not in request.query_params:
        raise HTTPException(400)

    payload = {'organization': request.query_params['organization']}

    if 'service' in request.query_params:
        payload['service'] = request.query_params['service']
        query = (
            select(exists(model.Service))
            .where(
                model.Service.id == request.query_params['service'],
                model.Service.organization_id == request.query_params['organization']
            )
            .limit(1)
        )
    else:
        query = (
            select(model.Organization)
            .where(model.Organization.id == request.query_params['organization'])
            .limit(1)
        )

    if not (await request.connection.execute(query)).scalar():
        raise HTTPException(404)

    img = qrcode.make(json.dumps(payload), image_factory=qrcode.image.svg.SvgImage)
    resp = io.BytesIO()
    img.save(resp)
    return Response(content=resp.getvalue(), headers={'Content-type': 'image/svg+xml'})


@route('/client/enter_queue', methods=['POST'], request_type=service_pb2.ServiceInfo)
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
        last_ticket_id = '000'
    else:
        last_ticket_id = last_queue_item.ticket_id

    query = (
        select(model.Ticket)
        .where(model.Ticket.user_id == request.user.id)
        .order_by(model.Ticket.enqueue_at.desc())
        .limit(1)
    )

    last_ticket: model.Ticket = (await request.connection.execute(query)).scalars().first()
    if last_ticket and last_ticket.state != 'PROCESSED':
        raise HTTPException(409)

    query = (
        select(model.Service.index)
        .where(model.Service.id == request.parsed.id)
    )

    service_index = (await request.connection.execute(query)).scalar()

    new_queue_item = model.Ticket(
        user_id=request.user.id,
        service_id=request.parsed.id,
        ticket_id=generate_next_ticket(last_ticket_id, service_index),
    )

    request.connection.add(new_queue_item)
    return ProtobufResponse(ticket_pb2.Ticket(ticket_id=new_queue_item.ticket_id))


@route('/admin/queue_tickets', methods=['POST'], request_type=organization_pb2.OrganizationInfo)
async def queue_tickets(request: Request):
    query = (
        select(model.Ticket)
        .where(
            model.Organization.id == request.parsed.id,
            model.Ticket.state != 'PROCESSED',
        )
        .join(model.Service, model.Ticket.service_id == model.Service.id)
        .join(model.Organization, model.Organization.id == model.Service.organization_id)
        .order_by(model.Ticket.enqueue_at.desc())
        .options(
            selectinload(model.Ticket.service)
        )
    )

    result = await request.connection.execute(query)
    tickets = result.scalars().all()

    response = []
    for ticket in tickets:
        response.append(ticket_from_model(ticket))

    return ProtobufResponse(ticket_pb2.TicketList(tickets=response))


@route('/admin/add_user', methods=['POST'], request_type=permissions_pb2.AddUserRequest)
@requires(['authenticated', 'add_admins'])
async def add_user(request: Request):
    object_model = (
        model.Service
        if request.parsed.target_object == permissions_pb2.TargetObject.SERVICE
        else model.Organization
    )

    query_object = (
        select(object_model)
        .where(object_model.id == request.parsed.id)
        .options(selectinload(object_model.admins))
    )

    result = await request.connection.execute(query_object)
    permission_object = result.scalars().first()

    if permission_object is None:
        raise HTTPException(404)

    user_query = (
        select(model.User)
        .where(model.User.email == request.parsed.email)
    )

    result = await request.connection.execute(user_query)
    user = result.scalars().first()

    if user is None:
        raise HTTPException(404)

    permission_query = (
        select(exists(model.Permission))
        .where(
            model.Permission.user_id == user.id,
            (
                model.Permission.organization_id == request.parsed.id
                if object_model == model.Organization
                else model.Permission.service_id == request.parsed.id
            )
        )
    )

    result = await request.connection.execute(permission_query)
    if result.scalar():
        raise HTTPException(409)

    new_permission = model.Permission(
        user_id=user.id,
        permission_type=permissions_pb2.PermissionType[request.parsed.permission_type],
    )

    permission_object.admins.append(new_permission)
    request.connection.add(new_permission)
    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/remove_user', methods=['POST'], request_type=permissions_pb2.RemoveUserRequest)
@requires(['authenticated', 'remove_admins'])
async def remove_user(request: Request):
    object_model = (
        model.Service
        if request.parsed.target_object == permissions_pb2.TargetObject.SERVICE
        else model.Organization
    )

    query_object = (
        select(object_model)
        .where(object_model.id == request.parsed.id)
        .options(selectinload(object_model.admins))
    )

    result = await request.connection.execute(query_object)
    permission_object = result.scalars().first()

    if permission_object is None:
        raise HTTPException(404)

    user_query = (
        select(model.User)
        .where(model.User.email == request.parsed.email)
    )

    result = await request.connection.execute(user_query)
    user = result.scalars().first()

    if user is None:
        raise HTTPException(404)

    permission_filter = (
        model.Permission.user_id == user.id,
        (
            model.Permission.organization_id == request.parsed.id
            if object_model == model.Organization
            else model.Permission.service_id == request.parsed.id
        )
    )

    permission_query = (
        select(exists(model.Permission))
        .where(*permission_filter)
    )

    result = await request.connection.execute(permission_query)
    if not result.scalar():
        raise HTTPException(404)

    delete_query = (
        delete(model.Permission)
        .where(*permission_filter)
    )

    await request.connection.execute(delete_query)
    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/update_user_privilege', methods=['POST'], request_type=permissions_pb2.AddUserRequest)
@requires(['authenticated', 'remove_admins', 'add_admins'])
async def update_user_privilege(request: Request):
    object_model = (
        model.Service
        if request.parsed.target_object == permissions_pb2.TargetObject.SERVICE
        else model.Organization
    )

    query_object = (
        select(object_model)
        .where(object_model.id == request.parsed.id)
        .options(selectinload(object_model.admins))
    )

    result = await request.connection.execute(query_object)
    permission_object = result.scalars().first()

    if permission_object is None:
        raise HTTPException(404)

    user_query = (
        select(model.User)
        .where(model.User.email == request.parsed.email)
    )

    result = await request.connection.execute(user_query)
    user = result.scalars().first()

    if user is None:
        raise HTTPException(404)

    permission_query = (
        select(model.Permission)
        .where(
            model.Permission.user_id == user.id,
            (
                model.Permission.organization_id == request.parsed.id
                if object_model == model.Organization
                else model.Permission.service_id == request.parsed.id
            )
        )
    )

    result = await request.connection.execute(permission_query)
    permission = result.scalars().first()
    permission.permission_type = permissions_pb2.PermissionType[request.parsed.permission_type]
    return ProtobufResponse(empty_pb2.Empty())


@route('/client/get_current_queue_info', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def get_current_queue_info(request: Request):
    query = (
        select(model.Ticket)
        .where(
            model.Ticket.user_id == request.user.id,
        )
        .order_by(model.Ticket.enqueue_at.desc())
        .options(
            selectinload(model.Ticket.service)
        )
    )

    result = await request.connection.execute(query)
    ticket = result.scalars().first()

    if not ticket:
        raise HTTPException(404)

    if ticket.state == 'PROCESSED':
        return ProtobufResponse(ticket_pb2.TicketInfo(
            ticket=ticket_from_model(ticket),
        ))

    query = (
        select(func.count(model.Ticket.id))
        .where(
            model.Ticket.service_id == ticket.service_id,
            model.Ticket.state != 'PROCESSED',
            model.Ticket.enqueue_at < ticket.enqueue_at,
        )
    )

    result = await request.connection.execute(query)

    people_count = result.scalar()

    ticket_proto = ticket_from_model(ticket)
    ticket_proto.user.CopyFrom(user_from_model(request.user, with_permissions=False))

    return ProtobufResponse(ticket_pb2.TicketInfo(
        ticket=ticket_proto,
        people_in_front_count=people_count,
        remaining_time=60 * 5 * people_count,  # FIXME: statistic
    ))


app = prepare_app(debug=True, middleware=middleware, on_startup=[prepare_db])
