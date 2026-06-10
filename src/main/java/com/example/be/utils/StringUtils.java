package com.example.be.utils;

import java.text.Normalizer;

public class StringUtils {

    public static String normalize(String source) {
        if (source == null) return "";
        // 1. Chuẩn hoá Unicode NFC
        String normalized = Normalizer.normalize(source, Normalizer.Form.NFC);
        // 2. Loại bỏ toàn bộ ký tự điều khiển và ký tự tàng hình (control/format characters như BOM, zero-width space, v.v.)
        normalized = normalized.replaceAll("\\p{C}", "");
        // 3. Thay thế toàn bộ khoảng trắng Unicode (khoảng trắng toàn chiều rộng, không ngắt, v.v.) bằng khoảng trắng thường
        normalized = normalized.replaceAll("(?U)\\s", " ");
        // 4. Trim khoảng trắng thừa ở đầu/cuối
        normalized = normalized.trim();
        // 5. Rút gọn nhiều khoảng trắng liên tiếp thành 1 và chuyển về chữ thường
        return normalized.toLowerCase().replaceAll("\\s+", " ");
    }

    public static boolean isValidString(String str) {
        return str != null && !str.isBlank();
    }
}
