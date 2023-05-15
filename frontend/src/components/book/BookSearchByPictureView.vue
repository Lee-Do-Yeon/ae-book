<template>
  <div>
    <div v-if="isLoading">
      <div class="loader"></div>
      <div class="h1-title">검색중...</div>
    </div>
    <div v-else>
      <h1 class="h1-title">ISBN 사진으로 검색</h1>
      <label for="file-upload" class="photo">
        <img :src="imageUrl" v-if="imageUrl" style="width:100%" />
        <img v-else src="https://img.icons8.com/ios/100/camera--v4.png" alt="camera--v4" class="camera"/>
      </label>
      <input id="file-upload" type="file" @change="onFileChange" accept=".jpg, .png, .jpeg"/>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import { getISBNfromPicture, searchByISBN } from '@/api/book.js'
import ModalView from '@/components/common/ModalView.vue'
const bookStore = 'bookStore'

export default {
  name: 'BookSearchByPictureView',
  data () {
    return {
      imageUrl: '',
      isLoading: false
    }
  },
  components: {
    ModalView
  },
  computed: {
    ...mapState(bookStore, ['book'])
  },
  methods: {
    ...mapActions(bookStore, ['getBookDetail']),
    async getISBNfromPicture (request) {
      await getISBNfromPicture(request)
        .then(({ data }) => {
          const rexp = /\b\d{10}\b|\b\d{13}\b/
          if (rexp.test(data.data)) {
            searchByISBN(data.data)
              .then(({data}) => {
                this.$router.push(`/book/detail/${data.result.isbn}`)
              }).catch(error => {
                alert('존재하지 않거나 접근이 불가능한 도서입니다.')
                console.error(error)
              })
          } else {
            alert('ISBN이 올바르지 않습니다.')
          }
        })
        .then(() => {
          this.isLoading = false
        })
        .catch(error => {
          alert('ISBN이 올바르지 않습니다.')
          console.error('FastAPI에서 ISBN 인식 오류.')
          console.error(error)
          this.isLoading = false
        })
    },
    onFileChange (e) {
      this.isLoading = true
      const file = e.target.files[0]
      this.previewImage(file)
      const formData = new FormData()
      formData.append('image', file)
      this.getISBNfromPicture(formData)
    },
    previewImage (file) {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = e => {
        this.imageUrl = e.target.result
      }
    }
  }
}
</script>

<style scoped>
.photo {
  height: 500px;
  width: 40%;
  margin: auto;
  border-radius: 10px;
  border: 1px solid var(--ae-navy);
  display: flex;
  justify-content: center;
  align-items: center;
}

.ae-btn {
  cursor: pointer;
}

#file-upload {
  display: none;
}

.camera {
  width: 100px;
  margin: auto;
}

img {
  max-width: 100%;
  max-height: 100%;
}

.loader,
.loader:before,
.loader:after {
  border-radius: 50%;
  width: 2.5em;
  height: 2.5em;
  -webkit-animation-fill-mode: both;
  animation-fill-mode: both;
  -webkit-animation: load7 1.8s infinite ease-in-out;
  animation: load7 1.8s infinite ease-in-out;
}
.loader {
  color: pink;
  font-size: 10px;
  margin: 80px auto;
  position: relative;
  text-indent: -9999em;
  -webkit-transform: translateZ(0);
  -ms-transform: translateZ(0);
  transform: translateZ(0);
  -webkit-animation-delay: -0.16s;
  animation-delay: -0.16s;
}
.loader:before,
.loader:after {
  content: '';
  position: absolute;
  top: 0;
}
.loader:before {
  left: -3.5em;
  -webkit-animation-delay: -0.32s;
  animation-delay: -0.32s;
}
.loader:after {
  left: 3.5em;
}
@-webkit-keyframes load7 {
  0%,
  80%,
  100% {
    box-shadow: 0 2.5em 0 -1.3em;
  }
  40% {
    box-shadow: 0 2.5em 0 0;
  }
}
@keyframes load7 {
  0%,
  80%,
  100% {
    box-shadow: 0 2.5em 0 -1.3em;
  }
  40% {
    box-shadow: 0 2.5em 0 0;
  }
}

</style>
