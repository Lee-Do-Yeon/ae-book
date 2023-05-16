package com.c201.aebook.api.notification.persistence.repository;

import com.c201.aebook.api.notification.persistence.entity.NotificationEntity;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    NotificationEntity findByUserIdAndBookId(Long userId, Long bookId);

    @Query(value = "SELECT n FROM NotificationEntity n LEFT JOIN FETCH n.user LEFT JOIN FETCH  n.book WHERE n.user.id = :userId",
            countQuery = "select count(n) from NotificationEntity n WHERE n.user.id = :userId")
    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT n FROM NotificationEntity n LEFT JOIN FETCH n.user LEFT JOIN FETCH n.book WHERE n.id = :notificationId")
    Optional<NotificationEntity> findByNotificationId(Long notificationId);

    Optional<NotificationEntity> findByIdAndUserId(Long notificationId, Long userId);

    @Query("SELECT count(n.id) > 0 FROM NotificationEntity n WHERE n.user.id = :userId AND n.book.id = :bookId")
    boolean existsByBookId(Long bookId, Long userId);

    void deleteByUserId(Long userId);
}
