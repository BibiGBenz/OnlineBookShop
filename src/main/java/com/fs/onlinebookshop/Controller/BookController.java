package com.fs.onlinebookshop.Controller;

import com.fs.onlinebookshop.Entity.Book;
import com.fs.onlinebookshop.Services.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/admin/add/{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addBook(@Valid @ModelAttribute Book book, @PathVariable long categoryId , @RequestParam("image") MultipartFile file) throws IOException {
        return bookService.addBook(book,categoryId,file);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<Book>>findAllBooks(){
        return bookService.findAllBooks();
    }

    @GetMapping("/find/{bookId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Book>findById(@PathVariable long bookId){
        return bookService.findById(bookId);
    }

    @DeleteMapping("/admin/delete/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>deleteBook(@PathVariable long bookId){
        return bookService.deleteById(bookId);
    }

    @PutMapping("/admin/update/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Book>updateBook(@ModelAttribute Book book,@RequestParam("image") MultipartFile file,@PathVariable long bookId) throws IOException {
        return bookService.updateBook(book,file,bookId);
    }

    @PutMapping("/admin/change/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>changePrice(@PathVariable long bookId,@RequestParam double newPrice){
        return bookService.changePrice(bookId, newPrice);
    }

}
