import requests


class NetworkLayer:
    def __init__(self, name):
        self.name = name

    def post(self, *args, **kwargs):
        self.log('Making post request')
        self.log(args)
        self.log(kwargs)
        response = requests.post(*args, **kwargs)
        self.log('Got response')
        self.log(response.text)
        return response

    def log(self, smth):
        with open('requests' + self.name + '.txt', 'a') as out:
            out.write(str(smth) + '\n')
        print(smth)

