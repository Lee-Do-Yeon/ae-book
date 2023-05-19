<template>
  <div>
    <h2>{{story.title}}</h2>
    <h4>동화작가 : {{story.nickname}}</h4>
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
  </div>
</template>

<script>
import { searchDetailStory } from '@/api/story'
import { mapActions, mapGetters } from 'vuex'
const storyStore = 'storyStore'

export default {
  name: 'StoryDetailView',
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
        this.audio.addEventListener('ended', () => {
          this.stop = !this.stop
        })
        this.$emit('audio-submit', this.audio)
      })
      .catch(error => {
        alert('정상적으로 조회하지 못했습니다. ' + error)
      })
  },
  methods: {
    ...mapActions(storyStore, ['updateTitleOfStory']),
    closeModal () {
      if (this.audio) {
        this.audio.pause()
        this.audio.currentTime = 0
      }
      this.$emit('close-modal')
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
.h2 {
  padding: 10px;
  text-align: center;
}

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
