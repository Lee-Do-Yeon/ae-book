<template>
  <div>
      <h1>ISBN 사진으로 검색</h1>
      <label for="file-upload" class="photo">
        <img :src="imageUrl" v-if="imageUrl" style="width:100%" />
        <img v-else src="https://img.icons8.com/ios/100/camera--v4.png" alt="camera--v4" class="camera"/>
      </label>
      <input id="file-upload" type="file" @change="onFileChange" accept=".jpg, .png, .jpeg"/>
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
      imageUrl: ''
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
            searchByISBN(request)
              .then(({data}) => {
                this.$router.push(`/book/detail/${data.data}`)
              }).catch(error => {
                alert('존재하지 않거나 접근이 불가능한 도서입니다.')
                console.error(error)
              })
          } else {
            alert('ISBN이 올바르지 않습니다.')
          }
        })
        .catch(error => {
          alert('ISBN이 올바르지 않습니다.')
          console.error('FastAPI에서 ISBN 인식 오류.')
          console.error(error)
        })
    },
    onFileChange (e) {
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

h1 {
  font-weight: 800;
  margin: 10px 0px;
}
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

</style>
