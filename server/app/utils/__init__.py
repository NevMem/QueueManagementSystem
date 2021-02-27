import random
import string
import sys
import traceback

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


def generate_next_ticket(previous_ticket: str) -> str:
    letter = previous_ticket[0]
    number = int(previous_ticket[1:])

    number += 1
    if number == 100:
        number = 1
        letter = string.ascii_uppercase[(string.ascii_uppercase.index(letter) + 1) % len(string.ascii_uppercase)]

    return f'{letter}{number:02d}'
