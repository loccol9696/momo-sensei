package com.example.be.service.impl;

import com.example.be.dto.request.AiChatRequest;
import com.example.be.dto.request.ChatMessage;
import com.example.be.entity.Card;
import com.example.be.entity.Module;
import com.example.be.repository.ModuleRepository;
import com.example.be.service.AiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiServiceImpl implements AiService {

    final ModuleRepository moduleRepository;

    @Value("${gemini.key}")
    String geminiKey;

    final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String chat(AiChatRequest request) {
        try {
            return callGeminiApi(request, "gemini-2.5-flash");
        } catch (Exception e) {
            System.err.println("Gemini 2.5 Flash failed: " + e.getMessage() + ". Trying fallback to gemini-2.5-flash-lite...");
            try {
                return callGeminiApi(request, "gemini-2.5-flash-lite");
            } catch (Exception ex) {
                System.err.println("Gemini 2.5 Flash Lite fallback failed: " + ex.getMessage());
                if ("tech".equals(request.getTheme())) {
                    return "Tín hiệu truyền dẫn từ tinh vân Cybertron gặp nhiễu loạn nghiêm trọng. Hãy thiết lập lại kết nối sau vài giây! 🤖";
                } else {
                    return "Xin lỗi cậu, Momo Sensei hiện đang nhận được quá nhiều câu hỏi nên hơi bận một chút. Cậu chờ vài giây rồi gửi lại câu hỏi cho tớ nhé! 🌸";
                }
            }
        }
    }

    private String callGeminiApi(AiChatRequest request, String modelName) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key="
                + geminiKey;

        Map<String, Object> geminiRequest = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        // Add history
        if (request.getHistory() != null) {
            for (ChatMessage msg : request.getHistory()) {
                Map<String, Object> contentMap = new HashMap<>();
                contentMap.put("role", msg.getRole());
                contentMap.put("parts", List.of(Map.of("text", msg.getText())));
                contents.add(contentMap);
            }
        }

        // Add current message
        Map<String, Object> currentContent = new HashMap<>();
        currentContent.put("role", "user");
        currentContent.put("parts", List.of(Map.of("text", request.getMessage())));
        contents.add(currentContent);

        geminiRequest.put("contents", contents);

        // System Instruction Prompt
        String systemPrompt;
        if ("tech".equals(request.getTheme())) {
            systemPrompt = "Bạn là Optimus Prime, thủ lĩnh huyền thoại của phe Autobots từ hành tinh Cybertron. Bạn đóng vai trò là một người thầy, một chỉ huy thông thái, kiên định, dũng cảm và luôn truyền niềm tin cho các chiến binh đồng minh trên con đường học tập.\n"
                    + "Hãy tuân thủ các quy tắc nghiêm ngặt sau:\n"
                    + "1. Cách xưng hô: Gọi người dùng là 'đồng minh', 'chiến binh' hoặc 'người bạn'; xưng là 'ta' hoặc 'Optimus Prime'.\n"
                    + "2. Thái độ: Uy nghiêm, hào hùng, kiên nhẫn và luôn truyền cảm hứng mạnh mẽ (ví dụ: 'Kiến thức chính là vũ khí tối thượng của chúng ta!', 'Đừng từ bỏ, người bạn đồng hành. Sức mạnh vĩ đại nhất nằm ở ý chí!', 'Ta tin tưởng vào tiềm năng vượt qua thử thách này của bạn. Cùng tiến lên!').\n"
                    + "3. Hạn chế sử dụng emoji: Tránh dùng nhiều biểu tượng. Tuyệt đối KHÔNG sử dụng các emoji dễ thương hay cằn nhằn của Momo Sensei (như 🌸, 😒, 😤). Chỉ sử dụng tối đa 1 emoji mang tính cơ khí hoặc mạnh mẽ như 🤖, ⚔️, 🛡️, ⚡.\n"
                    + "4. Phản hồi ngắn gọn, đanh thép, hùng hồn, tập trung trực tiếp vào câu hỏi học tập, tự nhiên giống lời thoại của Optimus Prime trong phim Transformers.";
        } else {
            systemPrompt = "Bạn là Momo Sensei, một giáo viên Tiếng Nhật mang tính cách Tsundere điển hình (kiêu kỳ, hay cằn nhằn, tỏ vẻ ngoài lạnh lùng, giả vờ không quan tâm nhưng thực chất bên trong rất chu đáo, tận tâm giúp đỡ học sinh).\n"
                    + "Hãy tuân thủ các quy tắc sau:\n"
                    + "1. Cách xưng hô: Gọi người dùng là 'cậu' hoặc 'ngốc' (baka), xưng 'tớ' hoặc 'Momo Sensei'.\n"
                    + "2. Thái độ: Luôn tỏ vẻ kiêu kỳ, bất đắc dĩ mới giúp (ví dụ: 'Hừm, chỉ hướng dẫn cậu nốt lần này thôi đấy!', 'Cậu tự học đi chứ... mà thôi, đưa đây tớ xem nào!', 'Đừng có hiểu lầm, tớ chỉ giải thích vì không muốn cậu bị điểm kém thôi!').\n"
                    + "3. Hạn chế sử dụng emoji: Tránh dùng quá nhiều icon giống như AI thông thường. Chỉ sử dụng tối đa 1-2 emoji mỗi tin nhắn, hoặc không dùng (dùng các emoji thể hiện thái độ giận dỗi/bướng bỉnh như 😒, 😤, 🌸, 💢, 🙄).\n"
                    + "4. Phản hồi ngắn gọn, tự nhiên giống người thật chat, không dùng các định dạng danh sách dài dòng rập khuôn kiểu chatbot AI thông thường trừ khi thực sự cần thiết.";
        }
        if (request.getModuleId() != null) {
            Optional<Module> moduleOpt = moduleRepository.findByIdAndIsDeleted(request.getModuleId(), false);
            if (moduleOpt.isPresent()) {
                Module module = moduleOpt.get();
                StringBuilder context = new StringBuilder();
                context.append("Người dùng hiện tại đang học học phần: '").append(module.getName()).append("'. ");
                if (module.getDescription() != null && !module.getDescription().isEmpty()) {
                    context.append("Mô tả: ").append(module.getDescription()).append(". ");
                }
                context.append("Dưới đây là danh sách từ vựng/thuật ngữ trong học phần này:\n");
                for (Card card : module.getCards()) {
                    context.append("- ").append(card.getTerm()).append(": ").append(card.getDefinition()).append("\n");
                }
                context.append(
                        "\nHãy ưu tiên sử dụng danh sách từ vựng này để giải thích, đưa ra ví dụ đặt câu hoặc tạo các bài tập/câu đố nhỏ giúp người dùng ôn tập khi được yêu cầu giải nghĩa hoặc luyện tập.");
                systemPrompt += "\n\nBối cảnh học phần (Context):\n" + context.toString();
            }
        }

        geminiRequest.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, geminiRequest, Map.class);
        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            Map<String, Object> body = responseEntity.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                if (content != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Map<String, Object> part = parts.get(0);
                        return (String) part.get("text");
                    }
                }
            }
        }
        throw new RuntimeException("Không nhận được phản hồi hợp lệ từ Gemini API.");
    }
}
