package com.c201.aebook.api.notification.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class NotificationRequestDTO {

    @Schema(description = "isbn", defaultValue = "0123456789103")
    private String isbn;
    @Schema(description = "금액 상한선", defaultValue = "5000")
    private int upperLimit;

    /**
     * S는 사용자가 가격을 지정한 경우(selected),
     * D는 사용자가 가격을 지정하지 않은 경우(default)
     */
    @Schema(description = "알림 타입", defaultValue = "S")
    private String notificationType;

    @Builder
    public NotificationRequestDTO(String isbn, int upperLimit, String notificationType) {
        this.isbn = isbn;
        this.upperLimit = upperLimit;
        this.notificationType = notificationType;
    }
}
