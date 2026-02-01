package nhom5.phamminhtan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFactorRequest {
    
    @NotNull(message = "Mã xác thực không được để trống")
    private Integer code;
    
    private String secret;
}
