package com.fs.onlinebookshop.Services;

import com.fs.onlinebookshop.Entity.Booking;
import com.fs.onlinebookshop.Entity.Payment;
import com.fs.onlinebookshop.Entity.PaymentStatus;
import com.fs.onlinebookshop.Entity.User;
import com.fs.onlinebookshop.Exception.*;
import com.fs.onlinebookshop.Repository.BookingRepository;
import com.fs.onlinebookshop.Repository.PaymentRepository;
import com.fs.onlinebookshop.Repository.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    public byte[] generateInvoice(String email, long bookingId) throws DocumentException {
        try {
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document,outputStream);
            document.open();
            document.add(new Paragraph(generateInvoiceContext(email,bookingId)));
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
    private String generateInvoiceContext(String email,long bookingId) {

        User user = userRepository.findByEmail(email).get();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("No booking found"));

        Payment payment = paymentRepository.findByBooking(booking).orElseThrow(() -> new PaymentNotFoundException("No Payments found"));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new UnableToGenerateInvoice("Unable to generate invoice for the booking");
        }

        if (user.getId()!=booking.getUser().getId()){
            throw new UserNotFoundException("Unauthorized User");
        }

        StringBuilder invoiceContext = new StringBuilder();
        invoiceContext.append("_______________________________Invoice Details___________________________\n");
        invoiceContext.append("Booking Id      :").append(bookingId).append("\n");
        invoiceContext.append("Name            :").append(user.getUserName()).append("\n");
        invoiceContext.append("Phone Number    :").append(user.getPhoneNumber()).append("\n");
        invoiceContext.append("Address         :").append(user.getAddress()).append("\n");
        invoiceContext.append("Email           :").append(user.getEmail()).append("\n");
        invoiceContext.append("Book Name       :").append(booking.getBook().getBookName()).append("\n");
        invoiceContext.append("Book Price      :").append(booking.getPrice()).append("\n");
        invoiceContext.append("Transaction Id  : ").append(payment.getTransactionId()).append("\n");
        double gst = booking.getPrice() * 0.12;
        invoiceContext.append("GST             :").append(gst).append("\n");
        double totalAmount = booking.getPrice() + gst;
        invoiceContext.append("Total Amount    :").append(totalAmount).append("\n");
        return invoiceContext.toString();
    }
}
