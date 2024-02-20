package com.fs.onlinebookshop.Services;

import com.fs.onlinebookshop.Entity.Book;
import com.fs.onlinebookshop.Entity.Category;
import com.fs.onlinebookshop.Exception.CategoryNotFoundException;
import com.fs.onlinebookshop.Repository.BookRepository;
import com.fs.onlinebookshop.Repository.BookingRepository;
import com.fs.onlinebookshop.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BookRepository bookRepository;

    public ResponseEntity<String> addCategory(Category category){
        categoryRepository.save(category);
        return new ResponseEntity<>("Category Successfully Added!", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Category>>findAllCategory(){
        List<Category>categories=categoryRepository.findAll();
        if (categories.isEmpty()){
            throw new CategoryNotFoundException("No categories found");
        }
        return new ResponseEntity<>(categories,HttpStatus.FOUND);
    }

    public ResponseEntity<Category>findById(long categoryId){
        Category category =categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category Not Found with ID: "+categoryId));
        return new ResponseEntity<>(category,HttpStatus.FOUND);
    }

    public ResponseEntity<String>deleteById(long categoryId){
        Category category=categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        categoryRepository.delete(category);
//        bookRepository.deleteAll(category.getBooks());
        return new ResponseEntity<>("Category Deleted Successfully ID: "+categoryId,HttpStatus.OK);
    }

    public ResponseEntity<Category>updatedCategory(Category category,long categoryId){
        Optional<Category> optionalCategory=categoryRepository.findById(categoryId);
        if(optionalCategory.isEmpty()) {
            throw new CategoryNotFoundException("Category Not Found with ID: "+categoryId);
        }
        Category updateCategory=optionalCategory.get();

        if(category.getCategoryName()!=null) {
            updateCategory.setCategoryName(category.getCategoryName());
        }
        if(category.getDescription()!=null) {
            updateCategory.setDescription(category.getDescription());
        }
        Category updated=categoryRepository.save(updateCategory);
        return new ResponseEntity<>(updated,HttpStatus.OK);
    }
}
