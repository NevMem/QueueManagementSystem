
import asyncio
import collections
import typing as tp


class EventManager:
    def __init__(self):
        self.futures: tp.DefaultDict[str, tp.List[asyncio.Future]] = collections.defaultdict(list)

    @staticmethod
    def _get_key(event: str, tags: tp.Dict[str, str]):
        return f'{event}:{",".join(map(lambda x: f"{x[0]}={x[1]}", tags.items()))}'

    async def listen(self, event: str, **tags: str):
        future = asyncio.get_event_loop().create_future()

        self.futures[self._get_key(event, tags)].append(future)
        return await future

    def publish(self, event: str, result, **tags: str):
        futures = self.futures.pop(self._get_key(event, tags), [])

        for future in futures:
            future.set_result(result)
