import aiosmtplib
import logging

from server.app.config import Config
from email.message import EmailMessage

logger = logging.getLogger()


class MailManager:

    @staticmethod
    async def send_confirmation_email(destination: str, confirmation_id: str):
        if Config.MAIL_PASSWORD is None:
            logger.warn(f'Skipping confirmation send for email {destination}')
            return
        try:
            message = EmailMessage()
            message['From'] = Config.MAIL_USERNAME
            message['To'] = destination
            message['Subject'] = 'Confirm your registration in Queue Management Service'
            message.set_content(f'https://qms-back.nikitonsky.tk/confirm_registration?confirmation_id={confirmation_id}')
            client = aiosmtplib.SMTP(
                hostname=Config.MAIL_HOST,
                port=Config.MAIL_PORT,
                username=Config.MAIL_USERNAME,
                password=Config.MAIL_PASSWORD,
                use_tls=True
            )
            async with client:
                await client.send_message(message)
            logger.info(f'Sent confirmation to {destination}')
        except Exception as e:
            logger.exception(f'Failed to send confirmation to {destination}')
