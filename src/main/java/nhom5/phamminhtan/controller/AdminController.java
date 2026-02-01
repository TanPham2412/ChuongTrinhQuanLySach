package nhom5.phamminhtan.controller;

import java.math.BigDecimal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Book;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.service.BookService;
import nhom5.phamminhtan.service.CategoryService;
import nhom5.phamminhtan.service.OrderService;
import nhom5.phamminhtan.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final BookService bookService;
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    
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
    
    // ===== QUẢN LÝ NGƯỜI DÙNG =====
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/user-edit";
    }
    
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                            @RequestParam String fullName,
                            @RequestParam String email,
                            @RequestParam(required = false) Boolean enabled,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                return "redirect:/admin/users";
            }
            
            user.setFullName(fullName);
            user.setEmail(email);
            user.setEnabled(enabled != null && enabled);
            
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    // ===== QUẢN LÝ SÁCH =====
    
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/books";
    }
    
    @GetMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id).orElse(null);
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/admin/books";
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/edit";
    }
    
    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sách thành công!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sách này vì đã có trong đơn hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
    
    // ===== QUẢN LÝ ĐỠN HÀNG =====
    
    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var order = orderService.findById(id).orElse(null);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("order", order);
        return "admin/order-detail";
    }
    
    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id, 
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
