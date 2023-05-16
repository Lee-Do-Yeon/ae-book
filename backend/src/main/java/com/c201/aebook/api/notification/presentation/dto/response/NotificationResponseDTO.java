package com.c201.aebook.api.notification.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponseDTO {

    @Schema(description = "알림 Id", defaultValue = "1")
    private Long id;

    @Schema(description = "금액 상한선", defaultValue = "5000")
    private int upperLimit;

    @Schema(description = "알림 타입", defaultValue = "S")
    private String notificationType;

    @Builder
    public NotificationResponseDTO(Long id, int upperLimit, String notificationType) {
        this.id = id;
        this.upperLimit = upperLimit;
        this.notificationType = notificationType;
    }
}