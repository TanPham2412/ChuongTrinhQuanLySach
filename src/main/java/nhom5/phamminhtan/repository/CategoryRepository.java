package nhom5.phamminhtan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nhom5.phamminhtan.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find all parent categories (categories without parent)
    List<Category> findByParentIsNull();
    
    // Find all subcategories of a parent
    List<Category> findByParentId(Long parentId);
    
    // Find category by name
    Category findByName(String name);
    
    // Find category by name (case-insensitive and trim)
    @Query("SELECT c FROM Category c WHERE TRIM(c.name) = TRIM(:name)")
    Category findByNameTrimmed(String name);
    
    // Get all categories with their children
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findAllParentsWithChildren();
}
