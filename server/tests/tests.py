def test_ping(server):
    server.ping()


def test_check_unique_user(server):
    assert server.check_unique_user() == {'is_unique': True}
