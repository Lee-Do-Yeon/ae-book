<template #modal-footer>
  <div class="modal-footer">
    <!-- Emulate built in modal footer ok and cancel button actions -->
    <b-button size="sm" variant="success" @click="updateTitle()">
      수정
    </b-button>
    <b-button size="sm" variant="danger" @click="deleteStoryById()">
      삭제
    </b-button>
    <b-button @click="playAudio(stop)" size="sm" variant="info">오디오 재생</b-button>
    <b-button @click="stopAudio(stop)" size="sm" variant="primary">오디오 일시 정지</b-button>
    <b-button @click="restartAudio()" size="sm" variant="warning">오디오 처음부터 듣기</b-button>
    <b-button @click="closeModal" size="sm" variant="dark">닫기</b-button>
  </div>
</template>
<script>

import { deleteStory, updateStoryTitle } from '@/api/story'
import { mapGetters } from 'vuex'

const storyStore = 'storyStore'

export default {

  components: {
  },
  data () {
    return {
      audio: Object,
      stop: true
    }
  },
  props: {
    title: String,
    voiceUrl: String
  },

  mounted () {
    this.audio = new Audio(this.voiceUrl)
  },

  methods: {
    closeModal () {
      this.audio.pause()
      this.$emit('close')
    },

    deleteStoryById () {
      this.storyId = this.getStoryId
      deleteStory(this.storyId)
        .then(response => {
          if (response.data.resultCode === 200) {
            alert('정상적으로 삭제했습니다.')
            this.closeModal()
          }
        })
        .catch(error => {
          alert('정상적으로 삭제하지 못했습니다. ' + error)
        })
    },
    updateTitle () {
      this.storyId = this.getStoryId
      this.request = {
        'title': this.title
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

  },

  computed: {
    ...mapGetters(storyStore, ['getStoryId'])
  }
}
</script>

<style>

</style>
