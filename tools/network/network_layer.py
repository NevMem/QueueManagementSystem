import requests


class NetworkLayer:
    def __init__(self, name):
        self.name = name

    def post(self, *args, **kwargs):
        self.log('Making post request')
        self.log(args, tabs=1)
        self.log(kwargs, tabs=1)
        response = requests.post(*args, **kwargs)
        self.log('Got response')
        self.log(response.text, tabs=1)
        self.log('\n')
        return response

    def log(self, smth, tabs=0):
        to_log = str(smth)
        for _ in range(tabs):
            to_log = '\t' + to_log
        with open('requests' + self.name + '.txt', 'a') as out:
            out.write(to_log + '\n')
        print(to_log)

