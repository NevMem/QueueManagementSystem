import asyncio
import pytest
from server.app.main import app
from starlette.testclient import TestClient
from server.app.utils.db_utils import drop_db

@pytest.fixture(scope='function')
def server():
    return Server()


@pytest.fixture(scope='function', autouse=True)
def clean_db():
    asyncio.run(drop_db())


class Server(TestClient):
    def __init__(self):
        super(Server, self).__init__(app)

    def ping(self):
        return self.get('/ping')
