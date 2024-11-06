# Transport_system

Project of the Web Applications II course at Polytechnic University of Turin (academic year
2021-2022).

## Group 22 members:

| Student ID | Surname | Name |
| -- | --- | --- |
| s281945 | Baudanza | Filippo |
| s287836 | Apicella | Luigi |
| s289555 | Baldazzi | Alessandro |
| s****** | Cannarella | Alessandro |


### Project structure:

- `AuthService`: Contains the login service
- `TravelerService`: Contains the traveler service and the instructions to setup the Postgres database container.
- `TicketCatalogueService`: Contains the catalogue service and the instructions to setup Apache Kafka.
- `PaymentService`: Contains the payment service. It requires Kafka.
- `FakeBankService`: Contains the bank service, used to mock a real bank service. It requires Kafka.
- `DiscoveryService`: Contains the discovery service, based on SpringCloud Netflix Eureka.
- `ApiGatewayService`: Contains the gateway service.
- `ReportService`: Contains the report service, used to compute reports about tickets and transits. It requires Apache Kafka.
- `TransitService`: Contains the transit service, used to verify the tickets validity and store information about transits.

### Services

| Service                  | Port |
|--------------------------|------|
| ApiGatewayService        | 8080 |
| PaymentService           | 8081 |
| TravelerService          | 8083 |
| FakeBankService          | 8084 |
| ReportService            | 8086 |
| TransitService           | 8087 |
| AuthService              | 8090 |
| TicketCatalogueService   | 8182 |
| DiscoveryService         | 8761 |


### Default user
| Username                 | Password |
|--------------------------|----------|
| admin              | admin |
| superadmin          | superadmin |
| machine   | machine |
| customer           | customer |

### API


#### POST /user/register  

creates a new user not yet active, generates an action code that is sent via email. Receives a json with user details and returns a json with some activation details.
Example:
    
    {
    "nickname": "Mario",
    "password": "Password1!sicura",
    "email": "mariorossi@email.it"
    }
    
    
    {
    "provisional_id": "996d8774-9396-4a81-8a38-30e2706328bd",
    "email": "baudanzapippo@gmail.com"
    }
    

#### POST /user/validate

It validates user registration. Receives a json with details for activation and returns details of the activated user.
Example:

    {
    "provisional_id": "996d8774-9396-4a81-8a38-30e2706328bd",
    "email": "mariorossi@email.it",
    "activation_code":"8fnPPFoCHvl5BYg"
     }

    {
    "userId": 5,
    "username": "Mario",
    "email": "baudanzapippo@gmail.com"
    }

#### POST /user/login

It logs in the user. It receives a json with credentials and returns a cookie with autorization
Example:

    {
    "username": "Mario",
    "password": "Password1!sicura"
    }
  
#### POST /admin

creates a new admin. Receives a json with the admin details and returns a json with user details.
Example:

    {
    "username": "Marioadmin",
    "password": "Password1!sicura",
    "email": "mariorossi@email.it",
    "role" : "ADMIN"
    }

    {
    "userId": 6,
    "username": "Marioadmin",
    "email": "mariorossi@email.it"
    }

### TRAVELER SERVICE
#### GET v1/profile

get a profile of the current user.

Example:

    {
    "username": "Mario22",
    "name": "Mario",
    "address": "via mario rossi",
    "telephone" : "33336889978",
    "dateofbirth": 28/01/1996
    }

#### PUT v1/profile

update a profile of the current user

Example:

    {
    "telephone" : "33336889978",
    "dateofbirth": 28/01/1996
    }
#### GET v1/tickets

get tickets of the current user.

Example:

    {
    "sub": "1234",
    "iat": "2022-11-05 01:54:39.0",
    "validfrom": "2022-11-05 01:54:39.0",
    "exp" : "2022-11-05 01:54:39.0",
    "zid": "1",
    "type": "WEEKEND",
    "jws": "gsgsgsrgsrggggrgrghdhttjjhsths8rt899j88...",
    }

#### GET v1/ticket/{ticketID}

get the qrcode of the ticket with the {ticketID} of the current user

Example: 

    {
    "qrCode": "iVBORw0KGgoAAAANSUhEUgAAAPoAAAD6CAIAAAAHjs1qAAATk0lEQVR4Xu2SUY7kyI4EB3uzvfm72S76ZxCQtUU7FVRm9RMB...."
    }

#### POST v1/ticket/generate

Generate new tickets. 

Example:

    {
    "sub": "1234",
    "iat": "2022-11-05 01:54:39.0",
    "validfrom": "2022-11-05 01:54:39.0",
    "exp" : "2022-11-05 01:54:39.0",
    "zid": "1",
    "type": "WEEKEND",
    "jws": "gsgsgsrgsrggggrgrghdhttjjhsths8rt899j88...",
    }

### REPORT SERVICE
#### POST admin/report

Generate a Global Report between two dates using HTTP requests

Example: 
##### REQUEST    
    {
        "start_date": "2022-04-12 12:00:00", 
        "end_date": "2023-12-14 18:00:00"
    }
##### RESPONSE
    {
        "purchases": 2,
        "profit": 50.0,
        "transits": 2,
        "avarageProfit": 25.0,
        "percClassicTickets": 50.0,
        "percTravelersCards": 50.0,
        "percTransitsClassicTickets": 50.0,
        "percTransitsTravelerCards": 50.0,
        "ticketsNumber": 2
    }

#### POST admin/report/{username}

Generate a User Report between two dates using HTTP requests

Example:
##### REQUEST
    {
        "start_date": "2022-04-12 12:00:00", 
        "end_date": "2023-12-14 18:00:00"
    }
##### RESPONSE
    {
        "purchases": 1,
        "shopping": 25.0,
        "transits": 2,
        "avarageSpend": 25.0,
        "minOrder": 25.0,
        "maxOrder": 25.0,
        "percClassicTickets": 100.0,
        "percTravelersCards": 9.0,
        "percTransitsClassicTickets": 100.0,
        "percTransitsTravelerCards": 0.0,
        "ticketsNumber": 1
    }

#### POST admin/stats

Generate a Global Report between two dates using Apache Kafka and a DB

Example: 
##### REQUEST    
    {
        "start_date": "2022-04-12 12:00:00", 
        "end_date": "2023-12-14 18:00:00"
    }
##### RESPONSE
    {
        "purchases": 2,
        "profit": 50.0,
        "transits": 2,
        "avarageProfit": 25.0,
        "percClassicTickets": 50.0,
        "percTravelersCards": 50.0,
        "percTransitsClassicTickets": 50.0,
        "percTransitsTravelerCards": 50.0,
        "ticketsNumber": 2
    }

#### POST admin/stats/{username}

Generate a User Report between two dates using Apache Kafka and a DB

Example:
##### REQUEST
    {
        "start_date": "2022-04-12 12:00:00", 
        "end_date": "2023-12-14 18:00:00"
    }
##### RESPONSE
    {
        "purchases": 1,
        "shopping": 25.0,
        "transits": 2,
        "avarageSpend": 25.0,
        "minOrder": 25.0,
        "maxOrder": 25.0,
        "percClassicTickets": 100.0,
        "percTravelersCards": 9.0,
        "percTransitsClassicTickets": 100.0,
        "percTransitsTravelerCards": 0.0,
        "ticketsNumber": 1
    }

### TRANSIT SERVICE
#### GET admin/transits

Get all transits data

Example:

##### RESPONSE
    [
        {
            "id": 1,
            "transit_date": "2022-05-30T16:00:00",
            "ticket_id": 1,
            "ticket_type": "Monthly",
            "ticket_user": "admin"
        },
        {
            "id": 2,
            "transit_date": "2022-07-23T17:30:00.1",
            "ticket_id": 1,
            "ticket_type": "Ordinary",
            "ticket_user": "admin"
        }
    ]

#### POST /transits

Insert new transit

Example:
##### REQUEST
    {
        "ticket_id" : "3",
        "user": "admin", 
        "ticket_type" : "Ordinary"
    }
##### RESPONSE
    {
        "id": 3,
        "transit_date": "2022-11-05T11:46:15.9213022",
        "ticket_id": 3,
        "ticket_type": "Ordinary",
        "ticket_user": "customer"
    }

#### POST /admin/transits/report

Get global transit report

Example:
##### REQUEST
    {
        "start_date": "2022-04-12 12:00:00", 
        "end_date": "2023-12-14 18:00:00"
    }
##### RESPONSE
    {
        "transits": 3,
        "percOrdinaryTransits": 66.666664,
        "percTravelerCardsTransits": 33.333332
    }

#### POST /admin/transits/report/{username}

Get user transit report

Example:
##### REQUEST
    {
        "start_date": "2022-04-12 12:00:00", 
        "end_date": "2023-12-14 18:00:00"
    }
##### RESPONSE
    {
        "transits": 1,
        "percOrdinaryTransits": 100.0,
        "percTravelerCardsTransits": 0.0
    }
