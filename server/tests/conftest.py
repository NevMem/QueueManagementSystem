import pytest
from server.app.main import app
from starlette.testclient import TestClient


@pytest.fixture(scope='function')
def server():
    return Server()



class Server(TestClient):
    def __init__(self):
        super(Server, self).__init__(app)

    def ping(self):
        return self.get('/ping')

    def check_unique_user(self):
        return self.post('/client/check_unique_user', json={'email': 'notpufit'})
