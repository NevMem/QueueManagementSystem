import asyncio
import pytest
from server.app.main import app
from starlette.testclient import TestClient
from server.app.utils.db_utils import drop_db, prepare_db

from proto.organization_pb2 import OrganizationList


@pytest.fixture(scope='function')
def server():
    return Server()


@pytest.fixture(scope='function', autouse=True)
def clean_db():
    try:
        loop = asyncio.get_event_loop_policy().get_event_loop()
    except:
        loop = asyncio.get_event_loop_policy().new_event_loop()

    loop.run_until_complete(drop_db())
    loop.run_until_complete(prepare_db())


class Server(TestClient):
    def __init__(self):
        super(Server, self).__init__(app)

    def ping(self):
        return self.get('/ping')

    def check_unique_user(self, email: str):
        return self.post('/client/check_unique_user', json={'email': email})

    def register_user(self, email: str, password: str, name: str = '', surname: str = ''):
        return self.post('/client/register', json={'identity': {'email': email, 'password': password}, 'name': name, 'surname': surname})

    def login(self, email: str, password: str):
        return self.post('/client/login', json={'email': email, 'password': password, })

    def get(self, *args, **kwargs):
        resp = super(Server, self).get(*args, **kwargs)
        resp.raise_for_status()
        return resp

    def post(self, *args, **kwargs):
        resp = super(Server, self).post(*args, **kwargs)
        resp.raise_for_status()
        return resp

    def create_organization(self, token: str, **kwargs):
        return self.post('/admin/create_organization', json=kwargs, headers={'session': token})

    def get_organizations(self, token: str):
        resp = self.post('/admin/get_organizations_list', headers={'session': token, 'content-type': 'application/protobuf'})
        res = OrganizationList()
        res.ParseFromString(resp.content)
        return res