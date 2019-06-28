FROM ubuntu:18.04

ADD etc/ssh/ssh_known_hosts /etc/ssh/ssh_known_hosts
ADD etc/apt/apt.conf.d/02-proxy /etc/apt/apt.conf.available/02-proxy
RUN nc -z -w 2 apt-proxy.eng.bigswitch.com 3142 >/dev/null 2>&1 && ln -s ../apt.conf.available/02-proxy /etc/apt/apt.conf.d || true

RUN apt-get update \
  && apt-get upgrade -y --no-install-recommends \
        build-essential \
        git \
        gnupg \
        maven \
        openjdk-8-jdk \
        openssh-client \
        python \
        python-nose \
        python-pip \
        python-setuptools \
        rsync

RUN pip install PyGithub==1.35

RUN useradd -m -d /loxi -c 'loxi build user' -u 1000 -s /bin/bash loxi
COPY run-as-loxi /
ENTRYPOINT [ "/run-as-loxi" ]
