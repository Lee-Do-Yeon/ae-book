<template>
  <div>
    <div>
      <div class='description'>
        <p>방법 1. 키워드를 작성한다. 자동 작성 버튼을 누른다.</p>
        <p class='p-bottom'>방법 2. 마이크 버튼을 누른다. 키워드를 나열하며 말한다.</p>
        <p class='p-line'>마이크 버튼을 눌러서 녹음을 멈춘다.</p>
      </div>
      <b-form @reset="onReset" v-if="show">
        <div class='line-1'>
          <div class='line-2'>
            <b-form-group
              id="input-group-1"
            >
              <b-input-group class="mt-3">
                <b-form-input
                  id="input-1"
                  class="input-line"
                  v-model="form.keyword"
                  type="text"
                  placeholder="키워드를 입력하세요."
                  ref="keywordInput"
                ></b-form-input>
                <b-input-group-append>
                    <b-button v-if="isRecording===false" class='' type="button" @click="soundToKeyword"><img width="20px" height="20px" src="https://img.icons8.com/fluency-systems-filled/48/microphone.png" alt="microphone"/></b-button>
                    <b-button v-else class='' type="button" @click="stopSoundToKeyword"><img width="20px" height="20px" src="https://img.icons8.com/doodle/48/bandicam.png" alt="bandicam"/></b-button>
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
              <b-form-group
                id="input-group-2"
                label-for="input-2"
              >
              </b-form-group>
          </div>
          <button id='auto-create' class='ae-btn btn-red' type="button" @click="createAIReview">자동 작성</button>

        </div>
        <hr>
        <b-form-group id="input-group-3" label="리뷰내용" label-for="input-2">
           <div class='score-container'>
            <review-modify-score-view v-if="isModify" :score=this.form.score :isModify=this.isModify @modify-score="modifyScore" />
          </div>
        </b-form-group>
        <div
          v-if="isLoading"
          :key = "repeatKey"
          class="animated-text">
          {{ loadingMessage }}
        </div>
        <b-form-textarea
          v-else
          id="input-3"
          v-model="form.content"
          placeholder="내용을 300자 내로 입력해주세요."
          rows="6"
          min="1"
          :max="150"
          ref="contentInput"
        ></b-form-textarea>
        <div v-show="isModify" class="limit">현재 {{ this.form.content.length }} / 300 자 입니다.</div>

        <b-button class='submit-btn b-btn' type="button" variant="primary" @click="onSubmit">등록</b-button>
        <b-button class='reset-btn b-btn' type="reset" variant="danger">초기화</b-button>
      </b-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { mapActions, mapState } from 'vuex'
import ReviewModifyScoreView from './ReviewModifyScoreView.vue'

const reviewStore = 'reviewStore'
const bookStore = 'bookStore'

export default {
  components: { ReviewModifyScoreView },
  name: 'ReviewCreateView',
  props: {
    bookInfo: Object
  },
  data () {
    return {
      form: {
        title: this.bookInfo.title,
        keyword: '',
        writer: this.bookInfo.writer,
        isbn: this.bookInfo.isbn,
        content: '',
        score: 5
      },
      show: true,
      isModify: true,
      audioArray: [],
      mediaRecorder: null,
      isRecording: false,
      isLoading: false,
      loadingMessage: 'AI가 글을 쓰고 있습니다...',
      repeatKey: 0,
      curse: 0
    }
  },
  mounted () {
    this.animateText()
  },
  computed: {
    ...mapState(bookStore, ['book'])
  },
  methods: {
    animateText () {
      setInterval(() => {
        this.repeatKey += 1
      }, 4000)
    },
    soundToKeyword () {
      navigator.mediaDevices.getUserMedia({ audio: true })
        .then((stream) => {
          this.mediaRecorder = new MediaRecorder(stream)
          this.mediaRecorder.ondataavailable = (event) => {
            this.audioArray.push(event.data)
          }
          this.mediaRecorder.start()
          this.isRecording = true
        })
    },
    async stopSoundToKeyword () {
      this.isLoading = true
      this.isRecording = false
      this.mediaRecorder.stop()
      this.mediaRecorder.onstop = (event) => {
        const blob = new Blob(this.audioArray, {type: 'audio/mp3'})
        this.audioArray.splice(0)
        const formData = new FormData()
        formData.append('audio', blob, 'recoding.mp3')
        formData.append('title', this.form.title)

        if (this.form.writer != null) {
          formData.append('writer', this.form.writer)
        }

        axios
          .post('/fast/reviews/sound', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          .then(result => {
            if (result.data.respond === 0) {
              alert('음성인식에 실패했습니다. 다시 말씀해주세요.')
            } else if (result.data.respond === 2) {
              alert('욕설이나 혐오적인 표현은 작성할 수 없습니다.')
            } else {
              this.form.keyword = result.data.words
              this.form.content = result.data.review
              this.form.score = result.data.star
            }
            this.isLoading = false
          })
          .catch(err => {
            throw new Error(err)
          })
      }
    },
    async createAIReview () {
      if (!this.form.keyword) {
        alert('키워드를 입력해주세요.')
        this.$refs.keywordInput.focus()
        return
      }
      this.isLoading = true
      axios
        .post(`/fast/reviews/gpt`, {
          title: this.form.title,
          keyword: this.form.keyword,
          writer: this.form.writer
        })
        .then(result => {
          this.curse = result.data.respond

          if (this.curse === 2) {
            alert('욕설이나 혐오적인 표현은 작성할 수 없습니다.')
            this.form.keyword = ''
          } else {
            this.form.content = result.data.review
            this.form.score = result.data.star
          }
          this.isLoading = false
        })
        .catch(err => {
          throw new Error(err)
        })
    },
    ...mapActions(reviewStore, ['saveReviewAction', 'getReviewBookListAction']),
    onSubmit (event) {
      if (!this.form.content) {
        alert('리뷰 내용을 입력해주세요')
        this.$refs.contentInput.focus()
        return
      }

      if (this.form.content.length > 300) {
        alert('리뷰 입력은 300자 내로 가능합니다. \n' + '현재 입력된 글자는 ' + this.form.content.length + '자 입니다.')
        this.$refs.contentInput.focus()
        return
      }

      const payload = {
        isbn: this.form.isbn,
        data: {
          content: this.form.content,
          score: this.form.score
        }
      }

      const request = {
        isbn: this.form.isbn,
        page: 0,
        size: 3,
        sort: 'createdAt',
        direction: 'DESC'
      }

      this.saveReviewAction(payload)
        .then(() => {
          return this.getReviewBookListAction(request)
        })
        .then(() => {
          this.$emit('close-modal')
          this.$emit('get-my-review')
          this.book.reviewCount += 1
          this.book.scoreSum += this.form.score
        })
        .catch((err) => {
          throw Error(err)
        })
    },
    onReset (event) {
      event.preventDefault()
      // Reset our form values
      this.form.keyword = ''
      this.form.content = ''
      this.form.score = 5
      // Trick to reset/clear native browser form validation state
      this.show = false
      this.$nextTick(() => {
        this.show = true
      })
    },
    modifyScore (index) {
      this.form.score = index
    }
  }

}
</script>

<style scoped>
#input-group-3 {
  margin-top: 30px;
  margin-bottom: 0px;
  text-align: left;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.limit {
  text-align: right;
}
.input-line {
  width: 287px;
}
.p-bottom {
  margin-bottom: 0rem;
}
.p-line {
  margin-left: 54px;
}
.description {
  margin-left: 18px;
  text-align: left;
}
.reset-btn {
  background-color: var(--ae-red);
}
.submit-btn {
  background-color: var(--ae-navy);
}
.line-1 {
  display: flex;
  margin-bottom: 20px;
}
.score-container {
  margin-left: auto;
  margin-bottom: 5px;
}
.btn-red {
  width: 120px;
  height: 43px;
  padding: 8px 5px;
  border-radius: 8px;
  border: 2px solid;
  font-weight: bold;
  margin-top: 14px;
}
.b-btn {
  width: 90px;
  font-weight: bold;
}
.animated-text {
  animation: fade-in-out-animation 4s linear;
}

@keyframes fade-in-out-animation {
  0%, 100% { opacity: 0; }
  50% { opacity: 1; }
}
</style>
