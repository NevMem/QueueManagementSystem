scenarios = []

def scenario(name):
    def wrapper(func):
        scenarios.append({'scenario_name': name, 'scenario': func})
        def inner_wrapper():
            func()
        return inner_wrapper
    return wrapper
