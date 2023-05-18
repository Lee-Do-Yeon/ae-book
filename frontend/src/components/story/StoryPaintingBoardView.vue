<template>
<div>
  <div class="container">
    <!-- 왼쪽 -->
    <div class="container-left">
      <canvas id="canvas"
        ref="canvas"
        @mousemove="onMove"
        @mousedown="startPainting"
        @mouseup="cancelPainting"
        @mouseleave="cancelPainting"
        @touchmove="onMove"
        @touchstart="startPainting"
        @touchend="cancelPainting"
        @touchcancel ="cancelPainting"
        width="500"
        height="500"
      ></canvas>
    </div>
    <!-- 왼쪽 -->
    <!-- 오른쪽 -->
    <div class="container-right">
    <div id="palette">
      <div class="left">
        <div
          v-for="(color, index) in colorOptions1"
          :key="index"
          :style="{ backgroundColor: color }"
          @click="onColorClick(color)"
          class="color-option"
        ></div>
      </div>
      <div class="right">
        <div
          v-for="(color, index) in colorOptions2"
          :key="index"
          :style="{ backgroundColor: color }"
          @click="onColorClick(color)"
          class="color-option"
        ></div>
      </div>
    </div>
    <div class="tools">
      <input id="line-width" @change="onLineWidthChange" type="range" min="5" max="20" value="10">
      <div>
        <img src="@/assets/images/icons/reset.png" width="40px" id="reset-btn" @click="onResetClick">
        <img src="@/assets/images/icons/eraser.png" width="40px" id="eraser-btn" @click="onEraserClick" :class="{'now-tool': mode == 'eraser'}">
        <img src="@/assets/images/icons/brush.png" width="40px" id="brush-btn" @click="onBrushClick" :class="{'now-tool': mode == 'brush'}">
      </div>
    </div>
    <button v-if="storyReady" @click="goReadStory" class="ae-btn btn-red">동화 보러 가기</button>
    <button v-else class="ae-btn btn-navy" style="background-color: lightgray; color: var(--ae-navy)" disabled>동화 만드는 중</button>
  </div>
  <!-- 오른쪽 -->
  </div>
</div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StoryPaintingBoardView',
  data () {
    return {
      clickedTool: '',
      keyword: '',
      storyResult: '',
      sound_filename: '',
      storyReady: false,
      canvas: Object,
      ctx: Object,
      isPainting: false,
      mode: 'brush',
      colorOptions1: ['#ff0000', '#ff8c00', '#ffff00', '#008000'],
      colorOptions2: ['#0000ff', '#800080', '#000080', '#000000'],
      title: '',
      color: '',
      voiceBlob: {}
    }
  },
  beforeMount () {
    window.addEventListener('beforeunload', this.showConfirmation)
  },
  beforeDestroy () {
    window.removeEventListener('beforeunload', this.showConfirmation)
  },
  created () {
    this.keyword = this.$route.params.inputValue
    this.generateStory()
  },
  mounted () {
    this.canvas = this.$refs.canvas
    this.ctx = this.canvas.getContext('2d')
    this.ctx.lineWidth = 10
    this.ctx.lineJoin = 'round'
    this.ctx.lineCap = 'round'
    this.onResetClick()
  },
  methods: {
    showConfirmation (event) {
      event.preventDefault()
      event.returnValue = ''
      return ''
    },
    generateStory () {
      axios
        .post(`/fast/stories/gpt`, {
          text: this.keyword
        })
        .then(result => {
          if (result.data.respond === 2) {
            alert('욕설이나 혐오적인 표현은 작성할 수 없습니다.')
            window.history.back()
          } else {
            this.storyResult = result.data.story
            axios({
              url: `/fast/stories/sound`,
              method: 'post',
              data: {
                data: this.storyResult
              }
            })
              .then(result => {
                console.log(result)
                this.sound_filename = result.data.sound
                this.storyReady = true
                axios({
                  url: `/fast/file/download/${this.sound_filename}`,
                  method: 'GET',
                  responseType: 'blob'
                }).then((response) => {
                  this.voiceBlob = new Blob([response.data], {type: 'audio/mp3'})
                  console.log(this.voiceBlob)
                })
              })
              .catch(err => {
                alert('TTS 생성 중 문제 발생' + err)
              })
          }
        })
        .catch(err => {
          alert('동화 생성 중 문제 발생' + err)
        })
    },
    goReadStory () {
      // 그림은 어떻게 할까.
      let imgBase64 = this.canvas.toDataURL('image/png')
      this.$router.push({ name: 'StoryResult', params: {painting: imgBase64, story: this.storyResult, voiceBlob: this.voiceBlob} })
    },
    onMove (event) {
      event.preventDefault()
      let x, y
      if (this.isPainting) {
        if (event.type === 'touchmove') {
          x = event.touches[0].clientX - this.canvas.offsetLeft
          y = event.touches[0].clientY - this.canvas.offsetTop
        } else {
          x = event.offsetX
          y = event.offsetY
        }
        if (this.mode === 'brush') {
          this.ctx.strokeStyle = this.color
          this.ctx.fillStyle = this.color
          this.ctx.lineTo(x, y)
          this.ctx.stroke()
        } else if (this.mode === 'eraser') {
          this.ctx.strokeStyle = 'white'
          this.ctx.fillStyle = 'white'
          this.ctx.lineTo(x, y)
          this.ctx.stroke()
        }
      }
      this.ctx.beginPath()
      this.ctx.moveTo(x, y)
    },
    startPainting () {
      this.isPainting = true
    },
    cancelPainting () {
      this.isPainting = false
      this.ctx.beginPath()
    },
    onLineWidthChange (event) {
      this.ctx.lineWidth = event.target.value
    },
    onColorClick (color) {
      this.color = color
      this.ctx.strokeStyle = color
      this.ctx.fillStyle = color
    },
    onBrushClick () {
      this.mode = 'brush'
    },
    onEraserClick () {
      this.mode = 'eraser'
    },
    onResetClick () {
      // 채울 스타일을 적용
      this.ctx.fillStyle = 'white'
      // 캔버스 크기의 사각형으로 채우기
      this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
      this.ctx.beginPath()
    },
    canvasToFile (canvas) {
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
      const paintingFile = new File([blob], 'painting_' + new Date().getMilliseconds() + '.png', { type: 'image/png' })

      return paintingFile
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0 100px;
  display: flex;
  flex-direction: row;
  margin-top: 45px;
}

.container-left {
  width: 60%;
  margin-right: 30px;
}

.container-right {
  width: 40%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

#canvas {
    border-radius: 15px;
    box-shadow: 0px 1px 10px 0.1px rgba(0, 0, 0, 0.3);
    float: left;
}

/* palette */
#palette {
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.left, .right {
  display: flex;
  flex-direction: column;
}

.color-option {
  width: 40px;
  height: 40px;
  margin: 5px;
  box-shadow: 1px 3px 3px 0.1px rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  cursor: pointer;
}

h1 {
  font-weight: 800;
  margin: 10px 0px;
}

#palette, .tools {
  background-color: white;
  border-radius: 30px;
  padding: 20px 0px;
}

.now-tool{
  width: 50px;
  border: 2px solid var(--ae-red);
  border-radius: 100%;
}
</style>
