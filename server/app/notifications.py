
import aioboto3
import asyncio
import contextlib
import typing as tp
import ujson

from sqlalchemy.orm import Session, selectinload, contains_eager
from sqlalchemy.sql import select, and_

from server.app import model
from server.app.config import Config
from server.app.utils.db_utils import session_scope


class NotificationsWorker:

    NOTIFY_THRESHOLD = 600
    SLEEP_INTERVAL = 60

    async def get_tickets(self, session: Session) -> tp.AsyncIterable[tp.Tuple[int, model.Ticket]]:
        query = (
            select(model.Service)
            .join(model.Service.tickets)
            .where(
                model.Ticket.state != 'PROCESSED',
                model.Service.tickets.any(and_(
                    model.Ticket.pushed.is_(False),
                    model.Ticket.state == 'WAITING',
                )),
            )
            .options(
                contains_eager(model.Service.tickets),
                selectinload(model.Service.tickets, model.Ticket.user)
            )
            .with_for_update(nowait=True)
        )

        result = await session.execute(query)
        services = result.scalars().unique().all()

        for service in services:
            if not service.average_waiting_time:
                index = len(service.tickets)
            else:
                index = self.NOTIFY_THRESHOLD // service.average_waiting_time

            for i in range(0, min(index, len(service.tickets))):
                ticket = service.tickets[i]

                if not ticket.pushed:
                    yield i, ticket

    async def loop(self):
        with contextlib.suppress(Exception):
            async with aioboto3.resource('sqs', region_name='ru-central1', endpoint_url='https://message-queue.api.cloud.yandex.net/') as resource:
                queue = await resource.Queue(url=Config.SQS_URL)

                while True:
                    with contextlib.suppress(Exception):
                        async with session_scope() as session:
                            async for position, ticket in self.get_tickets(session):
                                with contextlib.suppress(Exception):
                                    await queue.send_message(MessageBody=ujson.dumps(
                                        {
                                            'target': ticket.user.email,
                                            'time_left': ticket.service.average_waiting_time * position,
                                            'position': position,
                                            'from': self.__class__.__name__
                                        }
                                    ))

                                    ticket.pushed = True

                    await asyncio.sleep(self.SLEEP_INTERVAL)

    def start(self):
        asyncio.get_event_loop().create_task(self.loop())
