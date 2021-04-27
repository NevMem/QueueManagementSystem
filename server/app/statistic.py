
import asyncio
import contextlib
import numpy as np

from datetime import timedelta
from sqlalchemy.orm import Session
from sqlalchemy.sql import func, select

from server.app import model
from server.app.utils import now
from server.app.utils.db_utils import session_scope


class StatisticWorker:

    UPDATE_PERIOD = 300
    SLEEP_INTERVAL = 60

    MAX_TICKET_COUNT = 50
    MAX_TICKET_AGE = 3600

    async def update_service_statistic(self, session: Session, service: model.Service):
        query = (
            select(model.Ticket)
            .where(
                model.Ticket.enqueue_at > now() - timedelta(seconds=self.MAX_TICKET_AGE),
                model.Ticket.state == 'PROCESSED',
                model.Ticket.resolution != 'GONE',
                model.Ticket.service_id == service.id,
            )
            .limit(self.MAX_TICKET_COUNT)
        )

        result = await session.execute(query)
        tickets = result.scalars().all()

        weights = np.linspace(0, 1, num=50)
        times = [service.default_waiting_time] * (self.MAX_TICKET_COUNT - len(tickets))

        for ticket in tickets:
            times.append((ticket.accepted_at - ticket.enqueue_at).seconds)

        service.average_waiting_time = round(np.average(times, weights=weights))
        service.last_updated_at = now()

    async def loop(self):
        while True:
            with contextlib.suppress(Exception):
                async with session_scope() as session:
                    query = (
                        select(model.Service)
                        .where(
                            model.Service.last_updated_at < now() - timedelta(seconds=self.UPDATE_PERIOD),
                        )
                        .order_by(func.random())
                        .with_for_update(nowait=True)
                        .limit(10)
                    )

                    result = await session.execute(query)
                    services = result.scalars().all()

                    for service in services:
                        await self.update_service_statistic(session, service)

            await asyncio.sleep(self.SLEEP_INTERVAL)

    def start(self):
        asyncio.get_event_loop().create_task(self.loop())
