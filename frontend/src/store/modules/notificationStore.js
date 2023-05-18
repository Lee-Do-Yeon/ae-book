import { saveNotification, getNotificationList, getNotificationDetail, deleteNotification, updateNotification } from '@/api/notification'

const notificationStore = {
  namespaced: true,
  state: {
    isNotification: false,
    notification: null,
    notificationList: [],
    notificationPageSetting: null
  },
  getters: {
    getIsNotification: state => {
      return state.isNotification
    },
    getNotification: state => {
      return state.notification
    },
    getNotificationList: state => {
      return state.notificationList
    },
    getNotificationPageSetting: state => {
      return state.notificationPageSetting
    }
  },
  mutations: {
    SET_IS_NOTIFICATION: (state, data) => {
      state.isNotification = data
    },
    SET_NOTIFICATION: (state, data) => {
      state.notification = data
    },
    SET_NOTIFICATION_LIST: (state, data) => {
      state.notificationList = data
    },
    RESET_NOTIFICATION_LIST (state) {
      state.notificationList = []
    },
    UPDATE_NOTIFICATION: (state, data) => {
      state.notification.upperLimit = data.upperLimit
    },
    SET_PAGE_SETTING: (state, data) => {
      const { pageable, last, first, totalPages, size, totalElements, numberOfElements, empty } = data
      state.notificationPageSetting = { pageable, last, first, totalPages, size, totalElements, numberOfElements, empty }
    }
  },
  actions: {
    async notificationSave ({ commit }, data) {
      let result = -1
      await saveNotification(data)
        .then(({ data }) => {
          result = 1
        })
        .catch(error => {
          console.log(error)
          result = 0
        })
      return result
    },
    async getBookNotificationList ({ commit }, request) {
      await getNotificationList(request)
        .then(({ data }) => {
          commit('SET_NOTIFICATION_LIST', data.result.content)
          commit('SET_PAGE_SETTING', data.result)
        })
        .catch(error => {
          console.log(error)
        })
    },
    async getBookNotificationDetail ({ commit }, notificationId) {
      await getNotificationDetail(notificationId)
        .then(({ data }) => {
          commit('SET_NOTIFICATION', data.result)
        })
        .catch(error => {
          console.log(error)
        })
    },
    async notificationdelete ({ commit }, notificationId) {
      await deleteNotification(notificationId)
        .then(({ data }) => {
        })
        .catch(error => {
          console.log(error)
        })
    },
    async notificationUpdate ({ commit }, payload) {
      await updateNotification(payload)
        .then(({ data }) => {
          commit('UPDATE_NOTIFICATION', data.result)
        })
        .catch(error => {
          console.log(error)
        })
    }
  }
}

export default notificationStore
