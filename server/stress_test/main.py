
from locust import HttpUser, between, task

import random
import typing as tp
import uuid


class Organization(tp.NamedTuple):
    id: str
    services: tp.List[str]


organizations: tp.List[Organization] = []


class QMSUser(HttpUser):
    wait_time = between(0.8, 1)
    host = 'http://qms-back.nikitonsky.tk'

    user_session: str = None
    organization: Organization = None

    def on_start(self):
        login, password = str(uuid.uuid4()), str(uuid.uuid4())
        self.client.post('/client/register', json={'identity': {'email': login, 'password': password}, 'name': login})

        response = self.client.post('/client/login', json={'email': login, 'password': password})
        self.user_session = response.headers['session']

        if not random.randint(0, 10) or not organizations:
            self.client.post('/admin/create_organization', json={'name': str(uuid.uuid4())}, headers={'session': self.user_session})

            organization_id = self.client.post('/admin/get_organizations_list', headers={'session': self.user_session}).json()[0]['info']['id']

            for _ in range(3):
                self.client.post('/admin/create_service', json={'name': str(uuid.uuid4()), 'organization_id': organization_id}, headers={'session': self.user_session})

            services: tp.List[str] = []
            for service in self.client.post('/admin/get_organizations_list', headers={'session': self.user_session}).json()[0]['services']:
                services.append(service['info']['id'])

            organizations.append(Organization(organization_id, services))

        else:
            self.organization = random.choice(organizations)

        self.client.get('/client/fetch_organization', json={'id': self.organization.id}, headers={'session': self.user_session})
        self.client.post('/client/enter_queue', json={'id': random.choice(self.organization.services)}, headers={'session': self.user_session})

    @task
    def history(self):
        self.client.post('/client/get_user', headers={'session': self.user_session})
        self.client.post('/client/tickets_history', headers={'session': self.user_session})

    @task
    def fetch_organization(self):
        self.client.get('/client/fetch_organization', json={'id': self.organization.id}, headers={'session': self.user_session})

    @task(98)
    def get_current_queue_info(self):
        self.client.post('/client/get_current_queue_info', headers={'session': self.user_session})
