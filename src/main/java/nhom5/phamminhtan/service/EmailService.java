package nhom5.phamminhtan.service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nhom5.phamminhtan.model.Order;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    
    /**
     * Gửi email thông báo cập nhật trạng thái đơn hàng
     * 
     * @param order Đơn hàng cần gửi thông báo
     */
    @Async
    public void sendOrderStatusUpdateEmail(Order order) {
        try {
            String recipientEmail = order.getUser().getEmail();
            String subject = "Cập nhật đơn hàng #" + order.getId() + " - " + getStatusDisplayName(order.getStatus());
            
            // Chuẩn bị dữ liệu cho template
            Map<String, Object> variables = new HashMap<>();
            variables.put("order", order);
            variables.put("customerName", order.getUser().getFullName());
            variables.put("statusDisplayName", getStatusDisplayName(order.getStatus()));
            variables.put("statusDescription", getStatusDescription(order.getStatus()));
            variables.put("paymentStatusDisplayName", getPaymentStatusDisplayName(order.getPaymentStatus()));
            variables.put("paymentMethodDisplayName", getPaymentMethodDisplayName(order.getPaymentMethod()));
            
            String htmlContent = generateHtmlContent("email/order-status-update", variables);
            
            sendHtmlEmail(recipientEmail, subject, htmlContent);
            
            log.info("Đã gửi email cập nhật đơn hàng #{} đến {}", order.getId(), recipientEmail);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email cập nhật đơn hàng #{}: {}", order.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Gửi email HTML
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, 
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                StandardCharsets.UTF_8.name());
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("noreply@bookstore.com");
        
        mailSender.send(message);
    }
    
    /**
     * Tạo nội dung HTML từ template Thymeleaf
     */
    private String generateHtmlContent(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
    
    /**
     * Lấy tên hiển thị của trạng thái đơn hàng
     */
    private String getStatusDisplayName(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case PREPARING -> "Đang chuẩn bị";
            case SHIPPING -> "Đang giao hàng";
            case COMPLETED -> "Hoàn thành";
            case CANCELLED -> "Đã hủy";
        };
    }
    
    /**
     * Lấy mô tả chi tiết của trạng thái đơn hàng
     */
    private String getStatusDescription(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "Đơn hàng của bạn đang chờ được xác nhận. Chúng tôi sẽ xác nhận trong thời gian sớm nhất.";
            case CONFIRMED -> "Đơn hàng của bạn đã được xác nhận và đang được chuẩn bị.";
            case PREPARING -> "Chúng tôi đang chuẩn bị đơn hàng của bạn. Sản phẩm sẽ sớm được gửi đi.";
            case SHIPPING -> "Đơn hàng của bạn đang trên đường giao đến địa chỉ nhận hàng. Vui lòng chú ý điện thoại.";
            case COMPLETED -> "Đơn hàng đã được giao thành công. Cảm ơn bạn đã mua hàng!";
            case CANCELLED -> "Đơn hàng của bạn đã bị hủy. Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.";
        };
    }
    
    /**
     * Lấy tên hiển thị của trạng thái thanh toán
     */
    private String getPaymentStatusDisplayName(Order.PaymentStatus status) {
        return switch (status) {
            case UNPAID -> "Chưa thanh toán";
            case PAID -> "Đã thanh toán";
            case REFUNDED -> "Đã hoàn tiền";
        };
    }
    
    /**
     * Lấy tên hiển thị của phương thức thanh toán
     */
    private String getPaymentMethodDisplayName(Order.PaymentMethod method) {
        return switch (method) {
            case BANK_TRANSFER -> "Chuyển khoản ngân hàng";
            case COD -> "Thanh toán khi nhận hàng (COD)";
        };
    }
}
