-- Sửa lỗi column status quá ngắn
-- Enum OrderStatus dài nhất là "CONFIRMED", "PREPARING", "COMPLETED" (9 ký tự)
-- Cần tăng lên VARCHAR(20) để đảm bảo

USE bookstore_db;

-- Thay đổi độ dài column status
ALTER TABLE orders MODIFY COLUMN status VARCHAR(20) NOT NULL;

-- Cập nhật dữ liệu cũ nếu có
UPDATE orders SET status = 'PENDING' WHERE status IS NULL OR status = '';

-- Kiểm tra kết quả
SELECT 
    COLUMN_NAME, 
    COLUMN_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'bookstore_db' 
  AND TABLE_NAME = 'orders' 
  AND COLUMN_NAME = 'status';

-- Xem dữ liệu mẫu
SELECT id, status, order_date, total_amount FROM orders LIMIT 5;
