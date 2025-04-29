package com.example.aniting.admin.category;

import java.util.List;

import com.example.aniting.entity.Category;

public interface AdminCategoryService {

	public List<Category> getCategoryList();
	public Category addCategory(Category category);
	public Category updateCategory(Long id, Category category);
	public void deleteCategory(Long id);
	public Category getCategory(Long id);

}
