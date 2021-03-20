import requests


class Client:
    def __init__(self):
        self.base_url = 'http://qms-back.nikitonsky.tk/'

    def login(self, login, password):
        res = requests.post(
            self.base_url + 'client/login',
            json={'email': login, 'password': password })
        return res.status_code, res.text, res.headers['session']

    def register(self, login, password, name, surname):
        res = requests.post(
            self.base_url + 'client/register',
            json={'name': name, 'surname': surname, 'identity': { 'email': login, 'password': password }})
        return res.status_code, res.text

    def get_user(self, token):
        res = requests.post(
            self.base_url + 'client/get_user',
            headers={'session': token})
        return res.status_code, res.text, res.json()

    def create_organization(self, token, org_name):
        res = requests.post(
            self.base_url + 'admin/create_organization',
            json={'name': org_name},
            headers={'session': token})
        return res.status_code, res.text

    def load_organizations(self, token):
        res = requests.post(
            self.base_url + 'admin/get_organizations_list',
            headers={'session': token})
        return res.status_code, res.text, res.json()

    def create_service(self, token, org_id, name):
        res = requests.post(
            self.base_url + 'admin/create_service',
            json={'name': name, 'organizationId': org_id},
            headers={'session': token})
        return res.status_code, res.text
