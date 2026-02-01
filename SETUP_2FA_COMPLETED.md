# Hướng Dẫn Hoàn Tất Cài Đặt 2FA

## Đã Hoàn Thành

✅ Thêm các cột `two_factor_enabled` và `two_factor_secret` vào User model
✅ Tạo TwoFactorService để xử lý Google Authenticator
✅ Tạo ProfileController với các chức năng:
   - Xem profile
   - Đổi mật khẩu
   - Bật/tắt xác thực 2 lớp
✅ Cập nhật AuthController với flow xác thực 2FA
✅ Tạo TwoFactorAuthenticationSuccessHandler để kiểm tra 2FA khi đăng nhập
✅ Cập nhật SecurityConfig để tích hợp 2FA vào cả login form và OAuth2
✅ Tạo 3 templates:
   - profile.html (trang tài khoản)
   - 2fa-setup.html (thiết lập 2FA)
   - 2fa-verify.html (xác thực 2FA khi đăng nhập)
✅ Thêm dependencies: googleauth, zxing (QR code)

## Bước Tiếp Theo

### 1. Cập Nhật Database

Nếu Spring JPA không tự động tạo cột (kiểm tra xem có lỗi không), chạy SQL thủ công:

```sql
-- Kết nối MySQL
mysql -u root -p

-- Chọn database
USE bookstore_db;

-- Thêm cột 2FA
ALTER TABLE users 
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN two_factor_secret VARCHAR(255) DEFAULT NULL;

-- Kiểm tra
DESCRIBE users;
```

### 2. Khởi Động Lại Ứng Dụng

```bash
# Dừng ứng dụng hiện tại (Ctrl+C)
# Khởi động lại
./mvnw spring-boot:run
```

### 3. Kiểm Tra Chức Năng

#### A. Truy cập trang Profile
1. Đăng nhập vào ứng dụng: http://localhost:8080/login
2. Click vào dropdown user ở góc trên bên phải
3. Chọn "Tài Khoản" → Sẽ mở http://localhost:8080/profile

#### B. Đổi Mật Khẩu
- Nhập mật khẩu hiện tại, mật khẩu mới, xác nhận
- Click "Đổi Mật Khẩu"

#### C. Thiết Lập 2FA
1. Trên trang profile, click "Bật Xác Thực 2 Lớp"
2. Cài đặt Google Authenticator trên điện thoại:
   - Android: https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2
   - iOS: https://apps.apple.com/app/google-authenticator/id388497605
3. Mở app, quét mã QR hiển thị trên màn hình
4. Nhập mã 6 chữ số từ app
5. Click "Bật Xác Thực 2 Lớp"

#### D. Test 2FA Login
1. Đăng xuất
2. Đăng nhập lại (form login hoặc Google OAuth)
3. Sẽ được chuyển đến trang xác thực 2FA
4. Nhập mã từ Google Authenticator
5. Click "Xác Nhận" → Đăng nhập thành công

#### E. Tắt 2FA
1. Vào trang Profile
2. Click "Tắt Xác Thực 2 Lớp"
3. Nhập mã xác thực để xác nhận
4. 2FA sẽ bị tắt

## Các File Đã Tạo/Chỉnh Sửa

### Backend
- `src/main/java/nhom5/phamminhtan/model/User.java` - Thêm twoFactorEnabled, twoFactorSecret
- `src/main/java/nhom5/phamminhtan/service/TwoFactorService.java` - Service xử lý 2FA
- `src/main/java/nhom5/phamminhtan/controller/ProfileController.java` - Controller mới
- `src/main/java/nhom5/phamminhtan/controller/AuthController.java` - Thêm 2FA verify
- `src/main/java/nhom5/phamminhtan/config/TwoFactorAuthenticationSuccessHandler.java` - Handler mới
- `src/main/java/nhom5/phamminhtan/config/SecurityConfig.java` - Cập nhật
- `src/main/java/nhom5/phamminhtan/dto/ChangePasswordRequest.java` - DTO mới
- `src/main/java/nhom5/phamminhtan/dto/TwoFactorRequest.java` - DTO mới

### Frontend
- `src/main/resources/templates/profile.html` - Trang profile mới
- `src/main/resources/templates/2fa-setup.html` - Trang thiết lập 2FA
- `src/main/resources/templates/2fa-verify.html` - Trang xác thực 2FA

### Database
- `database/add_2fa_columns.sql` - Script SQL

### Dependencies
- `pom.xml` - Thêm googleauth, zxing

## Lưu Ý Quan Trọng

1. **Security**: Mã bí mật 2FA được mã hóa trong database
2. **OAuth Users**: Người dùng đăng nhập bằng Google cũng có thể bật 2FA
3. **Password**: User đăng nhập OAuth không thể đổi mật khẩu
4. **Recovery**: Nếu mất điện thoại, cần admin hỗ trợ tắt 2FA trong database

## Xử Lý Sự Cố

### Lỗi 404 /profile
- Kiểm tra ProfileController đã được tạo
- Restart ứng dụng

### Không quét được QR code
- Kiểm tra thư viện zxing đã được thêm vào pom.xml
- Maven clean install lại

### Mã xác thực không đúng
- Kiểm tra thời gian trên điện thoại và máy tính đã đồng bộ
- Mã thay đổi mỗi 30 giây

### Database không có cột mới
- Chạy SQL thủ công
- Hoặc xóa bảng users và để JPA tạo lại (mất data!)

## Tính Năng Đã Hoàn Thành

✅ Profile page với thông tin user
✅ Đổi mật khẩu cho local users
✅ Bật/tắt xác thực 2 lớp với Google Authenticator
✅ QR code tự động sinh
✅ Xác thực 2FA khi đăng nhập (form login và OAuth)
✅ UI đẹp với Bootstrap 5
✅ Responsive design
✅ Thông báo lỗi/thành công rõ ràng
✅ Bảo mật: verify code trước khi bật/tắt 2FA
