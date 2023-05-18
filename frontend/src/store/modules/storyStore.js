import { searchMyStory, registerStory, updateStoryTitle, deleteStory, searchStoryList } from '@/api/story'
import Vue from 'vue'

const storyStore = {
  namespaced: true,

  state: {
    storyId: null,
    storyPageSetting: null,
    storyList: [],
    story: null,
    currentPage: null
  },

  mutations: {
    SET_CURRENT_PAGE (state, page) {
      state.currentPage = page
    },
    setStoryId (state, id) {
      state.storyId = id
    },
    clearStoryId (state) {
      state.storyId = null
    },
    SET_PAGE_SETTING: (state, data) => {
      const { pageable, last, first, totalPages, size, totalElements, numberOfElements, empty } = data
      state.storyPageSetting = { pageable, last, first, totalPages, size, totalElements, numberOfElements, empty }
    },
    SET_LIST: (state, data) => {
      state.storyList = data
    },
    SET_STORY: (state, data) => {
      state.story = data
    },
    UPDATE_STORY: (state, data) => {
      const index = state.storyList.findIndex(item => item.storyId === data.storyId)
      // state.storyList[index].title = data.title
      Vue.set(state.storyList[index], 'title', data.title)
    },
    DELETE_STORY: (state, data) => {
      const index = state.storyList.findIndex(item => item.storyId === data.storyId)
      state.storyList.splice(index, 1)
    }
  },
  getters: {
    getStoryId: state => {
      return state.storyId
    },
    getStoryPageSetting: state => {
      return state.storyPageSetting
    }
  },

  actions: {
    async getMyStoryList ({commit}, request) {
      commit('SET_CURRENT_PAGE', request.page)
      await searchMyStory(request)
        .then(({data}) => {
          commit('SET_PAGE_SETTING', data.result)
          commit('SET_LIST', data.result.content)
        })
        .catch(error => {
          console.error(error)
        })
    },
    async getStoryList ({commit}, request) {
      await searchStoryList(request)
        .then(({data}) => {
          commit('SET_PAGE_SETTING', data.result)
          commit('SET_LIST', data.result.content)
        })
        .catch(error => {
          console.error(error)
        })
    },
    async saveStory ({ commit }, request) {
      await registerStory(request)
        .then(({ data }) => {
          commit('SET_STORY', data.result)
          searchStoryList({page: 0})
            .then(({ data }) => {
              commit('SET_PAGE_SETTING', data.result)
              commit('SET_LIST', data.result.content)
            })
        })
        .catch(error => {
          console.error(error)
        })
    },
    async updateTitleOfStory ({ commit }, payload) {
      await updateStoryTitle(payload)
        .then(({data}) => {
          alert('성공적으로 수정했습니다.')
          commit('UPDATE_STORY', data.result)
        })
        .catch(error => {
          alert('수정에 실패했습니다. ' + error)
        })
    },
    async deleteStoryById ({ commit, state }, payload) {
      const storyId = payload.id
      const flag = payload.flag
      if (flag === true) {
        commit('SET_CURRENT_PAGE', state.currentPage - 1)
      } else {
        commit('SET_CURRENT_PAGE', state.currentPage)
      }
      await deleteStory(storyId)
        .then(({data}) => {
          alert('성공적으로 삭제했습니다.')
          commit('DELETE_STORY', data.result)
          searchMyStory({page: state.currentPage})
            .then(({ data }) => {
              commit('SET_PAGE_SETTING', data.result)
              commit('SET_LIST', data.result.content)
            })
        })
        .catch(error => {
          alert('삭제 실패했습니다.' + error)
        })
    }
  }
}

export default storyStore
