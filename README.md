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
```
2.  Navigate into the project directory:
```bash
   cd cinema-ticket-service
``` 
3. Run the Maven build to install dependencies and compile the project
```bash
   mvn clean install
```
4. Run Unit Tests
```bash
   mvn test
``` 
5. Test Scenarios
```bash
   public class TicketServiceImplTest {

    private TicketPaymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketServiceImpl ticketService;

    @Before
    public void setUp() {
        paymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, seatReservationService);
    }

    @Test
    public void testValidPurchase() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(1L, adultRequest, childRequest);

        verify(seatReservationService).reserveSeat(1L, 3); 
        verify(paymentService).makePayment(1L, 65); 
       
        
        
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseWithoutAdult() throws InvalidPurchaseException {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(1L, childRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testExceedTicketLimit() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        ticketService.purchaseTickets(1L, adultRequest); // More than 25 tickets should throw an exception
    }
    @Test(expected = InvalidPurchaseException.class)
    public void testNoAccound() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        ticketService.purchaseTickets(0L, adultRequest); // More than 25 tickets should throw an exception
    }
    
    @Test
    public void TicketWithInfant() throws InvalidPurchaseException {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest chiRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        ticketService.purchaseTickets(1L, adultRequest,chiRequest,infantRequest); 
        verify(seatReservationService).reserveSeat(1L, 2); 
        verify(paymentService).makePayment(1L, 40);  
    }
    
}
```
