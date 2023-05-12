<template>
  <div>
    <div >
      <review-my-list-view></review-my-list-view>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import Pagination from '../common/Pagination.vue'
import ReviewMyListView from '../review/ReviewMyListView.vue'

const reviewStore = 'reviewStore'

export default {
  name: 'MyInfoReviewListView',
  components: {
    Pagination,
    ReviewMyListView
  },
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
</style>
