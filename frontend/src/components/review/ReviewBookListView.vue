<template>
  <div>
    <div class="review-container">
      <div v-show="reviewBookList.length == 0">
        <p class="p-font">등록된 리뷰가 없습니다</p>
      </div>
      <div v-show="reviewBookList.length > 0">
        <div
          class="list-group-item"
          v-for="review in reviewBookList"
          :key="review.id"
        >
          <review-book-item-view :review=review :page=request.page @paging="paging"></review-book-item-view>
        </div>
        <div class="pagination-container">
          <pagination :pageSetting="reviewBookPageSetting" @paging="paging"></pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import Pagination from '../common/Pagination.vue'
import ReviewBookItemView from './ReviewBookItemView.vue'

const reviewStore = 'reviewStore'

export default {
  name: 'ReviewBookListView',
  components: {
    Pagination,
    ReviewBookItemView
  },
  props: ['isbn'],
  data () {
    return {
      request: {
        isbn: this.isbn,
        page: 0,
        size: 3,
        sort: 'createdAt',
        direction: 'DESC'
      }
    }
  },
  methods: {
    ...mapActions(reviewStore, ['getReviewBookListAction']),
    paging (page) {
      this.request['page'] = page - 1
      this.getReviewBookListAction(this.request)
    }
  },
  computed: {
    ...mapState(reviewStore, ['reviewBookList', 'reviewBookPageSetting'])
  },
  mounted () {
    const request = {
      isbn: this.isbn,
      page: 0,
      size: 3,
      sort: 'createdAt',
      direction: 'DESC'
    }
    this.getReviewBookListAction(request)
  }
}
</script>

<style scoped>
.list-group-item {
  background: none;
  border: none;
  display: flex;
  justify-content: center;
}
.pagination-container {
  display:flex;
  justify-content: center;
}
</style>
