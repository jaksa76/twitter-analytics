FROM jetbrains/teamcity-agent:latest

# Add GCloud repository
RUN export CLOUD_SDK_REPO="cloud-sdk-$(lsb_release -c -s)" \
    && echo "deb http://packages.cloud.google.com/apt $CLOUD_SDK_REPO main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list
RUN curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -

# Install packages
RUN apt-get update \
    && apt-get install -y docker python-pip google-cloud-sdk \
    && pip install docker-compose

COPY service-account.json /usr/share/service-account.json

RUN gcloud auth activate-service-account --key-file /usr/share/service-account.json