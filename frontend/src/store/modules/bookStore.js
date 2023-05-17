import { searchByISBN, getSearchList, getNewBookList } from '@/api/book'

const bookStore = {
  namespaced: true,
  // state: 변수들의 집합
  state: {
    book: null,
    bookList: [],
    bookPageSetting: null,
    totalSearchCount: 0,
    searchKeyword: '',
    bookMainList: []
  },
  /*
  Gettes: state의 변수들을 get하는역할을 한다.
  - state 자체를 변경하지 않음.
  */
  getters: {
    getBook: state => {
      return state.book
    },
    getBookList: state => {
      return state.bookList
    }
  },
  /*
  Mutations: 변수들을 조작하는 함수들의 집합
  - State는 반드시 Mutations가 가진 method를 통해서만 조작 함.
  */
  mutations: {
    SET_BOOK: (state, data) => {
      state.book = data
    },
    SET_BOOK_LIST: (state, data) => {
      state.bookList = data
    },
    SET_MAIN_BOOK_LIST: (state, data) => {
      state.bookMainList = data
    },
    RESET_BOOK_SEARCH (state) {
      state.bookList = []
      state.bookPageSetting = null
      state.totalSearchCount = 0
      state.searchKeyword = ''
    },
    SET_PAGE_SETTING: (state, data) => {
      const { pageable, last, first, totalPages, size, totalElements, numberOfElements, empty } = data
      state.bookPageSetting = { pageable, last, first, totalPages, size, totalElements, numberOfElements, empty }
    },
    SET_COUNT: (state, data) => {
      state.totalSearchCount = data
    },
    SET_SEARCH_KEYWORD: (state, data) => {
      state.searchKeyword = data
    }
  },
  /*
  Actions: 비동기 처리를 하는 함수들의 집합
  - Actions에서는 비동기적 작업을 Mutations에서는 동기적 작업만을 함.
  */
  actions: {
    async getBookDetail ({ commit }, isbn) {
      await searchByISBN(isbn)
        .then(({ data }) => {
          commit('SET_BOOK', data.result)
        })
        .catch(error => {
          alert('존재하지 않거나 접근이 불가능한 도서입니다.')
          console.error(error)
          window.history.back()
        })
    },
    async getSearchList ({ commit }, request) {
      await getSearchList(request)
        .then(({ data }) => {
          commit('SET_BOOK_LIST', data.result.content)
          commit('SET_COUNT', data.result.totalElements)
          commit('SET_PAGE_SETTING', data.result)
        })
        .catch(error => {
          alert('도서 리스트를 조회하는데 문제가 발생했습니다.')
          console.error(error)
        })
    },
    async getNewBookList ({ commit }, request) {
      await getNewBookList(request)
        .then(({ data }) => {
          commit('SET_MAIN_BOOK_LIST', data.result)
        })
        .catch(error => {
          alert('최신 도서 리스트를 조회하는데 문제가 발생했습니다.')
          console.error(error)
        })
    }
  }
}

export default bookStore
