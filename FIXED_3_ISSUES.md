# HƯỚNG DẪN SỬA 3 LỖI

## 🔴 Vấn đề 1: Lỗi khi cập nhật trạng thái đơn hàng
**Lỗi:** `Data truncated for column 'status' at row 1`

**Nguyên nhân:** Column `status` trong database quá ngắn, không đủ chứa giá trị enum như "CONFIRMED", "PREPARING" (9 ký tự)

**Giải pháp:** Chạy SQL sau trong MySQL Workbench hoặc phpMyAdmin:

```sql
USE bookstore_db;
ALTER TABLE orders MODIFY COLUMN status VARCHAR(20) NOT NULL;
```

Hoặc chạy file SQL đã tạo:
```bash
# Trong PowerShell
Get-Content "database\fix_status_column.sql" | mysql -u root -p bookstore_db
```

---

## 🔴 Vấn đề 2: Tên hiển thị là số thay vì tên Gmail

**Lỗi:** Hiển thị "100257924086236530915" thay vì tên người dùng

**Nguyên nhân:** Spring Security OAuth2 mặc định hiển thị `sub` (Google ID) thay vì `name`

**Đã sửa:**
- ✅ Tạo `CustomOAuth2UserService` để xử lý user từ Google
- ✅ Tạo `AuthenticationFacade` để lấy thông tin user hiện tại
- ✅ Tạo `GlobalControllerAdvice` để inject `currentUserName` vào tất cả view

**Cách hiển thị tên đúng trong template:**
```html
<!-- Thay vì -->
<span th:text="${#authentication.name}">User</span>

<!-- Dùng -->
<span th:text="${currentUserName}">User</span>
```

---

## 🔴 Vấn đề 3: Tài khoản Google không lưu vào database

**Nguyên nhân:** Không có service xử lý OAuth2 user

**Đã sửa:**
- ✅ Tạo `CustomOAuth2UserService` để tự động tạo/cập nhật user khi đăng nhập Google
- ✅ Lưu thông tin: email, tên, providerId (Google ID), provider (GOOGLE)
- ✅ Tự động gán role USER cho tài khoản mới

**Cơ chế hoạt động:**
1. User click "Sign in with Google"
2. Google redirect về với thông tin user
3. `CustomOAuth2UserService.loadUser()` được gọi
4. Kiểm tra email đã tồn tại chưa:
   - Nếu có → Cập nhật thông tin
   - Nếu chưa → Tạo user mới với role USER
5. Lưu vào database

---

## ✅ Các file đã tạo/sửa:

### Mới tạo:
1. `CustomOAuth2UserService.java` - Xử lý OAuth2 user
2. `AuthenticationFacade.java` - Lấy user hiện tại
3. `GlobalControllerAdvice.java` - Inject user info vào view
4. `database/fix_status_column.sql` - Sửa column status

### Đã sửa:
1. `SecurityConfig.java` - Thêm CustomOAuth2UserService vào config
2. `OrderRepository.java` - Thêm @EntityGraph để fetch đầy đủ data
3. `OrderService.java` - Dùng findByIdWithDetails()
4. `order-detail.html` - Đơn giản hóa cú pháp Thymeleaf

---

## 🧪 Kiểm tra:

### 1. Sửa column status (BẮT BUỘC):
```sql
USE bookstore_db;
ALTER TABLE orders MODIFY COLUMN status VARCHAR(20) NOT NULL;
SELECT COLUMN_NAME, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'orders' AND COLUMN_NAME = 'status';
-- Kết quả phải hiển thị: status | varchar(20)
```

### 2. Đăng nhập Google và kiểm tra:
1. Logout khỏi tài khoản hiện tại
2. Truy cập: http://localhost:8080/login
3. Click "Sign in with Google"
4. Sau khi đăng nhập, kiểm tra database:

```sql
SELECT id, username, email, full_name, provider, provider_id, enabled 
FROM users 
WHERE provider = 'GOOGLE';
```

Phải thấy:
- `email`: tanpham2422@gmail.com
- `full_name`: Memories (hoặc tên Google của bạn)
- `provider`: GOOGLE
- `provider_id`: 100257924086236530915

### 3. Kiểm tra tên hiển thị:
- Tên trên thanh navbar phải hiển thị "Memories" thay vì số

### 4. Kiểm tra cập nhật trạng thái:
1. Truy cập: http://localhost:8080/admin/orders/2
2. Click nút "XÁC NHẬN ĐƠN HÀNG" → Trạng thái chuyển sang "Đã xác nhận"
3. Click "ĐANG CHUẨN BỊ" → Không còn lỗi SQL

---

## 📝 Lưu ý:

- **Password encoder:** Tài khoản OAuth2 không cần password, field password để trống
- **Role mặc định:** Tài khoản Google tự động có role USER
- **Email duy nhất:** Nếu email đã tồn tại, chỉ cập nhật thông tin, không tạo mới
- **Provider tracking:** Field `provider` và `provider_id` để phân biệt LOCAL vs GOOGLE login

---

## 🚀 Restart server:

Server đã tự động reload. Nếu cần restart thủ công:
```bash
mvn spring-boot:run
```
