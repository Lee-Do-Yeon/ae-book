package com.c201.aebook.api.story.service.impl;

import com.c201.aebook.api.story.persistence.entity.StoryEntity;
import com.c201.aebook.api.story.persistence.repository.StoryRepository;
import com.c201.aebook.api.story.presentation.dto.response.StoryListResponseDTO;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.converter.StoryConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class StoryServiceImplTest {

	@Mock
	private StoryRepository storyRepository;

	@Mock
	private StoryConverter storyConverter;

	@Mock
	private StoryServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	public void testSaveStory() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetStoryByUserId() {
		// given
		Long userId = 1l;
		Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());

		List<StoryEntity> storyPageList = new ArrayList<>();
		UserEntity user = UserEntity.builder().id(1l).nickname("nickname1").build();
		StoryEntity story = StoryEntity.builder().user(user).build();
		storyPageList.add(story);
		Page<StoryEntity> storyPage = new PageImpl<>(storyPageList, pageable, storyPageList.size());
		BDDMockito.given(storyRepository.findAll(pageable)).willReturn(storyPage);

		List<StoryListResponseDTO> storyList = new ArrayList<>();
		StoryListResponseDTO storyListResponseDTO = StoryListResponseDTO.builder()
				.nickname("nickname1")
				.build();
		storyList.add(storyListResponseDTO);

		BDDMockito.given(storyConverter.toStoryListResponseDTO(story))
				.willReturn(storyListResponseDTO);

//		BDDMockito.given(storyPage.stream()
//						.filter(storyEntity -> storyEntity.getUser().getId() == userId)
//						.map(storyEntity -> storyConverter.toStoryListResponseDTO(storyEntity))
//						.collect(Collectors.toList())
//		).willReturn(storyList);


		// when
		Page<StoryListResponseDTO> ret = subject.getStoryListByUserId(userId, pageable);
		System.out.println(ret);

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getContent().get(0).getTitle(), "title");
		});
		BDDMockito.then(storyRepository)
				.should(times(1))
				.findAll();
	}

	@Test
	public void testGetStoryList() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetStoryDetail() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateStoryTitle() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDeleteStory() {
		throw new RuntimeException("not yet implemented");
	}

}
