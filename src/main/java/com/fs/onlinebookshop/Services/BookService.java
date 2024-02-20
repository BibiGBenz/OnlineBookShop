package com.fs.onlinebookshop.Services;

import com.fs.onlinebookshop.Entity.Book;
import com.fs.onlinebookshop.Entity.Category;
import com.fs.onlinebookshop.Exception.BookNotFoundException;
import com.fs.onlinebookshop.Exception.CategoryNotFoundException;
import com.fs.onlinebookshop.Repository.BookRepository;
import com.fs.onlinebookshop.Repository.CategoryRepository;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<String> addBook(Book book,long categoryId,MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new FileNotFoundException("Image not found");
        }
        if (!(file.getContentType().equalsIgnoreCase("image/jpeg")
                ||file.getContentType().equalsIgnoreCase("image/jpg") ||file.getContentType().equalsIgnoreCase("image/png"))){
            throw new InvalidContentTypeException("Not an image format");
        }

        Category category=categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        byte[] image=file.getBytes();
        book.setBookImage(image);
        book.setCategory(category);

        bookRepository.save(book);
        return new ResponseEntity<>("Book added successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Book>>findAllBooks(){
        List<Book>books=bookRepository.findAll();
        if(books.isEmpty()) {
            throw new BookNotFoundException("No Books Found");
        }
        return new ResponseEntity<>(books,HttpStatus.FOUND);
    }

    public ResponseEntity<Book>findById(long bookId){
        Book book=bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));
        return new ResponseEntity<>(book,HttpStatus.FOUND);
    }

    public ResponseEntity<String>deleteById(long bookId){
        Book book=bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));
        bookRepository.delete(book);
        return new ResponseEntity<>("Booked deleted successfully",HttpStatus.OK);
    }

    public ResponseEntity<Book>updateBook(Book book,MultipartFile file, long bookId) throws IOException {
        Book updateBook=bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));

        if(book.getBookName()!=null) {
            updateBook.setBookName(book.getBookName());
        }
        if(book.getAuthor()!=null) {
            updateBook.setAuthor(book.getAuthor());
        }
        if(book.getCategory()!=null) {
            updateBook.setCategory(book.getCategory());
        }
        if(book.getIsbn()!=null) {
            updateBook.setIsbn(book.getIsbn());
        }
        if(book.getPrice()!=0) {
            updateBook.setPrice(book.getPrice());
        }
        if(book.getYearPublished()!=0) {
            updateBook.setYearPublished(book.getYearPublished());
        }
        if (file.isEmpty()) {
            throw new FileNotFoundException("File is Empty");
        }
        if (!(file.getContentType().equalsIgnoreCase("image/jpeg")
                ||file.getContentType().equalsIgnoreCase("image/jpg") ||file.getContentType().equalsIgnoreCase("image/png"))){
            throw new InvalidContentTypeException("Not an image format");
        }
        updateBook.setBookImage(file.getBytes());

        Book update=bookRepository.save(updateBook);
        return new ResponseEntity<>(update,HttpStatus.OK);
    }

    public ResponseEntity<String> changePrice(long bookId,double newPrice){
        Book book=bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found with id "+bookId));
        book.setPrice(newPrice);
        bookRepository.save(book);
        return new ResponseEntity<>("Price changed successfully",HttpStatus.OK);
    }


}
