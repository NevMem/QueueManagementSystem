import sqlalchemy
import uuid

from sqlalchemy import types
from sqlalchemy.dialects import postgresql
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

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
    tickets = relationship('Ticket', backref='service')

    data = sqlalchemy.Column(postgresql.JSONB, default={})


class Organization(BaseModel):
    __tablename__ = 'Organizations'

    id = sqlalchemy.Column(types.String, primary_key=True, default=fixed_uuid)
    services = relationship(Service, cascade='all, delete-orphan', backref='organization')

    name = sqlalchemy.Column(types.Text, nullable=False)
    admins = relationship('Permission', cascade='all, delete-orphan')

    address = sqlalchemy.Column(types.Text)
    data = sqlalchemy.Column(types.JSON, default={})


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


class Ticket(BaseModel):
    __tablename__ = 'Tickets'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    service_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Services.id'), index=True)

    ticket_id = sqlalchemy.Column(types.Text, nullable=False)
    state = sqlalchemy.Column(types.String, default='WAITING', index=True)

    enqueue_at = sqlalchemy.Column(types.DateTime, server_default=func.now())
    accepted_at = sqlalchemy.Column(types.DateTime)

    window = sqlalchemy.Column(types.String)


class User(BaseModel):
    __tablename__ = 'Users'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    email = sqlalchemy.Column(types.Text, unique=True, index=True, nullable=False)
    password = sqlalchemy.Column(types.CHAR(256))

    name = sqlalchemy.Column(types.Text)
    surname = sqlalchemy.Column(types.Text)

    permissions = relationship(Permission, backref='user')
    attachments = relationship(UserAttachments)
    tickets = relationship(Ticket, backref='user')

    data = sqlalchemy.Column(types.JSON, default={})
