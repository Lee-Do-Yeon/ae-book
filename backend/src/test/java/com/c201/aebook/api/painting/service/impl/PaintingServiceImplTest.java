package com.c201.aebook.api.painting.service.impl;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.c201.aebook.api.painting.persistence.entity.PaintingEntity;
import com.c201.aebook.api.painting.persistence.entity.PaintingType;
import com.c201.aebook.api.painting.persistence.repository.PaintingRepository;
import com.c201.aebook.api.painting.presentation.dto.response.PaintingResponseDTO;
import com.c201.aebook.api.painting.service.PaintingServiceHelper;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.PaintingPatchSO;
import com.c201.aebook.api.vo.PaintingSO;
import com.c201.aebook.converter.PaintingConverter;

@ExtendWith(MockitoExtension.class)
public class PaintingServiceImplTest {
	@Mock
	private PaintingRepository paintingRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PaintingConverter paintingConverter;
	@Mock
	private PaintingServiceHelper paintingServiceHelper;
	@InjectMocks
	private PaintingServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@DisplayName("testSavePainting: Happy Case")
	@Test
	public void testSavePainting() {
		// given
		PaintingSO paintingSO = PaintingSO.builder()
			.title("title")
			.userId(1L)
			.fileUrl("fileUrl")
			.type(PaintingType.COLOR)
			.build();
		UserEntity userEntity = UserEntity.builder().id(paintingSO.getUserId()).build();
		BDDMockito.given(userRepository.findById(paintingSO.getUserId()))
			.willReturn(Optional.of(userEntity));

		PaintingEntity paintingEntity = PaintingEntity.builder()
			.title(paintingSO.getTitle())
			.user(userEntity)
			.build();
		PaintingResponseDTO paintingResponseDTO = PaintingResponseDTO.builder()
			.title(paintingEntity.getTitle())
			.build();

		BDDMockito.given(paintingRepository.save(any()))
			.willReturn(paintingEntity);
		BDDMockito.given(paintingConverter.toPaintingResponseDTO(paintingEntity))
			.willReturn(paintingResponseDTO);
		// when
		PaintingResponseDTO ret = subject.savePainting(paintingSO);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getTitle(), "title");
		});
		BDDMockito.then(userRepository)
			.should(times(1))
			.findById(paintingSO.getUserId());
		BDDMockito.then(paintingRepository)
			.should(times(1))
			.save(any(PaintingEntity.class));
	}

	@DisplayName("testGetPaintingList: Happy Case")
	@Test
	public void testGetPaintingList() {
		// given
		Long userId = 1L;
		UserEntity userEntity = UserEntity.builder().id(userId).build();
		BDDMockito.given(userRepository.findById(userId))
			.willReturn(Optional.of(userEntity));
		PaintingType type = PaintingType.COLOR;
		Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
		List<PaintingEntity> paintingList = new ArrayList<>();
		PaintingEntity painting = PaintingEntity.builder().title("title").build();
		paintingList.add(painting);
		Page<PaintingEntity> paintingPage = new PageImpl<>(paintingList, pageable, paintingList.size());
		BDDMockito.given(paintingRepository.findByUserIdAndType(userId, type, pageable))
			.willReturn(paintingPage);
		PaintingResponseDTO paintingResponseDTO = PaintingResponseDTO.builder().title("title").build();
		BDDMockito.given(paintingConverter.toPaintingResponseDTO(painting))
			.willReturn(paintingResponseDTO);
		// when
		Page<PaintingResponseDTO> ret = subject.getPaintingList(userId, type, pageable);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getContent().get(0).getTitle(), "title");
		});
		BDDMockito.then(paintingRepository)
			.should(times(1))
			.findByUserIdAndType(userId, type, pageable);
	}

	@DisplayName("testDeletePainting: Happy Case")
	@Test
	public void testDeletePainting() {
		// given
		Long userId = 1L;
		Long paintingId = 1L;
		PaintingEntity painting = PaintingEntity.builder().title("title").build();
		BDDMockito.given(paintingServiceHelper.getOwnPainting(userId, paintingId)).willReturn(painting);
		// when
		subject.deletePainting(userId, paintingId);
		// then
		BDDMockito.then(paintingServiceHelper).should(times(1)).getOwnPainting(userId, paintingId);
	}

	@DisplayName("testGetFilePath: Happy Case")
	@Test
	public void testGetFilePath() {
		// given
		Long userId = 1L;
		Long paintingId = 1L;
		PaintingEntity painting = PaintingEntity.builder()
			.fileUrl("https://aebookbucket.s3.ap-northeast-2.amazonaws.com/test/paintings/painting-test.png")
			.build();
		BDDMockito.given(paintingServiceHelper.getOwnPainting(userId, paintingId)).willReturn(painting);
		// when
		String ret = subject.getFilePath(paintingId, userId);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret, "test/paintings/painting-test.png");
		});
		BDDMockito.then(paintingServiceHelper).should(times(1)).getOwnPainting(userId, paintingId);
	}

	@DisplayName("testUpdatePaintingTitle: Happy Case")
	@Test
	public void testUpdatePaintingTitle() {
		// given
		Long userId = 1L;
		Long paintingId = 1L;
		PaintingPatchSO paintingPatchSO = PaintingPatchSO.builder().userId(userId).paintingId(paintingId).build();
		PaintingEntity painting = PaintingEntity.builder().build();
		BDDMockito.given(paintingServiceHelper.getOwnPainting(paintingPatchSO.getUserId(),
				paintingPatchSO.getPaintingId()))
			.willReturn(painting);
		PaintingResponseDTO paintingResponseDTO = PaintingResponseDTO.builder()
			.id(paintingId)
			.build();
		BDDMockito.given(paintingConverter.toPaintingResponseDTO(painting))
			.willReturn(paintingResponseDTO);
		// when
		PaintingResponseDTO ret = subject.updatePaintingTitle(paintingPatchSO);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getId(), paintingId);
		});
		BDDMockito.then(paintingServiceHelper).should(times(1)).getOwnPainting(userId, paintingId);
		BDDMockito.then(paintingRepository).should(times(1)).save(painting);
	}

	@DisplayName("testGetPaintingDetails: Happy Case")
	@Test
	public void testGetPaintingDetails() {
		// given
		Long userId = 1L;
		Long paintingId = 1L;
		PaintingEntity painting = PaintingEntity.builder().title("title").build();
		BDDMockito.given(paintingServiceHelper.getOwnPainting(userId, paintingId)).willReturn(painting);
		PaintingResponseDTO responseDTO = PaintingResponseDTO.builder().title("title").build();
		BDDMockito.given(paintingConverter.toPaintingResponseDTO(painting)).willReturn(responseDTO);
		// when
		PaintingResponseDTO ret = subject.getPaintingDetails(userId, paintingId);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getTitle(), "title");
		});
		BDDMockito.then(paintingServiceHelper).should(times(1)).getOwnPainting(userId, paintingId);
	}

	@DisplayName("testGetNewPaintingList: Happy Case")
	@Test
	public void testGetNewPaintingList() {
		// given
		List<PaintingEntity> paintingList = new ArrayList<>();
		PaintingEntity painting = PaintingEntity.builder().title("title").type(PaintingType.COLOR).build();
		paintingList.add(painting);
		BDDMockito.given(paintingRepository.findTop12ByTypeOrderByCreatedAtDesc(PaintingType.COLOR))
			.willReturn(paintingList);

		PaintingResponseDTO responseDTO = PaintingResponseDTO.builder()
			.title(painting.getTitle())
			.build();

		BDDMockito.given(paintingConverter.toPaintingResponseDTO(painting))
			.willReturn(responseDTO);
		// when
		List<PaintingResponseDTO> ret = subject.getNewPaintingList();
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.get(0).getTitle(), "title");
		});
		BDDMockito.then(paintingRepository).should(times(1)).findTop12ByTypeOrderByCreatedAtDesc(PaintingType.COLOR);

	}
}
