

class _BasePermissions(type):

    def __getattribute__(cls, item):
        if item in super().__getattribute__('__dict__'):
            return super().__getattribute__(item)

        return False

    def __getitem__(self, item):
        return self.__getattribute__(item)


class UserPermissions(metaclass=_BasePermissions):
    pass


class OwnerPermissions(UserPermissions):
    update_info = True
    delete = True
    add_admins = True
    create_queue = True


PERMISSIONS_MAP = {
    'Owner': OwnerPermissions,
    'User': UserPermissions,
}
