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
4. Main Implementation
```bash

public class TicketServiceImpl implements TicketService {

    private static final int MAX_TICKETS = 25;
    private static final int ADULT_TICKET_PRICE = 25;
    private static final int CHILD_TICKET_PRICE = 15;
   

    private final TicketPaymentService paymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatReservationService) {
        this.paymentService = paymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account ID");
        }

        int totalTickets = 0;
        int totalCost = 0;
        int totalSeats = 0;
        boolean hasAdultTicket = false;

        // Loop through each ticket request and calculate totals
        for (TicketTypeRequest request : ticketTypeRequests) {
            int noOfTickets = request.getNoOfTickets();
            switch (request.getTicketType()) {
                case ADULT:
                    totalCost += noOfTickets * ADULT_TICKET_PRICE;
                    totalSeats += noOfTickets;
                    hasAdultTicket = true;
                    break;
                case CHILD:
                    totalCost += noOfTickets * CHILD_TICKET_PRICE;
                    totalSeats += noOfTickets;
                    break;
                case INFANT:
                    // Infants are free and don't get a seat
                    break;
            }
            totalTickets += noOfTickets;
        }

        // Check if total tickets exceed the allowed limit
        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + " tickets.");
        }

        // Check if there is at least one adult ticket
        if (!hasAdultTicket) {
            throw new InvalidPurchaseException("Child or Infant tickets cannot be purchased without at least one Adult ticket.");
        }

         // ideal scenario would be to block the seat 1st and then try the payment, Hence call the Reservation
         seatReservationService.reserveSeat(accountId, totalSeats);
        
         /*
          *  Once the seat is blocked / Reserved, try the payment, if payment is success we can show the succeess message to user. 
          *  else unblock the seat for other user.  This can be achieved by returning a boolean value from Payment service Interface
          */
         
         paymentService.makePayment(accountId, totalCost);

       
    }
    
}

```   
5. Run Unit Tests
```bash
   mvn test
``` 
6. Test Code with Scenarios
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
