package nhom5.phamminhtan.config;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.Category;
import nhom5.phamminhtan.model.Role;
import nhom5.phamminhtan.repository.CategoryRepository;
import nhom5.phamminhtan.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.findByName(Role.RoleName.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(Role.RoleName.ROLE_USER);
            roleRepository.save(userRole);
        }
        
        if (roleRepository.findByName(Role.RoleName.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(Role.RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
        
        // Initialize categories if they don't exist
        if (categoryRepository.count() == 0) {
            initializeCategories();
        }
    }
    
    private void initializeCategories() {
        // 1. Văn học & Hư cấu (Fiction)
        Category fiction = createCategory("Văn học & Hư cấu", 
            "Các thể loại sách giải trí phổ biến nhất", null);
        
        createCategory("Tiểu thuyết", "Văn học kinh điển, tiểu thuyết hiện đại", fiction);
        createCategory("Lãng mạn / Ngôn tình", "Tình cảm lứa đôi", fiction);
        createCategory("Trinh thám / Hình sự", "Điều tra phá án, tội phạm", fiction);
        createCategory("Kinh dị / Giật gân", "Ma quái, tâm lý ly kỳ", fiction);
        createCategory("Khoa học viễn tưởng", "Tương lai, vũ trụ, công nghệ cao", fiction);
        createCategory("Giả tưởng", "Phép thuật, thế giới huyền bí (Harry Potter, Chúa nhẫn)", fiction);
        createCategory("Kiếm hiệp / Tiên hiệp", "Võ thuật, tu tiên (đặc biệt phổ biến ở VN)", fiction);
        createCategory("Tiểu thuyết lịch sử", "Dựa trên bối cảnh lịch sử có thật", fiction);
        createCategory("Thơ & Ca dao", "Tuyển tập thơ", fiction);
        createCategory("Truyện ngắn / Tản văn", "Các câu chuyện nhỏ, chiêm nghiệm cuộc sống", fiction);
        
        // 2. Phi hư cấu & Kiến thức (Non-Fiction)
        Category nonFiction = createCategory("Phi hư cấu & Kiến thức", 
            "Sách dựa trên sự thật, dùng để học tập và tra cứu", null);
        
        createCategory("Kinh tế / Quản trị kinh doanh", "Marketing, đầu tư, khởi nghiệp, lãnh đạo", nonFiction);
        createCategory("Phát triển bản thân", "Kỹ năng sống, tư duy tích cực, chữa lành", nonFiction);
        createCategory("Tiểu sử / Hồi ký", "Câu chuyện về danh nhân, người nổi tiếng", nonFiction);
        createCategory("Tâm lý học", "Phân tích hành vi, tâm lý con người", nonFiction);
        createCategory("Lịch sử / Chính trị", "Sự kiện lịch sử, thể chế, ngoại giao", nonFiction);
        createCategory("Triết học / Tôn giáo", "Tư tưởng, Phật giáo, Thiên chúa giáo", nonFiction);
        createCategory("Văn hóa / Du lịch", "Phong tục tập quán, ký sự du lịch", nonFiction);
        createCategory("Nấu ăn / Ẩm thực", "Công thức nấu ăn", nonFiction);
        
        // 3. Sách Chuyên ngành & Giáo dục
        Category education = createCategory("Sách Chuyên ngành & Giáo dục", 
            "Sách học tập và chuyên môn", null);
        
        createCategory("Sách giáo khoa", "Từ lớp 1 đến lớp 12", education);
        createCategory("Giáo trình Đại học", "Chuyên sâu cho sinh viên", education);
        createCategory("Học ngoại ngữ", "Tiếng Anh, Nhật, Hàn, Trung (Sách luyện thi IELTS, TOEIC)", education);
        createCategory("Công nghệ thông tin", "Lập trình (Java, Python...), phần cứng, mạng", education);
        createCategory("Y học / Sức khỏe", "Dinh dưỡng, bệnh lý, y khoa", education);
        createCategory("Khoa học kỹ thuật", "Vật lý, hóa học, xây dựng, kiến trúc", education);
        
        // 4. Thiếu nhi & Truyện tranh
        Category kids = createCategory("Thiếu nhi & Truyện tranh", 
            "Sách dành cho trẻ em và thanh thiếu niên", null);
        
        createCategory("Truyện tranh", "Manga Nhật Bản, Comics phương Tây", kids);
        createCategory("Light Novel", "Tiểu thuyết nhẹ (kèm tranh minh họa)", kids);
        createCategory("Sách tranh", "Cho trẻ mầm non", kids);
        createCategory("Vừa học vừa chơi", "Sách tô màu, dán hình, đố vui", kids);
        
        System.out.println("✅ Đã khởi tạo thành công " + categoryRepository.count() + " danh mục sách!");
    }
    
    private Category createCategory(String name, String description, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        return categoryRepository.save(category);
    }
}
