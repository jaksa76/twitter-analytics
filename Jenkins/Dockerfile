FROM jenkins/jenkins:alpine

USER root

RUN apk add --update \
        sudo \
        docker \
        py-pip \
        maven \
    && pip install docker-compose \
    && rm -rf /var/cache/apk/*

RUN curl -o /usr/share/google-cloud-sdk.tar.gz https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-174.0.0-darwin-x86_64.tar.gz
WORKDIR /usr/share
RUN tar xvf google-cloud-sdk.tar.gz
RUN CLOUDSDK_CORE_DISABLE_PROMPTS=1 ./google-cloud-sdk/install.sh
COPY service-account.json /usr/share/service-account.json
RUN ./google-cloud-sdk/bin/gcloud auth activate-service-account --key-file service-account.json

# RUN /usr/local/bin/install-plugins.sh workflow-aggregator blueocean