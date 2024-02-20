package com.fs.onlinebookshop.Services;

import com.fs.onlinebookshop.Entity.*;
import com.fs.onlinebookshop.Exception.*;
import com.fs.onlinebookshop.Repository.BookingRepository;
import com.fs.onlinebookshop.Repository.PaymentRepository;
import com.fs.onlinebookshop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    //admin and user
    public ResponseEntity<String> makePayment(String email, long bookingId){
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(() ->new BookingNotFoundException("No booking found with booking id "+bookingId));

        User user=userRepository.findByEmail(email).get();

        if(booking.getPaymentStatus()== PaymentStatus.SUCCESS) {
            throw new PaymentAlreadyDoneException("Payment already done");
        }
        if (booking.getBookingStatus()==BookingStatus.CANCELED){
            throw new UnableToPayException("Unable to make payments for this booking");
        }
        if (booking.getUser().getId()!=user.getId()){
            throw new UserNotFoundException("Unauthorized User");
        }

        booking.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment payment=new Payment();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPrice(booking.getPrice());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setBooking(booking);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setUser(user);
        paymentRepository.save(payment);
        return new ResponseEntity<>("Payment successful"+booking.getPrice(), HttpStatus.CREATED);
    }

    //admin
    public ResponseEntity<List<Payment>>allPayments(){
        List<Payment>payments =paymentRepository.findAll();
        if(payments.isEmpty()) {
            throw new PaymentNotFoundException("No Payments found");
        }
        return new ResponseEntity<>(payments,HttpStatus.FOUND);
    }

    //admin and user
    public ResponseEntity<List<Payment>>checkPaymentInfo(String email){
        User user=userRepository.findByEmail(email).get();
        List<Payment>payments=paymentRepository.findByUser(user);
        if(payments.isEmpty()) {
            throw new PaymentNotFoundException("No payments found");
        }
        return new ResponseEntity<>(payments,HttpStatus.FOUND);
    }


    //admin
    public ResponseEntity<Payment>checkPaymentByBookingId(long bookingId){
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("No book Found with id "+bookingId));
        Payment payment=paymentRepository.findByBooking(booking).orElseThrow(() -> new PaymentNotFoundException("No payments have been done"));
        return new ResponseEntity<>(payment,HttpStatus.FOUND);
    }
}
