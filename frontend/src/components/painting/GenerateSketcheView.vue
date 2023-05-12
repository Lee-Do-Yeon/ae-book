<template>
  <div>
    <h1 class="h1-title">선화 만들기</h1>
    <div class="image-container">
      <div class="before">
        <p>원본</p>
        <div class="img-box">
          <label for="file-upload" class="photo">
            <img :src="imageUrl" v-if="imageUrl" style="width:100%" />
            <img v-else src="https://img.icons8.com/ios-filled/100/file-submodule.png" alt="camera--v4" class="camera"/>
          </label>
        </div>
        <input id="file-upload" type="file" @change="onFileChange" accept=".jpg, .png, .jpeg"/>
      </div>
      <div class="after">
        <p>변환</p>
        <div class="img-box">
          <img :src="sketch"/>
        </div>
      </div>
    </div>
    <button @click="goBack" class="ae-btn btn-navy">돌아가기</button>
    <button @click="onSaveClick" class="ae-btn btn-red">색칠하기</button>
  </div>
</template>

<script>
import { mapActions, mapState, mapMutations } from 'vuex'
const paintingStore = 'paintingStore'

export default {
  name: 'GenerateSketcheView',
  data () {
    return {
      imageUrl: ''
    }
  },
  computed: {
    ...mapState(paintingStore, ['paintingList', 'sketch'])
  },
  mounted () {
    this.RESET_SKETCH()
  },
  methods: {
    ...mapMutations(paintingStore, ['RESET_SKETCH']),
    ...mapActions(paintingStore, ['getPaintingList', 'convertSketch', 'savePainting']),
    onFileChange (e) {
      const file = e.target.files[0]
      this.previewImage(file)
      const formData = new FormData()
      formData.append('image', file)
      this.convertSketch(formData)
    },
    previewImage (file) {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = e => {
        this.imageUrl = e.target.result
      }
    },
    Base64ToFile (imgBase64) {
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
      const paintingFile = this.Base64ToFile(this.sketch)

      let data = {
        title: 'sketch_' + new Date().getMilliseconds(),
        type: 'LINE'
      }

      let formData = new FormData()
      formData.append('paintingFile', paintingFile)
      formData.append('data', new Blob([JSON.stringify(data)], {type: 'application/json'}))

      if (sessionStorage.getItem('isLoginUser') === 'true') {
        this.savePainting(formData)
          .then(
            alert('선화를 성공적으로 저장했습니다.')
          )
          .catch(error => {
            alert('선화 저장에 실패했습니다.')
            console.error(error)
          })
      }
      this.$router.push('/painting/board')
    },
    goBack () {
      window.history.back()
    }
  }
}
</script>

<style scoped>
img {
  max-width: 500px;
}
.image-container {
  margin: auto;
  width: 1000px;
  display: flex;
  flex-direction: row;
  min-height: 400px;
  margin-top: 20px;
  margin-bottom: 30px;
}

.image-container > div {
  border-radius: 10px;
  border: 1px solid var(--ae-navy);
}

.image-container p {
  font-weight: bold;
  font-size: 20px;
}

.before {
  width: 50%;
  margin-right: 30px;
}

.after {
  width: 50%;
}

#file-upload {
  display: none;
}

.img-box {
  width: 100%;
  min-height:400px;
  margin: auto;
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>
