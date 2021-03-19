from tools.exception_with_body import ExceptionWithBody


def sc_assert(check, body, code):
    if not check:
        raise ExceptionWithBody(body, code)
