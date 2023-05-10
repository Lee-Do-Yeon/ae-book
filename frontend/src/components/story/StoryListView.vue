<template>
  <div>
    <h1>동화책 목록</h1>
    <router-link to="/story/keyword"><button class="ae-btn btn-red">동화 만들러 가기</button></router-link>
    <ModalView :modalShow="isModalVisible" @close-modal="closeModal">
      <StoryDetailView />
    </ModalView>
    <div v-if="isLoginUser === false" class="story-container">
      로그인한 유저만 확인 가능합니다.
    </div>
    <div v-else class="story-container">
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
  name: 'StoryListView',
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
    if (sessionStorage.getItem('isLoginUser') === 'true') {
      this.isLoginUser = true
      this.getStoryList(this.request)
    }
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

.disabled{
  background-color: lightgray;
}

h1 {
  font-weight: 800;
  margin: 10px 0px;
}
</style>
