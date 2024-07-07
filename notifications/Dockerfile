FROM ubuntu:22.04
LABEL authors="chri"
WORKDIR /app

COPY . /app/
RUN apt update
RUN apt install -y curl
RUN curl -fsSL https://deb.nodesource.com/setup_21.x -o /tmp/nodesource_setup.sh
RUN bash /tmp/nodesource_setup.sh
RUN apt-get install -y nodejs
EXPOSE 4577

RUN npm i
RUN npx tsc
ENTRYPOINT ["node", "dist"]