from tools.scenario import scenario
from tools.sc_assert import sc_assert

@scenario('Login')
def just_login():
    print('Some logic here')
    sc_assert(False, 'Some body', 500)

@scenario('Register')
def just_login():
    print('Some logic here')
    sc_assert(False, 'Some body', 500)
