<template>
  <div>
    <h1>그림 목록</h1>
    <button v-if="isLoginUser" @click="onClickList('LINE')" class="ae-btn">선화 리스트</button>
    <button v-else class="ae-btn disabled" disabled>선화</button>
    <button v-if="isLoginUser" @click="onClickList('COLOR')" class="ae-btn">그림 리스트</button>
    <button v-else class="ae-btn disabled" disabled>그림</button>
    <router-link to="/painting/generate"><button class="ae-btn btn-red">선화 만들러 가기</button></router-link>
    <ModalView :modalShow="isModalVisible" @close-modal="closeModal">
      <painting-detail-view/>
      <painting-modal-button @close="closeModal"></painting-modal-button>
    </ModalView>
    <div v-if="isLoginUser === false" class="painting-container">
      로그인한 유저만 확인 가능합니다.
    </div>
    <div v-else class="painting-container">
      <div v-for="painting in paintingList" :key="painting.id" @click="showModal(painting.id)">
        <list-item
            :item="painting"
          >
        </list-item>
      </div>
    </div>
    <div class="pagination-container">
      <pagination :pageSetting="paintingPageSetting" @paging="paging"></pagination>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import ListItem from '../common/list/ListItem.vue'
import Pagination from '../common/Pagination.vue'
import PaintingModalButton from '../painting/PaintingModalButton.vue'
import PaintingDetailView from '@/components/painting/PaintingDetailView.vue'
import ModalView from '@/components/common/ModalView.vue'
const paintingStore = 'paintingStore'

export default {
  name: 'PaintingListView',
  data () {
    return {
      request: null,
      isModalVisible: false,
      isLoginUser: false
    }
  },
  components: {
    ListItem,
    Pagination,
    PaintingModalButton,
    PaintingDetailView,
    ModalView
  },
  computed: {
    ...mapState(paintingStore, ['paintingList', 'paintingPageSetting'])
  },
  mounted () {
    if (sessionStorage.getItem('isLoginUser') === 'true') {
      this.isLoginUser = true
      this.onClickList('COLOR')
    }
  },
  methods: {
    ...mapActions(paintingStore, ['getPaintingList', 'getPaintingDetail']),
    onClickList (type) {
      this.request = {
        type: type
      }
      this.getPaintingList(this.request)
    },
    paging (page) {
      this.request['page'] = page - 1
      this.getPaintingList(this.request)
    },
    showModal (paintingId) {
      this.getPaintingDetail(paintingId)
      this.isModalVisible = true
    },
    closeModal () {
      this.isModalVisible = false
    }
  }
}
</script>

<style scoped>

h1 {
  font-weight: 800;
  margin: 10px 0px;
}
.painting-container{
  margin: auto;
  margin-top: 30px;
  width: 1000px;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.pagination-container {
  display:flex;
  justify-content: center;
}

.disabled{
  background-color: lightgray;
}
</style>
