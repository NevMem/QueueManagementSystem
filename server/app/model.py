import sqlalchemy
import uuid
import datetime

from sqlalchemy import types
from sqlalchemy.dialects import postgresql
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func


from proto import user_pb2, permissions_pb2, organization_pb2, service_pb2, ticket_pb2

from server.app.permissions import PERMISSIONS_MAP, UserPermissions
from server.app.utils import fixed_uuid
from server.app.utils.db_utils import BaseModel, UUID


class UserAttachments(BaseModel):
    __tablename__ = 'UserAttachments'

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), primary_key=True)
    url = sqlalchemy.Column(types.Text)


class Service(BaseModel):
    __tablename__ = 'Services'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)
    index = sqlalchemy.Column(types.Integer, server_default='0')

    name = sqlalchemy.Column(types.Text, nullable=False)
    organization_id = sqlalchemy.Column(types.String, sqlalchemy.ForeignKey('Organizations.id'))

    admins = relationship('Permission', cascade='all, delete-orphan')
    tickets = relationship('Ticket', backref='service', order_by='desc(Ticket.enqueue_at)')

    default_waiting_time = sqlalchemy.Column(types.Integer, server_default='300')
    average_waiting_time = sqlalchemy.Column(types.Integer, server_default='300')

    last_updated_at = sqlalchemy.Column(types.DateTime, default=datetime.datetime(1970, 1, 1))

    timetable = sqlalchemy.Column(types.LargeBinary)
    data = sqlalchemy.Column(postgresql.JSONB, default={})

    def to_protobuf(self) -> service_pb2.Service:
        service_proto = service_pb2.Service(
            info=service_pb2.ServiceInfo(
                id=str(self.id),
                name=self.name,
                data=self.data,
                default_waiting_time=self.default_waiting_time,
                average_waiting_time=self.average_waiting_time,
            ),
            admins=[
                permission.user.to_protobuf(with_permissions=False, permission_type=permission.permission_type)
                for permission in self.admins
            ],
        )

        service_proto.info.timetable.MergeFromString(self.timetable or b'')
        return service_proto


class Organization(BaseModel):
    __tablename__ = 'Organizations'

    id = sqlalchemy.Column(types.String, primary_key=True, default=fixed_uuid)
    services = relationship(Service, cascade='all, delete-orphan', backref='organization')

    name = sqlalchemy.Column(types.Text, nullable=False)
    admins = relationship('Permission', cascade='all, delete-orphan')

    address = sqlalchemy.Column(types.Text)
    timetable = sqlalchemy.Column(types.LargeBinary)
    data = sqlalchemy.Column(types.JSON, default={})

    def to_protobuf(self, with_services: bool = True) -> organization_pb2.Organization:
        organization_proto = organization_pb2.Organization(
            info=organization_pb2.OrganizationInfo(
                id=str(self.id),
                name=self.name,
                address=self.address,
                data=self.data,
            ),
            services=[
                service.to_protobuf()
                for service in self.services
            ] if with_services else [],
            admins=[
                permission.user.to_protobuf(with_permissions=False, permission_type=permission.permission_type)
                for permission in self.admins
            ],
        )

        organization_proto.info.timetable.MergeFromString(self.timetable or b'')
        return organization_proto


class Permission(BaseModel):
    __tablename__ = 'Permissions'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    organization_id = sqlalchemy.Column(types.String, sqlalchemy.ForeignKey(Organization.id, ondelete="CASCADE"))
    service_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey(Service.id, ondelete="CASCADE"))

    permission_type = sqlalchemy.Column(types.Text)

    def __getattribute__(self, item: str):
        if item.startswith('can_'):
            item = item.split('can_')[1]
            return getattr(PERMISSIONS_MAP.get(super().__getattribute__('permission_type'), UserPermissions), item)
        return super().__getattribute__(item)

    @property
    def permissions_list(self):
        return PERMISSIONS_MAP[self.permission_type].permissions

    def to_protobuf(self) -> permissions_pb2.Permission:
        return permissions_pb2.Permission(
            id=str(self.id),
            organization_id=str(self.organization_id) if self.organization_id else None,
            service_id=str(self.service_id) if self.service_id else None,
            permission_type=self.permission_type,
        )


class Ticket(BaseModel):
    __tablename__ = 'Tickets'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    service_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Services.id'), index=True)

    ticket_id = sqlalchemy.Column(types.Text, nullable=False)
    state = sqlalchemy.Column(types.String, default='WAITING', index=True)
    resolution = sqlalchemy.Column(types.String, default='NONE', index=True)

    enqueue_at = sqlalchemy.Column(types.DateTime, server_default=func.now())
    accepted_at = sqlalchemy.Column(types.DateTime)
    finished_at = sqlalchemy.Column(types.DateTime)

    window = sqlalchemy.Column(types.String)

    servicing_by = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    manager = relationship('User', foreign_keys=[servicing_by])
    pushed = sqlalchemy.Column(types.BOOLEAN, server_default='f')

    def to_protobuf(self, with_user=False, with_permissions=False) -> ticket_pb2.Ticket:
        return ticket_pb2.Ticket(
            id=str(self.id),
            user_id=str(self.user_id),
            service_id=str(self.service_id),
            organization_id=self.service.organization_id,
            ticket_id=self.ticket_id,
            enqueue_at=self.enqueue_at.timestamp(),
            accepted_at=self.accepted_at.timestamp() if self.accepted_at else 0,
            state=ticket_pb2.Ticket.State[self.state],
            window=self.window,
            resolution=ticket_pb2.Ticket.Resolution[self.resolution],
            user=self.user.to_protobuf(with_permissions=with_permissions) if with_user else user_pb2.User()
        )


class User(BaseModel):
    __tablename__ = 'Users'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    email = sqlalchemy.Column(types.Text, unique=True, index=True, nullable=False)
    password = sqlalchemy.Column(types.CHAR(256))

    name = sqlalchemy.Column(types.Text)
    surname = sqlalchemy.Column(types.Text)

    permissions = relationship(Permission, backref='user')
    attachments = relationship(UserAttachments)
    tickets = relationship(Ticket, backref='user', foreign_keys=[Ticket.user_id])

    data = sqlalchemy.Column(types.JSON, default={})

    def to_protobuf(self, with_permissions=True, permission_type='') -> user_pb2.User:
        return user_pb2.User(
            name=self.name,
            surname=self.surname,
            email=self.email,
            id=str(self.id),
            data=self.data,
            permissions=[
                permission.to_protobuf()
                for permission in self.permissions
            ] if with_permissions else [],
            permission_type=permission_type,
        )
