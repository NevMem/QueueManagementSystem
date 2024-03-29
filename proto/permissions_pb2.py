# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: proto/permissions.proto
"""Generated protocol buffer code."""
from google.protobuf.internal import enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='proto/permissions.proto',
  package='qms',
  syntax='proto3',
  serialized_options=b'\n\016com.nevmem.qmsB\020PermissionsProto',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x17proto/permissions.proto\x12\x03qms\"\x83\x01\n\x0e\x41\x64\x64UserRequest\x12,\n\x0fpermission_type\x18\x01 \x01(\x0e\x32\x13.qms.PermissionType\x12(\n\rtarget_object\x18\x02 \x01(\x0e\x32\x11.qms.TargetObject\x12\n\n\x02id\x18\x03 \x01(\t\x12\r\n\x05\x65mail\x18\x04 \x01(\t\"X\n\x11RemoveUserRequest\x12(\n\rtarget_object\x18\x01 \x01(\x0e\x32\x11.qms.TargetObject\x12\n\n\x02id\x18\x02 \x01(\t\x12\r\n\x05\x65mail\x18\x03 \x01(\t\"o\n\nPermission\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0f\n\x07user_id\x18\x02 \x01(\t\x12\x17\n\x0forganization_id\x18\x03 \x01(\t\x12\x12\n\nservice_id\x18\x04 \x01(\t\x12\x17\n\x0fpermission_type\x18\x05 \x01(\t*2\n\x0ePermissionType\x12\x08\n\x04USER\x10\x00\x12\t\n\x05OWNER\x10\x01\x12\x0b\n\x07MANAGER\x10\x02*-\n\x0cTargetObject\x12\x0b\n\x07SERVICE\x10\x00\x12\x10\n\x0cORGANIZATION\x10\x01\x42\"\n\x0e\x63om.nevmem.qmsB\x10PermissionsProtob\x06proto3'
)

_PERMISSIONTYPE = _descriptor.EnumDescriptor(
  name='PermissionType',
  full_name='qms.PermissionType',
  filename=None,
  file=DESCRIPTOR,
  create_key=_descriptor._internal_create_key,
  values=[
    _descriptor.EnumValueDescriptor(
      name='USER', index=0, number=0,
      serialized_options=None,
      type=None,
      create_key=_descriptor._internal_create_key),
    _descriptor.EnumValueDescriptor(
      name='OWNER', index=1, number=1,
      serialized_options=None,
      type=None,
      create_key=_descriptor._internal_create_key),
    _descriptor.EnumValueDescriptor(
      name='MANAGER', index=2, number=2,
      serialized_options=None,
      type=None,
      create_key=_descriptor._internal_create_key),
  ],
  containing_type=None,
  serialized_options=None,
  serialized_start=369,
  serialized_end=419,
)
_sym_db.RegisterEnumDescriptor(_PERMISSIONTYPE)

PermissionType = enum_type_wrapper.EnumTypeWrapper(_PERMISSIONTYPE)
_TARGETOBJECT = _descriptor.EnumDescriptor(
  name='TargetObject',
  full_name='qms.TargetObject',
  filename=None,
  file=DESCRIPTOR,
  create_key=_descriptor._internal_create_key,
  values=[
    _descriptor.EnumValueDescriptor(
      name='SERVICE', index=0, number=0,
      serialized_options=None,
      type=None,
      create_key=_descriptor._internal_create_key),
    _descriptor.EnumValueDescriptor(
      name='ORGANIZATION', index=1, number=1,
      serialized_options=None,
      type=None,
      create_key=_descriptor._internal_create_key),
  ],
  containing_type=None,
  serialized_options=None,
  serialized_start=421,
  serialized_end=466,
)
_sym_db.RegisterEnumDescriptor(_TARGETOBJECT)

TargetObject = enum_type_wrapper.EnumTypeWrapper(_TARGETOBJECT)
USER = 0
OWNER = 1
MANAGER = 2
SERVICE = 0
ORGANIZATION = 1



_ADDUSERREQUEST = _descriptor.Descriptor(
  name='AddUserRequest',
  full_name='qms.AddUserRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='permission_type', full_name='qms.AddUserRequest.permission_type', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='target_object', full_name='qms.AddUserRequest.target_object', index=1,
      number=2, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='id', full_name='qms.AddUserRequest.id', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='email', full_name='qms.AddUserRequest.email', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=33,
  serialized_end=164,
)


_REMOVEUSERREQUEST = _descriptor.Descriptor(
  name='RemoveUserRequest',
  full_name='qms.RemoveUserRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='target_object', full_name='qms.RemoveUserRequest.target_object', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='id', full_name='qms.RemoveUserRequest.id', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='email', full_name='qms.RemoveUserRequest.email', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=166,
  serialized_end=254,
)


_PERMISSION = _descriptor.Descriptor(
  name='Permission',
  full_name='qms.Permission',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='qms.Permission.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='user_id', full_name='qms.Permission.user_id', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='organization_id', full_name='qms.Permission.organization_id', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='service_id', full_name='qms.Permission.service_id', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='permission_type', full_name='qms.Permission.permission_type', index=4,
      number=5, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=256,
  serialized_end=367,
)

_ADDUSERREQUEST.fields_by_name['permission_type'].enum_type = _PERMISSIONTYPE
_ADDUSERREQUEST.fields_by_name['target_object'].enum_type = _TARGETOBJECT
_REMOVEUSERREQUEST.fields_by_name['target_object'].enum_type = _TARGETOBJECT
DESCRIPTOR.message_types_by_name['AddUserRequest'] = _ADDUSERREQUEST
DESCRIPTOR.message_types_by_name['RemoveUserRequest'] = _REMOVEUSERREQUEST
DESCRIPTOR.message_types_by_name['Permission'] = _PERMISSION
DESCRIPTOR.enum_types_by_name['PermissionType'] = _PERMISSIONTYPE
DESCRIPTOR.enum_types_by_name['TargetObject'] = _TARGETOBJECT
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

AddUserRequest = _reflection.GeneratedProtocolMessageType('AddUserRequest', (_message.Message,), {
  'DESCRIPTOR' : _ADDUSERREQUEST,
  '__module__' : 'proto.permissions_pb2'
  # @@protoc_insertion_point(class_scope:qms.AddUserRequest)
  })
_sym_db.RegisterMessage(AddUserRequest)

RemoveUserRequest = _reflection.GeneratedProtocolMessageType('RemoveUserRequest', (_message.Message,), {
  'DESCRIPTOR' : _REMOVEUSERREQUEST,
  '__module__' : 'proto.permissions_pb2'
  # @@protoc_insertion_point(class_scope:qms.RemoveUserRequest)
  })
_sym_db.RegisterMessage(RemoveUserRequest)

Permission = _reflection.GeneratedProtocolMessageType('Permission', (_message.Message,), {
  'DESCRIPTOR' : _PERMISSION,
  '__module__' : 'proto.permissions_pb2'
  # @@protoc_insertion_point(class_scope:qms.Permission)
  })
_sym_db.RegisterMessage(Permission)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
