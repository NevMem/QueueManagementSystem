import pytest
from server.app.main import app
from starlette.testclient import TestClient


@pytest.fixture(scope='function')
def server():
    return Server()


@pytest.fixture(scope='function', autouse=True)
def clean_db():
    pass


class Server(TestClient):
    def __init__(self):
        super(Server, self).__init__(app)
