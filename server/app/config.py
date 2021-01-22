import os


class Config:

    @property
    def SECRET_KEY(self) -> str:
        return os.environ.get('SECRET_KEY', 'lol, so secret')

    @property
    def DB_URL(self) -> str:
        return os.environ['DB_URL']
