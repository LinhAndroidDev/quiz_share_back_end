# QuizShare – Backend Design Document (Spring Boot)

> Phiên bản: 1.0 | Ngày: 2026-05-04  
> Dựa trên phân tích toàn bộ Android client `com.example.appthitracnghiem`

---

## Mục lục

1. [Tổng quan kiến trúc](#1-tổng-quan-kiến-trúc)
2. [Thiết kế cơ sở dữ liệu](#2-thiết-kế-cơ-sở-dữ-liệu)
3. [API Endpoints](#3-api-endpoints)
4. [Authentication & Authorization](#4-authentication--authorization)
5. [File Storage](#5-file-storage)
6. [Cấu trúc dự án Spring Boot](#6-cấu-trúc-dự-án-spring-boot)
7. [Dependencies (pom.xml)](#7-dependencies-pomxml)

---

## 1. Tổng quan kiến trúc

```
Android Client  ──HTTP/JSON──▶  Spring Boot REST API
                                      │
                            ┌─────────┼──────────┐
                            ▼         ▼          ▼
                         MySQL    Cloud       JWT Auth
                        (RDS)    Storage     (StateLess)
                                 (MinIO /
                                  GCS / S3)
```

- **Framework:** Spring Boot 3.x
- **Database:** MySQL 8 (hoặc PostgreSQL)
- **Auth:** JWT (Bearer token)
- **File upload:** Lưu trữ file ảnh lên Cloud Storage (GCS / S3 / MinIO)
- **API style:** RESTful JSON — toàn bộ response bọc trong `BaseResponse<T>`

### Format Response chuẩn

```json
{
  "statusCode": 200,
  "message": "Success",
  "result": { ... }
}
```

---

## 2. Thiết kế cơ sở dữ liệu

### 2.1 Sơ đồ quan hệ (ERD tóm tắt)

```
users ─────────────────────────────────────────────┐
  │                                                 │
  ├──< exam_histories >──< exam_results >           │
  │                                                 │
  ├──< saved_exams                                  │
  │                                                 │
  └──< exams (author) ──< exam_questions            │
                │              └──< answers         │
                │                                   │
departments ──< subjects ──< exams ─────────────────┘
```

---

### 2.2 Bảng chi tiết

#### `users`

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID người dùng |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Email đăng nhập |
| `phone_number` | VARCHAR(20) | UNIQUE | Số điện thoại |
| `name` | VARCHAR(100) | NOT NULL | Tên hiển thị |
| `password` | VARCHAR(255) | NOT NULL | Mật khẩu đã hash (BCrypt) |
| `birthday` | DATE | | Ngày sinh |
| `avatar` | TEXT | | URL ảnh đại diện |
| `role` | ENUM('USER','ADMIN') | DEFAULT 'USER' | Vai trò |
| `status` | ENUM('ACTIVE','INACTIVE','BANNED') | DEFAULT 'ACTIVE' | Trạng thái |
| `created_at` | DATETIME | DEFAULT NOW() | |
| `updated_at` | DATETIME | | |

---

#### `departments`

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID khoa/ngành |
| `title` | VARCHAR(255) | NOT NULL | Tên khoa |
| `description` | TEXT | | Mô tả |
| `image` | TEXT | | URL ảnh bìa |
| `created_at` | DATETIME | DEFAULT NOW() | |

---

#### `subjects`

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID môn học |
| `department_id` | BIGINT | FK → departments.id | Thuộc khoa nào |
| `title` | VARCHAR(255) | NOT NULL | Tên môn |
| `description` | TEXT | | Mô tả |
| `image` | TEXT | | URL ảnh bìa |
| `created_at` | DATETIME | DEFAULT NOW() | |

---

#### `exams`

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID đề thi |
| `subject_id` | BIGINT | FK → subjects.id | Thuộc môn học nào |
| `author_id` | BIGINT | FK → users.id | Người tạo |
| `title` | VARCHAR(255) | NOT NULL | Tiêu đề đề thi |
| `description` | TEXT | | Mô tả |
| `image` | TEXT | | URL ảnh bìa |
| `time` | INT | NOT NULL | Thời gian làm bài (phút) |
| `number` | INT | NOT NULL | Số câu hỏi |
| `saved_num` | INT | DEFAULT 0 | Số lượt lưu |
| `status` | ENUM('PUBLIC','PRIVATE','DRAFT') | DEFAULT 'PUBLIC' | Trạng thái công khai |
| `created_at` | DATETIME | DEFAULT NOW() | |
| `updated_at` | DATETIME | | |

---

#### `questions`

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID câu hỏi |
| `exam_id` | BIGINT | FK → exams.id | Thuộc đề thi nào |
| `question_title` | TEXT | NOT NULL | Nội dung câu hỏi |
| `question_image` | TEXT | | URL ảnh minh họa |
| `question_level` | ENUM('EASY','MEDIUM','HARD') | DEFAULT 'MEDIUM' | Độ khó |
| `question_sort` | INT | NOT NULL | Thứ tự câu hỏi |
| `created_at` | DATETIME | DEFAULT NOW() | |

---

#### `answers`

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID đáp án |
| `question_id` | BIGINT | FK → questions.id | Thuộc câu hỏi nào |
| `content` | TEXT | NOT NULL | Nội dung đáp án |
| `image` | TEXT | | URL ảnh đáp án |
| `sort` | INT | NOT NULL | Thứ tự đáp án (0,1,2,3) |
| `type` | INT | NOT NULL | `1` = đúng, `0` = sai |

---

#### `exam_histories`

Lưu mỗi lần user bắt đầu / nộp bài.

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID lịch sử |
| `user_id` | BIGINT | FK → users.id | Người thi |
| `exam_id` | BIGINT | FK → exams.id | Đề thi |
| `score` | DECIMAL(5,2) | | Điểm số (0-10) |
| `correct_number` | INT | | Số câu đúng |
| `wrong_number` | INT | | Số câu sai |
| `skip_number` | INT | | Số câu bỏ qua |
| `start_time` | DATETIME | | Thời điểm bắt đầu |
| `finish_time` | DATETIME | | Thời điểm nộp bài |
| `created_at` | DATETIME | DEFAULT NOW() | |
| `updated_at` | DATETIME | | |

---

#### `exam_results`

Lưu đáp án của từng câu trong một lần thi (ánh xạ `question_id` → `answer_id`).

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | |
| `exam_history_id` | BIGINT | FK → exam_histories.id | Thuộc lần thi nào |
| `question_id` | BIGINT | FK → questions.id | Câu hỏi |
| `answer_id` | BIGINT | FK → answers.id, NULLABLE | Đáp án chọn (NULL = bỏ) |

> Tương đương `exam_result: HashMap<String, Int?>` trong client: key = question_id, value = answer_id.

---

#### `saved_exams`

User lưu đề thi yêu thích.

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | |
| `user_id` | BIGINT | FK → users.id | |
| `exam_id` | BIGINT | FK → exams.id | |
| `created_at` | DATETIME | DEFAULT NOW() | |

> UNIQUE(`user_id`, `exam_id`) — không lưu trùng.

---

#### `saved_departments`

User theo dõi khoa/bộ môn.

| Cột | Kiểu | Ràng buộc | Mô tả |
|-----|------|-----------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | |
| `user_id` | BIGINT | FK → users.id | |
| `department_id` | BIGINT | FK → departments.id | |
| `created_at` | DATETIME | DEFAULT NOW() | |

> UNIQUE(`user_id`, `department_id`)

---

### 2.3 Script SQL tham khảo

```sql
CREATE TABLE quiz_share.users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    phone_number  VARCHAR(20)  UNIQUE,
    name          VARCHAR(100) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    birthday      DATE,
    avatar        TEXT,
    role          ENUM('USER','ADMIN') DEFAULT 'USER',
    status        ENUM('ACTIVE','INACTIVE','BANNED') DEFAULT 'ACTIVE',
    created_at    DATETIME DEFAULT NOW(),
    updated_at    DATETIME ON UPDATE NOW()
);

CREATE TABLE quiz_share.departments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image       TEXT,
    created_at  DATETIME DEFAULT NOW()
);

CREATE TABLE quiz_share.subjects (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    image         TEXT,
    created_at    DATETIME DEFAULT NOW(),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE quiz_share.exams (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id  BIGINT NOT NULL,
    author_id   BIGINT NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image       TEXT,
    time        INT NOT NULL,
    number      INT NOT NULL,
    saved_num   INT DEFAULT 0,
    status      ENUM('PUBLIC','PRIVATE','DRAFT') DEFAULT 'PUBLIC',
    created_at  DATETIME DEFAULT NOW(),
    updated_at  DATETIME ON UPDATE NOW(),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (author_id)  REFERENCES users(id)
);

CREATE TABLE quiz_share.questions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    question_title  TEXT NOT NULL,
    question_image  TEXT,
    question_level  ENUM('EASY','MEDIUM','HARD') DEFAULT 'MEDIUM',
    question_sort   INT NOT NULL,
    created_at      DATETIME DEFAULT NOW(),
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

CREATE TABLE quiz_share.answers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    content     TEXT NOT NULL,
    image       TEXT,
    sort        INT  NOT NULL,
    type        TINYINT NOT NULL COMMENT '1=correct, 0=wrong',
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE quiz_share.exam_histories (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    exam_id        BIGINT NOT NULL,
    score          DECIMAL(5,2),
    correct_number INT,
    wrong_number   INT,
    skip_number    INT,
    start_time     DATETIME,
    finish_time    DATETIME,
    created_at     DATETIME DEFAULT NOW(),
    updated_at     DATETIME ON UPDATE NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (exam_id) REFERENCES exams(id)
);

CREATE TABLE quiz_share.exam_results (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_history_id  BIGINT NOT NULL,
    question_id      BIGINT NOT NULL,
    answer_id        BIGINT,
    FOREIGN KEY (exam_history_id) REFERENCES exam_histories(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id)     REFERENCES questions(id),
    FOREIGN KEY (answer_id)       REFERENCES answers(id)
);

CREATE TABLE quiz_share.saved_exams (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    exam_id    BIGINT NOT NULL,
    created_at DATETIME DEFAULT NOW(),
    UNIQUE KEY uq_saved_exam (user_id, exam_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (exam_id) REFERENCES exams(id)
);

CREATE TABLE quiz_share.saved_departments (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    created_at    DATETIME DEFAULT NOW(),
    UNIQUE KEY uq_saved_dept (user_id, department_id),
    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
```

---

## 3. API Endpoints

> **Base URL:** `https://your-domain.com/api/v1`  
> **Content-Type:** `application/json` (trừ upload multipart)  
> **Authorization:** Header `Authorization: Bearer <token>` (các API có dấu 🔒)

---

### 3.1 Auth

#### `POST /register`

Đăng ký tài khoản mới.

**Request:**
```json
{
  "email": "user@example.com",
  "name": "Nguyen Van A",
  "phone_number": "0901234567",
  "birthday": "2000-01-15",
  "password": "secret123"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Register successful",
  "result": {
    "access_token": "eyJhbGciOiJIUzI1NiJ9...",
    "user_id": 1
  }
}
```

---

#### `POST /login`

Đăng nhập bằng email hoặc số điện thoại.

**Request:**
```json
{
  "login_id": "user@example.com",
  "password": "secret123"
}
```

> `login_id` có thể là email hoặc phone_number.

**Response:**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "result": {
    "access_token": "eyJhbGciOiJIUzI1NiJ9...",
    "user_id": 1
  }
}
```

---

#### `POST /forgotPassword`

Gửi email reset mật khẩu.

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Email sent",
  "result": true
}
```

---

### 3.2 User / Profile

#### 🔒 `POST /getUserInfo`

Lấy thông tin profile đầy đủ.

**Request:**
```json
{ "user_id": 1 }
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "access_token": "eyJ...",
    "id": 1,
    "name": "Nguyen Van A",
    "email": "user@example.com",
    "phone_number": "0901234567",
    "birthday": "2000-01-15",
    "avatar": "https://storage.example.com/avatars/1.jpg",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

---

#### 🔒 `POST /updateUserInfo`

Cập nhật tên và ngày sinh.

**Request:**
```json
{
  "user_id": 1,
  "name": "Nguyen Van B",
  "birthday": "1999-05-20"
}
```

**Response:**
```json
{ "statusCode": 200, "message": "Updated", "result": "Update successful" }
```

---

#### 🔒 `POST /changeEmail`

Đổi email.

**Request:**
```json
{
  "user_id": 1,
  "email": "newemail@example.com"
}
```

**Response:**
```json
{ "statusCode": 200, "message": "Updated", "result": "Email updated" }
```

---

#### 🔒 `POST /changePassword`

Đổi mật khẩu.

**Request:**
```json
{
  "user_id": 1,
  "password": "newpass123",
  "cf_password": "newpass123"
}
```

**Response:**
```json
{ "statusCode": 200, "message": "Updated", "result": true }
```

---

#### 🔒 `POST /editAvatar`

Upload ảnh đại diện (multipart/form-data).

**Request:** `multipart/form-data`
- `user_id` (text part)
- `file` (binary part — image/jpeg hoặc image/png)

**Response:**
```json
{ "statusCode": 200, "message": "Updated", "result": true }
```

---

#### 🔒 `POST /unpublicUser`

Vô hiệu hóa tài khoản (soft delete / set status = INACTIVE).

**Request:**
```json
{ "user_id": 1 }
```

**Response:**
```json
{ "statusCode": 200, "message": "User deactivated", "result": "Deactivated" }
```

---

### 3.3 Department & Subject

#### 🔒 `POST /getDepartmentList`

Lấy danh sách tất cả khoa (có thể tìm kiếm).

**Request:**
```json
{
  "user_id": 1,
  "keyword": ""
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": [
    {
      "id": 1,
      "title": "Công nghệ thông tin",
      "description": "Khoa CNTT",
      "image": "https://storage.example.com/department/1.jpg"
    }
  ]
}
```

---

#### 🔒 `POST /listDepartmentInfo`

Lấy danh sách môn học chi tiết theo từng khoa (có số lượng đề thi).

**Request:**
```json
{ "user_id": 1 }
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": [
    {
      "id": 1,
      "title": "Công nghệ thông tin",
      "exam_num": 42,
      "subjects": [
        { "id": 10, "title": "Cơ sở dữ liệu", "image": "...", "description": "..." }
      ]
    }
  ]
}
```

---

#### 🔒 `POST /searchSubject`

Tìm kiếm môn học theo từ khóa và khoa.

**Request:**
```json
{
  "user_id": 1,
  "department_id": 1,
  "keyword": "cơ sở"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": [
    {
      "id": 10,
      "title": "Cơ sở dữ liệu",
      "description": "...",
      "image": "...",
      "count_exam": 15
    }
  ]
}
```

---

### 3.4 Exam (Đề thi)

#### 🔒 `POST /listExam`

Lấy danh sách đề thi của một môn học (có sort, phân trang ngầm định).

**Request:**
```json
{
  "user_id": 1,
  "subject_id": 10,
  "type": "ALL",
  "sort_field": "created_at",
  "sort_by": "DESC"
}
```

> `type`: `"ALL"` | `"MY"` — lọc tất cả hay chỉ đề của user hiện tại.

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "id": 10,
    "title": "Cơ sở dữ liệu",
    "description": "...",
    "department_id": 1,
    "department_title": "Công nghệ thông tin",
    "department_description": "...",
    "list_exam": [
      {
        "id": 100,
        "title": "Đề thi cuối kỳ CSDL 2024",
        "image": "...",
        "time": 60,
        "number": 40,
        "saved_num": 12,
        "status": "PUBLIC",
        "author_id": 1,
        "author_name": "GV Nguyen",
        "author_email": "gv@example.com"
      }
    ]
  }
}
```

---

#### 🔒 `POST /examListQuestion`

Lấy toàn bộ câu hỏi + đáp án của một đề thi (để làm bài).

**Request:**
```json
{
  "user_id": 1,
  "exam_id": 100
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "id": 100,
    "exam_question_list": [
      {
        "question_id": 1001,
        "question_title": "SQL là viết tắt của?",
        "question_image": null,
        "question_level": "EASY",
        "question_sort": 1,
        "answer_list": [
          { "answer_id": 4001, "content": "Structured Query Language", "image": null, "sort": 0, "type": 1 },
          { "answer_id": 4002, "content": "Simple Query Language",     "image": null, "sort": 1, "type": 0 },
          { "answer_id": 4003, "content": "Sequential Query Language", "image": null, "sort": 2, "type": 0 },
          { "answer_id": 4004, "content": "Standard Query Language",   "image": null, "sort": 3, "type": 0 }
        ]
      }
    ]
  }
}
```

---

#### 🔒 `POST /submitExam`

Nộp bài thi — tính điểm và lưu lịch sử.

**Request:**
```json
{
  "user_id": 1,
  "exam_id": 100,
  "answer_list": {
    "1001": 4001,
    "1002": null,
    "1003": 4010
  },
  "start_time": "2026-05-04T08:00:00",
  "finish_time": "2026-05-04T08:55:00"
}
```

> `answer_list`: map `question_id` → `answer_id` (null = bỏ qua câu).

**Response:**
```json
{
  "statusCode": 200,
  "message": "Submitted",
  "result": {
    "exam_history_id": 500,
    "exam_id": 100,
    "user_id": 1,
    "score": 8.5,
    "correct_number": 34,
    "wrong_number": 4,
    "skip_number": 2,
    "start_time": "2026-05-04T08:00:00",
    "finish_time": "2026-05-04T08:55:00",
    "create_at": "2026-05-04T08:55:10",
    "update_at": "2026-05-04T08:55:10",
    "exam_result": {
      "1001": 4001,
      "1002": null,
      "1003": 4010
    }
  }
}
```

---

#### 🔒 `POST /createExam`

Tạo đề thi mới kèm toàn bộ câu hỏi và đáp án.

**Request:**
```json
{
  "user_id": 1,
  "subject_id": 10,
  "title": "Đề ôn tập CSDL",
  "time": 45,
  "number": 30,
  "status": "PUBLIC",
  "question_exam_list": [
    {
      "question_title": "NoSQL là gì?",
      "question_image": null,
      "question_image_url": null,
      "question_level": "MEDIUM",
      "question_sort": 1,
      "answer_list": [
        { "content": "Not Only SQL", "image": null, "sort": 0, "type": 1 },
        { "content": "No SQL",       "image": null, "sort": 1, "type": 0 }
      ]
    }
  ]
}
```

**Response:**
```json
{ "statusCode": 200, "message": "Created", "result": "Exam created successfully" }
```

---

### 3.5 Exam History (Lịch sử thi)

#### 🔒 `POST /getExamHistoryList`

Danh sách lịch sử thi của user (có phân trang và sort).

**Request:**
```json
{
  "user_id": 1,
  "limit": 10,
  "offset": 0,
  "sort_field": "created_at",
  "sort_by": "DESC"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": [
    {
      "exam_history_id": 500,
      "title": "Đề thi cuối kỳ CSDL 2024",
      "number": 40,
      "user_create": "GV Nguyen",
      "image": "...",
      "score": 8.5
    }
  ]
}
```

---

#### 🔒 `GET /getExamHistoryDetail?user_id=1&exam_history_id=500`

Chi tiết một lần thi: thông tin đề, người tạo, thời gian.

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "id": 500,
    "exam_title": "Đề thi cuối kỳ CSDL 2024",
    "description": "...",
    "subject_title": "Cơ sở dữ liệu",
    "user_name": "GV Nguyen",
    "user_id": 2,
    "user_avatar": "...",
    "time": 60,
    "number": 40
  }
}
```

---

#### 🔒 `POST /getExamResult`

Lấy kết quả chi tiết (đáp án đã chọn) của một lần thi.

**Request:**
```json
{
  "user_id": 1,
  "exam_history_id": 500
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "id": 500,
    "exam_id": 100,
    "user_id": 1,
    "score": 8.5,
    "correct_number": 34,
    "wrong_number": 4,
    "skip_number": 2,
    "start_time": "2026-05-04T08:00:00",
    "finish_time": "2026-05-04T08:55:00",
    "exam_result": "{ \"1001\": 4001, \"1002\": null }",
    "create_at": "2026-05-04T08:55:10",
    "update_at": "2026-05-04T08:55:10",
    "delete_at": null
  }
}
```

---

### 3.6 Saved (Lưu yêu thích)

#### 🔒 `POST /postSaveExam`

Lưu / bỏ lưu một đề thi (toggle).

**Request:**
```json
{
  "user_id": 1,
  "exam_id": 100
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Saved",
  "result": {
    "user_id": 1,
    "exam_id": 100,
    "create_at": "2026-05-04T09:00:00"
  }
}
```

---

#### 🔒 `POST /savedDepartment`

Danh sách các khoa user đang theo dõi.

**Request:**
```json
{ "user_id": 1 }
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": [
    { "id": 1, "title": "Công nghệ thông tin", "image": "...", "description": "..." }
  ]
}
```

---

#### 🔒 `POST /savedSubject`

Danh sách môn học user đã lưu theo khoa.

**Request:**
```json
{
  "user_id": 1,
  "department_id": 1
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": [
    {
      "id": 10,
      "title": "Cơ sở dữ liệu",
      "image": "...",
      "exem_number": 15
    }
  ]
}
```

> Chú ý: client đọc field `exem_number` (typo từ client gốc) — **giữ nguyên để tương thích**.

---

#### 🔒 `POST /savedExam`

Danh sách đề thi user đã lưu (theo môn học hoặc tất cả).

**Request:**
```json
{
  "user_id": 1,
  "subject_id": 10,
  "type": "ALL",
  "sort_field": "created_at",
  "sort_by": "DESC"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "department_id": 1,
    "department_title": "Công nghệ thông tin",
    "subject_title": "Cơ sở dữ liệu",
    "exam_list": [
      {
        "id": 100,
        "title": "Đề thi cuối kỳ CSDL 2024",
        "image": "...",
        "time": 60,
        "number": 40,
        "saved_num": 12,
        "status": "PUBLIC"
      }
    ]
  }
}
```

---

### 3.7 File Upload

#### 🔒 `POST /postUploadFile`

Upload file ảnh cho câu hỏi / đáp án khi tạo đề thi.

**Request:** `multipart/form-data`
- `user_id` (text)
- `file` (binary)
- `folder_name` (text — ví dụ: `"question"`, `"answer"`)
- `file_name` (text — tên file mong muốn)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Uploaded",
  "result": "https://storage.example.com/question/abc123.jpg"
}
```

---

## 4. Authentication & Authorization

### 4.1 JWT

- Sử dụng **JJWT** hoặc **Spring Security OAuth2** để tạo JWT.
- Payload chứa: `sub` (user_id), `email`, `role`, `iat`, `exp`.
- Thời hạn token: **7 ngày** (configurable).
- Client gửi: `Authorization: Bearer <token>`.

### 4.2 Spring Security Config (tóm tắt)

```
Public (không cần token):
  POST /api/v1/register
  POST /api/v1/login
  POST /api/v1/forgotPassword

Protected (cần token):
  Tất cả các endpoint còn lại
```

### 4.3 Error codes

| HTTP Status | statusCode | Ý nghĩa |
|-------------|------------|---------|
| 200 | 200 | Thành công |
| 400 | 400 | Dữ liệu đầu vào không hợp lệ |
| 401 | 401 | Token không hợp lệ / hết hạn |
| 403 | 403 | Không có quyền |
| 404 | 404 | Không tìm thấy tài nguyên |
| 409 | 409 | Xung đột dữ liệu (email đã tồn tại, ...) |
| 500 | 500 | Lỗi server |

---

## 5. File Storage

Ảnh được lưu trữ trên Cloud Storage. Client đọc ảnh từ 2 base URL:

| Loại | Base URL |
|------|----------|
| Ảnh môn học | `https://storage.example.com/subject/` |
| Ảnh khoa | `https://storage.example.com/department/` |
| Ảnh avatar | `https://storage.example.com/avatars/` |
| Ảnh câu hỏi | `https://storage.example.com/question/` |
| Ảnh đáp án | `https://storage.example.com/answer/` |

Các endpoint upload (`/editAvatar`, `/postUploadFile`) nhận multipart, server lưu lên storage và trả về URL đầy đủ.

---

## 6. Cấu trúc dự án Spring Boot

```
quizshare-backend/
├── src/main/java/com/quizshare/
│   ├── QuizShareApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java          # Spring Security + JWT filter
│   │   ├── JwtConfig.java
│   │   └── StorageConfig.java           # Cloud Storage bean
│   ├── controller/
│   │   ├── AuthController.java          # /register, /login, /forgotPassword
│   │   ├── UserController.java          # /getUserInfo, /updateUserInfo, ...
│   │   ├── DepartmentController.java    # /getDepartmentList, /listDepartmentInfo
│   │   ├── SubjectController.java       # /searchSubject
│   │   ├── ExamController.java          # /listExam, /examListQuestion, /createExam
│   │   ├── ExamHistoryController.java   # /submitExam, /getExamHistoryList, ...
│   │   ├── SavedController.java         # /postSaveExam, /savedDepartment, ...
│   │   └── FileController.java          # /editAvatar, /postUploadFile
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── SubmitExamRequest.java
│   │   │   └── ...
│   │   └── response/
│   │       ├── BaseResponse.java
│   │       ├── LoginResult.java
│   │       ├── UserResult.java
│   │       └── ...
│   ├── entity/
│   │   ├── User.java
│   │   ├── Department.java
│   │   ├── Subject.java
│   │   ├── Exam.java
│   │   ├── Question.java
│   │   ├── Answer.java
│   │   ├── ExamHistory.java
│   │   ├── ExamResult.java
│   │   ├── SavedExam.java
│   │   └── SavedDepartment.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── DepartmentRepository.java
│   │   ├── SubjectRepository.java
│   │   ├── ExamRepository.java
│   │   ├── QuestionRepository.java
│   │   ├── AnswerRepository.java
│   │   ├── ExamHistoryRepository.java
│   │   ├── ExamResultRepository.java
│   │   ├── SavedExamRepository.java
│   │   └── SavedDepartmentRepository.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── DepartmentService.java
│   │   ├── SubjectService.java
│   │   ├── ExamService.java
│   │   ├── ExamHistoryService.java
│   │   ├── SavedService.java
│   │   ├── FileStorageService.java
│   │   └── JwtService.java
│   ├── security/
│   │   ├── JwtAuthFilter.java           # OncePerRequestFilter
│   │   └── UserDetailsServiceImpl.java
│   └── exception/
│       ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│       ├── AppException.java
│       └── ErrorCode.java
├── src/main/resources/
│   ├── application.yml
│   └── application-dev.yml
└── pom.xml
```

---

## 7. Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT (JJWT) -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Spring Boot Mail (forgot password) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <!-- Google Cloud Storage (tuỳ chọn — hoặc thay bằng AWS S3 SDK) -->
    <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>google-cloud-storage</artifactId>
        <version>2.40.0</version>
    </dependency>
</dependencies>
```

---

## Phụ lục: Bảng tóm tắt tất cả Endpoints

| # | Method | Path | Auth | Mô tả |
|---|--------|------|------|-------|
| 1 | POST | `/register` | ❌ | Đăng ký |
| 2 | POST | `/login` | ❌ | Đăng nhập |
| 3 | POST | `/forgotPassword` | ❌ | Quên mật khẩu |
| 4 | POST | `/getUserInfo` | ✅ | Lấy thông tin user |
| 5 | POST | `/updateUserInfo` | ✅ | Cập nhật tên, ngày sinh |
| 6 | POST | `/changeEmail` | ✅ | Đổi email |
| 7 | POST | `/changePassword` | ✅ | Đổi mật khẩu |
| 8 | POST | `/editAvatar` | ✅ | Upload ảnh đại diện |
| 9 | POST | `/unpublicUser` | ✅ | Vô hiệu hóa tài khoản |
| 10 | POST | `/getDepartmentList` | ✅ | Danh sách khoa |
| 11 | POST | `/listDepartmentInfo` | ✅ | Thông tin chi tiết các khoa |
| 12 | POST | `/searchSubject` | ✅ | Tìm kiếm môn học |
| 13 | POST | `/listExam` | ✅ | Danh sách đề thi theo môn |
| 14 | POST | `/examListQuestion` | ✅ | Câu hỏi + đáp án của đề thi |
| 15 | POST | `/submitExam` | ✅ | Nộp bài thi |
| 16 | POST | `/createExam` | ✅ | Tạo đề thi mới |
| 17 | POST | `/getExamHistoryList` | ✅ | Danh sách lịch sử thi |
| 18 | GET | `/getExamHistoryDetail` | ✅ | Chi tiết một lần thi |
| 19 | POST | `/getExamResult` | ✅ | Đáp án chi tiết của lần thi |
| 20 | POST | `/postSaveExam` | ✅ | Lưu / bỏ lưu đề thi |
| 21 | POST | `/savedDepartment` | ✅ | Khoa đang theo dõi |
| 22 | POST | `/savedSubject` | ✅ | Môn học đã lưu theo khoa |
| 23 | POST | `/savedExam` | ✅ | Đề thi đã lưu |
| 24 | POST | `/postUploadFile` | ✅ | Upload ảnh câu hỏi/đáp án |
