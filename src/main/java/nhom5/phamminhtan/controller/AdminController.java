package nhom5.phamminhtan.controller;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.service.BookService;
import nhom5.phamminhtan.service.UserService;
import nhom5.phamminhtan.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final BookService bookService;
    private final UserService userService;
    private final OrderService orderService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Thống kê tổng quan
        long totalBooks = bookService.getAllBooks().size();
        long totalUsers = userService.findAll().size();
        long totalOrders = orderService.getTotalOrders();
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        
        // Danh sách đơn hàng gần đây
        model.addAttribute("recentOrders", orderService.getRecentOrders(10));
        
        return "admin/dashboard";
    }
}
