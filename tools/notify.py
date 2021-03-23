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


def create_message(notify_success, notify_flaky):
    if not Path('run_results.json').exists():
        return textwrap.dedent("""
            ‼️‼️‼️ Это автоматическое сообщение из автотестов
            В последнем запуске произошли какие-то ошибки
            Скорее всего сработал какой-то ассерт, а автотестах
            так как не удалось получить файл с результатами
            Проверьте сборку на ТС
        """)
    with open('run_results.json', 'r') as inp:
        data = json.loads(inp.read())

        fails_count = 0
        flaky_fails_count = 0
        for fail in data['fails']:
            for report in data['fails']:
                if report['name'] == fail:
                    fails_count += report['is_flaky'] == False
                    flaky_fails_count += report['is_flaky'] == True

        actual_fails = fails_count
        if notify_flaky:
            actual_fails += flaky_fails_count

        if actual_fails == 0:
            if notify_success:
                return textwrap.dedent("""
                    ✅ Все сценарии отработали успешно!
                """)
            return None

        text = textwrap.dedent("""
            ‼️‼️‼️ Это автоматическое сообщение из автотестов
            В последнем запуске произошли какие-то ошибки

            Упавшие сценарии:
        """)
        for fail in data['fails']:
            text += fail + '\n'

        if notify_flaky:
            text += "Из них flaky тестов " + str(flaky_fails_count) + '\n'

        text += "\nСработавшие сценарии:\n"
        for success in data['success']:
            text += success + '\n'

        return text


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--token', help='Bot token')
    parser.add_argument('--chat_ids', help='Chat ids')
    parser.add_argument(
        '--notify_success',
        help='Should we notify about fully successfull run',
        default=False,
        type=bool)
    parser.add_argument(
        '--notify_flaky',
        help='Should we notify about flaky test fail',
        default=False)
    args = parser.parse_args()

    message = create_message(args.notify_success, args.notify_flaky)
    print(message)

    if message is not None:
        for chat_id in args.chat_ids.split(','):
            send_message(args.token, chat_id, message)

