package nhom5.phamminhtan.controller;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Category;
import nhom5.phamminhtan.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public String listCategories(Model model) {
        List<Category> parentCategories = categoryService.getAllParentCategoriesWithChildren();
        model.addAttribute("categories", parentCategories);
        return "categories/list";
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("parentCategories", categoryService.getAllParentCategories());
        return "categories/add";
    }
    
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        return "redirect:/categories";
    }
    
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryService.getCategoryById(id)
            .orElse(null);
        
        if (category == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục!");
            return "redirect:/categories";
        }
        
        model.addAttribute("category", category);
        model.addAttribute("parentCategories", categoryService.getAllParentCategories());
        return "categories/edit";
    }
    
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, 
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }
    
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục này!");
        }
        return "redirect:/categories";
    }
}
