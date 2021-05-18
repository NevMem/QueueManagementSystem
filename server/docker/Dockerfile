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
 nginx \
 syslog-ng \
 curl \
 less


RUN python3 -m pip install -r /usr/lib/qms/server/app/requirenments.txt
RUN python3 -m pip install supervisor

EXPOSE 80 443 9824

CMD [ "/bin/bash", "/etc/runner/run.sh" ]
