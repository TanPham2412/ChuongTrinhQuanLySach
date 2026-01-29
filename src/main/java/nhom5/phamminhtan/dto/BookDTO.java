package nhom5.phamminhtan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String category;
    private String description;
    private Integer quantity;
    private String imageUrl;
}
