import pytest
import subprocess
import typing as tp
import time
import requests
from server.app.main import app
from starlette.testclient import TestClient
import os

@pytest.fixture(scope='function')
def db():
    db = Database([])
    try:
        db.start()
        yield db
    finally:
        db.stop()


@pytest.fixture(scope='function')
def server(db: 'Database'):
    server = Server(db.connection_string)
    try:
        server.start()
        yield server
    finally:
        server.stop()


class Process:
    def __init__(self, cmd: tp.List[str], env: tp.Dict[str, str]=dict()):
        self.env = env
        self.cmd: tp.List[str] = cmd
        self.process: tp.Optional[subprocess.Popen] = None

    def healthcheck(self):
        return True

    def start(self):
        self.process = subprocess.Popen(self.cmd, stderr=subprocess.STDOUT, stdout=subprocess.STDOUT, env=self.env)
        tries_left = 5
        while tries_left:
            try:
                if self.healthcheck():
                    break
            except:
                pass
            time.sleep(1)
        assert tries_left

    def stop(self):
        if self.process is None:
            return
        self.process.terminate()
        time.sleep(1)
        if self.process.poll():
            self.process.kill()
        self.process = None


class Server(TestClient):
    def __init__(self, db_url):
        os.environ['DB_URL'] = db_url
        super(Server, self).__init__(app)


class Database(Process):
    def __init__(self):
        super(Database, self).__init__(['postgres', '-D', '/tmp/postgre'])

    @property
    def connection_string(self) -> str:
        return 'abrvalg'

