# QUAN TRỌNG - CHẠY NGAY!

## Lỗi Hiện Tại

Ứng dụng đang lỗi vì database chưa có cột `two_factor_enabled` và `two_factor_secret`.

```
Caused by: java.lang.IllegalArgumentException: Can not set boolean field nhom5.phamminhtan.model.User.twoFactorEnabled to null value
```

## CÁCH SỬA - CHẠY SQL SAU ĐÂY

### Cách 1: Sử dụng MySQL Command Line

```bash
# Mở Command Prompt hoặc PowerShell
# Tìm đường dẫn MySQL (thường là)
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
# Hoặc
cd "C:\xampp\mysql\bin"

# Kết nối MySQL
mysql -u root -p

# Sau khi nhập password (hoặc Enter nếu không có password)
USE bookstore_db;

# Chạy lệnh sau để thêm cột
ALTER TABLE users 
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN two_factor_secret VARCHAR(255) DEFAULT NULL;

# Kiểm tra
DESCRIBE users;

# Thoát
EXIT;
```

### Cách 2: Sử dụng phpMyAdmin

1. Mở http://localhost/phpmyadmin
2. Chọn database `bookstore_db`
3. Click tab "SQL"
4. Paste code sau và click "Go":

```sql
ALTER TABLE users 
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN two_factor_secret VARCHAR(255) DEFAULT NULL;
```

### Cách 3: Sử dụng MySQL Workbench

1. Mở MySQL Workbench
2. Kết nối đến database
3. Chọn schema `bookstore_db`
4. Mở SQL Editor
5. Paste code và Execute:

```sql
USE bookstore_db;

ALTER TABLE users 
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN two_factor_secret VARCHAR(255) DEFAULT NULL;
```

## SAU KHI CHẠY SQL

1. Dừng ứng dụng (Ctrl+C trong terminal)
2. Khởi động lại:
```bash
./mvnw spring-boot:run
```

3. Truy cập http://localhost:8080
4. Đăng nhập và click vào tên user ở góc trên phải
5. Chọn "Tài Khoản"

## Kiểm Tra Database Đã Cập Nhật

```sql
USE bookstore_db;
DESCRIBE users;
```

Bạn sẽ thấy 2 cột mới:
- `two_factor_enabled` (tinyint/boolean)
- `two_factor_secret` (varchar(255))
