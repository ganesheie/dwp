package uk.gov.dwp.uc.pairtest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

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
