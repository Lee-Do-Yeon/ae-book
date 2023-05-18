import axios from 'axios'
import api from '@/api/auth'

// 로그인한 유저의 동화 리스트
const searchMyStory = (pagination) => api.get(`/api/stories/my?page=${pagination.page}&size=3`)

// 특정 동화의 상세 정보 조회
const searchDetailStory = storyId => axios.get(`/api/stories/${storyId}`)

// 특정 동화를 삭제
const deleteStory = (storyId) => api.delete(`/api/stories/${storyId}`)

// 특정 동화의 제목 변경
const updateStoryTitle = (payload) => api.patch(`/api/stories/${payload.storyId}`, payload.title)

// 동화 등록
const registerStory = (payload) => api.post(`/api/stories`, payload)

// 전체 동화 리스트
const searchStoryList = (pagination) => axios.get(`/api/stories?page=${pagination.page}&size=3`)

export {searchMyStory, searchDetailStory, deleteStory, updateStoryTitle, registerStory, searchStoryList}
