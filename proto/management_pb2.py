# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: proto/management.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='proto/management.proto',
  package='qms',
  syntax='proto3',
  serialized_options=b'\n\016com.nevmem.qmsB\017ManagementProto',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x16proto/management.proto\x12\x03qms\"6\n\x0fNextUserRequest\x12\x0e\n\x06window\x18\x01 \x01(\t\x12\x13\n\x0bservice_ids\x18\x02 \x03(\tB!\n\x0e\x63om.nevmem.qmsB\x0fManagementProtob\x06proto3'
)




_NEXTUSERREQUEST = _descriptor.Descriptor(
  name='NextUserRequest',
  full_name='qms.NextUserRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='window', full_name='qms.NextUserRequest.window', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='service_ids', full_name='qms.NextUserRequest.service_ids', index=1,
      number=2, type=9, cpp_type=9, label=3,
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
  serialized_start=31,
  serialized_end=85,
)

DESCRIPTOR.message_types_by_name['NextUserRequest'] = _NEXTUSERREQUEST
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

NextUserRequest = _reflection.GeneratedProtocolMessageType('NextUserRequest', (_message.Message,), {
  'DESCRIPTOR' : _NEXTUSERREQUEST,
  '__module__' : 'proto.management_pb2'
  # @@protoc_insertion_point(class_scope:qms.NextUserRequest)
  })
_sym_db.RegisterMessage(NextUserRequest)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)