FROM ubuntu:20.04

COPY install /
COPY tmp /

RUN apt update && \
 DEBIAN_FRONTEND=noninteractive \
 apt-get install -y --allow-downgrades --allow-remove-essential --allow-change-held-packages \
 python3.9-minimal \
 python3-pip \
 vim \
 telnet \
 lsof \
 syslog-ng \
 curl \
 less \
 openjdk-8-jdk
 nginx


RUN python3 -m pip install supervisor

CMD [ "/bin/bash", "/etc/runner/run.sh" ]

EXPOSE 80 443 8080
