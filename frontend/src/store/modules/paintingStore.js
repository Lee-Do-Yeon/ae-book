import { savePainting, getPaintingList, getPaintingDetail, deletePainting, updatePaintingTitle, downloadPainting, convertSketch } from '@/api/painting'

const paintingStore = {
  namespaced: true,
  state: {
    painting: null,
    paintingList: [],
    sketch: ''
  },
  getters: {
    getPainting: state => {
      return state.painting
    },
    getPaintingList: state => {
      return state.paintingList
    }
  },
  mutations: {
    SET_PAINTING: (state, data) => {
      state.painting = data
    },
    SET_PAINTING_LIST: (state, data) => {
      state.paintingList = data
    },
    RESET_PAINTING_LIST (state) {
      state.paintingList = []
    },
    SET_SKETCH: (state, data) => {
      state.sketch = 'data:image/jpeg;base64,' + data
    }
  },
  actions: {
    async savePainting ({ commit }, formdata) {
      await savePainting(formdata)
        .then(({ data }) => {
          commit('SET_PAINTING', data.result)
          console.log(data.result)
        })
        .catch(error => {
          console.log(error)
        })
    },
    async getPaintingList ({ commit }, request) {
      await getPaintingList(request)
        .then(({ data }) => {
          commit('SET_PAINTING_LIST', data.result.content)
        })
        .catch(error => {
          alert(error)
        })
    },
    async convertSketch ({ commit }, request) {
      await convertSketch(request)
        .then(({ data }) => {
          commit('SET_SKETCH', data)
        })
        .catch(error => {
          alert(error)
        })
    },
    async getPaintingDetail ({ commit }, request) {
      await getPaintingDetail(request)
        .then(({ data }) => {
          commit('SET_PAINTING', data.result)
        })
        .catch(error => {
          alert(error)
        })
    },
    async downloadPainting ({ commit }, paintingId) {
      await downloadPainting(paintingId)
        .then(({ data }) => {
          // byte 배열을 Blob 객체로 변환
          // 다운로드 링크 생성
          const downloadLink = document.createElement('a')
          downloadLink.href = URL.createObjectURL(new Blob([data]))

          // 파일 이름 설정
          const fileName = 'image.jpg'
          downloadLink.download = fileName

          // 링크 클릭하여 다운로드 시작
          downloadLink.click()
        })
        .catch(error => {
          alert(error)
        })
    },
    async deletePainting ({ commit }, paintingId) {
      await deletePainting(paintingId)
        .then(({ data }) => {
          alert('그림을 성공적으로 삭제했습니다.')
          commit('SET_PAINTING', null)
        })
        .catch(error => {
          alert('그림 삭제에 실패했습니다.' + error)
        })
    },
    async updatePaintingTitle ({ commit }, request) {
      await updatePaintingTitle(request)
        .then(({ data }) => {
          alert(data)
          commit('SET_PAINTING', data.result)
        })
        .catch(error => {
          alert(error)
        })
    }
  }
}

export default paintingStore