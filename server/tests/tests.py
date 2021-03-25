def test_check_unique_user(server):
    response = server.check_unique_user(email='pufit@not.net')
    assert response.status_code == 200
    assert response.json() == {'isUnique': True}

    response = server.register_user(email='pufit@not.net', password='lolkek')
    assert response.status_code == 200

    response = server.check_unique_user(email='pufit@not.net')
    assert response.status_code == 200
    assert not response.json().get('isUnique', False)  # proto defaults


def test_configure_organization(server):
    server.register_user(email='mail@mail', password='password')
    token = server.login(email='mail@mail', password='password').headers['session']
    server.update_user(token, email='mail@mail', name='Myname', data={'key': 'value'})
    resp = server.get_user(token)
    assert resp.name == 'Myname'
    assert resp.data == {'key': 'value'}
    # create organization
    server.create_organization(token, name='Organization')
    resp = server.get_organizations(token)
    assert len(resp.organizations) == 1
    org = resp.organizations[0]
    assert len(org.services) == 0
    assert org.info.name == 'Organization'

    server.update_organization(token, id=org.info.id, name=org.info.name, data={'key': 'value'})
    resp = server.get_organizations(token)
    assert len(resp.organizations) == 1
    org = resp.organizations[0]
    assert len(org.services) == 0
    assert org.info.name == 'Organization'
    assert org.info.data == {'key': 'value'}

    # create service
    server.create_service(token, 'Service', organization_id=org.info.id)
    resp = server.get_organizations(token)
    assert len(resp.organizations) == 1
    org = resp.organizations[0]
    assert len(org.services) == 1
    service = org.services[0]
    assert service.info.name == 'Service'
    server.update_service(token, id=service.info.id, name=service.info.name, data={'key': 'value'})
    resp = server.get_organizations(token)
    assert len(resp.organizations) == 1
    org = resp.organizations[0]
    assert len(org.services) == 1
    service = org.services[0]
    assert service.info.name == 'Service'
    assert service.info.data == {'key': 'value'}

    assert server.get_qr(organization=org.info.id).content != b''
    assert server.get_qr(organization=org.info.id, service=service.info.id).content != b''

    server.remove_service(token, service.info.id)
    resp = server.get_organizations(token)
    assert len(resp.organizations) == 1
    org = resp.organizations[0]
    assert len(org.services) == 0
