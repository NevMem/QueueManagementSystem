

class _BasePermissions(type):

    def __init__(cls, name, bases, dct):
        super().__init__(name, bases, dct)
        permissions = set()

        for item in dir(cls):
            value = getattr(cls, item)

            if type(value) == bool and value:
                permissions.add(item)

        cls.permissions = permissions


class UserPermissions(metaclass=_BasePermissions):
    pass


class ManagerPermissions(UserPermissions):
    update = True


class OwnerPermissions(ManagerPermissions):
    delete = True

    add_admins = True
    remove_admins = True

    create_service = True


PERMISSIONS_MAP = {
    'OWNER': OwnerPermissions,
    'USER': UserPermissions,
    'MANAGER': ManagerPermissions,
}


if __name__ == '__main__':
    print(OwnerPermissions.permissions)