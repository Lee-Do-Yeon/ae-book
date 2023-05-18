<template>
  <div>
    <h1 class="subject">그림장</h1>
    <div style="height:2px; background-color: #E0E0E0;"></div>
    <ModalView :modalShow="isModalVisible" @close-modal="closeModal">
    <painting-detail-view/>
    <painting-modal-button @close="closeModal" :nowType="'COLOR'" :nowPage="this.request.page"></painting-modal-button>
    </ModalView>
    <div v-if="paintingList.length !== 0">
    <div class="painting-container">
      <div v-for="(painting, index) in paintingList" :key="index" @click="showModal(painting.id)">
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
  <div v-else>
    <p class="no-content">그림이 없습니다</p>
  </div>
</div>

</template>

<script>
import { mapActions, mapState } from 'vuex'
import PaintingDetailView from '@/components/painting/PaintingDetailView.vue'
import ModalView from '@/components/common/ModalView.vue'
import ListItem from '../common/list/ListItem.vue'
import Pagination from '../common/Pagination.vue'
import PaintingModalButton from '../painting/PaintingModalButton.vue'
const paintingStore = 'paintingStore'

export default {
  name: 'MyPaintingListView',
  components: {
    ModalView,
    PaintingDetailView,
    ListItem,
    PaintingModalButton,
    Pagination
  },
  data () {
    return {
      isModalVisible: false,
      request: {
        page: 0
      }
    }
  },
  computed: {
    ...mapState(paintingStore, ['paintingList', 'paintingPageSetting'])
  },
  mounted () {
    this.request = {
      type: 'COLOR',
      size: 3
    }
    this.getPaintingList(this.request)
  },
  methods: {
    ...mapActions(paintingStore, ['getPaintingList', 'getPaintingDetail']),
    showModal (paintingId) {
      this.getPaintingDetail(paintingId)
      this.isModalVisible = true
    },
    closeModal () {
      this.isModalVisible = false
    },
    paging (page) {
      this.request['page'] = page - 1
      this.getPaintingList(this.request)
    }
  }
}
</script>

<style scoped>
.subject{
  text-align: left;
  font-size: 24px;
  font-weight: 800;
}

.painting-container{
  margin: auto;
  margin-top: 30px;
  width: 100%;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.pagination-container {
  display:flex;
  justify-content: center;
}

.no-content{
  font-size: 25px;
  margin-top: 30px;
}
</style>
