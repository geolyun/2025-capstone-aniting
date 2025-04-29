package com.example.aniting.admin.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.entity.Category;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

	@Autowired
	private AdminCategoryService adminCategoryService;
	
    @GetMapping
    public List<Category> getCategoryList() {
        return adminCategoryService.getCategoryList();
    }
    
    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) {
        return adminCategoryService.getCategory(id);
    }

    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return adminCategoryService.addCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return adminCategoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
    	adminCategoryService.deleteCategory(id);
    }
    
}
