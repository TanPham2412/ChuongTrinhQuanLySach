package nhom5.phamminhtan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Order;
import nhom5.phamminhtan.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    public long getTotalOrders() {
        return orderRepository.count();
    }
    
    public BigDecimal getTotalRevenue() {
        List<Order> orders = orderRepository.findByPaymentStatus(Order.PaymentStatus.PAID);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"))
        ).getContent();
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
    }
    
    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findByIdWithDetails(id);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, String statusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }
}
