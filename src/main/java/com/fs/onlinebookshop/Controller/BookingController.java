package com.fs.onlinebookshop.Controller;

import com.fs.onlinebookshop.Entity.Book;
import com.fs.onlinebookshop.Entity.Booking;
import com.fs.onlinebookshop.Entity.BookingStatus;
import com.fs.onlinebookshop.Services.BookingService;
import com.fs.onlinebookshop.Services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/order")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String> newBooking(@RequestHeader(name = "Authorization") String token, @RequestParam long bookId){
        String email = jwtService.extractUsername(token.substring(7));
        return bookingService.newBooking(email, bookId);
    }

    @GetMapping("/myBooking")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<Booking>>findMyBooking(@RequestHeader(name = "Authorization") String token){
        String email = jwtService.extractUsername(token.substring(7));
        return bookingService.findMyBooking(email);
    }

    @PutMapping("/admin/updateStatus/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Booking>updateBookingStatus(@PathVariable long bookingId,@RequestParam BookingStatus newStatus){
        return bookingService.updateBookingStatus(bookingId, newStatus);
    }

    @PutMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String>cancelBooking(@RequestHeader(name = "Authorization") String token,@PathVariable long bookingId){
        String email = jwtService.extractUsername(token.substring(7));
        return bookingService.cancelBooking(email, bookingId);
    }

    @GetMapping("/admin/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Booking>findBookingById(@PathVariable long bookingId){
        return bookingService.findBooking(bookingId);
    }


//    @PostMapping("/newBookings")
//    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
//    public ResponseEntity<String>createBooking(@RequestHeader(name = "Authorization") String token,@RequestParam List<Long> Ids){
//        String email = jwtService.extractUsername(token.substring(7));
//        return bookingService.createBooking(email,Ids);
//    }

}
