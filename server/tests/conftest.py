import asyncio
import pytest
from server.app.main import app
from starlette.testclient import TestClient
from server.app.utils.db_utils import drop_db, prepare_db
from tools.network.network_layer import NetworkLayer
from tools.client.client import Client

from proto.organization_pb2 import OrganizationList, OrganizationInfo
from proto.service_pb2 import ServiceInfo
from proto.queue_pb2 import Queue
from proto.user_pb2 import User


@pytest.fixture(scope='function')
def server():
    return Server()


@pytest.fixture(scope='function')
def client():
    return AnotherClient()


@pytest.fixture(scope='function', autouse=True)
def clean_db():
    try:
        loop = asyncio.get_event_loop_policy().get_event_loop()
    except:
        loop = asyncio.get_event_loop_policy().new_event_loop()

    loop.run_until_complete(drop_db())
    loop.run_until_complete(prepare_db())


class MockNetworkLayer(NetworkLayer):
    def __init__(self):
        super(MockNetworkLayer, self).__init__("Mock")
        self.client = TestClient(app)

    def post(self, *args, **kwargs):
        return self.client.post(*args, **kwargs)


class AnotherClient(Client):
    def __init__(self):
        super(AnotherClient, self).__init__("Mock", use_https=False)
        self.base_url = ''
        self.layer = MockNetworkLayer()


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

    def update_user(self, token, **kwargs):
        req = User(**kwargs)
        return self.post('/client/update_user', headers={'session': token, 'content-type': 'application/protobuf'},
                         data=req.SerializeToString())

    def get_user(self, token) -> User:
        resp = self.post('/client/get_user', headers={'session': token, 'content-type': 'application/protobuf'})
        res = User()
        res.ParseFromString(resp.content)
        return res

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

    def update_organization(self, token: str, **kwargs):
        req = OrganizationInfo(**kwargs)
        return self.post('/admin/update_organization', headers={'session': token, 'content-type': 'application/protobuf'},
                         data=req.SerializeToString())

    def get_organizations(self, token: str):
        resp = self.post('/admin/get_organizations_list', headers={'session': token, 'content-type': 'application/protobuf'})
        res = OrganizationList()
        res.ParseFromString(resp.content)
        return res

    def create_service(self, token: str, name: str, organization_id: str, **kwargs):
        req = ServiceInfo(name=name, organization_id=organization_id, data=kwargs)
        return self.post('/admin/create_service', headers={'session': token, 'content-type': 'application/protobuf'}, data=req.SerializeToString())

    def update_service(self, token: str, **kwargs):
        req = ServiceInfo(**kwargs)
        return self.post('/admin/update_service', headers={'session': token, 'content-type': 'application/protobuf'},
                         data=req.SerializeToString())

    def remove_service(self, token: str, id: str):
        return self.post('/admin/remove_service', headers={'session': token}, json={'id': id})

    def create_queue(self, token: str, **kwargs):
        req = Queue(**kwargs)
        return self.post('/admin/create_queue', headers={'session': token, 'content-type': 'application/protobuf'},
                         data=req.SerializeToString())

    def get_qr(self, **kwargs):
        return self.get(
            '/admin/generate_qr',
            params=kwargs
        )
