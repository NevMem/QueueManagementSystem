import uuid
import contextlib
import enum
import functools
import logging
import os
import six
import threading
import traceback

from sqlalchemy.dialects.postgresql import UUID as _UUID
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.types import TypeDecorator, CHAR
from sqlalchemy_mixins import ReprMixin, SmartQueryMixin

from sqlalchemy.engine import create_engine
from sqlalchemy.exc import OperationalError
from sqlalchemy.orm import defer as _defer, noload, scoped_session, sessionmaker
from sqlalchemy.orm.attributes import InstrumentedAttribute
from sqlalchemy.orm.exc import NoResultFound
from sqlalchemy.sql import ClauseElement
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy_mixins import ReprMixin, SmartQueryMixin

Base = declarative_base()


class BaseModel(Base, SmartQueryMixin, ReprMixin):
    """
    Abstract base class for SQLAlchemy models.
    """
    __abstract__ = True
    __repr__ = ReprMixin.__repr__
    __repr_max_length__ = float('inf')

    subclasses_map = {}

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


@contextlib.contextmanager
def session_scope(
    timeout=None,
    statement_timeout=None,
    lock_timeout=None,
    interruption_log_level=logging.WARNING,
    isolation_level=None,
    nested=False,
    application_name=None,
):
    """
    Provides a transactional scope around a series of operations.

    If :param:`timeout` is set, cancellation request will be send after :param:`timeout` seconds.

    :param timeout: application level timeout in seconds
    :param statement_timeout: statement timeout for session in seconds (https://www.postgresql.org/docs/9.4/static/runtime-config-client.html)
    :param lock_timeout: lock timeout for session in seconds (https://www.postgresql.org/docs/9.4/static/runtime-config-client.html)
    :param interruption_log_level: logging level to use to log exceptions raised during session
    :param isolation_level: transaction isolation level (database default used if None)
    :param nested: if True, new session is created; otherwise the same session is used
    :param application_name: used for `SET application_name` if not None
    """
    prepare_module()

    timer = None

    if nested:
        Session.remove()

    session = BaseModel.session()

    try:
        if statement_timeout is not None and lock_timeout is not None:
            if lock_timeout > statement_timeout:
                LOGGER.warning('lock_timeout greater than statement_timeout is pointless')

        if statement_timeout is not None:
            session.execute('SET SESSION statement_timeout = {};'.format(int(statement_timeout * 1000)))

        if lock_timeout is not None:
            session.execute('SET SESSION lock_timeout = {};'.format(int(lock_timeout * 1000)))

        if isolation_level is not None:
            session.connection(execution_options=dict(isolation_level=isolation_level.value))

        if timeout is not None:
            timer = threading.Timer(timeout, lambda: _cancel_request(session))
            timer.start()

        if application_name is None:
            caller = traceback.extract_stack()[-3]
            application_name = '{}:{}'.format(caller[0], caller[1])

        session.execute("SET application_name = '{}';".format(six.moves.urllib_parse.quote(application_name)))

        yield session
        session.commit()

    except Exception as e:
        formatted_exception = str(e)

        session.rollback()

        if 'deadlock' in formatted_exception:
            log_pg_stat_activity()

        if isinstance(e, OperationalError) and e.args == TIMEOUT_EXCEPTION_ARGS:
            raise Timeout()

        raise

    finally:
        if timer:
            timer.cancel()

        session.close()

