import argparse
import json
import requests
import textwrap


def send_message(bot_token, chat_id, message):
    url = 'https://api.telegram.org/bot' + bot_token + '/sendMessage?chat_id=' + chat_id + '&text=' + message
    response = requests.get(url)
    assert(response.status_code == 200)


def create_message():
    with open('run_results.json', 'r') as inp:
        data = json.loads(inp.read())
        if len(data['fails']) == 0:
            return None

        text = textwrap.dedent("""
            Это автоматическое сообщение из автотестов
            В последнем заупске произошли какие-то ошибки

            Упавшие сценарии:
        """)
        for fail in data['fails']:
            text += fail + '\n'

        return text


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--token', help='Bot token')
    parser.add_argument('--chat_ids', help='Chat ids')
    args = parser.parse_args()

    message = create_message()
    print(message)

    if message is not None:
        for chat_id in args.chat_ids.split(','):
            pass #send_message(args.token, chat_id, message)

