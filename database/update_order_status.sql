-- Cập nhật trạng thái cho các đơn hàng cũ chưa có status
-- Chạy script này nếu các đơn hàng không hiển thị trạng thái

UPDATE orders 
SET status = 'PENDING' 
WHERE status IS NULL;

-- Kiểm tra kết quả
SELECT id, order_date, status, payment_status, total_amount 
FROM orders 
ORDER BY order_date DESC;
