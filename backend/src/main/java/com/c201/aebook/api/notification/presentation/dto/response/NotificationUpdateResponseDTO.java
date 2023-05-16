package com.c201.aebook.api.notification.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationUpdateResponseDTO {

    @Schema(description = "금액 상한선", defaultValue = "5000")
    private int upperLimit;

    /**
     * S는 사용자가 가격을 지정한 경우(selected),
     * D는 사용자가 가격을 지정하지 않은 경우(default)
     */
    @Schema(description = "알림 타입", defaultValue = "S")
    private String notificationType;

    @Builder
    public NotificationUpdateResponseDTO(int upperLimit, String notificationType) {
        this.upperLimit = upperLimit;
        this.notificationType = notificationType;
    }
}