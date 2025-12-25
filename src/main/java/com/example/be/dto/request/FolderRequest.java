package com.example.be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FolderRequest {
    @NotBlank(message = "Tên thư mục không được để trống")
    @Size(min = 1, max = 255, message = "Tên thư mục phải từ 1 đến 255 ký tự")
    String name;
}
