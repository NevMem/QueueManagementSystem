import requests
import sys


class NetworkLayer:
    def post(self, *args, **kwargs):
        print('Making post request', file=sys.stderr)
        print(args, file=sys.stderr)
        print(kwargs, file=sys.stderr)
        response = requests.post(*args, **kwargs)
        print('Got response', file=sys.stderr)
        print(response.text)
        return response

