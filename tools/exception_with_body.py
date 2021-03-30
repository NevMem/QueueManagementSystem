class ExceptionWithBody(Exception):
    def __init__(self, response_body, response_code):
        self.response_body = response_body
        self.response_code = response_code
