import requests
import sys


class NetworkLayer:
    def post(self, *args, **kwargs):
        print('Making post request')
        print(args)
        print(kwargs)
        response = requests.post(*args, **kwargs)
        print('Got response')
        print(response.text)
        return response

