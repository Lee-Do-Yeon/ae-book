<template>
  <div>
      <h1 class="subject">작성한 리뷰</h1>
      <div class="gray-bar"></div>
    <div class="review-container">
      <div v-show="reviewMyList.length == 0">
        <p>등록된 리뷰가 없습니다</p>
      </div>
      <div v-show="reviewMyList.length > 0">
        <div
          class="list-group-item"
          v-for="review in reviewMyList"
          :key="review.id"
        >
          <review-my-item-view :review=review :page=request.page @paging="paging"></review-my-item-view>
        </div>
        <div class="pagination-container">
          <pagination :pageSetting="reviewMyPageSetting" @paging="paging"></pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import Pagination from '../common/Pagination.vue'
import ReviewMyItemView from './ReviewMyItemView.vue'

const reviewStore = 'reviewStore'

export default {
  components: {
    Pagination,
    ReviewMyItemView
  },
  name: 'ReviewMyListView',
  data () {
    return {
      request: {
        page: 0,
        size: 3,
        sort: 'createdAt',
        direction: 'DESC'
      }
    }
  },
  methods: {
    ...mapActions(reviewStore, ['getReviewMyListAction']),
    paging (page) {
      this.request['page'] = page - 1
      this.getReviewMyListAction(this.request)
    }
  },
  computed: {
    ...mapState(reviewStore, ['reviewMyList', 'reviewMyPageSetting'])
  },
  mounted () {
    const request = {
      page: 0,
      size: 3,
      sort: 'createdAt',
      direction: 'DESC'
    }
    this.getReviewMyListAction(request)
  }
}
</script>

<style scoped>
.review-container {
  margin: auto;
  margin-top: 30px;
}
p{
  font-size: 25px;
  margin-left: 30px;
}
.list-group-item {
  background: none;
  border: none;
  display: flex;
  justify-content: center;
  padding: 3px;
}
.subject{
  text-align: left;
  font-size: 24px;
  font-weight: 800;
}
.gray-bar {
  height:2px; background-color: #E0E0E0;
}
</style>
