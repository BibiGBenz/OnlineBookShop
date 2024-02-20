package com.fs.onlinebookshop.Repository;

import com.fs.onlinebookshop.Entity.Booking;
import com.fs.onlinebookshop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findByUser(User user);
}
