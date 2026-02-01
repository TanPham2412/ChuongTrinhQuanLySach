package nhom5.phamminhtan.repository;

import nhom5.phamminhtan.model.Order;
import nhom5.phamminhtan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    List<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod);
}
