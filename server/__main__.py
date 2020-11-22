from aiohttp import web

routes = web.RouteTableDef()


@routes.get('/ping')
async def ping(request):
    return web.Response()


def main():
    app = web.Application()
    app.add_routes(routes)
    web.run_app(app)

if __name__ == '__main__':
    main()