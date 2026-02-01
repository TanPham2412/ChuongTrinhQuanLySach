package nhom5.phamminhtan.controller;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Book;
import nhom5.phamminhtan.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final BookService bookService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Lấy 6 sách nổi bật để hiển thị trên trang chủ
        List<Book> featuredBooks = bookService.getAllBooks();
        
        // Giới hạn chỉ hiển thị 6 sách đầu tiên
        if (featuredBooks.size() > 6) {
            featuredBooks = featuredBooks.subList(0, 6);
        }
        
        model.addAttribute("featuredBooks", featuredBooks);
        return "home";
    }
}
