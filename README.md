# Design and implement a REST API using SpringBoot 2

#### The task is:

Build a voting system for deciding where to have lunch.

- 2 types of users: admin and regular users
- Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
- Menu changes each day (admins do the updates)
- Users can vote on which restaurant they want to have lunch at
- Only one vote counted per user
- If user votes again the same day:

    - If it is before 11:00 we assume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

- Each restaurant provides new menu each day.

## API documentation

Requests to the API are made via the HTTP protocol with basic authorization. 
POST and PUT request's content must be transferred in JSON format, the Content-Type header must be set to "application/json".

## Sections

- [_Profile_](#profile)

- [_Restaurant_](#restaurant)

- [_Menu_](#menu)

- [_Vote_](#vote)

#### Basic authorization (Base64)

    "admin@yandex.ru:pass"          "YWRtaW5AeWFuZGV4LnJ1OnBhc3M="     
    "user@ya.ru:pass"               "dXNlckB5YS5ydTpwYXNz"
    "lisa@gmail.com:password"       "bGlzYTEyMkBnbWFpbC5jb206cGFzc3dvcmQ="

Authorization example for user admin@yandex.ru

    "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M="

## Profile

#### Register user
***

method: `POST`

path: `/rest/profile/register`

    curl -s -X POST -d '{"name": "Lisa", "email": "lisa@gmail.com", "password": "password"}' -H 'Content-Type:application/json;charset=UTF-8' http://{hostname}/rest/profile/register

Request:

    {
        "name": "Lisa",
        "email": "lisa@gmail.com",
        "password": "password"
    }
    
 Response:
 
    {
        "id":10021,
        "name":"Lisa",
        "email":"lisa@gmail.com",
        "enabled":true,
        "roles": [
            "ROLE_USER"
        ]
    }
    
HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 201 | Created | Normal status |
| 409 | Conflict | User exist |
| 422 | Unprocessable Entity | Any fields is not valid |

#### Get user profile
***

method: `GET`

path: `/rest/profile`

    curl -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/profile

Response:

    {
        "id": 10000,
        "name": "Sergey",
        "email": "admin@yandex.ru",
        "enabled": true,
        "roles": [
            "ROLE_ADMIN"
        ]
    }
 
HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 200 | OK |
| 401 | Unauthorized | 

#### Update user profile
***

method: `PUT`

path: `/rest/profile`

    curl -s -X PUT -H "Authorization: Basic bGlzYUBnbWFpbC5jb206cGFzc3dvcmQ=" -d '{"id": 10021, "name": "Elisabeth", "email": "lisa@gmail.com", "password": "password"}' -H 'Content-Type:application/json;charset=UTF-8' http://{hostname}/rest/profile

Request:

    {
        "id": 10021,
        "name": "Elisabeth",
        "email": "lisa@gmail.com",
        "password": "password"
    }
    
HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content | Normal status |
| 401 | Unauthorized |
| 409 | Conflict | User email exist |
| 422 | Unprocessable Entity | Any fields is not valid |


#### Delete profile 
***

method: `DELETE`

path: `/rest/profile`

    curl -s -X DELETE -H "Authorization: Basic bGlzYUBnbWFpbC5jb206cGFzc3dvcmQ=" http://{hostname}/rest/profile

HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content |
| 401 | Unauthorized |

[↑ sections](#sections)

## Restaurant


#### Get all restaurants
***

method: `GET`

path: `/rest/restaurant`

    curl -H "Authorization: Basic dXNlckB5YS5ydTpwYXNz" http://{hostname}/rest/restaurant

Response:

    [
        {
            "id": 10006,
            "name": "KFC3",
            "city": "Москва",
            "description": "Куриные бургеры и картошка",
            "added": "2019-01-01",
            "menus": null
        },
        {
            "id": 10007,
            "name": "McDs1",
            "city": "Москва",
            "description": "Бургеры и картошка",
            "added": "2019-01-01",
            "menus": null
        },
        ...
    ]
HTTP status codes:

| Code | HTTP status | Description |
| --- | --- | --- |
| 200 | OK |


#### Get restaurant
***

method: `GET`

path: `/rest/restaurant/{id}`

    curl -H "Authorization: Basic dXNlckB5YS5ydTpwYXNz" http://{hostname}/rest/restaurant/{id}

Response:

    {
        "id": 10006,
        "name": "KFC3",
        "city": "Москва",
        "description": "Куриные бургеры и картошка",
        "added": "2018-12-31",
        "menus": [
            {
                "id": 10012,
                "added": "2018-12-31",
                "dishes": [
                    {
                        "id": 10019,
                        "name": "1Пицца",
                        "price": 279.49
                    },
                    {
                        "id": 10020,
                        "name": "2Пицца",
                        "price": 279.49
                    }
                ]
            }
        ]
    }
HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 200 | OK |
| 422 | Unprocessable Entity |

#### Create restaurant
***

method: `POST`

path: `/rest/restaurant`

    curl -s -X POST -d '{"name": "Гусли", "city": "Александров", "description": "Ресторан русской кухни"}' -H 'Content-Type:application/json;charset=UTF-8' -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M="  http://{hostname}/rest/restaurant

Request:

    {
        "name": "Гусли",
        "city": "Александров",
        "description": "Ресторан русской кухни"
    }

Response:

    {
        "id": 10022,
        "name": "Гусли",
        "city": "Александров",
        "description": "Ресторан русской кухни",
        "added": "2018-12-31",
        "menus": []
    }
    
Response headers:

    Location http://localhost:8080/rest/restaurant/10022

HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 201 | Created |
| 409 | Conflict | Restaurant with this name and city is exist
| 422 | Unprocessable Entity |


#### Update restaurant
***

method: `PUT`

path: `/rest/restaurant/{id}`


    curl -s -X PUT -d '{"name": "Гусли", "city": "Александров", "description": "Ресторан русской кухни"}' -H 'Content-Type:application/json;charset=UTF-8' -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M="  http://{hostname}/rest/restaurant/{id}

Request:

    {
        "id": 10022,
        "name": "Гусли",
        "city": "Александров",
        "description": "Ресторан традиционной русской кухни"
    }
HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content |
| 409 | Conflict |
| 422 | Unprocessable Entity |


#### Delete restaurant
***

method: `DELETE`

path: `/rest/restaurant/{id}`

    curl -s -X DELETE -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/restaurant/{id}

HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content | Normal status |
| 422 | Unprocessable Entity | Not found entity |

[↑ sections](#sections)

## Menu

#### Create menu
***

method: `POST`

path: `/rest/menu`

    curl -s -X POST -d '{"name": "Гусли", "city": "Александров", "description": "Ресторан русской кухни"}' -H 'Content-Type:application/json;charset=UTF-8' -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M="  http://{hostname}/rest/menu

Request:

    {
        "restaurantId": 10006,
        "added": "2019-01-01",
        "dishes": [
            {
                "name": "Ролл1",
                "price": 270.15
            },
            {
                "name": "Ролл2",
                "price": 180.55
            }
        ]
    }
    
Response:

    {
        "id": 10025,
        "added": "2019-01-01",
        "dishes": [
            {
                "id": 10026,
                "name": "Ролл1",
                "price": 270.15
            },
            {
                "id": 10027,
                "name": "Ролл2",
                "price": 180.55
            }
        ]
    }

Response headers:

    Location http://localhost:8080/rest/menu/10025

HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 201 | Created | Menu created with location
| 409 | Conflict | Menu for that date is exist
| 422 | Unprocessable Entity | Any fields is not valid

#### Get all menus
***

method: `GET`

path: `/rest/menu`

    curl -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/menu

Response:

    [
        {
            "id": 10010,
            "restaurantId": 10006,
            "dishes": [
                {
                    "id": 10013,
                    "name": "Картошка",
                    "price": 70.15
                },
                {
                    "id": 10014,
                    "name": "Бургер куриный",
                    "price": 80.55
                },
                {
                    "id": 10015,
                    "name": "Салат",
                    "price": 100.35
                }
            ],
            "added": "2018-12-14"
        },
        
        ...
        
    ]
    
HTTP status codes:

| Code | HTTP status | Description |
| --- | --- | --- |
| 200 | OK | Normal status |
| 403 | Forbidden | Need admin role |

#### Get menu
***



method: `GET`

path: `/rest/menu/{id}`

    curl -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/menu/{id}

Response:

    {
        "id": 10010,
        "added": "2018-12-14",
        "dishes": [
            {
                "id": 10013,
                "name": "Картошка",
                "price": 70.15
            },
            {
                "id": 10014,
                "name": "Бургер куриный",
                "price": 80.55
            },
            {
                "id": 10015,
                "name": "Салат",
                "price": 100.35
            }
        ]
    }
    
    
HTTP status codes:

| Code | HTTP status | Description |
| --- | --- | --- |
| 200 | OK | Normal status |
| 403 | Forbidden | Need admin role |

#### Get menu by restaurant ID and date(optional)
***

method: `GET`

path: `/rest/menu/by`



| Request param | type | required | example |
| --- | --- | --- | --- |
| **restaurantId** | Number | true | 10006 |
| **date** | Date ISO 8601 | false | 2018-12-14 |

    curl -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/menu/by?restaurantId={id}&date={date}

Response:

    [
        {
            "id": 10010,
            "added": "2018-12-14",
            "dishes": [
                {
                    "id": 10013,
                    "name": "1Картошка",
                    "price": 70.15
                },
                {
                    "id": 10014,
                    "name": "2Бургер куриный",
                    "price": 80.55
                },
                {
                    "id": 10015,
                    "name": "3Салат",
                    "price": 100.35
                }
            ]
        },
        {
            "id": 10012,
            "added": "2019-01-01",
            "dishes": [
                {
                    "id": 10019,
                    "name": "1Пицца",
                    "price": 279.49
                },
                {
                    "id": 10020,
                    "name": "2Пицца",
                    "price": 279.49
                }
            ]
        }
    ]
    
    
HTTP status codes:

| Code | HTTP status | Description |
| --- | --- | --- |
| 200 | OK | Normal status |
| 403 | Forbidden | Need admin role |

#### Update menu
***

method: `PUT`

path: `/rest/menu/{id}`


    curl -s -X PUT -d '{"id": "10010", "restaurantId": 10006, "added": "2018-12-14", "dishes": [{"name": "Ролл1", "price": 270.15},{"name": "Ролл2", "price": 280.55}]}' -H 'Content-Type:application/json;charset=UTF-8' -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M="  http://{hostname}/rest/menu/{id}

Request:

    {
        "id": 10010,
        "restaurantId": 10006,
        "added": "2018-12-14",
        "dishes": [
            {
                "name": "Ролл1",
                "price": 270.15
            },
            {
                "name": "Ролл2",
                "price": 280.55
            }
        ]
    }
        
HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content | Normal status
| 409 | Conflict | 
| 422 | Unprocessable Entity | Any fields is not valid


#### Delete menu
***

method: `DELETE`

path: `/rest/menu/{id}`

    curl -s -X DELETE -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/menu/{id}

HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content | Normal status |
| 422 | Unprocessable Entity | Not found entity |

[↑ sections](#sections)

## Vote

#### Vote for restaurant
***

method: `POST`

path: `/rest/vote/for`

| Request param | type | required | example |
| --- | --- | --- | --- |
| **restaurantId** | Number | true | 10006 |

    curl -s -X POST -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/vote/for?restaurantId=10006

HTTP status codes:

| Code | Status | Description |
| --- | --- | --- |
| 204 | No Content | Normal status |
| 409 | Conflict | Restaurant not found |
| 412 | Precondition Failed | Vote time is expired |

#### Get all of auth User
***

method: `GET`

path: `/rest/vote`

    curl -H "Authorization: Basic YWRtaW5AeWFuZGV4LnJ1OnBhc3M=" http://{hostname}/rest/vote


response:

    [
        {
            "id": 10022,
            "date": "2019-01-02",
            "userEmail": "admin@yandex.ru",
            "restaurant": {
                "id": 10006,
                "name": "KFC3",
                "city": "Москва",
                "description": "Куриные бургеры и картошка",
                "added": "2019-01-02",
                "menus": null
            }
        },
        
        ...
        
    ]

[↑ sections](#sections)