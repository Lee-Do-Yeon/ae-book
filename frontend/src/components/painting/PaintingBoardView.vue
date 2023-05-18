<template>
  <div class="container">
    <!-- 왼쪽 -->
    <div class="container-left">
      <div class="title-box">
        <label class="title">제목 </label>
        <input type="text" v-model="title" class="input-box">
      </div>
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
        width="700"
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
        <img src="@/assets/images/icons/reset.png" width="30px" id="reset-btn" @click="onResetClick">
        <img src="@/assets/images/icons/eraser.png" width="30px" id="eraser-btn" @click="onEraserClick" :class="{'now-tool': mode == 'eraser'}">
        <img src="@/assets/images/icons/brush.png" width="30px" id="brush-btn" @click="onBrushClick" :class="{'now-tool': mode == 'brush'}">
      </div>
    </div>
    <div>
      <button @click="goBack" class="ae-btn btn-navy">종료</button>
      <button @click="onSaveClick" class="ae-btn btn-red">저장</button>
    </div>
  </div>
  <!-- 오른쪽 -->
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
const paintingStore = 'paintingStore'

export default {
  name: 'PaintingBoardView',
  data () {
    return {
      clickedTool: '',
      canvas: Object,
      ctx: Object,
      isPainting: false,
      mode: 'brush',
      colorOptions1: ['#ff0000', '#ff8c00', '#ffff00', '#008000'],
      colorOptions2: ['#0000ff', '#800080', '#000080', '#000000'],
      title: '',
      color: '',
      image: null
    }
  },
  computed: {
    ...mapState(paintingStore, ['sketch'])
  },
  mounted () {
    this.canvas = this.$refs.canvas
    this.ctx = this.canvas.getContext('2d')
    this.ctx.lineWidth = 10
    this.ctx.lineJoin = 'round'
    this.ctx.lineCap = 'round'
    // base64 문자열 이미지 로드
    this.image = new Image()
    this.image.crossOrigin = 'anonymous' // cors 설정
    this.image.src = this.sketch
    this.image.onload = function () {
      this.ctx.drawImage(this.image, 0, 0, this.canvas.width, this.canvas.height)
    }.bind(this)
  },
  methods: {
    ...mapActions(paintingStore, ['savePainting']),
    onMove (event) {
      event.preventDefault()
      let x, y
      if (this.isPainting) {
        if (event.type === 'touchmove') {
          x = event.touches[0].clientX - this.canvas.offsetLeft + window.scrollX
          y = event.touches[0].clientY - this.canvas.offsetTop + window.scrollY
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
          // this.ctx.clearRect(event.offsetX - 5, event.offsetY - 5, this.ctx.lineWidth, this.ctx.lineWidth)
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
      this.ctx.drawImage(this.image, 0, 0, this.canvas.width, this.canvas.height)
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
    },
    onSaveClick () {
      // 제목을 썼는지 체크
      if (this.title.trim() === '') {
        alert('제목을 입력해주세요.')
        return
      }

      if (sessionStorage.getItem('isLoginUser') !== 'true') {
        alert('로그인을 해야 저장이 가능합니다.')
        return
      }

      const paintingFile = this.canvasToFile(this.canvas)

      let data = {
        title: this.title,
        type: 'COLOR'
      }

      let formData = new FormData()
      formData.append('paintingFile', paintingFile)
      formData.append('data', new Blob([JSON.stringify(data)], {type: 'application/json'}))

      this.savePainting(formData)
        .then(
          alert('그림 저장에 성공했습니다.')
        )
        .catch(error => {
          alert('그림 저장에 실패했습니다.' + error)
        })

      this.$router.push('/painting/list')
    },
    goBack () {
      window.history.back()
    }
  }
}
</script>
<style scoped>
.container {
  padding: 0 100px;
  display: flex;
  flex-direction: row;
  margin-top: 35px;
}

.container-left {
  width: 80%;
  margin-right: 30px;
}

.container-right {
  width: 20%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin-top: 60px;
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

#palette, .tools {
  background-color: white;
  border-radius: 30px;
  padding: 30px;
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

.title {
  font-size: 35px;
  font-weight: 800;
}

.ae-btn {
  width: 80px;
}

.input-box {
  height: 40px;
  width: 300px;
  border-radius: 30px;
  border: 1px solid var(--ae-navy);
  padding-left: 10px;
  margin-left: 13px;
}

.title-box {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin-bottom: 5px;
}

.now-tool{
  width: 40px;
  border: 2px solid var(--ae-red);
  border-radius: 100%;
}

</style>
