package nhom5.phamminhtan.controller;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Book;
import nhom5.phamminhtan.model.Cart;
import nhom5.phamminhtan.model.Order;
import nhom5.phamminhtan.model.OrderItem;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.service.BookService;
import nhom5.phamminhtan.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final Cart cart;
    private final BookService bookService;
    private final OrderService orderService;
    
    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cart", cart);
        return "cart/view";
    }
    
    @PostMapping("/update/{bookId}")
    public String updateQuantity(@PathVariable Long bookId,
                                @RequestParam int quantity,
                                RedirectAttributes redirectAttributes) {
        if (quantity <= 0) {
            cart.removeItem(bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } else {
            cart.updateQuantity(bookId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật số lượng!");
        }
        return "redirect:/cart";
    }
    
    @GetMapping("/remove/{bookId}")
    public String removeFromCart(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        cart.removeItem(bookId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/cart";
    }
    
    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        return "cart/checkout";
    }
    
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam String paymentMethod,
                                  @RequestParam String shippingAddress,
                                  @RequestParam String phoneNumber,
                                  @RequestParam(required = false) String note,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống!");
            return "redirect:/cart";
        }
        
        // Tạo order
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotal());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setNote(note);
        order.setPaymentMethod(Order.PaymentMethod.valueOf(paymentMethod));
        
        if ("BANK_TRANSFER".equals(paymentMethod)) {
            order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        } else {
            order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        }
        
        // Tạo order items
        order.setOrderItems(new ArrayList<>());
        cart.getItems().forEach(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(item.getBook());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getBook().getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        });
        
        // Lưu order
        orderService.createOrder(order);
        
        if ("BANK_TRANSFER".equals(paymentMethod)) {
            // Hiển thị trang QR code
            model.addAttribute("order", order);
            model.addAttribute("bankInfo", getBankInfo());
            cart.clear();
            return "cart/payment-qr";
        }
        
        cart.clear();
        redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công! Chúng tôi sẽ liên hệ với bạn sớm.");
        return "redirect:/books";
    }
    
    private BankInfo getBankInfo() {
        BankInfo info = new BankInfo();
        info.setBankId("970422");
        info.setBankName("MB Bank");
        info.setAccountNumber("7053765633");
        info.setAccountName("Pham Minh Tan");
        return info;
    }
    
    public static class BankInfo {
        private String bankId;
        private String bankName;
        private String accountNumber;
        private String accountName;
        
        public String getBankId() { return bankId; }
        public void setBankId(String bankId) { this.bankId = bankId; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
    }
}
