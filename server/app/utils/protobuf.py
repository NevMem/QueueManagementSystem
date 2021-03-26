from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

import inspect
import sys
from google.protobuf.internal.enum_type_wrapper import EnumTypeWrapper


_ENUMS_PATCHED = False


def _get_enums(o, seen=None):
    """
    Returns an iterator over all :type:`EnumTypeWrapper` classes.
    """

    seen = seen if seen is not None else set()

    for _, member in inspect.getmembers(o, lambda x: isinstance(x, EnumTypeWrapper) or inspect.isclass(x)):
        if member not in seen:
            seen.add(member)

            if isinstance(member, EnumTypeWrapper):
                yield member

            for e in _get_enums(member, seen=seen):
                yield e


def patch_enums():
    """
    Adds enum value attributes to each generated protobuf enum wrapper.

    For example, consider following protocol:
        enum Alpha {
            FOO = 0;
            BAR = 1;
        }
        message Bravo {
            BAZ = 0;
        }

    Before `patch_enums` is ran, enum values are only accessible via `Value` method and enums aren't subscriptable:

        >>> Alpha.FOO
        <<< AttributeError: 'EnumTypeWrapper' object has no attribute 'FOO'

        >>> Alpha[0]
        <<< TypeError: 'EnumTypeWrapper' object is not subscriptable

        >>> Alpha.Value('FOO')
        <<< 0

    After `patch_enums` is ran, enum values are also accessible via class attributes and enums are subscriptable:
        >>> Alpha.FOO
        <<< 0

        >>> Alpha[0]
        <<< 'FOO'

        >>> Alpha['FOO']
        <<< 0
    """

    global _ENUMS_PATCHED

    if _ENUMS_PATCHED:
        return

    # Iterate over all python modules available in runtime.
    for module_name, module in sys.modules.items():

        # Only process protoc-generated modules.
        if module_name.endswith('_pb2'):

            for enum in _get_enums(module):
                setattr(enum, '_VALUE_MAP', {})

                for value, index in enum.items():
                    # noinspection PyProtectedMember
                    enum._VALUE_MAP[value] = index
                    # noinspection PyProtectedMember
                    enum._VALUE_MAP[index] = value

                def _get_item(self, item):
                    return self._VALUE_MAP[item]

                setattr(type(enum), '__getitem__', _get_item)

    _ENUMS_PATCHED = True
