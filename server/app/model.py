import sqlalchemy
import uuid

from sqlalchemy import types
from sqlalchemy.orm import relationship
from sqlalchemy.dialects import postgresql
from server.app.utils.db_utils import BaseModel, UUID


class User(BaseModel):
    __tablename__ = 'Users'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4())

    email = sqlalchemy.Column(types.Text, unique=True, index=True, nullable=False)
    password = sqlalchemy.Column(types.CHAR(256))

    name = sqlalchemy.Column(types.Text)
    surname = sqlalchemy.Column(types.Text)

    permissions = relationship('Permissions')
    attachments = relationship('UserAttachments')


class UserAttachments(BaseModel):
    __tablename__ = 'UserAttachments'

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    url = sqlalchemy.Column(types.Text)


class Organisation(BaseModel):
    __tablename__ = 'Organisations'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4())
    queues = relationship('Queues', cascade='all, delete-orphan')

    name = sqlalchemy.Column(types.Text, nullable=False)

    image_url = sqlalchemy.Column(types.Text)
    # todo: other fields


class Queue(BaseModel):
    __tablename__ = 'Queues'

    id = sqlalchemy.Column(UUID(), primary_key=True, default=uuid.uuid4())
    name = sqlalchemy.Column(types.Text)
    
    organisation_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Organisations.id'))

    image_url = sqlalchemy.Column(types.Text)
    description = sqlalchemy.Column(types.Text)
    # todo: other fields


class PermissionType(BaseModel):
    __tablename__ = 'PermissionTypes'

    name = sqlalchemy.Column(types.Text, primary_key=True)
    priority = sqlalchemy.Column(types.Integer, server_default=0)
    rights = sqlalchemy.Column(postgresql.JSONB, server_default='"{}"::jsonb')


class Permission(BaseModel):
    __tablename__ = 'Permissions'

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), index=True)
    organisation_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Organisation.id'))
    queue_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Queues.id'))

    permission_name = sqlalchemy.Column(types.Text, sqlalchemy.ForeignKey('PermissionTypes.name'))


class QueueItem(BaseModel):
    __tablename__ = 'QueueItems'

    user_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Users.id'), primary_key=True)
    queue_id = sqlalchemy.Column(UUID(), sqlalchemy.ForeignKey('Queues.id'), index=True)
