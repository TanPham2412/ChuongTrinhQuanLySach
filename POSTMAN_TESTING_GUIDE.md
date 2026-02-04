# Hướng dẫn Test API với Postman

## 1. Chuẩn bị

### Bước 1: Khởi động ứng dụng
```bash
mvn spring-boot:run
```
Ứng dụng sẽ chạy tại: `http://localhost:8080`

### Bước 2: Đăng nhập để lấy Session/Token
1. Mở trình duyệt, truy cập: `http://localhost:8080/login`
2. Đăng nhập với tài khoản admin
3. Mở Developer Tools (F12) → Tab "Application" hoặc "Storage"
4. Tìm trong:
   - **Cookies**: Tìm `JSESSIONID` hoặc cookie liên quan
   - **Local Storage**: Tìm token nếu có lưu

## 2. Cấu hình Postman

### Cách 1: Sử dụng Cookie (Spring Session)

1. Mở Postman
2. Tạo request mới
3. Vào tab **Headers**, thêm:
   - Key: `Cookie`
   - Value: `JSESSIONID=<giá trị cookie bạn copy>`

### Cách 2: Sử dụng JWT Token (nếu có)

1. Vào tab **Headers**, thêm:
   - Key: `Authorization`
   - Value: `Bearer <token của bạn>`

---

## 3. Test các API

### 📗 GET - Lấy danh sách thể loại (QUAN TRỌNG - LÀM TRƯỚC)
**Endpoint:** `GET http://localhost:8080/api/categories/names`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
```

**Không cần body**

**Response mẫu:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    "Giả tưởng",
    "Khoa học viễn tưởng",
    "Kiếm hiệp / Tiên hiệp",
    "Kinh dị / Giật gân",
    "Lãng mạn / Ngôn tình",
    "Phi hư cấu & Kiến thức",
    "Thơ & Ca dao",
    "Tiểu thuyết",
    "Tiểu thuyết lịch sử",
    "Trinh thám / Hình sự",
    "Truyện ngắn / Tản văn",
    "Văn học & Hư cấu"
  ]
}
```

**⚠️ LƯU Ý:** Copy chính xác tên thể loại từ đây để dùng khi thêm/sửa sách!

---

### 📗 GET - Lấy cây thể loại (Cha - Con)
**Endpoint:** `GET http://localhost:8080/api/categories/tree`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
```

**Response mẫu:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Văn học & Hư cấu",
      "description": "Thể loại văn học hư cấu",
      "children": [
        {
          "id": 2,
          "name": "Tiểu thuyết",
          "fullPath": "Văn học & Hư cấu > Tiểu thuyết"
        },
        {
          "id": 3,
          "name": "Lãng mạn / Ngôn tình",
          "fullPath": "Văn học & Hư cấu > Lãng mạn / Ngôn tình"
        }
      ]
    }
  ]
}
```

---

### 📗 GET - Lấy danh sách sách
**Endpoint:** `GET http://localhost:8080/api/books`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
```

**Không cần body**

**Response mẫu:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "title": "Spring Boot in Action",
      "author": "Craig Walls",
      "price": 299000.0,
      "category": "Programming",
      "description": "Learn Spring Boot",
      "quantity": 10,
      "imageUrl": "https://example.com/image.jpg"
    }
  ]
}
```

---

### 📗 GET - Lấy chi tiết 1 sách
**Endpoint:** `GET http://localhost:8080/api/books/1`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
```

**Không cần body**

---

### 📗 GET - Tìm kiếm sách
**Endpoint:** `GET http://localhost:8080/api/books/search?keyword=spring`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
```

**Không cần body**

---

### 📘 POST - Thêm sách mới (Cần quyền ADMIN)
**Endpoint:** `POST http://localhost:8080/api/books`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 350000.0,
  "category": "Lãng mạn / Ngôn tình",
  "description": "A Handbook of Agile Software Craftsmanship",
  "quantity": 20,
  "imageUrl": "https://example.com/cleancode.jpg"
}
```

**⚠️ LƯU Ý VỀ CATEGORY:**
- Nhập **TÊN CHÍNH XÁC** của thể loại con (không phải full path)
- Ví dụ: `"Lãng mạn / Ngôn tình"` (ĐÚNG) ❌ KHÔNG PHẢI: `"Văn học & Hư cấu > Lãng mạn / Ngôn tình"`
- Nếu chọn thể loại cha, nhập tên thể loại cha: `"Văn học & Hư cấu"`
- Tên phải khớp chính xác với tên trong database

**Response mẫu:**
```json
{
  "success": true,
  "message": "Book created successfully",
  "data": {
    "id": 5,
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "price": 350000.0,
    "category": "Programming",
    "description": "A Handbook of Agile Software Craftsmanship",
    "quantity": 20,
    "imageUrl": "https://example.com/cleancode.jpg"
  }
}
```

---

### 📙 PUT - Cập nhật sách (Cần quyền ADMIN)
**Endpoint:** `PUT http://localhost:8080/api/books/5`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "title": "Clean Code - Updated",
  "author": "Robert C. Martin",
  "price": 380000.0,
  "category": "Kinh dị / Giật gân",
  "description": "A Handbook of Agile Software Craftsmanship - Updated Edition",
  "quantity": 25,
  "imageUrl": "https://example.com/cleancode-updated.jpg"
}
```

**⚠️ LƯU Ý:** Category phải là tên chính xác của thể loại (xem phần POST ở trên)

**Response mẫu:**
```json
{
  "success": true,
  "message": "Book updated successfully",
  "data": {
    "id": 5,
    "title": "Clean Code - Updated",
    "author": "Robert C. Martin",
    "price": 380000.0,
    "category": "Programming",
    "description": "A Handbook of Agile Software Craftsmanship - Updated Edition",
    "quantity": 25,
    "imageUrl": "https://example.com/cleancode-updated.jpg"
  }
}
```

---

### 📕 DELETE - Xóa sách (Cần quyền ADMIN)
**Endpoint:** `DELETE http://localhost:8080/api/books/5`

**Headers:**
```
Cookie: JSESSIONID=<your-session-id>
```

**Không cần body**

**Response mẫu:**
```json
{
  "success": true,
  "message": "Book deleted successfully",
  "data": null
}
```

---

## 4. Xử lý lỗi thường gặp

### Lỗi 401 Unauthorized
**Nguyên nhân:** Cookie/token không hợp lệ hoặc đã hết hạn

**Giải pháp:**
1. Đăng nhập lại trên trình duyệt
2. Lấy cookie/token mới
3. Cập nhật lại trong Postman

### Lỗi 403 Forbidden
**Nguyên nhân:** Tài khoản không có quyền ADMIN

**Giải pháp:**
- Đảm bảo đăng nhập bằng tài khoản có role ADMIN
- Kiểm tra trong database: `SELECT * FROM users WHERE email = 'your-email'`

### Lỗi 404 Not Found
**Nguyên nhân:** ID sách không tồn tại

**Giải pháp:**
- Kiểm tra ID có đúng không
- Gọi API GET để xem danh sách sách hiện có

### Lỗi 400 Bad Request
**Nguyên nhân:** Dữ liệu JSON không hợp lệ

**Giải pháp:**
- Kiểm tra format JSON
- Đảm bảo có header `Content-Type: application/json`
- Kiểm tra các trường bắt buộc

---

## 5. Tips & Tricks

### Lưu Environment trong Postman
1. Tạo Environment mới (góc phải trên)
2. Thêm biến:
   - `base_url`: `http://localhost:8080`
   - `session_id`: `<your-session-id>`
3. Sử dụng: `{{base_url}}/api/books`

### Tạo Collection
1. Tạo Collection mới: "Book Store API"
2. Thêm các request vào
3. Set Headers chung cho cả Collection:
   - Cookie: `JSESSIONID={{session_id}}`

### Auto-refresh Cookie
1. Tạo một request đăng nhập trong Postman
2. Trong tab **Tests**, thêm script:
```javascript
pm.environment.set("session_id", pm.cookies.get("JSESSIONID"));
```

---

## 6. Checklist Test

- [ ] GET /api/books - Lấy danh sách
- [ ] GET /api/books/{id} - Lấy chi tiết
- [ ] GET /api/books/search?keyword=xxx - Tìm kiếm
- [ ] POST /api/books - Thêm mới (admin)
- [ ] PUT /api/books/{id} - Cập nhật (admin)
- [ ] DELETE /api/books/{id} - Xóa (admin)
- [ ] Test với user không phải admin (should return 403)
- [ ] Test với cookie không hợp lệ (should return 401)

---

## 7. Ví dụ nhanh

### Bước 1: Lấy danh sách categories có sẵn
**QUAN TRỌNG:** Bạn PHẢI lấy danh sách category trước khi thêm sách!

**Gọi API:**
```
GET http://localhost:8080/api/categories/names
```

**Hoặc kiểm tra database:**
```sql
SELECT id, name, parent_id FROM categories ORDER BY name;
```

---

### Bước 2: Test POST với dữ liệu mẫu

**⚠️ CHÚ Ý:** Tên category phải khớp 100% với database. Nếu sai 1 ký tự, API sẽ báo lỗi rõ ràng.

```json
{
  "title": "Nàng Công Chúa Ngọt Ngào",
  "author": "Nguyễn Nhật Ánh",
  "price": 120000.0,
  "category": "Lãng mạn / Ngôn tình",
  "description": "Tiểu thuyết lãng mạn hấp dẫn",
  "quantity": 50,
  "imageUrl": "https://example.com/nang-cong-chua.jpg"
}
```

```json
{
  "title": "Sherlock Holmes Toàn Tập",
  "author": "Arthur Conan Doyle",
  "price": 280000.0,
  "category": "Trinh thám / Hình sự",
  "description": "Bộ sưu tập truyện trinh thám kinh điển",
  "quantity": 25,
  "imageUrl": "https://example.com/sherlock.jpg"
}
```

```json
{
  "title": "Vũ Trụ Trong Vỏ Hạt Dẻ",
  "author": "Stephen Hawking",
  "price": 180000.0,
  "category": "Khoa học viễn tưởng",
  "description": "Khám phá bí ẩn vũ trụ",
  "quantity": 30,
  "imageUrl": "https://example.com/vu-tru.jpg"
}
```

```json
{
  "title": "Phàm Nhân Tu Tiên",
  "author": "Vong Ngữ",
  "price": 250000.0,
  "category": "Kiếm hiệp / Tiên hiệp",
  "description": "Phàm Nhân Tu Tiên là một câu chuyện Tiên Hiệp",
  "quantity": 20,
  "imageUrl": "https://example.com/pham-nhan.jpg"
}
```

---

### Xử lý lỗi category không tồn tại

**Nếu bạn nhập sai tên category**, ví dụ:
```json
{
  "category": "Programming"
}
```

**API sẽ trả về lỗi:**
```json
{
  "success": false,
  "message": "Không tìm thấy thể loại: 'Programming'. Vui lòng kiểm tra tên thể loại trong database.",
  "data": null
}
```

**Giải pháp:**
1. Gọi `GET /api/categories/names` để lấy danh sách đúng
2. Copy chính xác tên category từ response
3. Thử lại
