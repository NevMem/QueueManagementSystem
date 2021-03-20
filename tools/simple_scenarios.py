import random
import requests
import string
from tools.client.client import Client
from tools.scenario import scenario
from tools.sc_assert import sc_assert


def random_string(length=10):
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=length))


@scenario('Register')
def just_login():
    client = Client()
    login = random_string()
    password = random_string()
    name = random_string()
    surname = random_string()

    code, body = client.register(login, password, name, surname)
    sc_assert(code == 200, body, code)


@scenario('Reg -> Login')
def just_login():
    client = Client()
    login = random_string()
    password = random_string()
    name = random_string()
    surname = random_string()

    code, body = client.register(login, password, name, surname)
    sc_assert(code == 200, body, code)

    code, body, token = client.login(login, password)
    sc_assert(code == 200, body, code)

@scenario('Reg -> Login -> GetUser')
def just_login():
    client = Client()
    login = random_string()
    password = random_string()
    name = random_string()
    surname = random_string()

    code, body = client.register(login, password, name, surname)
    sc_assert(code == 200, body, code)

    code, body, token = client.login(login, password)
    sc_assert(code == 200, body, code)

    code, body, user_data = client.get_user(token)
    sc_assert(code == 200, body, code)
    sc_assert(
        user_data['name'] == name and user_data['surname'] == surname and user_data['email'] == login,
        body,
        code)
