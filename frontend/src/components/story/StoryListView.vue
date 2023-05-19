<template>
  <div>
    <div class="title-container">
      <h1 class="h1-title">동화책 목록</h1>
      <router-link to="/story/keyword"><button class="ae-btn btn-red">동화 만들러 가기</button></router-link>
    </div>
    <ModalView :audio="audio" :modalShow="isModalVisible" @close-modal="closeModal">
      <StoryListDetailView @audio-submit="audioSubmit" @close-modal="closeModal"/>
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
import StoryListDetailView from '@/components/story/StoryListDetailView'
import { mapMutations, mapGetters, mapState, mapActions } from 'vuex'
import ListItem from '../common/list/ListItem.vue'
import Pagination from '../common/Pagination.vue'

const storyStore = 'storyStore'

export default {
  name: 'StoryListView',
  components: {
    ModalView,
    StoryListDetailView,
    ListItem,
    Pagination
  },
  data () {
    return {
      request: {
        page: 0
      },
      isModalVisible: false,
      audio: Object
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
    audioSubmit (audio) {
      this.audio = audio
    },
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
  width: 800px;
  margin: auto;
}

.disabled{
  background-color: lightgray;
}
.title-contanier {
  width: 800px;
  margin: auto;
}

</style>
