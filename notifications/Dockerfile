FROM node-ubuntu-local
WORKDIR /app

COPY . /app/
EXPOSE 4577

RUN npm i
RUN npx tsc
ENTRYPOINT ["node", "dist"]