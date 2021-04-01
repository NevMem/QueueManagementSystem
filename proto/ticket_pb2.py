# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: proto/ticket.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from proto import user_pb2 as proto_dot_user__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='proto/ticket.proto',
  package='qms',
  syntax='proto3',
  serialized_options=b'\n\016com.nevmem.qmsB\013TicketProto',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x12proto/ticket.proto\x12\x03qms\x1a\x10proto/user.proto\"\x8c\x01\n\x06Ticket\x12\n\n\x02id\x18\x01 \x01(\t\x12\x0f\n\x07user_id\x18\x02 \x01(\t\x12\x12\n\nservice_id\x18\x03 \x01(\t\x12\x11\n\tticket_id\x18\x04 \x01(\t\x12\x12\n\nenqueue_at\x18\x05 \x01(\r\x12\x11\n\tprocessed\x18\x06 \x01(\x08\x12\x17\n\x04user\x18\x07 \x01(\x0b\x32\t.qms.User\"*\n\nTicketList\x12\x1c\n\x07tickets\x18\x01 \x03(\x0b\x32\x0b.qms.TicketB\x1d\n\x0e\x63om.nevmem.qmsB\x0bTicketProtob\x06proto3'
  ,
  dependencies=[proto_dot_user__pb2.DESCRIPTOR,])




_TICKET = _descriptor.Descriptor(
  name='Ticket',
  full_name='qms.Ticket',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='id', full_name='qms.Ticket.id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='user_id', full_name='qms.Ticket.user_id', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='service_id', full_name='qms.Ticket.service_id', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='ticket_id', full_name='qms.Ticket.ticket_id', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=b"".decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='enqueue_at', full_name='qms.Ticket.enqueue_at', index=4,
      number=5, type=13, cpp_type=3, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='processed', full_name='qms.Ticket.processed', index=5,
      number=6, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='user', full_name='qms.Ticket.user', index=6,
      number=7, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
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
  serialized_start=46,
  serialized_end=186,
)


_TICKETLIST = _descriptor.Descriptor(
  name='TicketList',
  full_name='qms.TicketList',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='tickets', full_name='qms.TicketList.tickets', index=0,
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
  serialized_start=188,
  serialized_end=230,
)

_TICKET.fields_by_name['user'].message_type = proto_dot_user__pb2._USER
_TICKETLIST.fields_by_name['tickets'].message_type = _TICKET
DESCRIPTOR.message_types_by_name['Ticket'] = _TICKET
DESCRIPTOR.message_types_by_name['TicketList'] = _TICKETLIST
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Ticket = _reflection.GeneratedProtocolMessageType('Ticket', (_message.Message,), {
  'DESCRIPTOR' : _TICKET,
  '__module__' : 'proto.ticket_pb2'
  # @@protoc_insertion_point(class_scope:qms.Ticket)
  })
_sym_db.RegisterMessage(Ticket)

TicketList = _reflection.GeneratedProtocolMessageType('TicketList', (_message.Message,), {
  'DESCRIPTOR' : _TICKETLIST,
  '__module__' : 'proto.ticket_pb2'
  # @@protoc_insertion_point(class_scope:qms.TicketList)
  })
_sym_db.RegisterMessage(TicketList)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
