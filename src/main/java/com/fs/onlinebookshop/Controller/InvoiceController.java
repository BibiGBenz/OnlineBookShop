package com.fs.onlinebookshop.Controller;

import com.fs.onlinebookshop.Services.InvoiceService;
import com.fs.onlinebookshop.Services.JwtService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private InvoiceService invoiceService;
    @GetMapping("/download/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<byte[]> downloadInvoice(@RequestHeader(name = "Authorization") String token, @PathVariable long bookingId) throws DocumentException {
        String email = jwtService.extractUsername(token.substring(7));
        byte[] invoiceContext = invoiceService.generateInvoice(email, bookingId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentDispositionFormData("attachment", "invoice.pdf");
        return new ResponseEntity<>(invoiceContext,httpHeaders, HttpStatus.OK);
    }
}
