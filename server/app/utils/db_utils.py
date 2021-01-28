import uuid
import contextlib

from sqlalchemy.dialects.postgresql import UUID as _UUID
from sqlalchemy.types import TypeDecorator, CHAR
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.util.concurrency import await_fallback

from sqlalchemy_mixins import ReprMixin, SmartQueryMixin

from server.app.config import Config

Base = declarative_base()


class BaseModel(Base, SmartQueryMixin, ReprMixin):
    """
    Abstract base class for SQLAlchemy models.
    """
    __abstract__ = True
    __repr__ = ReprMixin.__repr__
    __repr_max_length__ = float('inf')

    subclasses_map = {}

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    @property
    def pks(self):
        """
        :rtype: typing.Dict[str, Any]
        """
        return dict(zip(self.pk_names, self.pk_values))


# noinspection PyAbstractClass
class UUID(TypeDecorator):
    """Platform-independent GUID type.

    Uses PostgreSQL's UUID type, otherwise uses
    CHAR(32), storing as stringified hex values.
    http://docs.sqlalchemy.org/en/latest/core/custom_types.html#backend-agnostic-guid-type
    """
    impl = CHAR

    def load_dialect_impl(self, dialect):
        if dialect.name == 'postgresql':
            return dialect.type_descriptor(_UUID(as_uuid=True))
        else:
            return dialect.type_descriptor(CHAR(32))

    # noinspection PyUnresolvedReferences
    def process_bind_param(self, value, dialect):
        if value is None:
            return value
        elif dialect.name == 'postgresql':
            return str(value)
        else:
            if not isinstance(value, uuid.UUID):
                return "%.32x" % uuid.UUID(value).int
            else:
                # hexstring
                return "%.32x" % value.int

    def process_result_value(self, value, dialect):
        if value is None:
            return value
        else:
            if not isinstance(value, uuid.UUID):
                return uuid.UUID(value)
            else:
                return value


class NotFound(Exception):
    pass


class Locked(Exception):
    pass


class Timeout(Exception):
    pass


engine = create_async_engine(Config.DB_URL, echo=True)


@contextlib.asynccontextmanager
async def session_scope(autocommit=True):
    async with AsyncSession(engine) as session:
        try:
            yield session

            if autocommit:
                await session.commit()

        except:
            await session.rollback()
            raise


def generate_session():
    return AsyncSession(engine)


async def prepare_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)


async def drop_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)


def drop_db_sync():
    conn = engine.begin()
    await_fallback(conn.run_sync(Base.metadata.drop_all))
