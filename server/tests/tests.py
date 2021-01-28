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
