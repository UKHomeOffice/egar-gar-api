#!/bin/sh
kubectl delete -f /home/centos/egar-gar-api/scripts/kube/gar-api-deployment.yaml
kubectl delete -f /home/centos/egar-gar-api/scripts/kube/gar-api-service.yaml
