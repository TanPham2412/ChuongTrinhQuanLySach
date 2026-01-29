# HƯỚNG DẪN NHANH - QUICK START GUIDE

## 🚀 Chạy ứng dụng trong 5 phút

### Bước 1: Chuẩn bị môi trường
✅ Java 21 đã cài đặt
✅ XAMPP hoặc MySQL đang chạy
✅ IDE (IntelliJ/Eclipse/VS Code) đã mở project

### Bước 2: Khởi động MySQL
```
1. Mở XAMPP Control Panel
2. Click "Start" cho MySQL
3. Click "Admin" để mở phpMyAdmin
```

### Bước 3: Tạo Database (Tự động hoặc thủ công)

#### Tự động (Khuyến nghị):
- Ứng dụng sẽ tự tạo database khi chạy lần đầu

#### Thủ công:
```sql
CREATE DATABASE bookstore_db;
```

### Bước 4: Cấu hình Database (Nếu cần)
Mở file: `src/main/resources/application.properties`

Nếu MySQL có password, sửa dòng:
```properties
spring.datasource.password=your_password
```

### Bước 5: Chạy ứng dụng

#### Cách 1: Sử dụng Maven (Command Line)
```bash
mvn clean install
mvn spring-boot:run
```

#### Cách 2: Chạy từ IDE
```
1. Mở file: src/main/java/nhom5/phamminhtan/PhamminhtanApplication.java
2. Click chuột phải → Run
```

### Bước 6: Truy cập ứng dụng
```
🌐 Mở trình duyệt: http://localhost:8080
```

---

## 🔐 Tài khoản mặc định

### Tạo tài khoản ADMIN (Lần đầu tiên)

1. Chạy ứng dụng một lần để tạo tables
2. Mở phpMyAdmin
3. Chọn database `bookstore_db`
4. Click tab "SQL"
5. Copy và chạy script sau:

```sql
-- Tạo user admin (password: admin123)
INSERT INTO users (username, password, email, full_name, enabled, provider) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'admin@bookstore.com', 'Administrator', 1, 'LOCAL');

-- Gán role ADMIN
INSERT INTO user_roles (user_id, role_id) 
VALUES (
    (SELECT id FROM users WHERE username = 'admin'), 
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);
```

6. Đăng nhập với:
   - Username: `admin`
   - Password: `admin123`

### Tạo tài khoản USER
```
1. Truy cập: http://localhost:8080/register
2. Điền form đăng ký
3. Click "Đăng Ký"
```

---

## 📚 Thêm dữ liệu sách mẫu

Vào phpMyAdmin → database `bookstore_db` → tab SQL, chạy:

```sql
INSERT INTO books (title, author, price, category, description, quantity) VALUES
('Lập Trình Java', 'Nguyễn Văn A', 250000, 'Programming', 'Sách học Java cơ bản', 50),
('Spring Boot in Action', 'Craig Walls', 450000, 'Programming', 'Spring Boot tutorial', 30),
('Clean Code', 'Robert Martin', 350000, 'Programming', 'Viết code sạch', 40),
('Harry Potter', 'J.K. Rowling', 180000, 'Fiction', 'Truyện phiêu lưu', 100),
('Đắc Nhân Tâm', 'Dale Carnegie', 120000, 'Self-help', 'Kỹ năng giao tiếp', 80);
```

---

## ✨ Chức năng chính

### 1. Xem danh sách sách
```
URL: http://localhost:8080/books
- Hiển thị tất cả sách
- Không cần đăng nhập
```

### 2. Tìm kiếm sách
```
- Nhập từ khóa vào ô tìm kiếm
- Click nút "Tìm"
- Tìm theo: tên sách, tác giả, thể loại
```

### 3. Thêm vào giỏ hàng
```
- Click "Thêm vào giỏ" trên card sách
- Xem giỏ hàng: Click icon giỏ hàng trên menu
```

### 4. Quản lý sách (Chỉ ADMIN)
```
✏️ Thêm sách: http://localhost:8080/books/add
📝 Sửa sách: Click nút "Sửa" trên card sách
🗑️ Xóa sách: Click nút "Xóa" trên card sách
```

### 5. Thanh toán
```
1. Thêm sách vào giỏ
2. Vào giỏ hàng: http://localhost:8080/cart
3. Kiểm tra số lượng
4. Click "Thanh Toán"
```

---

## 🔧 Kiểm tra REST API

### Test bằng trình duyệt:
```
http://localhost:8080/api/books
```

### Test bằng Postman:
```
GET    http://localhost:8080/api/books
GET    http://localhost:8080/api/books/1
GET    http://localhost:8080/api/books/search?keyword=java
POST   http://localhost:8080/api/books
PUT    http://localhost:8080/api/books/1
DELETE http://localhost:8080/api/books/1
```

**Lưu ý**: POST, PUT, DELETE cần authentication
- Authorization: Basic Auth
- Username: admin
- Password: admin123

---

## ❌ Xử lý lỗi thường gặp

### Lỗi 1: Cannot connect to database
```
✅ Kiểm tra MySQL đã chạy chưa (XAMPP)
✅ Kiểm tra port 3306 có bị chiếm không
✅ Kiểm tra username/password trong application.properties
```

### Lỗi 2: Port 8080 already in use
```
✅ Tắt ứng dụng khác đang chạy port 8080
✅ Hoặc đổi port: server.port=8081 trong application.properties
```

### Lỗi 3: Whitelabel Error Page
```
✅ Kiểm tra URL có đúng không
✅ Xóa thư mục target/ và build lại
✅ Restart ứng dụng
```

### Lỗi 4: Template parsing error
```
✅ Kiểm tra file HTML trong templates/
✅ Kiểm tra Thymeleaf syntax
✅ Clear cache và rebuild
```

### Lỗi 5: 403 Forbidden
```
✅ Đăng nhập với tài khoản có quyền
✅ ADMIN mới được thêm/sửa/xóa sách
```

---

## 📱 Demo các chức năng

### Scenario 1: User mua sách
```
1. Truy cập http://localhost:8080
2. Đăng ký tài khoản mới
3. Đăng nhập
4. Tìm sách "Java"
5. Thêm vào giỏ hàng
6. Vào giỏ hàng
7. Thanh toán
```

### Scenario 2: Admin quản lý
```
1. Đăng nhập với tài khoản admin
2. Click "Thêm Sách" trên menu
3. Điền thông tin sách mới
4. Lưu sách
5. Về trang danh sách
6. Click "Sửa" hoặc "Xóa" để quản lý
```

---

## 📞 Hỗ trợ

### Xem log lỗi:
```
- Trong terminal/console nơi chạy ứng dụng
- Hoặc file: target/logs/spring.log
```

### Debug:
```
- Bật logging trong application.properties
- Xem console output
- Dùng Postman test API
```

### Tài liệu chi tiết:
```
Xem file: README.md
```

---

## 🎯 Checklist hoàn thành

- [ ] MySQL đã chạy
- [ ] Database bookstore_db đã tạo
- [ ] Ứng dụng chạy thành công (port 8080)
- [ ] Tài khoản admin đã tạo
- [ ] Đã thêm dữ liệu sách mẫu
- [ ] Đăng nhập thành công
- [ ] Xem danh sách sách OK
- [ ] Tìm kiếm hoạt động
- [ ] Thêm vào giỏ hàng OK
- [ ] Admin có thể thêm/sửa/xóa sách
- [ ] REST API trả về dữ liệu

✅ **Nếu tất cả checklist OK → Ứng dụng đã sẵn sàng!**

---

## 🚀 Next Steps

1. **Tùy chỉnh giao diện**
   - Sửa file CSS trong templates/
   - Thêm ảnh cho sách

2. **Thêm chức năng**
   - Upload hình ảnh
   - Xử lý thanh toán thực tế
   - Quản lý đơn hàng

3. **Deploy lên server**
   - Build file .jar
   - Deploy lên Heroku/AWS/Azure

4. **Push lên GitHub**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin <your-repo-url>
   git push -u origin main
   ```

---

**🎉 CHÚC BẠN THÀNH CÔNG! 🎉**
