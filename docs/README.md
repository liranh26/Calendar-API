# Calendar REST API application

> This is a Calendar application providing a REST API to users scheduling events with configurable push notifications to their web browser prior to the desired time of  event.

## Versions
Minimum requirement of the following versions
* Maven 4.0
* Java 17 
* Spring Boot 2.7.1 
* Microsoft JDBC Driver For SQL Server 11.2
* Lombok 1.18

## Install

    mvn install
    java -jar calendar-0.0.1-SNAPSHOT.jar
Note to change directry to the far file in target folder and run.

## Run the app

    ./mvnw spring-boot:run
Note to assign existing user + password and approved check permissions to the DB.   


# REST API

The REST API are described below.


## Get list of all Users
This call gets all users from the DB.
### Request

`GET /allUsers`

    http://localhost:8080/users/allUsers

### Response

    HTTP/1.1 200 OK
    Date: Fri, 28 Oct 2022 11:30:05 GMT
    Status: 200 OK
    Connection: keep-alive
    Content-Type: application/json
    Content-Length: 3

    [
        {
            "userId": 1,
            "firstName": "Liran",
            "lastName": "Hadad",
            "email": "test@test.com",
            "birthDate": "1990-02-26",
            "joinDate": "2022-01-01",
            "discontinued": false
        },
        {
            "userId": 2,
            "firstName": "Snir",
            "lastName": "Hadad",
            "email": "test2@test.com",
            "birthDate": "1993-07-08",
            "joinDate": "2022-05-05",
            "discontinued": false
        },
        {
            "userId": 3,
            "firstName": "Sapir",
            "lastName": "Hadad",
            "email": "test3@test.com",
            "birthDate": "1990-07-23",
            "joinDate": "2022-06-06",
            "discontinued": false
        }
    ]


## Get list of Users to event
This call gets all users assigned to an event from the DB.
### Request

`GET /event/{eventId}`

    http://localhost:8080/users/event/1000

### Response

    HTTP/1.1 200 OK
    Date: Fri, 28 Oct 2022 11:30:05 GMT
    Status: 200 OK
    Connection: keep-alive
    Content-Type: application/json
    Content-Length: 2

    [
        {
            "userId": 1,
            "firstName": "Liran",
            "lastName": "Hadad",
            "email": "test@test.com",
            "birthDate": "1990-02-26",
            "joinDate": "2022-01-01",
            "discontinued": false
        },
        {
            "userId": 2,
            "firstName": "Snir",
            "lastName": "Hadad",
            "email": "test2@test.com",
            "birthDate": "1993-07-08",
            "joinDate": "2022-05-05",
            "discontinued": false
        }
    ]


