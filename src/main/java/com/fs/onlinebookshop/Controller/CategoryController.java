package com.fs.onlinebookshop.Controller;

import com.fs.onlinebookshop.Entity.Category;
import com.fs.onlinebookshop.Services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/admin/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<Category>> findAllCategory(){
        return categoryService.findAllCategory();
    }

    @GetMapping("/find/{categoryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Category>findCategoryById(@PathVariable long categoryId){
        return categoryService.findById(categoryId);
    }

    @DeleteMapping("/admin/delete/{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String>deleteById(@PathVariable long categoryId){
        return categoryService.deleteById(categoryId);
    }

    @PutMapping("/admin/update/{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Category>updateCategory(@RequestBody Category category,@PathVariable long categoryId){
        return categoryService.updatedCategory(category, categoryId);
    }
}
