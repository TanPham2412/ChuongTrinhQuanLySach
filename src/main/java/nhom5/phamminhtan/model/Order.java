package nhom5.phamminhtan.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(length = 500)
    private String shippingAddress;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 1000)
    private String note;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    public enum OrderStatus {
        PENDING,        // Chờ xác nhận
        CONFIRMED,      // Đã xác nhận
        SHIPPING,       // Đang giao hàng
        COMPLETED,      // Hoàn thành
        CANCELLED       // Đã hủy
    }
    
    public enum PaymentMethod {
        BANK_TRANSFER,  // Chuyển khoản
        COD             // Thanh toán khi nhận hàng
    }
    
    public enum PaymentStatus {
        UNPAID,         // Chưa thanh toán
        PAID,           // Đã thanh toán
        REFUNDED        // Đã hoàn tiền
    }
}
