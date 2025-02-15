# Flight Service Application

This is a Spring Boot application that provides a service to find the top 5 fastest flights between two airports. The application reads flight data from a CSV file and processes it to find direct and connecting flights.

## Features

- Find the top 5 fastest direct and connecting flights between two airports.
- Ensure each via-airport is used only once in the one-stop journeys.
- Error handling for bad requests and server errors.

## Prerequisites

- Java 11 or higher
- Maven
- Spring Boot

## Getting Started

### Clone the repository

```sh
git clone https://github.com/rohitkhandelwal88/flight-service.git
cd flight-service
```
### Build the project

```sh
mvn clean install
```

### Run the application

```sh
mvn spring-boot:run
```

#### API Endpoints

Get the top 5 fastest flights between two airports:

```sh
GET /flights/fastest?src={source_airport_code}&dest={destination_airport_code}
```

#### Parameters:  
- **src**: Source airport code (e.g., IXC)
- **dest**: Destination airport code (e.g., DEL)
#### Response:  
Returns a list of the top 5 fastest flights between the source and destination airports.
#### Example:

```sh
GET /flights/fastest?src=IXC&dest=DEL
```

#### Error Handling
- **400 Bad Request:** Returned when the source or destination airport code is missing or invalid.
- **500 Internal Server Error:** Returned for any other server errors.