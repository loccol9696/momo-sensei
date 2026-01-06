package com.example.be.controller;

import com.example.be.dto.request.CheckAnswerRequest;
import com.example.be.dto.response.*;
import com.example.be.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Study Mode", description = "Các API hỗ trợ chế độ học tập")
public class StudyController {

    StudyService studyService;

    @GetMapping("/write/{moduleId}")
    @Operation(summary = "Lấy danh sách câu hỏi tự luận")
    public ResponseEntity<ApiResponse<List<WriteQuestionResponse>>> getWriteQuestions(
            Authentication authentication,
            @PathVariable Long moduleId,
            @RequestParam(defaultValue = "false", required = false) boolean isStarred
    ) {
        List<WriteQuestionResponse> questions = studyService.getWriteQuestions(authentication, moduleId, isStarred);

        return ResponseEntity.ok(
                ApiResponse.<List<WriteQuestionResponse>>builder()
                        .success(true)
                        .data(questions)
                        .message("Bắt đầu bài kiểm tra viết")
                        .build()
        );
    }

    @GetMapping("/choice/{moduleId}")
    @Operation(summary = "Lấy danh sách câu hỏi trắc nghiệm")
    public ResponseEntity<ApiResponse<List<ChoiceQuestionResponse>>> getChoiceQuestions(
            Authentication authentication,
            @PathVariable Long moduleId,
            @RequestParam(defaultValue = "false", required = false) boolean isStarred
    ) {
        List<ChoiceQuestionResponse> questions = studyService.getChoiceQuestions(authentication, moduleId, isStarred);
        return ResponseEntity.ok(
                ApiResponse.<List<ChoiceQuestionResponse>>builder()
                        .success(true)
                        .data(questions)
                        .message("Bắt đầu bài kiểm tra trắc nghiệm")
                        .build()
        );
    }

    @PostMapping("/check")
    @Operation(summary = "Kiểm tra đáp án chung cho cả tự luận và trắc nghiệm")
    public ResponseEntity<ApiResponse<CheckAnswerResponse>> checkAnswer(
            Authentication authentication,
            @Valid @RequestBody CheckAnswerRequest request
    ) {
        CheckAnswerResponse response = studyService.checkAnswer(authentication, request);

        return ResponseEntity.ok(
                ApiResponse.<CheckAnswerResponse>builder()
                        .success(true)
                        .data(response)
                        .message("Đã xử lý kết quả kiểm tra")
                        .build()
        );
    }

    @GetMapping("/match/{moduleId}")
    @Operation(summary = "Lấy danh sách câu hỏi cho trò chơi nối thẻ")
    public ResponseEntity<ApiResponse<List<MatchGameResponse>>> getMatchGame(
            Authentication authentication,
            @PathVariable Long moduleId,
            @RequestParam(defaultValue = "1") int level
    ) {
        List<MatchGameResponse> responses = studyService.getMatchGame(authentication, moduleId, level);

        return ResponseEntity.ok(
                ApiResponse.<List<MatchGameResponse>>builder()
                        .success(true)
                        .data(responses)
                        .message("Bắt đầu màn chơi thứ " + level)
                        .build()
        );
    }
}
