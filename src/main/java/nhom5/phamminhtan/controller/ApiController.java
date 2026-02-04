package nhom5.phamminhtan.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.dto.ApiResponse;
import nhom5.phamminhtan.dto.BookDTO;
import nhom5.phamminhtan.model.Book;
import nhom5.phamminhtan.model.Category;
import nhom5.phamminhtan.service.BookService;
import nhom5.phamminhtan.service.CategoryService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class ApiController {
    
    private final BookService bookService;
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", books));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
            .map(book -> ResponseEntity.ok(new ApiResponse<>(true, "Success", convertToDTO(book))))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Book not found", null)));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchBooks(@RequestParam String keyword) {
        List<BookDTO> books = bookService.searchBooks(keyword).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", books));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@RequestBody BookDTO bookDTO) {
        try {
            Book book = convertToEntity(bookDTO);
            Book savedBook = bookService.saveBook(book);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Book created successfully", convertToDTO(savedBook)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        try {
            Book book = convertToEntity(bookDTO);
            Book updatedBook = bookService.updateBook(id, book);
            return ResponseEntity.ok(new ApiResponse<>(true, "Book updated successfully", convertToDTO(updatedBook)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Book deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setCategory(book.getCategory() != null ? book.getCategory().getFullPath() : null);
        dto.setDescription(book.getDescription());
        dto.setQuantity(book.getQuantity());
        dto.setImageUrl(book.getImageUrl());
        return dto;
    }
    
    private Book convertToEntity(BookDTO dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        
        // Validate category
        if (dto.getCategory() != null) {
            String categoryName = dto.getCategory().trim();
            System.out.println("🔍 Searching for category: '" + categoryName + "' (length: " + categoryName.length() + ")");
            
            Category category = categoryService.getCategoryByName(categoryName);
            
            if (category == null) {
                // List all available categories for debugging
                System.out.println("❌ Category not found. Available categories:");
                categoryService.getAllCategories().forEach(c -> 
                    System.out.println("  - '" + c.getName() + "' (length: " + c.getName().length() + ")")
                );
                throw new RuntimeException("Không tìm thấy thể loại: '" + categoryName + "'. Vui lòng kiểm tra tên thể loại trong database.");
            }
            
            System.out.println("✅ Found category: " + category.getId() + " - " + category.getName());
            book.setCategory(category);
        } else {
            throw new RuntimeException("Thể loại không được để trống");
        }
        
        book.setDescription(dto.getDescription());
        book.setQuantity(dto.getQuantity());
        book.setImageUrl(dto.getImageUrl());
        return book;
    }
}
