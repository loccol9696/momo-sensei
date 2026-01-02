package com.example.be.controller;

import com.example.be.dto.request.CardRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.CardResponse;
import com.example.be.service.CardService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(
        name = "Card Management",
        description = "API xử lý các chức năng liên quan đến quản lý thẻ"
)
public class CardController {

    CardService cardService;

    @PostMapping("/modules/{moduleId}/cards")
    @Operation
    (
            summary = "Tạo thẻ mới"
    )
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            Authentication authentication,
            @PathVariable Long moduleId,
            @Valid @RequestBody CardRequest cardRequest
    ) {
        CardResponse cardResponse = cardService.createCard(authentication, moduleId, cardRequest);

        return ResponseEntity.ok(
                ApiResponse.<CardResponse>builder()
                        .success(true)
                        .data(cardResponse)
                        .message("Tạo thẻ thành công")
                        .build()
        );
    }

    @PutMapping("/cards/{cardId}")
    @Operation
    (
            summary = "Cập nhật thẻ"
    )
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            Authentication authentication,
            @PathVariable Long cardId,
            @Valid @RequestBody CardRequest cardRequest
    ) {
        CardResponse cardResponse = cardService.updateCard(authentication, cardId, cardRequest);

        return ResponseEntity.ok(
                ApiResponse.<CardResponse>builder()
                        .success(true)
                        .data(cardResponse)
                        .message("Cập nhật thẻ thành công")
                        .build()
        );
    }

    @DeleteMapping("/cards/{cardId}")
    @Operation
    (
            summary = "Xóa thẻ"
    )
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            Authentication authentication,
            @PathVariable Long cardId
    ) {
        cardService.deleteCard(authentication, cardId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa thẻ thành công")
                        .build()
        );
    }

    @GetMapping("/modules/{moduleId}/cards")
    @Operation
    (
            summary = "Lấy danh sách thẻ theo học phần"
    )
    public ResponseEntity<ApiResponse<List<CardResponse>>> getCards(
            Authentication authentication,
            @PathVariable Long moduleId
    ) {
        List<CardResponse> cardResponses = cardService.getCards(authentication, moduleId);

        return ResponseEntity.ok(
                ApiResponse.<List<CardResponse>>builder()
                        .success(true)
                        .data(cardResponses)
                        .message("Lấy danh sách thẻ thành công")
                        .build()
        );
    }

    @PostMapping("/modules/{moduleId}/cards/shuffle")
    @Operation
    (
            summary = "Xáo trộn danh sách thẻ trong học phần"
    )
    public ResponseEntity<ApiResponse<List<CardResponse>>> shuffleCards(
            Authentication authentication,
            @PathVariable Long moduleId
    ) {
        List<CardResponse> cardResponses = cardService.shuffleCards(authentication, moduleId);

        return ResponseEntity.ok(
                ApiResponse.<List<CardResponse>>builder()
                        .success(true)
                        .data(cardResponses)
                        .message("Xáo trộn danh sách thẻ thành công")
                        .build()
        );
    }

    @PostMapping("/cards/{cardId}/star")
    @Operation
    (
            summary = "Chuyển đổi trạng thái yêu thích của thẻ"
    )
    public ResponseEntity<ApiResponse<Void>> toggleStar(
            Authentication authentication,
            @PathVariable Long cardId
    ) {
        cardService.toggleStar(authentication, cardId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Chuyển đổi trạng thái yêu thích của thẻ thành công")
                        .build()
        );
    }

    @PostMapping("/modules/{moduleId}/cards/reorder")
    @Operation
    (
            summary = "Sắp xếp lại thứ tự thẻ trong học phần"
    )
    public ResponseEntity<ApiResponse<Void>> reorderCards(
            Authentication authentication,
            @PathVariable Long moduleId,
            @RequestBody List<Long> cardIds
    ) {
        cardService.reorderCards(authentication, moduleId, cardIds);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Sắp xếp lại thứ tự thẻ thành công")
                        .build()
        );
    }
}
