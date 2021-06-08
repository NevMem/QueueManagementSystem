import aiosmtplib

from server.app.config import Config
from email.message import EmailMessage


class MailManager:
    client = None

    @classmethod
    async def send_confirmation_email(cls, destination: str, confirmation_id: str):
        if cls.client is None:
            if Config.MAIL_PASSWORD is None:
                return
            cls.client = aiosmtplib.SMTP(
                hostname=Config.REDIS_HOST,
                port=Config.MAIL_PORT,
                username=Config.MAIL_USERNAME,
                password=Config.MAIL_PASSWORD,
                use_tls=True
            )
            await cls.client.connect()
        message = EmailMessage()
        message['From'] = Config.MAIL_USERNAME
        message['To'] = destination
        message['Subject'] = 'Confirm your registration in Queue Management Service'
        message.set_content(
            f'https://qms-back.nikitonsky.tk/confirm_registration?confirmation_id={confirmation_id}'
        )
        await cls.client.send_message(
            message
        )
