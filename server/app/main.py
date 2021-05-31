
from google.protobuf import empty_pb2
from sqlalchemy.sql import select, delete, update, exists
from sqlalchemy.orm import selectinload
from sqlalchemy import func
import contextlib

import io
import json
import qrcode
import qrcode.image.svg
import typing as tp

from aiocache import caches
from starlette.authentication import requires
from starlette.exceptions import HTTPException

from proto import user_pb2, permissions_pb2, organization_pb2, service_pb2, ticket_pb2, management_pb2
from server.app import model
from server.app.middleware import middleware, redis_prepare
from server.app.notifications import NotificationsWorker
from server.app.statistic import StatisticWorker
from server.app.utils import sha_hash, generate_next_ticket, now
from server.app.utils.db_utils import prepare_db, session_scope
from server.app.utils.long_polling import EventManager
from server.app.utils.protobuf import patch_enums
from server.app.utils.web import route, prepare_app, Request, ProtobufResponse, Response


patch_enums()

event_manager = EventManager()


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
    return ProtobufResponse(request.user)


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

    with contextlib.suppress(Exception):
        await caches.get('redis').delete(f'user_{request.parsed.email}', timeout=0.5)

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
        data=dict(**request.parsed.data),
        timetable=request.parsed.timetable.SerializeToString(),
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='OWNER',
    )

    new_organization.admins.append(new_permission)
    request.connection.add(new_organization)

    with contextlib.suppress(Exception):
        await caches.get('redis').delete(f'user_{request.user.email}', timeout=0.5)

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
            model.Organization.data: dict(**request.parsed.data),
            model.Organization.timetable: request.parsed.timetable.SerializeToString(),
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
            organization.to_protobuf()
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
        organization = service.organization.to_protobuf(with_services=False)
        organization.services.extend((service.to_protobuf(), ))
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

    return ProtobufResponse(organization.to_protobuf())


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
        default_waiting_time=request.parsed.default_waiting_time or 300,
        timetable=request.parsed.timetable.SerializeToString(),
    )

    new_permission = model.Permission(
        user_id=request.user.id,
        permission_type='OWNER',
    )

    new_service.admins.append(new_permission)
    request.connection.add(new_service)

    with contextlib.suppress(Exception):
        await caches.get('redis').delete(f'user_{request.user.email}', timeout=0.5)

    return ProtobufResponse(empty_pb2.Empty())


@route('/admin/update_service', methods=['POST'], request_type=service_pb2.ServiceInfo)
@requires(['authenticated', 'update'])
async def update_service(request: Request) -> empty_pb2.Empty:
    query = (
        update(model.Service)
        .where(model.Service.id == request.parsed.id)
        .values({
            model.Service.name: request.parsed.name,
            model.Service.data: dict(**request.parsed.data),
            model.Service.default_waiting_time: request.parsed.default_waiting_time or 300,
            model.Service.timetable: request.parsed.timetable.SerializeToString(),
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
        response.append(ticket.to_protobuf())

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

    with contextlib.suppress(Exception):
        await caches.get('redis').delete(f'user_{user.email}', timeout=0.5)

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

    with contextlib.suppress(Exception):
        await caches.get('redis').delete(f'user_{user.email}', timeout=0.5)

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
        .with_for_update(nowait=True)
    )

    result = await request.connection.execute(permission_query)
    permission = result.scalars().first()
    permission.permission_type = permissions_pb2.PermissionType[request.parsed.permission_type]

    with contextlib.suppress(Exception):
        await caches.get('redis').delete(f'user_{user.email}', timeout=0.5)

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
        .limit(1)
    )

    result = await request.connection.execute(query)
    ticket = result.scalars().first()

    if ticket is None:
        raise HTTPException(404)

    if ticket.state == 'PROCESSED':
        return ProtobufResponse(ticket_pb2.TicketInfo(
            ticket=ticket.to_protobuf(),
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

    ticket_proto = ticket.to_protobuf()
    ticket_proto.user.CopyFrom(request.user)

    return ProtobufResponse(ticket_pb2.TicketInfo(
        ticket=ticket_proto,
        people_in_front_count=people_count,
        remaining_time=ticket.service.average_waiting_time * people_count,
    ))


@route('/client/notify_ticket_state_changed', methods=['POST', 'GET'], request_type=ticket_pb2.TicketInfo, use_connection=False)
@requires('authenticated')
async def notify_ticket_state_changed(request: Request):
    async with session_scope() as session:
        query = (
            select(model.Ticket)
            .join(model.User, model.User.id == model.Ticket.user_id)
            .where(
                model.User.email == request.user.email,
            )
            .options(
                selectinload(model.Ticket.service),
                selectinload(model.Ticket.user),
            )
            .order_by(model.Ticket.enqueue_at.desc())
            .limit(1)
        )

        result = await session.execute(query)
        ticket = result.scalars().first()

        query = (
            select(func.count(model.Ticket.id))
            .where(
                model.Ticket.service_id == ticket.service_id,
                model.Ticket.state != 'PROCESSED',
                model.Ticket.enqueue_at < ticket.enqueue_at,
            )
        )

        result = await session.execute(query)
        order = result.scalar()

        if ticket is None:
            return ProtobufResponse(ticket_pb2.Ticket())

        if ticket.state != ticket_pb2.Ticket.State[request.parsed.ticket.state] or order != request.parsed.people_in_front_count:
            return ProtobufResponse(ticket_pb2.TicketInfo(
                ticket=ticket.to_protobuf(with_user=True),
                people_in_front_count=order,
                remaining_time=ticket.service.average_waiting_time * order,
            ))

        service_id = ticket.service.id

    return ProtobufResponse(await event_manager.listen('ticket_state_changed', service_id=service_id))


@route('/admin/service_next_user', methods=['POST'], request_type=management_pb2.NextUserRequest, permission_check_attr='service_ids')
@requires(['authenticated', 'serve_users'])
async def service_next_user(request: Request):
    current_ticket_query = (
        select(model.Ticket)
        .where(
            model.Ticket.servicing_by == request.user.id,
            model.Ticket.state == 'PROCESSING',
        )
        .limit(1)
    )

    result = await request.connection.execute(current_ticket_query)
    ticket = result.scalars().first()

    if ticket is not None:
        raise HTTPException(409)

    next_ticket_query = (
        select(model.Ticket)
        .where(
            model.Ticket.service_id.in_(request.parsed.service_ids),
            model.Ticket.state == 'WAITING',
        )
        .order_by(model.Ticket.enqueue_at.asc())
        .options(
            selectinload(model.Ticket.user),
            selectinload(model.Ticket.service),
        )
        .with_for_update(nowait=True)
        .limit(1)
    )

    result = await request.connection.execute(next_ticket_query)
    ticket = result.scalars().first()

    if ticket is None:
        raise HTTPException(404)

    ticket.servicing_by = request.user.id
    ticket.state = 'PROCESSING'
    ticket.accepted_at = now()
    ticket.window = request.parsed.window

    ticket_proto = ticket.to_protobuf(with_user=True)
    event_manager.publish('ticket_state_changed', ticket_proto, service_id=ticket.service_id)

    return ProtobufResponse(ticket_proto)


@route('/admin/end_servicing', methods=['POST'], request_type=management_pb2.EndServicingRequest)
@requires('authenticated')
async def end_servicing(request: Request):
    current_ticket_query = (
        select(model.Ticket)
        .where(
            model.Ticket.servicing_by == request.user.id,
            model.Ticket.state == 'PROCESSING',
        )
        .options(
            selectinload(model.Ticket.service),
        )
        .with_for_update(nowait=True)
        .limit(1)
    )

    result = await request.connection.execute(current_ticket_query)
    ticket = result.scalars().first()

    if ticket is None:
        raise HTTPException(404)

    ticket.state = 'PROCESSED'
    ticket.resolution = ticket_pb2.Ticket.Resolution[request.parsed.resolution]
    ticket.finished_at = now()
    if ticket.resolution == 'NONE':
        ticket.resolution = 'SERVICED'

    ticket_proto = ticket.to_protobuf()
    event_manager.publish('ticket_state_changed', ticket_proto, service_id=ticket.service_id)

    return ProtobufResponse(ticket_proto)


@route('/admin/get_current_ticket', methods=['POST', 'GET'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def get_current_ticket(request: Request):
    current_ticket_query = (
        select(model.Ticket)
        .where(
            model.Ticket.servicing_by == request.user.id,
            model.Ticket.state == 'PROCESSING',
        )
        .options(
            selectinload(model.Ticket.user),
            selectinload(model.Ticket.service),
        )
        .limit(1)
    )

    result = await request.connection.execute(current_ticket_query)
    ticket = result.scalars().first()

    if ticket is None:
        raise HTTPException(404)

    return ProtobufResponse(ticket.to_protobuf(with_user=True))


@route('/client/left_queue', methods=['POST'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def left_queue(request: Request):
    current_ticket_query = (
        select(model.Ticket)
        .where(
            model.Ticket.user_id == request.user.id,
            model.Ticket.state == 'WAITING',
        )
        .options(
            selectinload(model.Ticket.service),
        )
        .with_for_update(nowait=True)
        .limit(1)
    )

    result = await request.connection.execute(current_ticket_query)
    ticket = result.scalars().first()

    if ticket is None:
        raise HTTPException(404)

    ticket.state = 'PROCESSED'
    ticket.resolution = 'GONE'
    ticket.finished_at = now()

    ticket_proto = ticket.to_protobuf()
    event_manager.publish('ticket_state_changed', ticket_proto, service_id=ticket.service_id)

    return ProtobufResponse(ticket_proto)


@route('/admin/service_tickets_history', methods=['POST'], request_type=service_pb2.ServiceInfo)
@requires(['authenticated', 'update'])
async def service_tickets_history(request: Request):
    tickets_query = (
        select(model.Ticket)
        .where(
            model.Ticket.service_id == request.parsed.id,
            model.Ticket.state == 'PROCESSED',
        )
        .options(
            selectinload(model.Ticket.user),
            selectinload(model.Ticket.service),
        )
    )

    result = await request.connection.execute(tickets_query)
    tickets = result.scalars().all()

    return ProtobufResponse(ticket_pb2.TicketList(
        tickets=[ticket.to_protobuf(with_user=True) for ticket in tickets]
    ))


@route('/client/tickets_history', methods=['POST'], request_type=empty_pb2.Empty)
@requires('authenticated')
async def user_tickets_history(request: Request):
    tickets_query = (
        select(model.Ticket)
        .where(
            model.Ticket.state == 'PROCESSED',
            model.Ticket.user_id == request.user.id,
        )
        .options(
            selectinload(model.Ticket.user),
            selectinload(model.Ticket.service),
        )
    )

    result = await request.connection.execute(tickets_query)
    tickets = result.scalars().all()

    return ProtobufResponse(ticket_pb2.TicketList(
        tickets=[ticket.to_protobuf(with_user=True) for ticket in tickets]
    ))


app = prepare_app(debug=True, middleware=middleware, on_startup=[prepare_db, StatisticWorker().start, NotificationsWorker().start, redis_prepare])
