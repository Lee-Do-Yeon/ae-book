<template>
  <div>
    <h2>나의 동화 목록</h2>
    <ModalView :modalShow="isModalVisible" @close-modal="closeModal">
      <StoryDetailView @close="closeModal"/>
    </ModalView>
    <div class="story-container">
      <div v-for="story in storyList" :key="story.storyId" @click="showModal(story.storyId)">
        <list-item
        :item="story"
      >
    </list-item>
      </div>
    </div>
    <div class="pagination-container">
      <pagination :pageSetting="storyPageSetting" @paging="paging"></pagination>
    </div>
  </div>
</template>

<script>
import ModalView from '@/components/common/ModalView'
import StoryDetailView from '@/components/story/StoryDetailView'
import { mapMutations, mapGetters, mapState, mapActions } from 'vuex'
import ListItem from '../common/list/ListItem.vue'
import Pagination from '../common/Pagination.vue'

const storyStore = 'storyStore'

export default {
  name: 'MyStoryListView',
  components: {
    ModalView,
    StoryDetailView,
    ListItem,
    Pagination
  },
  data () {
    return {
      request: {
        page: 0
      },
      isModalVisible: false
    }
  },

  mounted () {
    this.getStoryList(this.request)
  },
  computed: {
    ...mapState(storyStore, ['storyList', 'storyPageSetting']),
    ...mapGetters(storyStore, ['getStoryId'])
  },
  methods: {
    ...mapActions(storyStore, ['getStoryList']),
    ...mapMutations(storyStore, ['setStoryId', 'clearStoryId']),
    showModal (storyId) {
      this.setStoryId(storyId)
      this.isModalVisible = true
    },
    closeModal () {
      this.isModalVisible = false
      this.clearStoryId()
    },
    paging (page) {
      this.request['page'] = page - 1
      this.getStoryList(this.request)
    }
  }
}
</script>

<style scoped>
.story-container{
  margin: auto;
  margin-top: 30px;
  width: 800px;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.pagination-container {
  display:flex;
  justify-content: center;
}
</style>
