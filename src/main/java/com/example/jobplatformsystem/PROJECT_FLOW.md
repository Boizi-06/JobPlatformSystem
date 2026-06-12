# PROJECT FLOW - JOB PLATFORM SYSTEM

## 1. Tổng quan luồng hoạt động

Hệ thống được chia thành các tầng chính:

Client/Postman
→ Controller
→ Service
→ Repository
→ Database

Ý nghĩa từng tầng:

* **Controller**: nhận request từ client, lấy dữ liệu trong body/path/query, sau đó gọi service.
* **Service**: xử lý nghiệp vụ chính, kiểm tra dữ liệu, tạo/sửa/xóa entity.
* **Repository**: giao tiếp với database thông qua Spring Data JPA.
* **Database**: lưu trữ user, job, application, refresh token.
* **Security**: kiểm tra JWT token khi gọi các API cần đăng nhập.
* **AOP Logging**: tự động ghi log thời gian chạy của controller và service.

---

# 2. AUTH MODULE

## 2.1 Đăng ký tài khoản

### Mục đích

Chức năng này dùng để tạo tài khoản mới cho người dùng. Người dùng có thể đăng ký với vai trò `ADMIN`, `EMPLOYER` hoặc `CANDIDATE`.

### API

```http
POST /api/v1/auth/register
```

### Body

```json
{
  "username": "son",
  "email": "son@gmail.com",
  "password": "123456",
  "role": "CANDIDATE"
}
```

### Luồng hoạt động

Client gửi thông tin đăng ký lên `AuthController`.

`AuthController.register()` nhận dữ liệu dạng `RegisterRequest`, sau đó gọi `UserService.register()`.

Trong `UserServiceImpl.register()` hệ thống kiểm tra username đã tồn tại chưa bằng `existsByUsername()`. Nếu username đã tồn tại thì trả lỗi.

Sau đó hệ thống kiểm tra email đã tồn tại chưa bằng `existsByEmail()`. Nếu email đã tồn tại thì trả lỗi.

Nếu dữ liệu hợp lệ, hệ thống mã hóa password bằng `PasswordEncoder`. Mật khẩu thật không được lưu trực tiếp trong database.

Sau đó hệ thống tạo object `User`, set trạng thái `active = true`, rồi lưu vào bảng `users`.

Cuối cùng trả về `UserResponse`.

### Response thành công

```json
{
  "id": 1,
  "username": "son",
  "email": "son@gmail.com",
  "role": "CANDIDATE",
  "active": true
}
```

### Test case

| Mã         | Trường hợp          | Dữ liệu test        | Kết quả mong đợi          |
| ---------- | ------------------- | ------------------- | ------------------------- |
| TC-AUTH-01 | Đăng ký thành công  | username/email mới  | Tạo user thành công       |
| TC-AUTH-02 | Trùng username      | username đã tồn tại | `Username already exists` |
| TC-AUTH-03 | Trùng email         | email đã tồn tại    | `Email already exists`    |
| TC-AUTH-04 | Email sai định dạng | email = `abc`       | 400 Bad Request           |
| TC-AUTH-05 | Thiếu password      | không gửi password  | 400 Bad Request           |

---

## 2.2 Đăng nhập

### Mục đích

Chức năng này dùng để xác thực tài khoản. Nếu đăng nhập đúng, hệ thống cấp `accessToken` và `refreshToken`.

### API

```http
POST /api/v1/auth/login
```

### Body

```json
{
  "email": "son@gmail.com",
  "password": "123456"
}
```

### Luồng hoạt động

Client gửi email và password lên `AuthController.login()`.

Controller gọi `UserService.login()`.

Trong service, hệ thống tìm user theo email bằng `findByEmail()`.

Nếu email không tồn tại, hệ thống trả lỗi `Email not found`.

Nếu email tồn tại, hệ thống dùng `passwordEncoder.matches()` để so sánh password người dùng nhập với password đã mã hóa trong database.

Nếu password sai, hệ thống trả lỗi `Invalid password`.

Nếu đúng, hệ thống dùng `JwtService.generateToken()` để tạo access token.

Sau đó hệ thống tạo refresh token bằng `RefreshTokenService.createRefreshToken()` và lưu vào bảng `refresh_tokens`.

Cuối cùng trả về `AuthResponse`.

### Response thành công

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "b4c7-xxxx-xxxx",
  "id": 1,
  "username": "son",
  "email": "son@gmail.com",
  "role": "CANDIDATE"
}
```

### Test case

| Mã          | Trường hợp          | Dữ liệu test        | Kết quả mong đợi                |
| ----------- | ------------------- | ------------------- | ------------------------------- |
| TC-LOGIN-01 | Login thành công    | email/password đúng | Trả accessToken và refreshToken |
| TC-LOGIN-02 | Sai email           | email không tồn tại | `Email not found`               |
| TC-LOGIN-03 | Sai password        | password sai        | `Invalid password`              |
| TC-LOGIN-04 | Email sai định dạng | email = `abc`       | 400 Bad Request                 |
| TC-LOGIN-05 | Thiếu password      | không gửi password  | 400 Bad Request                 |

---

## 2.3 Refresh Token

### Mục đích

Khi access token hết hạn, người dùng có thể dùng refresh token để lấy access token mới mà không cần đăng nhập lại.

### API

```http
POST /api/v1/auth/refresh-token
```

### Body

```json
{
  "refreshToken": "refresh-token-value"
}
```

### Luồng hoạt động

Client gửi refresh token lên `AuthController.refreshToken()`.

Controller gọi `RefreshTokenService.verifyRefreshToken()`.

Service tìm refresh token trong database bằng `findByToken()`.

Nếu token không tồn tại, trả lỗi `Refresh token not found`.

Nếu token đã hết hạn, hệ thống xóa token khỏi database và trả lỗi `Refresh token expired`.

Nếu token hợp lệ, hệ thống lấy user từ refresh token, load user bằng email, tạo access token mới bằng `JwtService.generateToken()`.

Sau đó trả về access token mới và refresh token cũ.

### Response thành công

```json
{
  "accessToken": "new-access-token",
  "refreshToken": "old-refresh-token"
}
```

### Test case

| Mã            | Trường hợp          | Dữ liệu test        | Kết quả mong đợi          |
| ------------- | ------------------- | ------------------- | ------------------------- |
| TC-REFRESH-01 | Refresh thành công  | refreshToken hợp lệ | Trả accessToken mới       |
| TC-REFRESH-02 | Token không tồn tại | token sai           | `Refresh token not found` |
| TC-REFRESH-03 | Token hết hạn       | token expired       | `Refresh token expired`   |
| TC-REFRESH-04 | Body rỗng           | không gửi token     | Lỗi xử lý token           |

---

## 2.4 Logout

### Mục đích

Chức năng logout dùng để đăng xuất người dùng bằng cách xóa refresh token khỏi database.

### API

```http
POST /api/v1/auth/logout
```

### Body

```json
{
  "refreshToken": "refresh-token-value"
}
```

### Luồng hoạt động

Client gửi refresh token lên `AuthController.logout()`.

Controller gọi `RefreshTokenService.revokeRefreshToken()`.

Service tìm refresh token bằng `findByToken()`.

Nếu tồn tại, hệ thống xóa token khỏi bảng `refresh_tokens`.

Sau khi xóa, refresh token đó không thể dùng để lấy access token mới nữa.

### Response thành công

```text
Logout successful
```

### Test case

| Mã           | Trường hợp                 | Dữ liệu test         | Kết quả mong đợi          |
| ------------ | -------------------------- | -------------------- | ------------------------- |
| TC-LOGOUT-01 | Logout thành công          | refreshToken hợp lệ  | `Logout successful`       |
| TC-LOGOUT-02 | Token không tồn tại        | token sai            | `Refresh token not found` |
| TC-LOGOUT-03 | Logout xong dùng lại token | dùng refreshToken cũ | Không refresh được nữa    |

---

## 2.5 Đổi mật khẩu

### Mục đích

Cho phép người dùng đã đăng nhập đổi mật khẩu hiện tại sang mật khẩu mới.

### API

```http
POST /api/v1/auth/change-password
```

### Header

```http
Authorization: Bearer access-token
```

### Body

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

### Luồng hoạt động

Client gửi request kèm access token.

`JwtAuthenticationFilter` đọc token trong header `Authorization`.

Filter giải mã token, lấy email người dùng, load user và đưa thông tin vào `SecurityContextHolder`.

`AuthController.changePassword()` lấy email hiện tại bằng `authentication.getName()`.

Controller gọi `UserService.changePassword(email, request)`.

Service tìm user theo email bằng `findByEmail()`.

Sau đó kiểm tra mật khẩu cũ bằng `passwordEncoder.matches()`.

Nếu mật khẩu cũ sai, trả lỗi `Old password is incorrect`.

Nếu đúng, hệ thống mã hóa mật khẩu mới bằng `passwordEncoder.encode()` rồi lưu lại vào database.

### Response thành công

```text
Password changed successfully
```

### Test case

| Mã           | Trường hợp                             | Dữ liệu test        | Kết quả mong đợi                |
| ------------ | -------------------------------------- | ------------------- | ------------------------------- |
| TC-CHANGE-01 | Đổi mật khẩu thành công                | oldPassword đúng    | `Password changed successfully` |
| TC-CHANGE-02 | Sai mật khẩu cũ                        | oldPassword sai     | `Old password is incorrect`     |
| TC-CHANGE-03 | Không gửi token                        | thiếu Authorization | 401 Unauthorized                |
| TC-CHANGE-04 | Token sai                              | token không hợp lệ  | 401 Unauthorized                |
| TC-CHANGE-05 | Đăng nhập bằng mật khẩu cũ sau khi đổi | password cũ         | Login fail                      |
| TC-CHANGE-06 | Đăng nhập bằng mật khẩu mới            | password mới        | Login thành công                |

---

## 2.6 Quên mật khẩu

### Mục đích

Chức năng này dùng để reset password khi người dùng quên mật khẩu.

### API

```http
POST /api/v1/auth/forgot-password
```

### Body

```json
{
  "email": "son@gmail.com"
}
```

### Luồng hoạt động

Client gửi email lên `AuthController.forgotPassword()`.

Controller gọi `UserService.forgotPassword(email)`.

Service tìm user theo email bằng `findByEmail()`.

Nếu không tìm thấy email, trả lỗi `Email not found`.

Nếu tìm thấy, hệ thống sinh mật khẩu mới theo dạng `Job + số ngẫu nhiên`.

Sau đó password mới được mã hóa bằng `passwordEncoder.encode()` và lưu vào database.

Cuối cùng hệ thống trả mật khẩu mới cho client.

### Response thành công

```text
New password: Job5732
```

### Test case

| Mã           | Trường hợp                  | Dữ liệu test  | Kết quả mong đợi  |
| ------------ | --------------------------- | ------------- | ----------------- |
| TC-FORGOT-01 | Reset thành công            | email tồn tại | Trả password mới  |
| TC-FORGOT-02 | Email không tồn tại         | email sai     | `Email not found` |
| TC-FORGOT-03 | Đăng nhập bằng password cũ  | password cũ   | Login fail        |
| TC-FORGOT-04 | Đăng nhập bằng password mới | password mới  | Login thành công  |

---

# 3. USER MODULE

## 3.1 Lấy danh sách user

### API

```http
GET /api/v1/users
```

### Luồng hoạt động

Client gọi `UserController.getAllUsers()`.

Controller gọi `UserService.getAllUsers()`.

Service gọi `userRepository.findAll()` để lấy toàn bộ user.

Sau đó dữ liệu entity được map sang `UserResponse` bằng `UserMapper`.

Kết quả trả về là danh sách user.

### Test case

| Mã         | Trường hợp                        | Kết quả mong đợi   |
| ---------- | --------------------------------- | ------------------ |
| TC-USER-01 | Có user trong DB                  | Trả danh sách user |
| TC-USER-02 | DB chưa có user                   | Trả list rỗng      |
| TC-USER-03 | Không gửi token nếu API protected | 401 Unauthorized   |

---

## 3.2 Lấy user theo ID

### API

```http
GET /api/v1/users/{id}
```

Ví dụ:

```http
GET /api/v1/users/1
```

### Luồng hoạt động

Controller nhận `id` từ path variable.

Service gọi `userRepository.findById(id)`.

Nếu tìm thấy user, hệ thống map sang `UserResponse`.

Nếu không tìm thấy, ném `ResourceNotFoundException`.

### Test case

| Mã         | Trường hợp       | Kết quả mong đợi         |
| ---------- | ---------------- | ------------------------ |
| TC-USER-04 | ID tồn tại       | Trả thông tin user       |
| TC-USER-05 | ID không tồn tại | `User not found with id` |
| TC-USER-06 | ID sai định dạng | 400 Bad Request          |

---

## 3.3 Xóa user

### API

```http
DELETE /api/v1/users/{id}
```

### Luồng hoạt động

Controller nhận id.

Service tìm user bằng `findById()`.

Nếu user tồn tại, gọi `userRepository.delete(user)`.

Nếu không tồn tại, trả lỗi.

### Response thành công

```text
User deleted successfully
```

### Test case

| Mã         | Trường hợp             | Kết quả mong đợi            |
| ---------- | ---------------------- | --------------------------- |
| TC-USER-07 | Xóa user tồn tại       | `User deleted successfully` |
| TC-USER-08 | Xóa user không tồn tại | `User not found with id`    |
| TC-USER-09 | Xóa xong tìm lại       | Không tìm thấy user         |

---

## 3.4 Tìm kiếm user

### API

```http
GET /api/v1/users/search?keyword=son
```

### Luồng hoạt động

Controller lấy keyword từ query param.

Service gọi `findByUsernameContainingIgnoreCase(keyword)`.

Hệ thống tìm user có username chứa keyword, không phân biệt hoa thường.

### Test case

| Mã         | Trường hợp                        | Kết quả mong đợi      |
| ---------- | --------------------------------- | --------------------- |
| TC-USER-10 | Keyword có kết quả                | Trả danh sách phù hợp |
| TC-USER-11 | Keyword không có kết quả          | List rỗng             |
| TC-USER-12 | Keyword viết hoa/thường khác nhau | Vẫn tìm được          |

---

## 3.5 Phân trang user

### API

```http
GET /api/v1/users/page?page=0&size=5
```

### Luồng hoạt động

Controller nhận `page` và `size`.

Service tạo `PageRequest.of(page, size)`.

Repository gọi `findAll(pageable)`.

Kết quả trả về dạng `Page<UserResponse>`.

### Test case

| Mã         | Trường hợp             | Kết quả mong đợi            |
| ---------- | ---------------------- | --------------------------- |
| TC-USER-13 | page=0 size=5          | Trả 5 user đầu              |
| TC-USER-14 | page lớn hơn số trang  | content rỗng                |
| TC-USER-15 | Không truyền page/size | Dùng mặc định page=0 size=5 |

---

# 4. JOB MODULE

## 4.1 Tạo job

### API

```http
POST /api/v1/jobs
```

### Body

```json
{
  "title": "Java Developer",
  "description": "Spring Boot backend",
  "salary": 1500,
  "location": "Ha Noi",
  "deadline": "2026-12-31",
  "employerId": 2
}
```

### Luồng hoạt động

Client gửi thông tin job lên `JobController.createJob()`.

Controller gọi `JobService.createJob()`.

Service kiểm tra employer có tồn tại không bằng `userRepository.findById(employerId)`.

Nếu employer không tồn tại, trả lỗi `Employer not found`.

Nếu tồn tại, hệ thống tạo entity `Job`.

Trạng thái ban đầu của job là `PENDING`, nghĩa là job đang chờ admin duyệt.

Sau đó gọi `jobRepository.save(job)` để lưu vào database.

### Response thành công

```json
{
  "id": 1,
  "title": "Java Developer",
  "description": "Spring Boot backend",
  "salary": 1500,
  "location": "Ha Noi",
  "deadline": "2026-12-31",
  "status": "PENDING",
  "employerId": 2,
  "employerUsername": "employer01"
}
```

### Test case

| Mã        | Trường hợp             | Kết quả mong đợi             |
| --------- | ---------------------- | ---------------------------- |
| TC-JOB-01 | Tạo job hợp lệ         | Job được tạo, status PENDING |
| TC-JOB-02 | Employer không tồn tại | `Employer not found`         |
| TC-JOB-03 | Thiếu title            | 400 Bad Request              |
| TC-JOB-04 | Thiếu salary           | 400 Bad Request              |
| TC-JOB-05 | Thiếu deadline         | 400 Bad Request              |

---

## 4.2 Lấy danh sách job

### API

```http
GET /api/v1/jobs
```

### Luồng hoạt động

Controller gọi `JobService.getAllJobs()`.

Service gọi `jobRepository.findAll()`.

Danh sách job được map sang `JobResponse`.

### Test case

| Mã        | Trường hợp   | Kết quả mong đợi  |
| --------- | ------------ | ----------------- |
| TC-JOB-06 | Có job       | Trả danh sách job |
| TC-JOB-07 | Không có job | List rỗng         |

---

## 4.3 Lấy job theo ID

### API

```http
GET /api/v1/jobs/{id}
```

### Luồng hoạt động

Service gọi `jobRepository.findById(id)`.

Nếu tìm thấy, trả `JobResponse`.

Nếu không thấy, trả `Job not found`.

### Test case

| Mã        | Trường hợp       | Kết quả mong đợi  |
| --------- | ---------------- | ----------------- |
| TC-JOB-08 | ID tồn tại       | Trả thông tin job |
| TC-JOB-09 | ID không tồn tại | `Job not found`   |

---

## 4.4 Cập nhật job

### API

```http
PUT /api/v1/jobs/{id}
```

### Body

```json
{
  "title": "Senior Java Developer",
  "description": "Java Spring Boot",
  "salary": 2000,
  "location": "Ha Noi",
  "deadline": "2026-12-31"
}
```

### Luồng hoạt động

Controller nhận id và body.

Service tìm job theo id.

Nếu job không tồn tại, trả lỗi.

Nếu tồn tại, cập nhật các trường title, description, salary, location, deadline.

Sau đó lưu lại database.

### Test case

| Mã        | Trường hợp        | Kết quả mong đợi  |
| --------- | ----------------- | ----------------- |
| TC-JOB-10 | Update thành công | Job được cập nhật |
| TC-JOB-11 | Job không tồn tại | `Job not found`   |
| TC-JOB-12 | Thiếu title       | 400 Bad Request   |

---

## 4.5 Xóa job

### API

```http
DELETE /api/v1/jobs/{id}
```

### Response

```text
Job deleted successfully
```

### Test case

| Mã        | Trường hợp            | Kết quả mong đợi |
| --------- | --------------------- | ---------------- |
| TC-JOB-13 | Xóa job tồn tại       | Thành công       |
| TC-JOB-14 | Xóa job không tồn tại | `Job not found`  |

---

## 4.6 Approve job

### API

```http
PUT /api/v1/jobs/{id}/approve
```

### Luồng hoạt động

Admin gọi API approve.

Service tìm job.

Nếu có, set status từ `PENDING` sang `APPROVED`.

Sau đó lưu lại database.

### Test case

| Mã        | Trường hợp          | Kết quả mong đợi  |
| --------- | ------------------- | ----------------- |
| TC-JOB-15 | Approve job tồn tại | status = APPROVED |
| TC-JOB-16 | Job không tồn tại   | `Job not found`   |

---

## 4.7 Reject job

### API

```http
PUT /api/v1/jobs/{id}/reject
```

### Luồng hoạt động

Service tìm job.

Nếu có, set status = `REJECTED`.

### Test case

| Mã        | Trường hợp         | Kết quả mong đợi  |
| --------- | ------------------ | ----------------- |
| TC-JOB-17 | Reject job tồn tại | status = REJECTED |
| TC-JOB-18 | Job không tồn tại  | `Job not found`   |

---

## 4.8 Search job

### API

```http
GET /api/v1/jobs/search?keyword=java
```

### Luồng hoạt động

Repository gọi `findByTitleContainingIgnoreCase(keyword)`.

Hệ thống tìm job có title chứa keyword.

### Test case

| Mã        | Trường hợp               | Kết quả mong đợi  |
| --------- | ------------------------ | ----------------- |
| TC-JOB-19 | Keyword có kết quả       | Trả danh sách job |
| TC-JOB-20 | Keyword không có kết quả | List rỗng         |
| TC-JOB-21 | Keyword khác hoa thường  | Vẫn tìm được      |

---

## 4.9 Phân trang job

### API

```http
GET /api/v1/jobs/paging?page=0&size=5
```

### Test case

| Mã        | Trường hợp             | Kết quả mong đợi |
| --------- | ---------------------- | ---------------- |
| TC-JOB-22 | page=0 size=5          | Trả 5 job đầu    |
| TC-JOB-23 | page vượt quá số trang | content rỗng     |
| TC-JOB-24 | Không truyền page/size | Dùng mặc định    |

---

# 5. APPLICATION MODULE

## 5.1 Ứng tuyển job

### API

```http
POST /api/v1/applications/apply
```

### Body

```json
{
  "candidateId": 1,
  "jobId": 2,
  "coverLetter": "Tôi muốn ứng tuyển vị trí này."
}
```

### Luồng hoạt động

Client gửi candidateId, jobId và coverLetter.

Controller gọi `ApplicationService.applyJob()`.

Service tìm candidate bằng `userRepository.findById(candidateId)`.

Nếu không tồn tại, trả `Candidate not found`.

Service tìm job bằng `jobRepository.findById(jobId)`.

Nếu không tồn tại, trả `Job not found`.

Nếu cả candidate và job đều hợp lệ, hệ thống tạo entity `Application`.

Application có:

* candidate
* job
* coverLetter
* appliedAt = thời gian hiện tại
* status = PENDING

Sau đó lưu vào bảng `applications`.

### Response thành công

```json
{
  "id": 1,
  "candidateId": 1,
  "candidateName": "son",
  "jobId": 2,
  "jobTitle": "Java Developer",
  "coverLetter": "Tôi muốn ứng tuyển vị trí này.",
  "appliedAt": "2026-06-12T08:00:00",
  "status": "PENDING"
}
```

### Test case

| Mã        | Trường hợp              | Kết quả mong đợi           |
| --------- | ----------------------- | -------------------------- |
| TC-APP-01 | Apply thành công        | Application status PENDING |
| TC-APP-02 | Candidate không tồn tại | `Candidate not found`      |
| TC-APP-03 | Job không tồn tại       | `Job not found`            |
| TC-APP-04 | Thiếu candidateId       | 400 Bad Request            |
| TC-APP-05 | Thiếu jobId             | 400 Bad Request            |

---

## 5.2 Xem hồ sơ theo candidate

### API

```http
GET /api/v1/applications/candidate/{candidateId}
```

### Luồng hoạt động

Controller nhận candidateId.

Service gọi `applicationRepository.findByCandidateId(candidateId)`.

Kết quả được map sang danh sách `ApplicationResponse`.

### Test case

| Mã        | Trường hợp               | Kết quả mong đợi          |
| --------- | ------------------------ | ------------------------- |
| TC-APP-06 | Candidate có hồ sơ       | Trả danh sách application |
| TC-APP-07 | Candidate chưa ứng tuyển | List rỗng                 |

---

## 5.3 Xem hồ sơ theo job

### API

```http
GET /api/v1/applications/job/{jobId}
```

### Luồng hoạt động

Service gọi `applicationRepository.findByJobId(jobId)`.

Dùng để employer xem danh sách ứng viên đã nộp vào một job cụ thể.

### Test case

| Mã        | Trường hợp           | Kết quả mong đợi    |
| --------- | -------------------- | ------------------- |
| TC-APP-08 | Job có ứng viên      | Trả danh sách hồ sơ |
| TC-APP-09 | Job chưa có ứng viên | List rỗng           |

---

## 5.4 Cập nhật trạng thái hồ sơ

### API

```http
PUT /api/v1/applications/{id}/status
```

### Body

```json
{
  "status": "INTERVIEWING"
}
```

### Luồng hoạt động

Employer hoặc admin cập nhật trạng thái hồ sơ.

Controller nhận id của application và status mới.

Service tìm application theo id.

Nếu không tồn tại, trả lỗi `Application not found`.

Nếu tồn tại, cập nhật status mới và lưu database.

### Các trạng thái hợp lệ

```text
PENDING
REVIEWING
INTERVIEWING
ACCEPTED
REJECTED
```

### Test case

| Mã        | Trường hợp                | Kết quả mong đợi        |
| --------- | ------------------------- | ----------------------- |
| TC-APP-10 | Update thành công         | status được cập nhật    |
| TC-APP-11 | Application không tồn tại | `Application not found` |
| TC-APP-12 | Status sai enum           | 400 Bad Request         |

---

# 6. FILE MODULE

## 6.1 Upload CV

### API

```http
POST /api/v1/files/upload-cv/{userId}
```

### Postman

Chọn tab Body.

Chọn `form-data`.

Thêm key:

```text
file
```

Type chọn `File`.

Sau đó chọn file CV từ máy.

### Luồng hoạt động

Client gửi file dạng MultipartFile.

Controller nhận `userId` và file.

Controller gọi `FileStorageService.uploadCv(userId, file)`.

Service tìm user bằng `userRepository.findById(userId)`.

Nếu user không tồn tại, trả `User not found`.

Nếu tồn tại, hệ thống tạo tên file mới bằng thời gian hiện tại kết hợp với tên file gốc.

Sau đó tạo thư mục `uploads/cv` nếu chưa tồn tại.

File được ghi xuống thư mục này bằng `Files.write()`.

Sau khi lưu file thành công, hệ thống cập nhật `cvUrl` của user.

Cuối cùng lưu user lại database.

### Response thành công

```text
uploads/cv/1718171000000_cv.pdf
```

### Test case

| Mã         | Trường hợp         | Kết quả mong đợi   |
| ---------- | ------------------ | ------------------ |
| TC-FILE-01 | Upload thành công  | Trả đường dẫn file |
| TC-FILE-02 | User không tồn tại | `User not found`   |
| TC-FILE-03 | Không chọn file    | `Upload failed`    |
| TC-FILE-04 | File rỗng          | `Upload failed`    |

---

# 7. EXCEPTION HANDLING

## Mục đích

Hệ thống dùng `GlobalExceptionHandler` để xử lý lỗi tập trung.

Thay vì mỗi controller tự xử lý lỗi, mọi exception sẽ được đưa về một nơi.

Điều này giúp response lỗi đồng nhất và dễ debug.

### Các lỗi chính

| Exception                       | HTTP Status | Ý nghĩa                |
| ------------------------------- | ----------- | ---------------------- |
| ResourceNotFoundException       | 404         | Không tìm thấy dữ liệu |
| DuplicateResourceException      | 409         | Dữ liệu bị trùng       |
| BadRequestException             | 400         | Request sai            |
| UnauthorizedException           | 401         | Chưa xác thực          |
| ForbiddenException              | 403         | Không đủ quyền         |
| MethodArgumentNotValidException | 400         | Validation lỗi         |
| Exception                       | 500         | Lỗi hệ thống           |

### Ví dụ lỗi

```json
{
  "timestamp": "2026-06-12T08:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Job not found"
}
```

---

# 8. AOP LOGGING

## Mục đích

AOP Logging dùng để ghi lại thời gian thực hiện của các chức năng trong hệ thống.

Thay vì viết log trong từng method, hệ thống dùng `LoggingAspect` để tự động bắt các method trong controller và service.

### Pointcut

```java
execution(* com.example.jobplatformsystem.controller..*(..)) ||
execution(* com.example.jobplatformsystem.service..*(..))
```

Nghĩa là mọi method trong package controller và service đều được log.

### Ví dụ log

```text
UserServiceImpl.register executed in 396 ms
AuthController.register executed in 396 ms
JobServiceImpl.createJob executed in 120 ms
ApplicationServiceImpl.applyJob executed in 80 ms
```

### Ý nghĩa

Log này giúp biết chức năng nào chạy nhanh, chức năng nào chạy chậm.

Khi hệ thống có lỗi hiệu năng, ta có thể dựa vào log để tìm điểm nghẽn.

### Test case

| Mã        | Trường hợp         | Kết quả mong đợi                                       |
| --------- | ------------------ | ------------------------------------------------------ |
| TC-LOG-01 | Gọi API register   | Có log AuthController và UserServiceImpl               |
| TC-LOG-02 | Gọi API create job | Có log JobController và JobServiceImpl                 |
| TC-LOG-03 | Gọi API apply job  | Có log ApplicationController và ApplicationServiceImpl |
| TC-LOG-04 | Gọi API lỗi        | Có log failed hoặc exception                           |
| TC-LOG-05 | Kiểm tra file log  | Có file `logs/job-platform.log`                        |

---

# 9. KỊCH BẢN TEST POSTMAN HOÀN CHỈNH

## Bước 1: Register Candidate

```http
POST /api/v1/auth/register
```

```json
{
  "username": "candidate01",
  "email": "candidate01@gmail.com",
  "password": "123456",
  "role": "CANDIDATE"
}
```

## Bước 2: Register Employer

```json
{
  "username": "employer01",
  "email": "employer01@gmail.com",
  "password": "123456",
  "role": "EMPLOYER"
}
```

## Bước 3: Login Employer

```json
{
  "email": "employer01@gmail.com",
  "password": "123456"
}
```

Copy accessToken và refreshToken.

## Bước 4: Create Job

```http
POST /api/v1/jobs
```

```json
{
  "title": "Java Developer",
  "description": "Spring Boot Backend",
  "salary": 1500,
  "location": "Ha Noi",
  "deadline": "2026-12-31",
  "employerId": 2
}
```

## Bước 5: Approve Job

```http
PUT /api/v1/jobs/1/approve
```

## Bước 6: Login Candidate

```json
{
  "email": "candidate01@gmail.com",
  "password": "123456"
}
```

## Bước 7: Upload CV

```http
POST /api/v1/files/upload-cv/1
```

Body: form-data, key `file`, type File.

## Bước 8: Apply Job

```http
POST /api/v1/applications/apply
```

```json
{
  "candidateId": 1,
  "jobId": 1,
  "coverLetter": "Tôi muốn ứng tuyển vị trí Java Developer."
}
```

## Bước 9: Employer xem hồ sơ theo job

```http
GET /api/v1/applications/job/1
```

## Bước 10: Update Application Status

```http
PUT /api/v1/applications/1/status
```

```json
{
  "status": "INTERVIEWING"
}
```

## Bước 11: Candidate xem lịch sử ứng tuyển

```http
GET /api/v1/applications/candidate/1
```

## Bước 12: Change Password

```http
POST /api/v1/auth/change-password
```

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

## Bước 13: Forgot Password

```http
POST /api/v1/auth/forgot-password
```

```json
{
  "email": "candidate01@gmail.com"
}
```

## Bước 14: Logout

```http
POST /api/v1/auth/logout
```

```json
{
  "refreshToken": "refresh-token-value"
}
```

---

# 10. KẾT LUẬN

Tài liệu này mô tả chi tiết luồng hoạt động của hệ thống Job Platform System.

Hệ thống đã có đầy đủ các nhóm chức năng:

* Xác thực và phân quyền
* Quản lý người dùng
* Quản lý việc làm
* Ứng tuyển việc làm
* Upload CV
* Quản lý trạng thái hồ sơ
* Refresh token
* Đổi mật khẩu
* Quên mật khẩu
* Ghi log thời gian thực hiện bằng AOP
* Xử lý lỗi tập trung bằng GlobalExceptionHandler

Hệ thống phù hợp với mô hình RESTful API và có thể dùng làm backend cho website hoặc mobile app tuyển dụng.
