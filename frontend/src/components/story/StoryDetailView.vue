<template>
  <div>
    <b-input-group v-if="story">
      <template #prepend>
        <b-input-group-text>제목</b-input-group-text>
      </template>
      <b-form-input v-model="story.title" placeholder="Enter your name"></b-form-input>
      <template #append>
        <b-button @click="updateTitle()" variant="success" size="sm" >수정</b-button>
      </template>
    </b-input-group>
    <div class="container">
      <div class="container-left" style="margin-bottom:20px;">
        <img v-bind:src="story.imgUrl" alt="story image" v-if="story" id="storyImg" style="margin-bottom:10px;"/>
        <div>
          <b-button @click="playAudio(stop)" size="sm" variant="link"><img src="../../assets/images/icons/play.png" width="40" height="40"></b-button>
          <b-button @click="stopAudio(stop)" size="sm" variant="link"><img src="../../assets/images/icons/pause.png" width="40" height="40"></b-button>
          <b-button @click="restartAudio()" size="sm" variant="link"><img src="../../assets/images/icons/redo.png" width="40" height="40"></b-button>
        </div>
      </div>
      <div class="container-right">
        <p>{{ story.content }}</p>
      </div>

    </div>
    <StoryModalButton :voiceUrl="story.voiceUrl" @close="closeModal"></StoryModalButton>
  </div>
</template>

<script>
import { searchDetailStory, updateStoryTitle } from '@/api/story'
import { mapGetters } from 'vuex'
import StoryModalButton from '@/components/story/StoryModalButton'
const storyStore = 'storyStore'

export default {
  name: 'StoryDetailView',
  components: {
    StoryModalButton
  },
  computed: {
    ...mapGetters(storyStore, ['getStoryId'])
  },

  data () {
    return {
      story: null,
      audio: Object,
      stop: true
    }
  },
  mounted () {
    this.storyId = this.getStoryId

    searchDetailStory(this.storyId)
      .then(response => {
        this.story = response.data.result
        this.audio = new Audio(this.story.voiceUrl)
      })
      .catch(error => {
        alert('정상적으로 조회하지 못했습니다. ' + error)
      })
  },
  methods: {
    closeModal () {
      this.audio.pause()
      this.$emit('close')
    },
    updateTitle () {
      this.storyId = this.getStoryId
      this.request = {
        'title': this.story.title
      }
      updateStoryTitle(this.storyId, this.request)
        .then(response => {
          if (response.data.resultCode === 200) {
            alert('정상적으로 수정했습니다.')
            this.closeModal()
          } else {
            alert('정상적으로 수정하지 못했습니다.')
          }
        }).catch(error => {
          alert('정상적으로 수정하지 못했습니다. ' + error)
        })
    },
    playAudio (stop) {
      if (stop) {
        this.audio.play()
        this.stop = !stop
      }
    },
    stopAudio (stop) {
      if (stop === false) {
        this.audio.pause()
        this.stop = !stop
      }
    },
    restartAudio () {
      this.audio.pause()
      this.audio.currentTime = 0
      this.audio.play()
      this.stop = !stop
    }

  }

}
</script>

<style scoped>
.container {
  padding: 10px;
  display: flex;
  flex-direction: row;
}

.container-left {
  width: 50%;
  margin-left: 0px;
  margin-right: 30px;
}

.container-right {
  width: 50%;
  height: 500px;
  padding:0px;
  margin:0px;
  flex-direction: column;
  justify-content: space-evenly;
  overflow-y: scroll;
  white-space: pre-wrap;
  display:block;
}

#storyImg{
  border-radius: 15px;
  box-shadow: 0px 1px 10px 0.1px rgba(0, 0, 0, 0.3);
  width: 100%;
  height: 95%;
}

p{
  font-size: 25px;
  line-height: 2;
  letter-spacing: 2;
}

</style>
