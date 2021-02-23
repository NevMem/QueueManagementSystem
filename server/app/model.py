import sqlalchemy
import uuid

from sqlalchemy import types
from sqlalchemy.orm import relationship

from server.app.utils.db_utils import BaseModel, UUID
from server.app.permissions import PERMISSIONS_MAP, UserPermissions


class UserAttachments(BaseModel):
    __tablename__ = 'UserAttachments'

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), primary_key=True)
    url = sqlalchemy.Column(types.Text)


class Queue(BaseModel):
    __tablename__ = 'Queues'

    id = sqlalchemy.Column(types.Integer, primary_key=True, autoincrement=True)
    name = sqlalchemy.Column(types.Text)

    service_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Services.id'))

    image_url = sqlalchemy.Column(types.Text)
    description = sqlalchemy.Column(types.Text)
    admins = relationship('Permission', cascade='all, delete-orphan')
    # todo: other fields


class Service(BaseModel):
    __tablename__ = 'Services'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)
    name = sqlalchemy.Column(types.Text, nullable=False)
    organisation_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Organisations.id'))

    queues = relationship(Queue, cascade='all, delete-orphan')

    name = sqlalchemy.Column(types.Text, nullable=False)
    data = sqlalchemy.Column(types.JSON, default={})


class Organisation(BaseModel):
    __tablename__ = 'Organisations'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)
    services = relationship(Service, cascade='all, delete-orphan')

    name = sqlalchemy.Column(types.Text, nullable=False)
    admins = relationship('Permission', cascade='all, delete-orphan')

    data = sqlalchemy.Column(types.JSON, default={})


class Permission(BaseModel):
    __tablename__ = 'Permissions'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4)

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    organisation_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey(Organisation.id))
    queue_id = sqlalchemy.Column(types.Integer, sqlalchemy.ForeignKey('Queues.id'))

    permission_type = sqlalchemy.Column(types.Text)

    def __getattribute__(self, item: str):
        if item.startswith('can_'):
            item = item.split('can_')[1]
            return getattr(PERMISSIONS_MAP.get(super().__getattribute__('permission_type'), UserPermissions), item)
        return super().__getattribute__(item)


class QueueItem(BaseModel):
    __tablename__ = 'QueueItems'

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), primary_key=True)
    queue_id = sqlalchemy.Column(types.Integer, sqlalchemy.ForeignKey('Queues.id'), index=True)


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
