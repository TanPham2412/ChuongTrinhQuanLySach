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
    private final EmailService emailService;
    
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
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr);
        order.setStatus(status);
        
        // Tự động cập nhật trạng thái thanh toán
        updatePaymentStatusBasedOnOrderStatus(order, status);
        
        Order savedOrder = orderRepository.save(order);
        
        // Gửi email thông báo
        emailService.sendOrderStatusUpdateEmail(savedOrder);
        
        return savedOrder;
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        
        // Tự động cập nhật trạng thái thanh toán
        updatePaymentStatusBasedOnOrderStatus(order, status);
        
        Order savedOrder = orderRepository.save(order);
        
        // Gửi email thông báo
        emailService.sendOrderStatusUpdateEmail(savedOrder);
        
        return savedOrder;
    }
    
    /**
     * Tự động cập nhật trạng thái thanh toán dựa trên phương thức thanh toán và trạng thái đơn hàng
     * - Chuyển khoản: Khi đơn hàng được xác nhận (CONFIRMED) -> Đã thanh toán (PAID)
     * - COD: Khi đơn hàng hoàn thành (COMPLETED) -> Đã thanh toán (PAID)
     */
    private void updatePaymentStatusBasedOnOrderStatus(Order order, Order.OrderStatus newStatus) {
        if (order.getPaymentMethod() == Order.PaymentMethod.BANK_TRANSFER 
                && newStatus == Order.OrderStatus.CONFIRMED
                && order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            // Chuyển khoản: xác nhận đơn = đã thanh toán
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        } else if (order.getPaymentMethod() == Order.PaymentMethod.COD 
                && newStatus == Order.OrderStatus.COMPLETED
                && order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            // COD: hoàn thành đơn = đã thanh toán
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }
    }
    
    @Transactional
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }
}
