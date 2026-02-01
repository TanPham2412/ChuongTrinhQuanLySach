USE bookstore_db;

-- BẮT BUỘC: Sửa column status từ VARCHAR(7) thành VARCHAR(20)
ALTER TABLE orders MODIFY COLUMN status VARCHAR(20);

-- Xác nhận đã sửa thành công
SELECT 
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'bookstore_db' 
  AND TABLE_NAME = 'orders'
  AND COLUMN_NAME = 'status';

-- Kết quả phải hiển thị: status | varchar(20) | NO
