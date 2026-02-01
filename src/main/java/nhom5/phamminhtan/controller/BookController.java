package nhom5.phamminhtan.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Book;
import nhom5.phamminhtan.model.Cart;
import nhom5.phamminhtan.service.BookService;
import nhom5.phamminhtan.service.CategoryService;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    private final CategoryService categoryService;
    private final Cart cart;
    
    @GetMapping
    public String listBooks(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Long categoryId,
                           Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(keyword));
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null) {
            model.addAttribute("books", bookService.getBooksByCategory(categoryId));
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("books", bookService.getAllBooks());
        }
        model.addAttribute("cart", cart);
        model.addAttribute("categories", categoryService.getAllParentCategoriesWithChildren());
        model.addAttribute("noGlobalAlerts", true);
        return "books/list";
    }
    
    @GetMapping({"/{id}", "/detail/{id}"})
    public String viewBookDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id)
            .orElse(null);
        
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/books";
        }
        
        model.addAttribute("book", book);
        model.addAttribute("cart", cart);
        return "books/detail";
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/add";
    }
    
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addBook(@Valid @ModelAttribute("book") Book book,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/add";
        }
        
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sách thành công!");
        return "redirect:/books";
    }
    
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id)
            .orElse(null);
        
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/books";
        }
        
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/edit";
    }
    
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateBook(@PathVariable Long id,
                           @Valid @ModelAttribute("book") Book book,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "books/edit";
        }
        
        try {
            bookService.updateBook(id, book);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/books";
    }
    
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    
    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                          @RequestParam(defaultValue = "1") int quantity,
                          RedirectAttributes redirectAttributes,
                          @RequestHeader(value = "referer", required = false) String referer) {
        Book book = bookService.getBookById(id)
            .orElse(null);
        
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/books";
        }
        
        if (book.getQuantity() < quantity) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng không đủ!");
            return "redirect:/books";
        }
        
        cart.addItem(book, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm \"" + book.getTitle() + "\" vào giỏ hàng!");
        if (referer != null && referer.contains("#")) {
            return "redirect:" + referer.substring(referer.indexOf("/books"));
        }
        return "redirect:/books#book-" + book.getId();
    }
}
