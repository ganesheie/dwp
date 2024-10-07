# Cinema Ticket Service

## Overview

This project is a coding exercise implementing a Cinema Ticket Booking Service in Java. The solution is designed to handle various business rules for purchasing cinema tickets, including seat reservation and payment processing. The solution includes ticket types, pricing, and validation based on the business rules provided.

## Business Rules

- There are 3 types of tickets: **Infant**, **Child**, and **Adult**.
- **Infant tickets** are free and do not require a seat (they sit on an adult’s lap).
- **Child tickets** cost £15, and **Adult tickets** cost £25.
- **Child** and **Infant tickets** cannot be purchased without at least one **Adult ticket**.
- A maximum of 25 tickets can be purchased in one transaction.
- Payment is processed via an external service (`TicketPaymentService`).
- Seats are reserved through an external service (`SeatReservationService`).

## Requirements

- **Java version**: Java 11 or later
- **Build tool**: Maven
- **Testing**: JUnit

## Installation

1. Clone the repository to your local machine:
```bash
   git clone https://github.com/ganesheie/dwp.git
2.  Navigate into the project directory:
   ```bash cd cinema-ticket-service
4. Run the Maven build to install dependencies and compile the project
   ```bash mvn clean install
5.Run Unit Tests
  ```bash mvn test
  
