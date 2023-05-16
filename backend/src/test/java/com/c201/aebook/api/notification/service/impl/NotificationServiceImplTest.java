package com.c201.aebook.api.notification.service.impl;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.api.book.persistence.repository.BookRepository;
import com.c201.aebook.api.notification.persistence.entity.NotificationEntity;
import com.c201.aebook.api.notification.persistence.repository.NotificationRepository;
import com.c201.aebook.api.notification.presentation.dto.request.NotificationRequestDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationBookDetailResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationBookListResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationUpdateResponseDTO;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.NotificationPatchSO;
import com.c201.aebook.api.vo.NotificationSO;
import com.c201.aebook.converter.NotificationConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private NotificationRepository notificationRepository;
	@Mock
	private NotificationConverter notificationConverter;

	@InjectMocks
	private NotificationServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	@DisplayName("testSaveNotification: Happy Case")
	public void testSaveNotification() {
		// given
		String userId = "1";
		int upperLimit = 1000;
		String notificationType = "D";
		String isbn = "9788915016521";

		NotificationSO notificationSO = NotificationSO.builder()
				.notificationType(notificationType).upperLimit(upperLimit).isbn("9788915016521").build();

		BookEntity bookEntity = BookEntity.builder()
				.id(1L)
				.isbn(isbn)
				.title("Test Book")
				.build();
		BDDMockito.given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(bookEntity));

		UserEntity userEntity = UserEntity.builder()
				.id(1L)
				.nickname("Test User")
				.build();
		BDDMockito.given(userRepository.findById(Long.valueOf(userId))).willReturn(Optional.of(userEntity));

		BDDMockito.given(notificationRepository.findByUserIdAndBookId(userEntity.getId(), bookEntity.getId())).willReturn(null);
		NotificationEntity savedNotificationEntity = NotificationEntity.builder()
				.id(1L)
				.notificationType(notificationSO.getNotificationType())
				.upperLimit(notificationSO.getUpperLimit())
				.book(bookEntity)
				.user(userEntity)
				.build();
		NotificationEntity notificationEntity = new NotificationEntity();

		BDDMockito.given(notificationRepository.save(Mockito.any(NotificationEntity.class))).willReturn(savedNotificationEntity);
		NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.builder()
				.id(savedNotificationEntity.getId())
				.notificationType(savedNotificationEntity.getNotificationType())
				.upperLimit(savedNotificationEntity.getUpperLimit())
				.build();
		BDDMockito.given(notificationConverter.toNotificationResponseDTO(savedNotificationEntity)).willReturn(notificationResponseDTO);


		// when
		NotificationResponseDTO ret = subject.saveNotification(userId, notificationSO);

		// then

		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getId(), 1L);
			Assertions.assertTrue("S".equals(ret.getNotificationType()) || "D".equals(ret.getNotificationType()));
			Assertions.assertTrue(ret.getUpperLimit() >= 0);
		});
		BDDMockito.then(bookRepository).should(times(1)).findByIsbn(isbn);
		BDDMockito.then(userRepository).should(times(1)).findById(Long.valueOf(userId));
		BDDMockito.then(notificationRepository).should(times(1)).findByUserIdAndBookId(userEntity.getId(), bookEntity.getId());

	}

	@Test
	@DisplayName("testGetMyNotificationBookList: Happy Case")
	public void testGetMyNotificationBookList() {
		// given
		String userId = "1";
		Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

		List<NotificationEntity> notificationList = new ArrayList<>();
		BookEntity book = BookEntity.builder().title("book test").isbn("9788915016521").price(5000).coverImageUrl("img url").build();
		NotificationEntity notification = NotificationEntity.builder().notificationType("D").upperLimit(0).book(book).build();
		notificationList.add(notification);
		Page<NotificationEntity> notificationPage = new PageImpl<>(notificationList, pageable, notificationList.size());

		BDDMockito.given(notificationRepository.findByUserId(Long.valueOf(userId), pageable)).willReturn(notificationPage);
		NotificationBookListResponseDTO notificationBookListResponseDTO = NotificationBookListResponseDTO.builder()
				.notificationType("D").upperLimit(0).title("book test").isbn("9788915016521").price(5000).coverImageUrl("img url").build();

		// when
		Page<NotificationBookListResponseDTO> ret = subject.getMyNotificationBookList(userId, pageable);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getContent().get(0).getNotificationType(), "D");
			Assertions.assertEquals(ret.getContent().get(0).getUpperLimit(), 0);
			Assertions.assertEquals(ret.getContent().get(0).getTitle(), "book test");
			Assertions.assertEquals(ret.getContent().get(0).getPrice(), 5000);
			Assertions.assertEquals(ret.getContent().get(0).getIsbn(), "9788915016521");
			Assertions.assertEquals(ret.getContent().get(0).getCoverImageUrl(), "img url");
		});
		BDDMockito.then(notificationRepository).should(times(1)).findByUserId(Long.valueOf(userId), pageable);
	}

	@Test
	@DisplayName("testGetMyNotificationBookDetail: Happy Case")
	public void testGetMyNotificationBookDetail() {
		// given
		Long notificationId = 1L;

		BookEntity book = BookEntity.builder().title("book title").author("author").publisher("publisher")
				.isbn("9788915016521").price(5000).coverImageUrl("img url").aladinUrl("aladin url").build();
		NotificationEntity notification = NotificationEntity.builder().notificationType("D").upperLimit(0).book(book).build();

		BDDMockito.given(notificationRepository.findByNotificationId(notificationId)).willReturn(Optional.of(notification));

		NotificationBookDetailResponseDTO notificationBookDetailResponseDTO = NotificationBookDetailResponseDTO.builder()
				.id(notificationId)
				.upperLimit(notification.getUpperLimit())
				.notificationType(notification.getNotificationType())
				.createdAt(notification.getCreatedAt())
				.updatedAt(notification.getUpdatedAt())
				.title(notification.getBook().getTitle())
				.author(notification.getBook().getAuthor())
				.publisher(notification.getBook().getPublisher())
				.isbn(notification.getBook().getIsbn())
				.price(notification.getBook().getPrice())
				.coverImageUrl(notification.getBook().getCoverImageUrl())
				.aladinUrl(notification.getBook().getAladinUrl())
				.build();

		// when
		NotificationBookDetailResponseDTO ret = subject.getMyNotificationBookDetail(notificationId);

		// given
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getNotificationType(), "D");
			Assertions.assertEquals(ret.getUpperLimit(), 0);
			Assertions.assertEquals(ret.getTitle(), "book title");
			Assertions.assertEquals(ret.getPrice(), 5000);
			Assertions.assertEquals(ret.getIsbn(), "9788915016521");
			Assertions.assertEquals(ret.getCoverImageUrl(), "img url");
			Assertions.assertEquals(ret.getAuthor(), "author");
			Assertions.assertEquals(ret.getPublisher(), "publisher");
			Assertions.assertEquals(ret.getAladinUrl(), "aladin url");
		});
		BDDMockito.then(notificationRepository).should(times(1)).findByNotificationId(notificationId);
	}

	@Test
	@DisplayName("testUpdateNotification: Happy Case")
	public void testUpdateNotification() {
		// given
		String userId = "1";
		Long notificationId = 1L;
		int upperLimit = 1000;
		String notificationType = "D";

		NotificationPatchSO notificationPatchSO = NotificationPatchSO.builder()
				.notificationType(notificationType).upperLimit(upperLimit).build();
		NotificationEntity notification = NotificationEntity.builder().notificationType("D").upperLimit(0).build();

		BDDMockito.given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));
		notification.updateNotificationEntity(notificationPatchSO.getUpperLimit(), notificationPatchSO.getNotificationType());

		NotificationUpdateResponseDTO notificationUpdateResponseDTO = NotificationUpdateResponseDTO.builder()
				.upperLimit(notification.getUpperLimit())
				.notificationType(notification.getNotificationType())
				.build();
		BDDMockito.given(notificationConverter.toNotificationUpdateResponseDTO(notification.getUpperLimit(), notification.getNotificationType()))
				.willReturn(notificationUpdateResponseDTO);

		// when
		NotificationUpdateResponseDTO ret = subject.updateNotification(userId, notificationId, notificationPatchSO);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getNotificationType(), "D");
			Assertions.assertEquals(ret.getUpperLimit(), 1000);
		});
		BDDMockito.then(notificationRepository).should(times(1)).findById(notificationId);

	}

	@Test
	@DisplayName("testDeleteNotification: Happy Case")
	public void testDeleteNotification() {
		// given
		String userId = "1";
		Long notificationId = 1L;

		NotificationEntity notification = NotificationEntity.builder().notificationType("D").upperLimit(0).build();

		BDDMockito.given(notificationRepository.findByIdAndUserId(Long.valueOf(userId), notificationId)).willReturn(Optional.of(notification));

		// when
		subject.deleteNotification(userId, notificationId);

		// then

	}

}
