import requests
from tools.network.network_layer import NetworkLayer


class Client:
    def __init__(self, name, use_https=False):
        if use_https:
            self.base_url = 'https://qms-back.nikitonsky.tk/'
        else:
            self.base_url = 'http://qms-back.nikitonsky.tk/'
        self.layer = NetworkLayer(name)

    def login(self, login, password):
        res = self.layer.post(
            self.base_url + 'client/login',
            json={'email': login, 'password': password })
        return res.status_code, res.text, res.headers['session']

    def register(self, login, password, name, surname):
        res = self.layer.post(
            self.base_url + 'client/register',
            json={'name': name, 'surname': surname, 'identity': {'email': login, 'password': password}},
            headers={'X-No-Confirmation': 'yes'}
        )
        return res.status_code, res.text

    def get_user(self, token):
        res = self.layer.post(
            self.base_url + 'client/get_user',
            headers={'session': token})
        return res.status_code, res.text, res.json()

    def create_organization(self, token, org_name):
        res = self.layer.post(
            self.base_url + 'admin/create_organization',
            json={'name': org_name},
            headers={'session': token})
        return res.status_code, res.text

    def load_organizations(self, token):
        res = self.layer.post(
            self.base_url + 'admin/get_organizations_list',
            headers={'session': token})
        return res.status_code, res.text, res.json()

    def create_service(self, token, org_id, name):
        res = self.layer.post(
            self.base_url + 'admin/create_service',
            json={'name': name, 'organizationId': org_id},
            headers={'session': token})
        return res.status_code, res.json()

    def enter_queue(self, token, service_id):
        res = self.layer.post(
            self.base_url + 'client/enter_queue',
            json={'id': service_id},
            headers={'session': token})
        return res.status_code, res.json()

    def left_queue(self, token):
        res = self.layer.post(
            self.base_url + 'client/left_queue',
            headers={'session': token})
        return res.status_code, res.text
