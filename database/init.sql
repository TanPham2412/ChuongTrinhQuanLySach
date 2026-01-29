-- ====================================
-- SCRIPT KHỞI TẠO DATABASE
-- Book Management System
-- ====================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS bookstore_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE bookstore_db;

-- ====================================
-- Tables sẽ tự động tạo bởi JPA
-- Script này chỉ để tham khảo cấu trúc
-- ====================================

-- ====================================
-- DỮ LIỆU KHỞI TẠO
-- ====================================

-- 1. Thêm Roles (Tự động tạo bởi DataInitializer)
-- INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- 2. Thêm User ADMIN
-- Password: admin123 (đã được mã hóa bằng BCrypt)
INSERT INTO users (username, password, email, full_name, enabled, provider) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'admin@bookstore.com', 'Administrator', 1, 'LOCAL')
ON DUPLICATE KEY UPDATE username=username;

-- Gán role ADMIN cho user admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 3. Thêm User thường
-- Password: user123
INSERT INTO users (username, password, email, full_name, enabled, provider) 
VALUES ('user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'user@bookstore.com', 'Regular User', 1, 'LOCAL')
ON DUPLICATE KEY UPDATE username=username;

-- Gán role USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'user' AND r.name = 'ROLE_USER'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- ====================================
-- DỮ LIỆU MẪU - SÁCH
-- ====================================

INSERT INTO books (title, author, price, category, description, quantity, image_url) VALUES
('Lập Trình Java Cơ Bản', 'Nguyễn Văn A', 250000, 'Programming', 
 'Sách hướng dẫn học Java từ cơ bản đến nâng cao, phù hợp cho người mới bắt đầu.', 50, 
 'https://via.placeholder.com/200x300?text=Java'),

('Spring Boot in Action', 'Craig Walls', 450000, 'Programming', 
 'Hướng dẫn xây dựng ứng dụng web với Spring Boot framework.', 30,
 'https://via.placeholder.com/200x300?text=Spring+Boot'),

('Clean Code', 'Robert C. Martin', 350000, 'Programming', 
 'Nghệ thuật viết code sạch và dễ bảo trì.', 40,
 'https://via.placeholder.com/200x300?text=Clean+Code'),

('Design Patterns', 'Gang of Four', 420000, 'Programming', 
 'Các mẫu thiết kế phần mềm cơ bản.', 25,
 'https://via.placeholder.com/200x300?text=Design+Patterns'),

('Effective Java', 'Joshua Bloch', 380000, 'Programming', 
 'Best practices cho lập trình Java.', 35,
 'https://via.placeholder.com/200x300?text=Effective+Java'),

('Harry Potter và Hòn Đá Phù Thủy', 'J.K. Rowling', 180000, 'Fiction', 
 'Cuộc phiêu lưu kỳ ảo của cậu bé phù thủy Harry Potter.', 100,
 'https://via.placeholder.com/200x300?text=Harry+Potter'),

('Đắc Nhân Tâm', 'Dale Carnegie', 120000, 'Self-help', 
 'Nghệ thuật giao tiếp và ứng xử để thành công.', 80,
 'https://via.placeholder.com/200x300?text=Dac+Nhan+Tam'),

('Nhà Giả Kim', 'Paulo Coelho', 95000, 'Fiction', 
 'Hành trình tìm kiếm kho báu và ý nghĩa cuộc đời.', 120,
 'https://via.placeholder.com/200x300?text=Nha+Gia+Kim'),

('Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 280000, 'History', 
 'Lịch sử tiến hóa của loài người từ thời kỳ đồ đá đến hiện đại.', 60,
 'https://via.placeholder.com/200x300?text=Sapiens'),

('Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Rosie Nguyễn', 85000, 'Self-help', 
 'Cuốn sách truyền cảm hứng cho tuổi trẻ.', 150,
 'https://via.placeholder.com/200x300?text=Tuoi+Tre'),

('Python Crash Course', 'Eric Matthes', 320000, 'Programming', 
 'Học Python nhanh chóng và hiệu quả.', 45,
 'https://via.placeholder.com/200x300?text=Python'),

('JavaScript: The Good Parts', 'Douglas Crockford', 280000, 'Programming', 
 'Những điều hay nhất về JavaScript.', 38,
 'https://via.placeholder.com/200x300?text=JavaScript'),

('Tôi Tài Giỏi, Bạn Cũng Thế', 'Adam Khoo', 110000, 'Self-help', 
 'Phương pháp học tập hiệu quả cho học sinh, sinh viên.', 90,
 'https://via.placeholder.com/200x300?text=Tai+Gioi'),

('Nghĩ Giàu Làm Giàu', 'Napoleon Hill', 135000, 'Business', 
 'Bí quyết thành công trong kinh doanh và cuộc sống.', 75,
 'https://via.placeholder.com/200x300?text=Nghi+Giau'),

('Atomic Habits', 'James Clear', 195000, 'Self-help', 
 'Xây dựng thói quen tốt và phá vỡ thói quen xấu.', 110,
 'https://via.placeholder.com/200x300?text=Atomic+Habits');

-- ====================================
-- QUERIES HỮU ÍCH
-- ====================================

-- Xem tất cả sách
-- SELECT * FROM books;

-- Xem tất cả users và roles
-- SELECT u.username, u.email, r.name as role 
-- FROM users u 
-- JOIN user_roles ur ON u.id = ur.user_id 
-- JOIN roles r ON ur.role_id = r.id;

-- Tìm sách theo category
-- SELECT * FROM books WHERE category = 'Programming';

-- Tìm sách theo keyword
-- SELECT * FROM books WHERE title LIKE '%Java%' OR author LIKE '%Java%';

-- Thống kê số lượng sách theo category
-- SELECT category, COUNT(*) as total, SUM(quantity) as total_quantity 
-- FROM books GROUP BY category;

-- ====================================
-- RESET DATABASE (CHỈ DÙNG KHI CẦN)
-- ====================================

-- DROP DATABASE IF EXISTS bookstore_db;
