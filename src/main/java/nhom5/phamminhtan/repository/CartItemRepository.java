package nhom5.phamminhtan.repository;

import nhom5.phamminhtan.model.CartItemEntity;
import nhom5.phamminhtan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    
    List<CartItemEntity> findByUser(User user);
    
    Optional<CartItemEntity> findByUserAndBookId(User user, Long bookId);
    
    void deleteByUser(User user);
}
