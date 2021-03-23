from tools.client.client import Client

scenarios = []

def scenario(name):
    def wrapper(func):
        def inner_wrapper():
            client = Client()
            func(client)
        scenarios.append({'scenario_name': name, 'scenario': inner_wrapper})
        return inner_wrapper
    return wrapper
