package nhom5.phamminhtan.service;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Category;
import nhom5.phamminhtan.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public List<Category> getAllParentCategories() {
        return categoryRepository.findByParentIsNull();
    }
    
    public List<Category> getAllParentCategoriesWithChildren() {
        return categoryRepository.findAllParentsWithChildren();
    }
    
    public List<Category> getSubcategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }
    
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setParent(categoryDetails.getParent());
        
        return categoryRepository.save(category);
    }
}
