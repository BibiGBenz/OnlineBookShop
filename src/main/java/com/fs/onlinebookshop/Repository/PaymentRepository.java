package com.fs.onlinebookshop.Repository;

import com.fs.onlinebookshop.Entity.Booking;
import com.fs.onlinebookshop.Entity.Payment;
import com.fs.onlinebookshop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//
@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByBooking(Booking booking);

    List<Payment> findByUser(User user);
}
