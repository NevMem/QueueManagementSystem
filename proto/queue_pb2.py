# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: proto/queue.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='proto/queue.proto',
  package='qms',
  syntax='proto3',
  serialized_options=b'\n\016com.nevmem.qmsB\nQueueProto',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x11proto/queue.proto\x12\x03qms\"H\n\x05Queue\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0c\n\x04name\x18\x02 \x01(\t\x12\x10\n\x08imageUrl\x18\x03 \x01(\t\x12\x13\n\x0b\x64\x65scription\x18\x04 \x01(\tB\x1c\n\x0e\x63om.nevmem.qmsB\nQueueProtob\x06proto3'
)




_QUEUE = _descriptor.Descriptor(
  name='Queue',
  full_name='qms.Queue',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='qms.Queue.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='name', full_name='qms.Queue.name', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='imageUrl', full_name='qms.Queue.imageUrl', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='description', full_name='qms.Queue.description', index=3,
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
  serialized_start=26,
  serialized_end=98,
)

DESCRIPTOR.message_types_by_name['Queue'] = _QUEUE
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Queue = _reflection.GeneratedProtocolMessageType('Queue', (_message.Message,), {
  'DESCRIPTOR' : _QUEUE,
  '__module__' : 'proto.queue_pb2'
  # @@protoc_insertion_point(class_scope:qms.Queue)
  })
_sym_db.RegisterMessage(Queue)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
