# HƯỚNG DẪN SỬA LỖI GOOGLE OAUTH

## Vấn đề: Error 400: redirect_uri_mismatch

### Nguyên nhân:
Spring Security OAuth2 sử dụng endpoint mặc định: `/login/oauth2/code/{registrationId}`
Nhưng trong Google Cloud Console bạn đã cấu hình sai URI.

### Giải pháp:

1. **Truy cập Google Cloud Console:**
   - Mở: https://console.cloud.google.com/auth/clients/828302699790-s0pf0iel60a3rp2hk1bdonkj1n6hc4i5.apps.googleusercontent.com/2project=school-food-order-483005

2. **Cập nhật "Authorized redirect URIs":**
   - **XÓA** các URI cũ:
     - http://localhost:8000/api/v1/auth/google/callback
     - http://localhost:5173/auth/google/callback
   
   - **THÊM** URI mới (đúng cho Spring Security):
     - `http://localhost:8080/login/oauth2/code/google`
   
   - Nếu muốn hỗ trợ cả port 5173:
     - `http://localhost:5173/login/oauth2/code/google`

3. **Giữ nguyên "Authorized JavaScript origins":**
   - http://localhost:5173
   - http://localhost:8000
   - http://localhost:8080

4. **Click "Save"** và đợi 5 phút để Google cập nhật.

5. **Khởi động lại ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```

6. **Test lại đăng nhập Google:**
   - Truy cập: http://localhost:8080/login
   - Click nút "Sign in with Google"

---

## Sửa lỗi trạng thái đơn hàng

### Nếu đơn hàng không hiển thị trạng thái:

Chạy lệnh SQL sau trong MySQL:

```sql
USE bookstore_db;
UPDATE orders SET status = 'PENDING' WHERE status IS NULL;
```

Hoặc chạy file SQL đã tạo:
```bash
mysql -u root < database/update_order_status.sql
```

---

## Pattern URI cho các framework khác (tham khảo):

- **Spring Security OAuth2:** `{baseUrl}/login/oauth2/code/{provider}`
- **Next.js (NextAuth):** `{baseUrl}/api/auth/callback/{provider}`
- **Laravel Socialite:** `{baseUrl}/auth/{provider}/callback`
- **Passport.js:** `{baseUrl}/auth/{provider}/callback`
