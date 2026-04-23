package com.ticketsystem.exception;

public class CapacityExceededException extends TicketSystemException {

    public CapacityExceededException(String message) {
        super(message);
    }
}
