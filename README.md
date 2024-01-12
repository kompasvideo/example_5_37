Паттерн - Idempotent reciever


Архитектура решения

![image-20200512154011363](./README.assets/image-20200512154011363.png)



В начале убедиться, что nginx ingress запущен

```
minikube addons  enable ingress
```

Создаем и делаем дефолтным неймспейс auth
```
kubectl create ns auth
kubectl config set-context --current --namespace=auth
```

Собираем и запускаем с помощью helm сервис аутентификации
```
cd auth-service
helm install auth chart/ --values chart/auth-values.yaml
```

И приложение, в котором мы будем проверять аутентификацию 
```
cd app
helm install app chart/ --values chart/app-values.yaml
```

Применяем манифст для сервиса аутентификации
```
kubectl apply -f auth-ingress.yaml
```

В файле app-ingress.yaml выставлены настройки аутентификации через аннотации.

Auth-url - это урл, который осуществляет проверку на аутентификацию 
Стоит обратить внимание, что урл имеет полное доменное имя внутри кластера (вместе с указанием неймспейса - auth), потому что nginx запущен в другом неймспейсе. 

Также есть указание какие заголовки будут прокидываться в сервис app из сервиса auth.

```yaml
-- cat app-ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app
  annotations:
    nginx.ingress.kubernetes.io/auth-url: "http://auth.auth.svc.cluster.local:9000/auth"
    nginx.ingress.kubernetes.io/auth-signin: "http://$host/signin"
    nginx.ingress.kubernetes.io/auth-response-headers: "X-User,X-Email,X-UserId,X-First-Name,X-Last-Name"
spec:
  rules:
    - host: arch.homework
      http:
        paths:
          - path: /users/
            pathType: Prefix
            backend:
              service:
                name: app
                port:
                  number: 9000
          - path: /order
            pathType: Prefix
            backend:
              service:
                name: app
                port:
                  number: 9000
```

Применяем ингресс для приложения
```
kubectl apply -f app-ingress.yaml
```

Собираем и запускаем с помощью helm сервис заказы
```
cd order-service
helm install order-service chart/ --values chart/order-values.yaml
```

Применяем манифст для сервиса закаов
```
kubectl apply -f orders-ingress.yaml
```




После настройки
Запускаем тесты с помощью newman и проверяем, что все корректно запустилось. 
```
newman run "Otus_3.22.postman_collection.json"


→ регистрация 1
POST http://arch.homework/register [200 OK, 267B, 256ms]
√  [INFO] Request: {
"login": "Keely.Nienow",
"password": "dJvRgCUfVgy73wG",
"email": "Kip53@yahoo.com",
"first_name": "Amber",
"last_name": "Thiel"
}

√  [INFO] Response: {"login":"Keely.Nienow","password":"dJvRgCUfVgy73wG","email":"Kip53@yahoo.com","first_name":"Amber","last_name":"Thiel","id":1}

→ логин 1
POST http://arch.homework/login [200 OK, 260B, 80ms]
√  [INFO] Request: {"login": "Keely.Nienow", "password": "dJvRgCUfVgy73wG"}
√  [INFO] Response: {"IDtoken": "UVGphVDrxhqGW8dFWEiQZvycGghJWOMNyzylReVK"}

→ создание заказа
POST http://arch.homework/order [200 OK, 226B, 713ms]
√  [INFO] Request: {
"id": "eafbadf7-c5ba-4fbe-a711-d6938071bb16",
"name": "name1",
"count": "10",
"price": 10
}
√  [INFO] Response: {"id":"eafbadf7-c5ba-4fbe-a711-d6938071bb16","name":"name1","count":"10","price":10.0}
√  Status code is 200

→ дублирование заказа
POST http://arch.homework/order [409 Conflict, 163B, 21ms]
√  [INFO] Request: {
"id": "eafbadf7-c5ba-4fbe-a711-d6938071bb16",
"name": "name1",
"count": "10",
"price": 10
}
√  [INFO] Response: ConflictException
√  Status code is 409

→ получить заказ
GET http://arch.homework/order?uuid=eafbadf7-c5ba-4fbe-a711-d6938071bb16 [200 OK, 226B, 15ms]
√  [INFO] Request: [object Object]
√  [INFO] Response: {"id":"eafbadf7-c5ba-4fbe-a711-d6938071bb16","name":"name1","count":"10","price":10.0}
√  Status code is 200

→ получить несуществующий заказ
GET http://arch.homework/order?uuid=eafbadf7-c5ba-4fbe-a711-d6938071bb15 [400 Bad Request, 168B, 12ms]
√  [INFO] Request: [object Object]
√  [INFO] Response: BadRequestException
√  Status code is 400

┌─────────────────────────┬────────────────────┬────────────────────┐
│                         │           executed │             failed │
├─────────────────────────┼────────────────────┼────────────────────┤
│              iterations │                  1 │                  0 │
├─────────────────────────┼────────────────────┼────────────────────┤
│                requests │                  6 │                  0 │
├─────────────────────────┼────────────────────┼────────────────────┤
│            test-scripts │                 12 │                  0 │
├─────────────────────────┼────────────────────┼────────────────────┤
│      prerequest-scripts │                  9 │                  0 │
├─────────────────────────┼────────────────────┼────────────────────┤
│              assertions │                 16 │                  0 │
├─────────────────────────┴────────────────────┴────────────────────┤
│ total run duration: 1629ms                                        │
├───────────────────────────────────────────────────────────────────┤
│ total data received: 390B (approx)                                │
├───────────────────────────────────────────────────────────────────┤
│ average response time: 182ms [min: 12ms, max: 713ms, s.d.: 251ms] │
└───────────────────────────────────────────────────────────────────┘
```