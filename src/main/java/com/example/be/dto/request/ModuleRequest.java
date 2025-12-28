package com.example.be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleRequest {
    @NotBlank(message = "Tên module không được để trống")
    @Size(min = 1, max = 255, message = "Tên module phải có độ dài từ 1 đến 255 ký tự")
    String name;

    @Size(max = 1000, message = "Mô tả module không được vượt quá 1000 ký tự")
    String description;

    @Size(min = 6, max = 100, message = "Mật khẩu phải có độ dài từ 6 đến 100 ký tự")
    String password;

    @Size(min = 6, max = 100, message = "Mật khẩu mới phải có độ dài từ 6 đến 100 ký tự")
    String newPassword;
}
