FROM jenkins/jenkins:alpine

USER root

RUN apk add --update \
        sudo \
        docker \
        py-pip \
        maven \
    && pip install docker-compose \
    && rm -rf /var/cache/apk/*

RUN /usr/local/bin/install-plugins.sh workflow-aggregator blueocean