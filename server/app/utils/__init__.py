import random
import string
import sys
import traceback
import typing as tp

from Crypto.Hash import SHA256


uuid_letters = string.ascii_letters + string.digits


def get_traceback_string(exception):
    if hasattr(exception, '__traceback__'):
        tb_strings = traceback.format_tb(exception.__traceback__)
    else:
        tb_strings = traceback.format_exception(*sys.exc_info())
    return ''.join(tb_strings)


def format_exception(e, with_traceback=False):
    if hasattr(e, '__module__'):
        exc_string = u'{}.{}: {}'.format(e.__module__, e.__class__.__name__, e)
    else:
        exc_string = u'{}: {}'.format(e.__class__.__name__, e)

    if with_traceback:
        traceback_string = ':\n' + get_traceback_string(exception=e)
    else:
        traceback_string = ''

    return u'{}{}'.format(exc_string, traceback_string)


def sha_hash(s: str) -> str:
    return SHA256.new(s.encode()).hexdigest()


def fixed_uuid(length=6) -> str:
    return ''.join(random.choice(uuid_letters) for i in range(length))


def number_to_base(n: int, b: int) -> tp.List[int]:
    if n == 0:
        return [0]

    digits = []
    while n:
        digits.append(int(n % b))
        n //= b

    return digits[::-1]


def generate_next_ticket(previous_ticket: str, service_index: int) -> str:
    number = (int(previous_ticket[-3:]) + 1) % 1000

    letter_digits = number_to_base(service_index, 26)

    return f'{"".join(string.ascii_uppercase[d] for d in letter_digits)}{number:03d}'


def isiterable(obj):
    try:
        iter(obj)
        return True
    except TypeError:
        return False
