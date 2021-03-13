def test_ping(server):
    server.ping()


def test_check_unique_user(server):
    response = server.check_unique_user(email='pufit@not.net')
    assert response.status_code == 200
    assert response.json() == {'isUnique': True}

    response = server.register_user(email='pufit@not.net', password='lolkek')
    assert response.status_code == 200

    response = server.check_unique_user(email='pufit@not.net')
    assert response.status_code == 200
    assert not response.json().get('isUnique', False)  # proto defaults


def test_create_organization(server):
    server.register_user(email='mail@mail', password='password')
    token = server.login(email='mail@mail', password='password').headers['session']
    server.create_organization(token, name='Organization')
    resp = server.get_organizations(token)
    assert len(resp.organizations) == 1
    org = resp.organizations[0]
    assert len(org.services) == 0
    assert org.info.name == 'Organization'
