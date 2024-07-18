# User API Spec

## Register User

Endpoint : POST /api/users

Request Body :
```json
{
    "username" : "dedeherdiana",
    "password" : "12345678",
    "email" : "dedeherdiana@gmail.com",
}
```

Response Body Success:

```json
{
    "data" : "Ok"
}

Response Body Failed:

```json
{
    "errors" : "Username is mandatory"
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :
```json
{
    "username" : "dedeherdiana",
    "password" : "12345678",
}
```

Response Body Success:

```json
{
    "data" : {
        "token" : "eyJhbGciOiJIUzI1NiIsInR",
        "expiredAt": 1234567 //miliseconds
    }
}

Response Body (Failed, 401):

```json
{
    "errors" : "Username and password not match"
}
```

## Get User

Endpoint : GET /api/users/current

Request Header : 
- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
    "data" : {
        "username" : "dedeherdiana",
        "name" : "Dede Herdiana"
    }
}

Response Body (Failed, 401):

```json
{
    "errors" : "Username and password not match"
}
```


## Update User

Endpoint : PATCH /api/users/current

Request Header : 
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
    "name" : "Dede Herdiana",
    "password" : "new password",
}
```
Response Body Success:

```json
{
    "data" : {
        "username" : "dedeherdiana",
        "name" : "Dede Herdiana"
    }
}

Response Body (Failed, 401):

```json
{
    "errors" : "Username and password not match"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header : 
- X-API-TOKEN : Token (Mandatory)

Response Body Success:

```json
{
    "data" : "OK"
}