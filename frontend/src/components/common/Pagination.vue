<template>
    <div class="pagination-box">
    <div class="page-list" v-if="pageSetting !== null">
      <ul>
        <li
          v-if="pageSetting.first"
          class="pagebtn pre disabled"
          style="margin-left:0px"
      >◀</li>
        <li
          v-else
          class="pagebtn pre"
          @click="sendPage(pageSetting.pageable.pageNumber)"
      >◀</li>
        <li
          class="pagebtn"
          :class="{ active: page-1 === pageSetting.pageable.pageNumber }"
          v-for="page in computedPages"
          :key="page"
          @click="sendPage(page)"
        >
          {{ page }}
        </li>
        <li
          v-if="pageSetting.last"
          class="pagebtn next disabled"
        >▶</li>
        <li
          v-else
          class="pagebtn next"
          @click="sendPage(pageSetting.pageable.pageNumber+2)"
        >▶</li>
      </ul>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    pageSetting: {
      required: true
    }
  },
  computed: {
    computedPages () {
      const currentPage = this.pageSetting.pageable.pageNumber + 1
      const totalPages = this.pageSetting.totalPages

      let startIndex = Math.floor((currentPage - 1) / 10) * 10
      let endIndex = Math.min(startIndex + 9, totalPages - 1)

      let computedPages = []

      for (let i = startIndex; i <= endIndex; i++) {
        computedPages.push(i + 1)
      }

      return computedPages
    }
  },
  methods: {
    sendPage (page) {
      this.$emit('paging', page)
    }
  }
}
</script>

<style scoped>

ul {
  padding: 0px;
}

.pagination-box{
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40px;
}
</style>
