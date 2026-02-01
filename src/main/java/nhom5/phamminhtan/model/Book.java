package nhom5.phamminhtan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tên sách không được để trống")
    @Size(min = 1, max = 200, message = "Tên sách phải từ 1-200 ký tự")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Tác giả không được để trống")
    @Size(min = 1, max = 100, message = "Tên tác giả phải từ 1-100 ký tự")
    @Column(nullable = false)
    private String author;
    
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @Column(nullable = false)
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    @Column(nullable = false)
    private Integer quantity;
    
    private String imageUrl;
}
