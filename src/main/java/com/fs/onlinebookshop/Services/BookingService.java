package com.fs.onlinebookshop.Services;

import com.fs.onlinebookshop.Entity.*;
import com.fs.onlinebookshop.Exception.*;
import com.fs.onlinebookshop.Repository.BookRepository;
import com.fs.onlinebookshop.Repository.BookingRepository;
import com.fs.onlinebookshop.Repository.PaymentRepository;
import com.fs.onlinebookshop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public ResponseEntity<String> newBooking(String email, long bookId) {
        User user=userRepository.findByEmail(email).get();
        Book book=bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("No book Found with id:"+bookId));
        if(book.getAvailableCount()<1){
            throw new BookNotFoundException("No Book Available with id "+bookId);
        }
        Booking booking=new Booking();
        booking.setPrice(book.getPrice());
        booking.setBookingStatus(BookingStatus.PLACED);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setBook(book);
        booking.setUser(user);
        book.setAvailableCount(book.getAvailableCount()-1);
        bookingRepository.save(booking);
        return new ResponseEntity<>("Booked Successfully", HttpStatus.CREATED);
    }


    public ResponseEntity<List<Booking>>findMyBooking(String email){
        User user=userRepository.findByEmail(email).get();
        List<Booking> bookings=bookingRepository.findByUser(user);
        if(bookings.isEmpty()) {
            throw new BookingNotFoundException("No bookings found");
        }
        return new ResponseEntity<>(bookings,HttpStatus.FOUND);
    }

    public ResponseEntity<Booking>updateBookingStatus(long bookingId,BookingStatus newStatus){
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("No booking found with id:"+bookingId));

        switch(newStatus) {
            case CONFIRMED:
                if(booking.getBookingStatus()==BookingStatus.PLACED) {
                    booking.setBookingStatus(newStatus);
                    bookingRepository.save(booking);
                }
                else {
                    throw new InvalidBookingStatus("Inappropriate booking status");
                }
                break;
            case SHIPPED:
                if(booking.getBookingStatus()==BookingStatus.CONFIRMED) {
                    booking.setBookingStatus(newStatus);
                    bookingRepository.save(booking);
                }else {
                    throw new InvalidBookingStatus("Inappropriate booking status");
                }
                break;
            case DELIVERED:
                if(booking.getBookingStatus()==BookingStatus.SHIPPED) {
                    booking.setBookingStatus(newStatus);
                    bookingRepository.save(booking);
                }else {
                    throw new InvalidBookingStatus("Inappropriate booking status");
                }
                break;
            case CANCELED:
                if(booking.getBookingStatus()==BookingStatus.PLACED || booking.getBookingStatus()==BookingStatus.CONFIRMED) {
                    if (booking.getPaymentStatus()==PaymentStatus.SUCCESS){
                        Payment payment=paymentRepository.findByBooking(booking).orElseThrow(() -> new PaymentNotFoundException("No payments not found"));

                        payment.setPaymentStatus(PaymentStatus.REFUND);
                        booking.setPaymentStatus(PaymentStatus.REFUND);
                        booking.setBookingStatus(newStatus);
                        paymentRepository.save(payment);
                        bookingRepository.save(booking);
                    }
                    else {
                        booking.setPaymentStatus(PaymentStatus.FAILED);
                        booking.setBookingStatus(newStatus);
                        bookingRepository.save(booking);
                    }
                }
                else {
                    throw new InvalidBookingStatus("Inappropriate booking status");
                }
                break;
            default:{
                throw new InvalidBookingStatus("Invalid booking status: "+booking.getBookingStatus());
            }
        }
        return new ResponseEntity<>(booking,HttpStatus.OK);
    }


    public ResponseEntity<String>cancelBooking(String email,long bookingId){
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("No booking found with id"+bookingId));

        User user=userRepository.findByEmail(email).get();

        if(booking.getUser().getId()!=user.getId()) {
            throw new UserNotFoundException("Unauthorized User");
        }

        if(booking.getBookingStatus()==BookingStatus.PLACED || booking.getBookingStatus()==BookingStatus.CONFIRMED) {
            if (booking.getPaymentStatus()==PaymentStatus.SUCCESS){
                Payment payment=paymentRepository.findByBooking(booking).orElseThrow(() -> new PaymentNotFoundException("No payments found"));

                booking.setBookingStatus(BookingStatus.CANCELED);
                booking.setPaymentStatus(PaymentStatus.REFUND);
                payment.setPaymentStatus(PaymentStatus.REFUND);
                paymentRepository.save(payment);
                bookingRepository.save(booking);
            }else {
                booking.setBookingStatus(BookingStatus.CANCELED);
                booking.setPaymentStatus(PaymentStatus.FAILED);
                bookingRepository.save(booking);
            }
        }else {
            throw new UnableToCancelException("Unable to cancel");
        }
        return new ResponseEntity<>("Cancel the Order successfully",HttpStatus.OK);

    }

    public ResponseEntity<Booking>findBooking(long bookingId){
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("No booking found with id"+bookingId));
        return new ResponseEntity<>(booking,HttpStatus.FOUND);
    }

}
