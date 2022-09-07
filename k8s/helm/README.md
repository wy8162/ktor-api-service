1. ```kubectl create namespace ktor```
2. Create PVC: ```kubectl -n ktor apply -f PVC.yaml```
2. Deploy
```shell
$ helm dependency update ktor-api-service

$ helm install ktor-api-service ktor-api-service -n ktor
```