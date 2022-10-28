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

## User Entity Actions

#### User entity GET APIs

```
Get user by id:
    GET /users/id/{id}
    Response:
        status: 200
        type: JSON
        payload: user: User

Get user by email:
    GET /users//email/{email}
    Response:
        status: 200
        type: JSON
        payload: user: User

Get all users:
    GET /users/allUsers
    Response:
        status: 200
        type: JSON
        payload: user: User

Get list of users assigned to event:
    GET users/event/{eventId}
    Response:
        status: 200
        type: JSON
        payload: users: List<User>

Get list of users that have an event between start date and time to end date and time:
    GET users/range
        type: JSON
        body: map: Map<String, String>
    Response:
        status: 200
        type: JSON
        payload: users: List<User>

```
#### User entity POST APIs
```
Create a user:
    POST /users/
        type: JSON
        payload: {firstName: str, lastName: str, email: str, birthDate: str, joinDate: str, discontinued: boolean}
    Response:
        Status: 201
        type: JSON
        payload: user: User

Create list of users:
    POST /users/list
        type: JSON
        payload: users: List<User>
    Response:
        Status: 201
        type: JSON
        payload: users: List<User>
        
Log out a user:
    POST /users/logout/{email}
        type: JSON
        payload: subscription: SubscriptionEndpoint, 
    Response:
        Status: 200
        type: JSON
        payload: email: String
```
#### User entity PUT APIs
```
Update a user by id:
    PUT /users/{id}
        type: JSON
        payload: user: User
    Response:
        Status: 200
        type: JSON
        payload: user: User
        
Update a list of users:
    PUT /users/{id}
        type: JSON
        payload: [{firstName: str, lastName: str, email: str, birthDate: str, joinDate: str, discontinued: boolean}]
    Response:
        Status: 200
        type: JSON
        payload: users: List<User>     
```
#### User entity DELETE APIs
```
Delete a user by id:
    DELETE /users/{id}
        type: JSON
        payload: map: Map<String, String>
    Response:
        Status: 200
        type: JSON
        payload: user: User   
```

## Event Entity Actions

#### Event entity GET APIs
```
Get event by id:
    GET /events/{id}
    Response:
        Status: 200
        type: JSON
        payload: event: Event
 
Get all events:
    GET /events/
    Response:
        Status: 200
        type: JSON
        payload: event: List<Event>
        
Get all events for a user:
    GET /events/user/{id}
        type: JSON
        payload: map: Map<String, String>, id: int
    Response:
        Status: 200
        type: JSON
        payload: events: List<Event>
        
Get events in a range between start LocalDateTime to end LocalDateTime:
    GET /events/range
        type: JSON
        payload: map: Map<String, String>
    Response:
        Status: 200
        type: JSON
        payload: events: List<Event> 
```

#### Event entity POST APIs
```
Creates event for user:
    POST /events/single/{userId}
        type: JSON
        payload: event: Event, userId: int 
    Response:
        Status: 201
        type: JSON
        payload: event: Event
        
Creates List of events for a user:
    POST /events/list/{userId}
        type: JSON
        payload: events: List<Event>, userId: int 
    Response:
        Status: 201
        type: JSON
        payload: events: List<Event>
        
Add List of guests to an event:
    POST /events/guests/{eventId}
        type: JSON
        payload: guests: List<User>, eventId: int 
    Response:
        Status: 201
        type: JSON
        payload: event: Event
```

#### Event entity PUT APIs
```
Update event:
    PUT /events/{eventId}
        type: JSON
        payload: event: Event, eventId: int 
    Response:
        Status: 200
        type: JSON
        payload: event: Event

Update list of events:
    PUT /events
        type: JSON
        payload: events: List<Event> 
    Response:
        Status: 200
        type: JSON
        payload: events: List<Event> 
```


#### Event entity DELETE APIs
```
delete event:
    DELETE /events/{id}
        type: JSON
        payload: map: Map<String, String> , eventId: Integer 
    Response:
        Status: 200
        type: JSON
        payload: event: Event
```

