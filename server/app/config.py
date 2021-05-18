import os


class Config:

    SECRET_KEY = os.environ.get('SECRET_KEY', 'lol, so secret')
    DB_URL = os.environ['DB_URL']
    DEBUG = bool(os.environ.get('DEBUG', False))
    SQS_URL = os.environ.get('SQS_URL')

    REDIS_HOST = os.environ.get('REDIS_HOST', 'localhost')
    REDIS_PORT = os.environ.get('REDIS_PORT', 26379)
    REDIS_USER = os.environ.get('REDIS_USER', 'lol')
    REDIS_PASSWORD = os.environ.get('REDIS_PASSWORD', 'kek')
