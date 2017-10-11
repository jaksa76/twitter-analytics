#!/bin/bash
set -e

version=$1

if [[ -z "kubectl get deployments 2>&1 | grep master" ]] then
    kubectl create -f ../kubernetes/cluster-master-config.yml
    kubectl create -f ../kubernetes/cluster-worker-config.yml
    kubectl create -f ../kuberenetes/cluster-web-app-config.yml
else
    kubectl set image deployment/master-deployment master=eu.gcr.io/apt-sentinel-180609/ta-master:$version
    kubectl set image deployment/master-deployment master=eu.gcr.io/apt-sentinel-180609/ta-worker:$version
    kubectl set image deployment/master-deployment master=eu.gcr.io/apt-sentinel-180609/ta-web-app:$version
fi