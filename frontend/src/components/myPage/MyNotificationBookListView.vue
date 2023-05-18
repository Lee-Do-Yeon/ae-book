<template>
  <div>
    <h1 class="subject">알림 신청한 책 목록</h1>
    <div style="height:2px; background-color: #E0E0E0;"></div>
    <ModalView :modalShow="isModalVisible" @close-modal="closeModal">
      <notification-detail-view :notificationId="notificationId" :page="this.request.page" :isbn="isbn"/>
      <notification-modal-button :notificationId="notificationId" :request="this.request" @close="closeModal" @paging="paging"></notification-modal-button>
    </ModalView>
    <div v-if="notificationList.length >=1">
      <div class="notification-container">
        <div v-for="notification in notificationList" :key="notification.id" @click="showModal(notification.id, notification.isbn)">
          <list-item
              :item="notification"
            >
          </list-item>
        </div>
      </div>
      <div class="pagination-container" v-if="notificationList.length >=1">
        <pagination :pageSetting="notificationPageSetting" @paging="paging"></pagination>
      </div>
    </div>
    <div v-else>
      <p class="no-content">알림 신청한 책이 없습니다</p>
    </div>

  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import ListItem from '../common/list/ListItem.vue'
import Pagination from '../common/Pagination.vue'
import ModalView from '@/components/common/ModalView.vue'
import ImgCarouselView from '@/components/common/list/ImgCarouselView.vue'
import NotificationDetailView from '@/components/notification/NotificationDetailView.vue'
import NotificationModalButton from '@/components/notification/NotificationModalButton.vue'
const notificationStore = 'notificationStore'

export default {
  name: 'MyNotificationBookListView',
  components: {
    ModalView,
    ImgCarouselView,
    NotificationDetailView,
    NotificationModalButton,
    ListItem,
    Pagination
  },
  data () {
    return {
      isModalVisible: false,
      notificationId: null,
      isbn: null,
      request: {
        page: 0,
        size: 3,
        sort: 'createdAt',
        direction: 'DESC'
      }
    }
  },
  computed: {
    ...mapState(notificationStore, ['notificationList', 'notificationPageSetting'])
  },
  mounted () {
    this.getBookNotificationList(this.request)
  },
  methods: {
    ...mapActions(notificationStore, ['getBookNotificationList', 'getBookNotificationDetail']),
    paging (page) {
      this.request['page'] = page - 1
      this.getBookNotificationList(this.request)
    },
    showModal (notificationId, isbn) {
      this.notificationId = notificationId
      this.isbn = isbn
      this.getBookNotificationDetail(notificationId)
      this.isModalVisible = true
    },
    closeModal () {
      this.isModalVisible = false
      this.getBookNotificationList(this.request)
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
.notification-container{
  margin: auto;
  margin-top: 30px;
  /* width: 1000px; */
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
