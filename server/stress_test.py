from locust import HttpUser, task, between
import uuid


class QuickstartUser(HttpUser):
    wait_time = between(1, 2)
    host = 'https://qms-back.nikitonsky.tk'
    user_session = None

    def on_start(self):
        login, password = str(uuid.uuid4()), str(uuid.uuid4())
        self.client.post("/client/register", json={"identity": {"email": login, "password": password}, "name": login})

        response = self.client.post("/client/login", json={"email": login, "password": password})
        self.user_session = response.headers['session']

    @task
    def get_user(self):
        self.client.post("/client/get_user", headers={'session': self.user_session})

    @task
    def fetch_organization(self):
        self.client.get("/client/fetch_organization", json={"id": "NwHjJk"}, headers={'session': self.user_session})
