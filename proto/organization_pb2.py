# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: proto/organization.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from proto import service_pb2 as proto_dot_service__pb2
from proto import timetable_pb2 as proto_dot_timetable__pb2
from proto import user_pb2 as proto_dot_user__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='proto/organization.proto',
  package='qms',
  syntax='proto3',
  serialized_options=b'\n\016com.nevmem.qmsB\021OrganizitionProto',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x18proto/organization.proto\x12\x03qms\x1a\x13proto/service.proto\x1a\x15proto/timetable.proto\x1a\x10proto/user.proto\"\xce\x01\n\x10OrganizationInfo\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0c\n\x04name\x18\x02 \x01(\t\x12\x0f\n\x07\x61\x64\x64ress\x18\x03 \x01(\t\x12-\n\x04\x64\x61ta\x18\x04 \x03(\x0b\x32\x1f.qms.OrganizationInfo.DataEntry\x12\x10\n\x08timezone\x18\x05 \x01(\x05\x12!\n\ttimetable\x18\x06 \x01(\x0b\x32\x0e.qms.Timetable\x1a+\n\tDataEntry\x12\x0b\n\x03key\x18\x01 \x01(\t\x12\r\n\x05value\x18\x02 \x01(\t:\x02\x38\x01\"n\n\x0cOrganization\x12#\n\x04info\x18\x01 \x01(\x0b\x32\x15.qms.OrganizationInfo\x12\x19\n\x06\x61\x64mins\x18\x03 \x03(\x0b\x32\t.qms.User\x12\x1e\n\x08services\x18\x02 \x03(\x0b\x32\x0c.qms.Service\"D\n\x14OrganizationInfoList\x12,\n\rorganizations\x18\x01 \x03(\x0b\x32\x15.qms.OrganizationInfo\"<\n\x10OrganizationList\x12(\n\rorganizations\x18\x01 \x03(\x0b\x32\x11.qms.OrganizationB#\n\x0e\x63om.nevmem.qmsB\x11OrganizitionProtob\x06proto3'
  ,
  dependencies=[proto_dot_service__pb2.DESCRIPTOR,proto_dot_timetable__pb2.DESCRIPTOR,proto_dot_user__pb2.DESCRIPTOR,])




_ORGANIZATIONINFO_DATAENTRY = _descriptor.Descriptor(
  name='DataEntry',
  full_name='qms.OrganizationInfo.DataEntry',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='key', full_name='qms.OrganizationInfo.DataEntry.key', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='value', full_name='qms.OrganizationInfo.DataEntry.value', index=1,
      number=2, type=9, cpp_type=9, label=1,
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
  serialized_options=b'8\001',
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=259,
  serialized_end=302,
)

_ORGANIZATIONINFO = _descriptor.Descriptor(
  name='OrganizationInfo',
  full_name='qms.OrganizationInfo',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='qms.OrganizationInfo.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='name', full_name='qms.OrganizationInfo.name', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='address', full_name='qms.OrganizationInfo.address', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='data', full_name='qms.OrganizationInfo.data', index=3,
      number=4, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='timezone', full_name='qms.OrganizationInfo.timezone', index=4,
      number=5, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='timetable', full_name='qms.OrganizationInfo.timetable', index=5,
      number=6, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[_ORGANIZATIONINFO_DATAENTRY, ],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=96,
  serialized_end=302,
)


_ORGANIZATION = _descriptor.Descriptor(
  name='Organization',
  full_name='qms.Organization',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='info', full_name='qms.Organization.info', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='admins', full_name='qms.Organization.admins', index=1,
      number=3, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='services', full_name='qms.Organization.services', index=2,
      number=2, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
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
  serialized_start=304,
  serialized_end=414,
)


_ORGANIZATIONINFOLIST = _descriptor.Descriptor(
  name='OrganizationInfoList',
  full_name='qms.OrganizationInfoList',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='organizations', full_name='qms.OrganizationInfoList.organizations', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
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
  serialized_start=416,
  serialized_end=484,
)


_ORGANIZATIONLIST = _descriptor.Descriptor(
  name='OrganizationList',
  full_name='qms.OrganizationList',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='organizations', full_name='qms.OrganizationList.organizations', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
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
  serialized_start=486,
  serialized_end=546,
)

_ORGANIZATIONINFO_DATAENTRY.containing_type = _ORGANIZATIONINFO
_ORGANIZATIONINFO.fields_by_name['data'].message_type = _ORGANIZATIONINFO_DATAENTRY
_ORGANIZATIONINFO.fields_by_name['timetable'].message_type = proto_dot_timetable__pb2._TIMETABLE
_ORGANIZATION.fields_by_name['info'].message_type = _ORGANIZATIONINFO
_ORGANIZATION.fields_by_name['admins'].message_type = proto_dot_user__pb2._USER
_ORGANIZATION.fields_by_name['services'].message_type = proto_dot_service__pb2._SERVICE
_ORGANIZATIONINFOLIST.fields_by_name['organizations'].message_type = _ORGANIZATIONINFO
_ORGANIZATIONLIST.fields_by_name['organizations'].message_type = _ORGANIZATION
DESCRIPTOR.message_types_by_name['OrganizationInfo'] = _ORGANIZATIONINFO
DESCRIPTOR.message_types_by_name['Organization'] = _ORGANIZATION
DESCRIPTOR.message_types_by_name['OrganizationInfoList'] = _ORGANIZATIONINFOLIST
DESCRIPTOR.message_types_by_name['OrganizationList'] = _ORGANIZATIONLIST
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

OrganizationInfo = _reflection.GeneratedProtocolMessageType('OrganizationInfo', (_message.Message,), {

  'DataEntry' : _reflection.GeneratedProtocolMessageType('DataEntry', (_message.Message,), {
    'DESCRIPTOR' : _ORGANIZATIONINFO_DATAENTRY,
    '__module__' : 'proto.organization_pb2'
    # @@protoc_insertion_point(class_scope:qms.OrganizationInfo.DataEntry)
    })
  ,
  'DESCRIPTOR' : _ORGANIZATIONINFO,
  '__module__' : 'proto.organization_pb2'
  # @@protoc_insertion_point(class_scope:qms.OrganizationInfo)
  })
_sym_db.RegisterMessage(OrganizationInfo)
_sym_db.RegisterMessage(OrganizationInfo.DataEntry)

Organization = _reflection.GeneratedProtocolMessageType('Organization', (_message.Message,), {
  'DESCRIPTOR' : _ORGANIZATION,
  '__module__' : 'proto.organization_pb2'
  # @@protoc_insertion_point(class_scope:qms.Organization)
  })
_sym_db.RegisterMessage(Organization)

OrganizationInfoList = _reflection.GeneratedProtocolMessageType('OrganizationInfoList', (_message.Message,), {
  'DESCRIPTOR' : _ORGANIZATIONINFOLIST,
  '__module__' : 'proto.organization_pb2'
  # @@protoc_insertion_point(class_scope:qms.OrganizationInfoList)
  })
_sym_db.RegisterMessage(OrganizationInfoList)

OrganizationList = _reflection.GeneratedProtocolMessageType('OrganizationList', (_message.Message,), {
  'DESCRIPTOR' : _ORGANIZATIONLIST,
  '__module__' : 'proto.organization_pb2'
  # @@protoc_insertion_point(class_scope:qms.OrganizationList)
  })
_sym_db.RegisterMessage(OrganizationList)


DESCRIPTOR._options = None
_ORGANIZATIONINFO_DATAENTRY._options = None
# @@protoc_insertion_point(module_scope)
