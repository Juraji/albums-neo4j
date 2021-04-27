FROM node:14-alpine AS builder
WORKDIR /build

COPY package.json yarn.lock .npmrc ./

RUN yarn install

COPY .browserslistrc \
     .eslintrc.json \
     angular.json \
     karma.conf.js \
     tsconfig.app.json \
     tsconfig.json \
     tsconfig.spec.json \
     ./

COPY src/web ./src/web
RUN yarn run build

FROM nginx:stable-alpine AS server
RUN rm -rf /usr/share/nginx/html/*

COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

WORKDIR /usr/share/nginx/html
COPY --from=builder /build/dist/albums .

CMD ["nginx", "-g", "daemon off;"]
