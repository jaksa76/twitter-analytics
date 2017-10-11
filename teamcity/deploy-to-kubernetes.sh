#!/bin/bash
set -e

version=$1

if [[ -z "kubectl get deployments 2>&1 | grep master-deployment" ]];
then
    echo Creating master deployment
    kubectl create -f ../kubernetes/cluster-master-config.yml
else
    echo Updating master deployment to version $version
    kubectl set image deployment/master-deployment master=eu.gcr.io/apt-sentinel-180609/ta-master:$version
fi

if [[ -z "kubectl get deployments 2>&1 | grep worker-deployment" ]];
then
    echo Creating worker deployment
    kubectl create -f ../kubernetes/cluster-worker-config.yml
else
    echo Updating worker deployment to version $version
    kubectl set image deployment/worker-deployment worker=eu.gcr.io/apt-sentinel-180609/ta-worker:$version
fi

if [[ -z "kubectl get deployments 2>&1 | grep web-app-deployment" ]];
then
    echo Creating worker deployment
    kubectl create -f ../kubernetes/cluster-web-app-config.yml
else
    echo Updating worker deployment to version $version
    kubectl set image deployment/web-app-deployment web-app=eu.gcr.io/apt-sentinel-180609/ta-web-app:$version
fi