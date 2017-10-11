#!/bin/bash
set -e

version=$1

if [[ -z `kubectl get deployments 2>&1 | grep worker-deployment` ]];
then
    echo Creating worker deployment
    sed -i "s/%VERSION%/$version/" ./worker-deployment.yaml
    kubectl create -f ./worker-deployment.yaml
else
    echo Updating worker deployment to version $version
    kubectl set image deployment/worker-deployment worker=eu.gcr.io/genuine-axe-182507/worker:$version
fi