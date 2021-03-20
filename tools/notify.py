import argparse
import json
import os
import requests
import textwrap
from pathlib import Path


def send_message(bot_token, chat_id, message):
    url = 'https://api.telegram.org/bot' + bot_token + '/sendMessage?chat_id=' + chat_id + '&text=' + message
    response = requests.get(url)
    assert(response.status_code == 200)


def create_message():
    if not Path('run_results.json').exists():
        return textwrap.dedent("""
            Это автоматическое сообщение из автотестов
            В последнем запуске произошли какие-то ошибки
            Скорее всего сработал какой-то ассерт, а автотестах
            так как не удалось получить файл с результатами
            Проверьте сборку на ТС
        """)
    with open('run_results.json', 'r') as inp:
        data = json.loads(inp.read())
        if len(data['fails']) == 0:
            return None

        text = textwrap.dedent("""
            Это автоматическое сообщение из автотестов
            В последнем запуске произошли какие-то ошибки

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

