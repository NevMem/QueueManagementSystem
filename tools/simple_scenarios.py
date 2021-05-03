import random
import requests
import string
from tools.scenario import scenario
from tools.sc_assert import sc_assert


def random_string(length=10):
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=length))


def create_user_data():
    return [random_string() for _ in range(4)]


def check_login_successfull(login_resp) -> str:
    code, body, token = login_resp
    sc_assert(code == 200, body, code)
    return token


def check_registration_successfull(reg_resp):
    code, body = reg_resp
    sc_assert(code == 200, body, code)


def check_default(resp):
    code, body = resp
    sc_assert(code == 200, body, code)
    return body


@scenario('Register')
def test_just_login(client):
    login, password, name, surname = create_user_data()

    code, body = client.register(login, password, name, surname)
    sc_assert(code == 200, body, code)


@scenario('Reg -> Login')
def test_reg_log(client):
    login, password, name, surname = create_user_data()

    code, body = client.register(login, password, name, surname)
    sc_assert(code == 200, body, code)

    code, body, token = client.login(login, password)
    sc_assert(code == 200, body, code)

@scenario('Reg -> Login -> GetUser')
def test_reg_log_get_user(client):
    login, password, name, surname = create_user_data()

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


@scenario('Reg -> Login -> Create org')
def test_reg_log_create_org(client):
    login, password, name, surname = create_user_data()
    check_registration_successfull(client.register(login, password, name, surname))
    token = check_login_successfull(client.login(login, password))
    code, body = client.create_organization(token, random_string())
    sc_assert(code == 200, code, body)


@scenario('Reg -> Login -> Create org -> Get orgs')
def test_reg_log_create_get(client):
    login, password, name, surname = create_user_data()
    check_registration_successfull(client.register(login, password, name, surname))
    token = check_login_successfull(client.login(login, password))
    org_name = random_string()
    check_default(client.create_organization(token, org_name))

    code, body, data = client.load_organizations(token)
    sc_assert(code == 200, body, code)
    sc_assert(len(data['organizations']) == 1, body, code)
    sc_assert(data['organizations'][0]['info']['name'] == org_name, body, code)

@scenario('Reg -> Login -> Create org -> Add service -> Get orgs')
def test_reg_log_create_org_add_service_get(client):
    login, password, name, surname = create_user_data()
    check_registration_successfull(client.register(login, password, name, surname))
    token = check_login_successfull(client.login(login, password))
    org_name = random_string()
    check_default(client.create_organization(token, org_name))

    code, body, data = client.load_organizations(token)
    sc_assert(code == 200, body, code)
    sc_assert(len(data['organizations']) == 1, body, code)
    sc_assert(data['organizations'][0]['info']['name'] == org_name, body, code)

    service_name = random_string()
    check_default(client.create_service(token, data['organizations'][0]['info']['id'], service_name))

    code, body, data = client.load_organizations(token)
    sc_assert(code == 200, body, code)
    sc_assert(len(data['organizations']) == 1, body, code)
    sc_assert(data['organizations'][0]['info']['name'] == org_name, body, code)
    sc_assert(len(data['organizations'][0]['services']) == 1, body, code)
    sc_assert(data['organizations'][0]['services'][0]['info']['name'] == service_name, body, code)


@scenario('Leave queue')
def test_full_pipeline(client):
    admin_login, admin_password, admin_name, admin_surname = create_user_data()
    check_registration_successfull(client.register(admin_login, admin_password, admin_name, admin_surname))
    admin_token = check_login_successfull(client.login(admin_login, admin_password))
    org_name = random_string()
    check_default(client.create_organization(admin_token, org_name))

    code, body, data = client.load_organizations(admin_token)
    sc_assert(code == 200, body, code)
    sc_assert(len(data['organizations']) == 1, body, code)
    sc_assert(data['organizations'][0]['info']['name'] == org_name, body, code)

    service_name = random_string()
    service = check_default(client.create_service(admin_token, data['organizations'][0]['info']['id'],
                                                        service_name))

    user_login, user_password, user_name, user_surname = create_user_data()
    check_registration_successfull(client.register(user_login, user_password, user_name, user_surname))
    user_token = check_login_successfull(client.login(user_login, user_password))

    ticket = check_default(client.enter_queue(user_token, service['id']))
    check_default(client.left_queue(user_token))

