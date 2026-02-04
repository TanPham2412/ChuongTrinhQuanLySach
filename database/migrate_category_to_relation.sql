-- Migration: Chuyển từ cột category (string) sang category_id (foreign key)

USE bookstore_db;

-- Bước 1: Thêm cột category_id nếu chưa có
ALTER TABLE books ADD COLUMN IF NOT EXISTS category_id BIGINT;

-- Bước 2: Map dữ liệu từ category (string) sang category_id
-- Cập nhật các sách có category = tên thể loại
UPDATE books b
JOIN categories c ON TRIM(b.category) = TRIM(c.name)
SET b.category_id = c.id
WHERE b.category IS NOT NULL;

-- Bước 3: Xóa cột category (string) cũ
ALTER TABLE books DROP COLUMN IF EXISTS category;

-- Bước 4: Thêm foreign key constraint
ALTER TABLE books 
ADD CONSTRAINT fk_book_category 
FOREIGN KEY (category_id) REFERENCES categories(id) 
ON DELETE SET NULL 
ON UPDATE CASCADE;

-- Kiểm tra kết quả
SELECT 
    b.id,
    b.title,
    b.category_id,
    c.name as category_name
FROM books b
LEFT JOIN categories c ON b.category_id = c.id
LIMIT 10;
