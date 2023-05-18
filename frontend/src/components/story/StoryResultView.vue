<template>
  <div>

    <b-input-group v-if="story" style="margin-left:500px; width:500px; text-align: center; margin-top:20px; margin-bottom:30px;">
      <template #prepend>
        <b-input-group-text>제목</b-input-group-text>
      </template>
      <b-form-input v-model="title" placeholder="제목을 입력하세요"></b-form-input>
      <template #append>
        <b-button @click="onSaveClick" class="ae-btn btn-red" >동화 저장</b-button>
      </template>
    </b-input-group>
    <div class="container">
      <div class="container-left">
        <canvas id="canvas"
          ref="canvas"
          width="500"
          height="500"
        ></canvas>
      </div>
      <div class="container-right">
        <div id="scrollStory"><p>{{ story }}</p></div>
        <div id="buttonGroup" >
          <b-button @click="playAudio(stop)" size="sm" variant="link"><img src="https://img.icons8.com/fluency-systems-filled/48/play.png" width="48" height="48"  alt="play"/></b-button>
          <b-button @click="stopAudio(stop)" size="sm" variant="link"><img src="https://img.icons8.com/ios-glyphs/60/stop.png" width="60" height="60"  alt="stop"/></b-button>
          <b-button @click="restartAudio()" size="sm" variant="link"><img  src="https://img.icons8.com/glyph-neue/64/restart--v1.png" width="60" height="60" alt="restart--v1"/></b-button>
        </div>
      </div>

    </div>
  </div>
  </template>

<script>
import { mapActions } from 'vuex'
const storyStore = 'storyStore'

export default {
  name: 'StoryResultView',
  data () {
    return {
      title: '',
      painting: '',
      story: null,
      voiceBlob: {},
      audio: Object,
      stop: true
    }
  },
  beforeMount () {
    window.addEventListener('beforeunload', this.showConfirmation)
  },
  beforeDestroy () {
    window.removeEventListener('beforeunload', this.showConfirmation)
  },
  created () {
    this.painting = this.$route.params.painting
    this.story = this.$route.params.story
    this.voiceBlob = this.$route.params.voiceBlob
  },
  beforeRouteLeave (to, from, next) {
    // 오디오 요소를 찾아 재생을 멈춘다
    if (this.audio) {
      this.audio.pause()
      this.audio.currentTime = 0
    }
    next()
  },
  mounted () {
    this.canvas = this.$refs.canvas
    this.ctx = this.canvas.getContext('2d')
    let image = new Image()
    image.src = this.painting
    image.onload = function () {
      this.ctx.drawImage(image, 0, 0, 500, 500)
    }.bind(this)
    this.createAudio()
  },
  methods: {
    ...mapActions(storyStore, ['saveStory']),
    showConfirmation (event) {
      event.preventDefault()
      event.returnValue = ''
      return ''
    },
    createAudio () {
      var blobURL = window.URL.createObjectURL(this.voiceBlob)
      this.audio = new Audio(blobURL)
      this.audio.addEventListener('ended', () => {
        this.stop = !this.stop
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
    },
    canvasToFile (canvas, milliseconds) {
      // canvas -> dataURL
      let imgBase64 = canvas.toDataURL('image/png')

      const byteString = atob(imgBase64.split(',')[1])
      const ab = new ArrayBuffer(byteString.length)
      const ia = new Uint8Array(ab)
      for (let i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i)
      }
      const blob = new Blob([ab], { type: 'image/png' })

      // blob -> file
      const paintingFile = new File([blob], 'story_painting_' + milliseconds + '.png', { type: 'image/png' })

      return paintingFile
    },
    voiceBlobToFile (voiceBlob, milliseconds) {
      return new File([voiceBlob], 'story_voice_' + milliseconds + '.wav', { type: 'audio/wav' })
    },
    onSaveClick () { // 임시 저장. (api 확인 XXXX)
      if (this.title.trim() === '') {
        alert('제목을 입력해주세요.')
        return
      }
      let milliseconds = new Date().getMilliseconds()
      const paintingFile = this.canvasToFile(this.canvas, milliseconds)
      const voiceFile = this.voiceBlobToFile(this.voiceBlob, milliseconds)

      let data = {
        title: this.title,
        content: this.story
      }

      let formData = new FormData()
      formData.append('voiceFile', voiceFile)
      formData.append('imageFile', paintingFile)
      formData.append('data', new Blob([JSON.stringify(data)], {type: 'application/json'}))

      this.saveStory(formData)
        .then(result => {
          alert('동화 저장에 성공했습니다.')
        }
        )
        .catch(error => {
          alert('동화 저장에 실패했습니다.' + error)
        })

      this.audio.pause()
      this.$router.push('/story/list')
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 100px;
  display: flex;
  flex-direction: row;
}

.container-left {
  width: 50%;
  margin-right: 50px;
}

.container-right {
  width: 50%;
  height: 400px;
  flex-direction: column;
  justify-content: space-evenly;
  background: white;
  border-radius: 15px;
  box-shadow: 0px 1px 10px 0.1px rgba(0, 0, 0, 0.3);
}

#scrollStory{
  overflow-y: scroll;
  white-space: pre-wrap;
  display:block;
  height: 100%;

}

#canvas {
    border-radius: 15px;
    box-shadow: 0px 1px 10px 0.1px rgba(0, 0, 0, 0.3);
    float: left;
    margin-bottom:20px;

}

.title {
  font-size: 35px;
  font-weight: 800;
}

p{
  font-size: 25px;
  line-height: 2;
  letter-spacing: 2;
}

#buttonGroup{
  margin-top: 30px;
  border-radius: 15px;
  box-shadow: 0px 1px 10px 0.1px rgba(0, 0, 0, 0.3);
  background: white;
}

</style>
