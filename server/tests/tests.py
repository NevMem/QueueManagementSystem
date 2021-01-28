def test_ping(server):
    server.ping()


def test_check_unique_user(server):
    response = server.check_unique_user()
    assert response.status_code == 200
    assert response.json() == {'isUnique': True}
