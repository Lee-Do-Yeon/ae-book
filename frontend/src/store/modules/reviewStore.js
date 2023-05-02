import { saveReview, getReview, getReviewMainList, getReviewBookList, getReviewMyList, modifyReview, deleteReview } from '../../api/review'

const reviewStore = {
  namespaced: true,
  // state: 변수들의 집합
  state: {
    review: null,
    reviewMainList: [],
    reviewMyList: [],
    reviewBookList: []
  },
  /*
  Gettes: state의 변수들을 get하는역할을 한다.
  - state 자체를 변경하지 않음.
  */
  getters: {
    getReview: state => {
      return state.review
    },
    getReviewMainList: state => {
      return state.reviewMainList
    },
    getReviewMyList: state => {
      return state.reviewMyList
    },
    getReviewBookList: state => {
      return state.reviewBookList
    }
  },
  /*
  Mutations: 변수들을 조작하는 함수들의 집합
  - State는 반드시 Mutations가 가진 method를 통해서만 조작 함.
  */
  mutations: {
    SET_REVIEW: (state, data) => {
      state.review = data
    },
    SET_REVIEW_MAIN_LIST: (state, data) => {
      state.reviewMainList = data
    },
    SET_REVIEW_MY_LIST: (state, data) => {
      state.reviewMyList = data
    },
    SET_REVIEW_BOOK_LIST: (state, data) => {
      state.reviewBookList = data
    },
    RESET_REVIEW (state) {
      state.review = null
    }
  },
  /*
  Actions: 비동기 처리를 하는 함수들의 집합
  - Actions에서는 비동기적 작업을 Mutations에서는 동기적 작업만을 함.
  - commit은 mutation명을 쓰면 됨
  */
  actions: {
    async saveReviewAction ({ commit }, payload) {
      console.log('saveReviewAction')
      await saveReview(payload)
        .then(({ data }) => {
          console.log('Review Save Complete')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    async getReviewAction ({ commit }, reviewId) {
      await getReview(reviewId)
        .then(({ data }) => {
          commit('SET_REVIEW', data.result)
          console.log('getReview')
          console.log(data.result)
        }).catch((err) => {
          console.log(err)
        })
    },
    async modifyReviewAction ({ commit }, payload) {
      console.log('modifyReviewAction')
      await modifyReview(payload)
        .then(({ data }) => {
          console.log('Review Modify Complete')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    async deleteReviewAction ({ commit }, reviewId) {
      await deleteReview(reviewId)
        .then(({ data }) => {
          commit('RESET_REVIEW')
          console.log('Review Delete Complete')
        })
    },
    async getReviewMainListAction ({ commit }) {
      await getReviewMainList()
        .then(({ data }) => {
          commit('SET_REVIEW_MAIN_LIST', data.result)
          console.log('REVIEW_MAIN_LIST')
          console.log(data.result)
        }).catch((err) => {
          console.log(err)
        })
    },
    async getReviewBookListAction ({ commit }, request) {
      await getReviewBookList(request)
        .then(({ data }) => {
          console.log('getReviewBookListAction')
          console.log(data.result.content)
          commit('SET_REVIEW_BOOK_LIST', data.result.content)
          commit('SET_REVIEW_BOOK_PAGE_SETTING', data.result)
        }).catch((err) => {
          console.log(err)
        })
    },
    async getReviewMyListAction ({ commit }, request) {
      await getReviewMyList(request)
        .then(({ data }) => {
          console.log('getReviewMyListAction')
          console.log(data.result)
          console.log(data.result.content)
          commit('SET_REVIEW_MY_LIST', data.result.content)
          commit('SET_REVIEW_MY_PAGE_SETTING', data.result)
        })
    }
  }
}

export default reviewStore