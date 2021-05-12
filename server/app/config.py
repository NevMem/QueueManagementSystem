import os


class Config:

    SECRET_KEY = os.environ.get('SECRET_KEY', 'lol, so secret')
    DB_URL = os.environ['DB_URL']
    DEBUG = bool(os.environ.get('DEBUG', False))
    SQS_URL = os.environ.get('SQS_URL')
