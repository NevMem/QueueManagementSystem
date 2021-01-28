import asyncio
import pytest
from server.app.main import app
from starlette.testclient import TestClient
from server.app.utils.db_utils import drop_db, prepare_db


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

    def check_unique_user(self):
        return self.post('/client/check_unique_user', json={'email': 'notpufit'})
