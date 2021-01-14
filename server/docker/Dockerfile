FROM ubuntu:20.04

COPY install /
COPY

RUN apt update && \
 apt-get install -y --allow-downgrades --allow-remove-essential --allow-change-held-packages \
 python3 \
 python3-pip

RUN pip install supervisor

EXPOSE 80

CMD /etc/runner/run.sh
