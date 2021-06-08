import os


class Config:

    SECRET_KEY = os.environ.get('SECRET_KEY', 'lol, so secret')
    DB_URL = os.environ['DB_URL']
    DEBUG = bool(os.environ.get('DEBUG', False))
    SQS_URL = os.environ.get('SQS_URL')

    IN_MEMORY_CACHE = os.environ.get('IN_MEMORY_CACHE', False)

    REDIS_HOST = os.environ.get('REDIS_HOST', 'localhost')
    REDIS_PORT = os.environ.get('REDIS_PORT', 26379)
    REDIS_USER = os.environ.get('REDIS_USER', 'lol')
    REDIS_PASSWORD = os.environ.get('REDIS_PASSWORD', 'kek')

    MAIL_PASSWORD = os.environ.get('MAIL_PASSWORD', None)
    MAIL_USERNAME = os.environ.get('MAIL_USERNAME', 'no-reply@nikitonsky.tk')
    MAIL_HOST = os.environ.get('MAIL_HOST', 'smtp.yandex.ru')
    MAIL_PORT = int(os.environ.get('MAIL_PORT', 465))

