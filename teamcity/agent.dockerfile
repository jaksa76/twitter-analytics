FROM jetbrains/teamcity-agent:latest
RUN apt-get update \
    && apt-get install -y docker python-pip\
    && pip install docker-compose