# Hệ Thống Quản Lý Sách - Book Management System

## Mô tả dự án
Ứng dụng web quản lý sách được xây dựng bằng Spring Boot, Thymeleaf, MySQL với các chức năng:
- Quản lý sách (CRUD)
- Tìm kiếm sách
- Giỏ hàng
- Xác thực người dùng (OAuth2 + Local)
- Phân quyền ADMIN/USER
- REST API với JWT

## Công nghệ sử dụng
- **Backend**: Spring Boot 4.0.2, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome
- **Database**: MySQL 8.0
- **Authentication**: OAuth2 (Google, Facebook), JWT
- **Build Tool**: Maven

## Cài đặt và chạy

### 1. Yêu cầu hệ thống
- Java 21 hoặc cao hơn
- Maven 3.6+
- MySQL 8.0+
- IDE: IntelliJ IDEA / Eclipse / VS Code

### 2. Cấu hình Database

#### Bước 1: Khởi động MySQL (XAMPP/WAMP)
- Mở XAMPP Control Panel
- Start Apache và MySQL
- Mở phpMyAdmin: http://localhost/phpmyadmin

#### Bước 2: Tạo database
Database sẽ tự động được tạo khi chạy ứng dụng, hoặc có thể tạo thủ công:

```sql
CREATE DATABASE bookstore_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore_db;
```

#### Bước 3: Cấu hình trong application.properties
File đã được cấu hình sẵn tại: `src/main/resources/application.properties`

**Lưu ý**: Thay đổi `spring.datasource.password` nếu MySQL của bạn có password:
```properties
spring.datasource.password=your_password_here
```

### 3. Cài đặt OAuth2 (Tùy chọn)

#### Google OAuth2:
1. Truy cập: https://console.cloud.google.com/
2. Tạo project mới
3. Vào "APIs & Services" > "Credentials"
4. Tạo "OAuth 2.0 Client IDs"
5. Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
6. Copy Client ID và Client Secret vào `application.properties`

#### Facebook OAuth2:
1. Truy cập: https://developers.facebook.com/
2. Tạo app mới
3. Thêm "Facebook Login"
4. Valid OAuth Redirect URIs: `http://localhost:8080/login/oauth2/code/facebook`
5. Copy App ID và App Secret vào `application.properties`

### 4. Build và chạy ứng dụng

#### Sử dụng Maven:
```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

#### Sử dụng IDE:
- Mở project trong IDE
- Chạy class `PhamminhtanApplication.java`

### 5. Truy cập ứng dụng
- **URL**: http://localhost:8080
- **Trang chủ**: http://localhost:8080/books
- **Đăng nhập**: http://localhost:8080/login
- **Đăng ký**: http://localhost:8080/register

## Tài khoản mặc định

### Tạo tài khoản ADMIN:
Sau khi chạy ứng dụng lần đầu, thực hiện SQL sau trong phpMyAdmin:

```sql
-- Tạo user admin
INSERT INTO users (username, password, email, full_name, enabled, provider) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'admin@bookstore.com', 'Administrator', 1, 'LOCAL');

-- Lấy ID của user vừa tạo
SET @admin_id = LAST_INSERT_ID();

-- Gán role ADMIN
INSERT INTO user_roles (user_id, role_id) 
VALUES (@admin_id, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));
```

**Thông tin đăng nhập:**
- Username: `admin`
- Password: `admin123`

### Tạo tài khoản USER thông thường:
- Truy cập http://localhost:8080/register
- Điền form đăng ký
- User mới sẽ tự động có role USER

## Chức năng của hệ thống

### 1. Quản lý Sách (ADMIN)
- **Thêm sách**: `/books/add` (Chỉ ADMIN)
- **Sửa sách**: `/books/edit/{id}` (Chỉ ADMIN)
- **Xóa sách**: `/books/delete/{id}` (Chỉ ADMIN)
- **Xem danh sách**: `/books` (Tất cả user)

### 2. Tìm kiếm
- Tìm theo tên sách, tác giả, thể loại
- URL: `/books?keyword=...`

### 3. Giỏ hàng
- Thêm sách vào giỏ
- Cập nhật số lượng
- Xóa sản phẩm
- Thanh toán
- URL: `/cart`

### 4. Xác thực & Phân quyền
- **Đăng ký**: `/register`
- **Đăng nhập**: `/login`
- **OAuth2**: Google, Facebook
- **Phân quyền**:
  - ADMIN: Toàn quyền
  - USER: Xem, mua sách

### 5. REST API
Base URL: `http://localhost:8080/api/books`

#### Endpoints:
```
GET    /api/books           - Lấy tất cả sách
GET    /api/books/{id}      - Lấy sách theo ID
GET    /api/books/search?keyword=...  - Tìm kiếm
POST   /api/books           - Thêm sách (ADMIN)
PUT    /api/books/{id}      - Cập nhật sách (ADMIN)
DELETE /api/books/{id}      - Xóa sách (ADMIN)
```

#### Test API với curl:
```bash
# Lấy tất cả sách
curl http://localhost:8080/api/books

# Tìm kiếm
curl "http://localhost:8080/api/books/search?keyword=java"

# Thêm sách (cần authentication)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{"title":"Java Programming","author":"John Doe","price":250000,"category":"Programming","quantity":10}'
```

## Cấu trúc dự án

```
phamminhtan/
├── src/
│   ├── main/
│   │   ├── java/nhom5/phamminhtan/
│   │   │   ├── config/              # Security, DataInitializer
│   │   │   ├── controller/          # BookController, CartController, AuthController, ApiController
│   │   │   ├── model/               # Book, User, Role, Cart
│   │   │   ├── repository/          # BookRepository, UserRepository, RoleRepository
│   │   │   ├── service/             # BookService, UserService, CustomUserDetailsService
│   │   │   ├── dto/                 # BookDTO, ApiResponse
│   │   │   └── PhamminhtanApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── layout.html
│   │       │   ├── books/           # list.html, add.html, edit.html
│   │       │   ├── cart/            # view.html
│   │       │   └── auth/            # login.html, register.html
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Dữ liệu mẫu

### Thêm sách mẫu:
```sql
INSERT INTO books (title, author, price, category, description, quantity) VALUES
('Lập Trình Java', 'Nguyễn Văn A', 250000, 'Programming', 'Sách học Java cơ bản', 50),
('Spring Boot in Action', 'Craig Walls', 450000, 'Programming', 'Spring Boot tutorial', 30),
('Clean Code', 'Robert Martin', 350000, 'Programming', 'Viết code sạch', 40),
('Harry Potter', 'J.K. Rowling', 180000, 'Fiction', 'Truyện phiêu lưu kỳ ảo', 100),
('Đắc Nhân Tâm', 'Dale Carnegie', 120000, 'Self-help', 'Kỹ năng giao tiếp', 80);
```

## Troubleshooting

### Lỗi kết nối Database:
```
Error: Could not create connection to database server
```
**Giải pháp:**
- Kiểm tra MySQL đã chạy chưa (XAMPP)
- Kiểm tra username/password trong application.properties
- Tạo database thủ công nếu chưa có

### Lỗi port đã được sử dụng:
```
Port 8080 is already in use
```
**Giải pháp:**
- Thay đổi port trong application.properties: `server.port=8081`
- Hoặc tắt ứng dụng đang chạy trên port 8080

### Lỗi Thymeleaf layout:
Nếu layout không hoạt động, cài thêm dependency:
```xml
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
</dependency>
```

## Git và GitHub

### Khởi tạo Git:
```bash
git init
git add .
git commit -m "Initial commit - Book Management System"
```

### Push lên GitHub:
```bash
git remote add origin https://github.com/username/phamminhtan.git
git branch -M main
git push -u origin main
```

### File .gitignore:
```
target/
!.mvn/wrapper/maven-wrapper.jar
.mvn/
mvnw
mvnw.cmd

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/

### VS Code ###
.vscode/

### Application Properties (nếu có thông tin nhạy cảm) ###
# application-prod.properties
```

## Liên hệ
- **Tác giả**: Nhóm 5 - Phạm Minh Tân
- **Email**: phamminhtan@example.com
- **GitHub**: https://github.com/username/phamminhtan

## License
Dự án này được phát triển cho mục đích học tập.
