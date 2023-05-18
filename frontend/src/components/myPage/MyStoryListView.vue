<template>
  <div>
    <h1 class="subject">동화책</h1>
    <div style="height:2px; background-color: #E0E0E0;"></div>
    <ModalView :modalShow="isModalVisible" @close-modal="closeModal">
      <StoryDetailView @close="closeModal"/>
    </ModalView>

    <div v-if="storyList.length !== 0">
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
    <div v-else>
      <p class="no-content">동화가 없습니다</p>
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
    this.getMyStoryList(this.request)
  },
  computed: {
    ...mapState(storyStore, ['storyList', 'storyPageSetting']),
    ...mapGetters(storyStore, ['getStoryId'])
  },
  methods: {
    ...mapActions(storyStore, ['getMyStoryList']),
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
      this.getMyStoryList(this.request)
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

.subject{
  text-align: left;
  font-size: 24px;
  font-weight: 800;
}

.no-content{
  font-size: 25px;
  margin-top: 30px;
}

</style>
