from tools.client.client import Client

scenarios = []

def scenario(name):
    def wrapper(func):
        def actual_runner_http():
            client = Client('[HTTP] ' + name)
            func(client)

        def actual_runner_https():
            client = Client('[HTTPS] ' + name, use_https=True)
            func(client)
        scenarios.append({
            'scenario_name': '[HTTP] ' + name,
            'scenario': actual_runner_http,
            'flaky': False})
        scenarios.append({
            'scenario_name': '[HTTPS] ' + name,
            'scenario': actual_runner_https,
            'flaky': True})
    return wrapper
