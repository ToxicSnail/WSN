# Gaechka Auth Service

Микросервис написанный на Spring Boot и Java 17, который принимает учетные данные пользователя, выпускает для них JWT и отправляет токен во внешний API.

## Возможности
- REST endpoint POST /api/auth/token принимает JSON { "username": "...", "password": "..." }.
- Валидация входящих данных на пустые значения.
- Временная (грамотная) генерация HMAC-SHA256 JWT с настраиваемыми issuer, сроком жизни и секретом.
- HTTP POST отправка токена на внешний сервис (адрес якобы задается в настройках).

## Требования
- Java 17+
- Gradle 8.x (или совместимый). Можно использовать локально установленный Gradle или добавить обертку (gradle wrapper).

## Настройка
Конфиг находится в `src/main/resources/application.yml`:

    app:
      jwt:
        secret: "секрет"
        expiration-seconds: 3600
        issuer: gaechka-auth-service
      forwarding:
        url: "https://external.service.example/api/token"

## Пример работы:

Запро:

    curl --request POST       --url http://localhost:8080/api/auth/token       --header 'Content-Type: application/json'       --data '{"username":"alice","password":"secret"}'

Ответ:

    {"token":"<jwt>"}

## Тесты

    gradle test

Их я написал по приколу :)