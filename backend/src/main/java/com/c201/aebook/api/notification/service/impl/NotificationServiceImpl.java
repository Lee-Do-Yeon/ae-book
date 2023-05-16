package com.c201.aebook.api.notification.service.impl;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.api.book.persistence.repository.BookRepository;
import com.c201.aebook.api.notification.persistence.entity.NotificationEntity;
import com.c201.aebook.api.notification.persistence.repository.NotificationRepository;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationBookDetailResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationBookListResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationUpdateResponseDTO;
import com.c201.aebook.api.notification.service.NotificationService;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.NotificationPatchSO;
import com.c201.aebook.api.vo.NotificationSO;
import com.c201.aebook.converter.NotificationConverter;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private final NotificationRepository notificationRepository;
    private final NotificationConverter notificationConverter;

    @Override
    public NotificationResponseDTO saveNotification(String userId, NotificationSO notificationSO) {
        // 1. isbn 유효성 검증
        BookEntity bookEntity = bookRepository.findByIsbn(notificationSO.getIsbn())
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
        // log.info("book : {}", bookEntity.getIsbn());

        // 2. userId 유효성 검증
        UserEntity userEntity = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. 알림 타입이 D이면서 upperLimit이 0 이상인지 확인
        if("D".equals(notificationSO.getNotificationType()) && notificationSO.getUpperLimit() >= 0) {
            notificationSO.setUpperLimit(0);
        }

        // 4. 해당 책에 알림 신청을 한 적이 있는지 검증
        NotificationEntity notificationEntity = notificationRepository
                .findByUserIdAndBookId(userEntity.getId(), bookEntity.getId());
        if(notificationEntity != null) {
            throw new CustomException(ErrorCode.DUPLICATED_NOTIFICATION);
        }

        // 4. 알림 신청 저장
        NotificationEntity notification = notificationRepository.save(NotificationEntity.builder()
                .upperLimit(notificationSO.getUpperLimit())
                .notificationType(notificationSO.getNotificationType())
                .user(userEntity)
                .book(bookEntity)
                .build());

        NotificationResponseDTO notificationResponseDTO = notificationConverter.toNotificationResponseDTO(notification);
        return notificationResponseDTO;
    }

    @Override
    public Page<NotificationBookListResponseDTO> getMyNotificationBookList(String userId, Pageable pageable) {
        // 사용자가 알림 신청된 책 목록 가져오기
        Page<NotificationEntity> notifications = notificationRepository.findByUserId(Long.valueOf(userId), pageable);

        return notifications.map(notification -> NotificationBookListResponseDTO.builder()
                .id(notification.getId())
                .upperLimit(notification.getUpperLimit())
                .notificationType(notification.getNotificationType())
                .title(notification.getBook().getTitle())
                .isbn(notification.getBook().getIsbn())
                .price(notification.getBook().getPrice())
                .coverImageUrl(notification.getBook().getCoverImageUrl())
                .build());
//        return notifications.map(notification -> notificationConverter.toNotificationBookListResponseDTO(notification));
    }

    @Override
    public NotificationBookDetailResponseDTO getMyNotificationBookDetail(Long notificationId) {
        // 사용자가 알림 신청한 내역 가져오기
        NotificationEntity notificationEntity = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        log.info("notification : {}", notificationEntity.getBook().getTitle());

        return NotificationBookDetailResponseDTO.builder()
                .id(notificationId)
                .upperLimit(notificationEntity.getUpperLimit())
                .notificationType(notificationEntity.getNotificationType())
                .createdAt(notificationEntity.getCreatedAt())
                .updatedAt(notificationEntity.getUpdatedAt())
                .title(notificationEntity.getBook().getTitle())
                .author(notificationEntity.getBook().getAuthor())
                .publisher(notificationEntity.getBook().getPublisher())
                .isbn(notificationEntity.getBook().getIsbn())
                .price(notificationEntity.getBook().getPrice())
                .coverImageUrl(notificationEntity.getBook().getCoverImageUrl())
                .aladinUrl(notificationEntity.getBook().getAladinUrl())
                .build();
    }

    @Override
    @Transactional
    public NotificationUpdateResponseDTO updateNotification(String userId, Long notificationId, NotificationPatchSO notificationPatchSO) {
        // 1. notificationId 유효성 검증
        NotificationEntity notificationEntity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 2. notification 정보(upperLimit) 업데이트
        notificationEntity.updateNotificationEntity(notificationPatchSO.getUpperLimit(), notificationPatchSO.getNotificationType());

        // 3. NofiticationUpdateResponseDTO에 담기
        NotificationUpdateResponseDTO notificationUpdateResponseDTO =
                notificationConverter.toNotificationUpdateResponseDTO(notificationEntity.getUpperLimit(), notificationEntity.getNotificationType());

        return notificationUpdateResponseDTO;
    }

    @Override
    public void deleteNotification(String userId, Long notificationId) {
        // 1. notificaionId 유효성 검증 및 사용자 일치 여부 확인
        NotificationEntity notificationEntity = notificationRepository.findByIdAndUserId(notificationId, Long.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 2. 알림 삭제
        notificationRepository.delete(notificationEntity);
    }

    @Override
    public void deleteAllNoticiation(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }
}
