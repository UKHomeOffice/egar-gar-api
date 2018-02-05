#!/bin/sh
echo Starting GAR-API version: $GAR_API_VER
rm -rf /home/centos/egar-gar-api/scripts/kube/gar-api-deployment.yaml; envsubst < "/home/centos/egar-gar-api/scripts/kube/gar-api-deployment-template.yaml" > "/home/centos/egar-gar-api/scripts/kube/gar-api-deployment.yaml"
kubectl create -f /home/centos/egar-gar-api/scripts/kube/gar-api-deployment.yaml
kubectl create -f /home/centos/egar-gar-api/scripts/kube/gar-api-service.yaml
