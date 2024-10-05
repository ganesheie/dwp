package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

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
