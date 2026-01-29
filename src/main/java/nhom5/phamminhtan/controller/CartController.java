package nhom5.phamminhtan.controller;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Cart;
import nhom5.phamminhtan.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final Cart cart;
    private final BookService bookService;
    
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
    
    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng trống!");
            return "redirect:/cart";
        }
        
        // Xử lý thanh toán ở đây
        cart.clear();
        redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công!");
        return "redirect:/books";
    }
}
