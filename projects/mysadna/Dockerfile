FROM node:8
COPY . /usr/src/mysadna
WORKDIR /usr/src/mysadna
RUN npm install && npm run build

FROM nginx:alpine
COPY --from=0 /usr/src/mysadna/build /usr/share/nginx/html
