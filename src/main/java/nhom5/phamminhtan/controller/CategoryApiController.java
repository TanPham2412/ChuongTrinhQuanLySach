package nhom5.phamminhtan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.dto.ApiResponse;
import nhom5.phamminhtan.model.Category;
import nhom5.phamminhtan.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<Map<String, Object>> categoryList = categories.stream()
            .map(this::convertToMap)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryList));
    }
    
    @GetMapping("/names")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategoryNames() {
        List<Category> categories = categoryService.getAllCategories();
        List<String> categoryNames = categories.stream()
            .map(Category::getName)
            .sorted()
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryNames));
    }
    
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategoryTree() {
        List<Category> parentCategories = categoryService.getAllParentCategoriesWithChildren();
        List<Map<String, Object>> categoryTree = parentCategories.stream()
            .map(this::convertToTreeMap)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", categoryTree));
    }
    
    private Map<String, Object> convertToMap(Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("description", category.getDescription());
        map.put("fullPath", category.getFullPath());
        map.put("isParent", category.isParent());
        if (category.getParent() != null) {
            map.put("parentName", category.getParent().getName());
        }
        return map;
    }
    
    private Map<String, Object> convertToTreeMap(Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("description", category.getDescription());
        
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<Map<String, Object>> children = category.getChildren().stream()
                .map(child -> {
                    Map<String, Object> childMap = new HashMap<>();
                    childMap.put("id", child.getId());
                    childMap.put("name", child.getName());
                    childMap.put("description", child.getDescription());
                    childMap.put("fullPath", child.getFullPath());
                    return childMap;
                })
                .collect(Collectors.toList());
            map.put("children", children);
        }
        
        return map;
    }
}
