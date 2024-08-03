FROM ubuntu:22.04
LABEL authors="chri"

RUN apt update && apt install -y curl
RUN curl -fsSL https://deb.nodesource.com/setup_21.x -o /tmp/nodesource_setup.sh
RUN bash /tmp/nodesource_setup.sh
RUN apt-get install -y nodejs