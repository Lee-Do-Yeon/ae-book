import axios from 'axios'
import api from '@/api/auth'

// 1. 서평 등록
const saveReview = async (payload) => {
  try {
    const response = await api.post(`/api/reviews/${payload.isbn}`, payload.data)
    return response
  } catch (err) {
    throw new Error('서평 등록을 실패하였습니다.')
  }
}
// 2. 서평 상세 조회
const getMyReview = async isbn => {
  try {
    const response = await api.get(`/api/reviews/${isbn}/mine`)
    return response
  } catch (err) {
    throw new Error('특정 도서 내 서평 조회를 실패했습니다.')
  }
}

// 3. 서평 수정
const modifyReview = async (payload) => {
  try {
    const response = await api.patch(`/api/reviews/${payload.reviewId}`, payload.data, { headers: {'Content-Type': 'application/json'} })
    return response
  } catch (err) {
    throw new Error('서평 수정을 실패했습니다.')
  }
}
// 4. 서평 삭제
const deleteReview = async reviewId => {
  try {
    const response = await api.delete(`/api/reviews/${reviewId}`)
    return response
  } catch (err) {
    throw new Error('서평 삭제를 실패했습니다.')
  }
}

// 5. 최신 서평 리스트 - 메인페이지 전용, 12개
const getReviewMainList = async () => {
  try {
    const response = await axios.get(`/api/reviews/latest`)
    return response
  } catch (err) {
    throw new Error('최신 서평 조회를 실패했습니다.')
  }
}

// 6. 특정 도서의 서평 리스트 by ISBN
const getReviewBookList = async (request) => {
  try {
    const response = await axios.get(`/api/reviews/${request.isbn}?page=${request.page}&size=${request.size}&sort=${request.sort},${request.direction}`)
    return response
  } catch (err) {
    throw new Error('특정 도서의 서평 조회를 실패했습니다.')
  }
}
// /reviews/{isbn}?page=int&size=5&sort=key,direction
// sort 종류
// 1. id, DESC // LATEST
// 2. score, DESC // SCORE_HIGHEST
// 3. score, ASC // SCORE_LOWEST

// 7. 로그인한 사용자의 서평 리스트
const getReviewMyList = async (request) => {
  try {
    const response = await api.get(`/api/reviews?page=${request.page}&size=${request.size}&sort=${request.sort},${request.direction}`)
    return response
  } catch (err) {
    throw new Error('나의 서평 조회를 실패했습니다.')
  }
}

export { saveReview, getMyReview, modifyReview, deleteReview, getReviewMainList, getReviewBookList, getReviewMyList }
