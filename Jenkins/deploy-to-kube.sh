#!/bin/bash
set -e

version=$1

if [[ -z `/usr/share/google-cloud-sdk/bin/kubectl get deployments 2>&1 | grep worker-deployment` ]];
then
    echo Creating worker deployment
    sed -i "s/%VERSION%/$version/" ./worker-deployment.yaml
    /usr/share/google-cloud-sdk/bin/kubectl create -f ./worker-deployment.yaml
else
    echo Updating worker deployment to version $version
    /usr/share/google-cloud-sdk/bin/kubectl set image deployment/worker-deployment worker=eu.gcr.io/genuine-axe-182507/worker:$version
fi


if [[ -z `/usr/share/google-cloud-sdk/bin/kubectl get deployments 2>&1 | grep master-deployment` ]];
then
    echo Creating master deployment
    sed -i "s/%VERSION%/$version/" ./master-deployment.yaml
    /usr/share/google-cloud-sdk/bin/kubectl create -f ./master-deployment.yaml
else
    echo Updating master deployment to version $version
    /usr/share/google-cloud-sdk/bin/kubectl set image deployment/master-deployment master-service=eu.gcr.io/genuine-axe-182507/master:$version
fi