FROM ubuntu:14.04

RUN apt-get update \
  && apt-get upgrade -y --no-install-recommends build-essential python python-nose rsync openjdk-7-jdk maven

RUN useradd -m -d /loxi -c 'loxi build user' -u 300 -s /bin/bash loxi
COPY run-as-loxi /
ENTRYPOINT [ "/run-as-loxi" ]
