package com.example.aniting.admin.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.entity.Category;
import com.example.aniting.repository.CategoryRepository;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public List<Category> getCategoryList() {
		return categoryRepository.findAll();
	}
	
	@Override
	public Category addCategory(Category category) {
		return categoryRepository.save(category);
	}

    @Override
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        category.setCategory(updatedCategory.getCategory());
        category.setScoreStandard(updatedCategory.getScoreStandard());
        category.setStandardDescription(updatedCategory.getStandardDescription());

        return categoryRepository.save(category);
    }

	@Override
	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}
	
	@Override
	public Category getCategory(Long id) {
	    return categoryRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
	}

}
