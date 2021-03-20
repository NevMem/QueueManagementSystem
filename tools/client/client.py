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
