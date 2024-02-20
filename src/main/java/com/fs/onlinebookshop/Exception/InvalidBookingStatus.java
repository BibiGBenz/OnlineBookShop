package com.fs.onlinebookshop.Exception;

public class InvalidBookingStatus extends RuntimeException {
    public InvalidBookingStatus(String message) {
        super(message);
    }
}
