package nhom5.phamminhtan.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Order;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.repository.OrderRepository;
import nhom5.phamminhtan.service.OrderService;
import nhom5.phamminhtan.service.UserService;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class UserOrderController {
    
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    
    @GetMapping
    public String listOrders(Principal principal, Model model) {
        String username = getUsername(principal);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        model.addAttribute("orders", orders);
        return "orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        String username = getUsername(principal);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        Order order = orderService.findById(id).orElse(null);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/orders";
        }
        
        // Kiểm tra đơn hàng có thuộc về user hiện tại không
        if (!order.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/orders";
        }
        
        model.addAttribute("order", order);
        return "orders/detail";
    }
    
    private String getUsername(Principal principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            String email = oauth2User.getAttribute("email");
            return email != null ? email : principal.getName();
        }
        return principal.getName();
    }
}
