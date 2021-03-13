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


class Queue(BaseModel):
    __tablename__ = 'Queues'

    id = sqlalchemy.Column(types.String, primary_key=True, default=fixed_uuid)
    name = sqlalchemy.Column(types.Text)

    service_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Services.id', ondelete="CASCADE"))

    image_url = sqlalchemy.Column(types.Text)
    description = sqlalchemy.Column(types.Text)
    admins = relationship('Permission', cascade='all, delete-orphan')
    # todo: other fields


class Service(BaseModel):
    __tablename__ = 'Services'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)
    name = sqlalchemy.Column(types.Text, nullable=False)
    organization_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Organizations.id'))

    queues = relationship(Queue, cascade='all, delete-orphan')
    admins = relationship('Permission', cascade='all, delete-orphan')

    data = sqlalchemy.Column(postgresql.JSONB, default={})


class Organization(BaseModel):
    __tablename__ = 'Organizations'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)
    services = relationship(Service, cascade='all, delete-orphan')

    name = sqlalchemy.Column(types.Text, nullable=False)
    admins = relationship('Permission', cascade='all, delete-orphan')

    data = sqlalchemy.Column(types.JSON, default={})


class Permission(BaseModel):
    __tablename__ = 'Permissions'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    organization_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey(Organization.id, ondelete="CASCADE"))
    service_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey(Service.id, ondelete="CASCADE"))
    queue_id = sqlalchemy.Column(types.String, sqlalchemy.ForeignKey('Queues.id', ondelete="CASCADE"))

    permission_type = sqlalchemy.Column(types.Text)

    def __getattribute__(self, item: str):
        if item.startswith('can_'):
            item = item.split('can_')[1]
            return getattr(PERMISSIONS_MAP.get(super().__getattribute__('permission_type'), UserPermissions), item)
        return super().__getattribute__(item)


class QueueItem(BaseModel):
    __tablename__ = 'QueueItems'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    queue_id = sqlalchemy.Column(types.String, sqlalchemy.ForeignKey('Queues.id'), index=True)

    ticket_id = sqlalchemy.Column(types.Text, nullable=False)
    enqueue_at = sqlalchemy.Column(types.DateTime, server_default=func.now())
    processed = sqlalchemy.Column(types.Boolean, default=False)


class User(BaseModel):
    __tablename__ = 'Users'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    email = sqlalchemy.Column(types.Text, unique=True, index=True, nullable=False)
    password = sqlalchemy.Column(types.CHAR(256))

    name = sqlalchemy.Column(types.Text)
    surname = sqlalchemy.Column(types.Text)

    permissions = relationship(Permission, backref='user')
    attachments = relationship(UserAttachments)
    current_queue = relationship(QueueItem, uselist=False)
