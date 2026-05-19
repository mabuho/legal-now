# Dockerfile
FROM node:18-alpine

# Instalar dependencias del sistema
RUN apk add --no-cache bash

WORKDIR /redis-api

COPY package*.json ./
RUN npm install
RUN npm install --save-dev typescript@latest ts-node@latest @types/node @types/express @types/cors
COPY . ./

EXPOSE 3030
CMD ["npm", "run", "dev"]
