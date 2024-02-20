package com.fs.onlinebookshop.Controller;

import com.fs.onlinebookshop.Entity.Payment;
import com.fs.onlinebookshop.Entity.PaymentStatus;
import com.fs.onlinebookshop.Services.JwtService;
import com.fs.onlinebookshop.Services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/make/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String> makePayment(@RequestHeader(name = "Authorization") String token, @PathVariable long bookingId) {
        String email = jwtService.extractUsername(token.substring(7));
        return paymentService.makePayment(email, bookingId);
    }

    @GetMapping("/admin/all-payments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> allPayments() {
        return paymentService.allPayments();
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> checkPaymentInfo(@RequestHeader(name = "Authorization") String token) {
        String email = jwtService.extractUsername(token.substring(7));
        return paymentService.checkPaymentInfo(email);
    }

    @GetMapping("/admin/checkPayment/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Payment>checkPayments(@PathVariable long bookingId){
        return paymentService.checkPaymentByBookingId(bookingId);
    }
}
