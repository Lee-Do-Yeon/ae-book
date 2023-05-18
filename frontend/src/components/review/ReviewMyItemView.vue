<template>
  <div class="review-item" :style="{ height: isExpanded ? 'auto' : '170px' }">
    <div class="review-group">
      <div class="item-group-1">
        <div class="item-info">
          <div>
            <div class="book-title">
              <router-link class='router-font' :to="{ name: 'BookDetail', params: { isbn: review.isbn } }">
            {{ review.title | removeTitlePrefix | shortText(17., '...')}}
              </router-link>
              <div class="item-updated-at">
              {{ review.updatedAt.slice(0, 10) }}
              </div>
            </div>
          </div>
        </div>
        <div class="item-score">
          <review-score-view v-if="!isModify" :score=review.score />
          <review-modify-score-view v-if="isModify" :score=review.score :isModify=this.isModify @modify-score="modifyScore" />
        </div>
      </div>
      <div class="item-group-2">
        <div
          v-show="!isModify"
          class="item-content"
          ref="itemContent"
        >
          <p v-show="!isTruncated && !isExpanded">{{ review.content }} </p>
          <p v-show="isTruncated && !isExpanded">
            {{ review.content | shortText(94, '...') }} &nbsp;
            <button class="more-content orange-btn" @click="expandContent">
            더보기
          </button></p>
          <p v-show="isTruncated && isExpanded">
            {{ review.content }}
            <button class="more-content orange-btn" @click="shrinkContent">
            닫기
          </button>
          </p>
        </div>
        </div>
        <div>
          <textarea v-show="isModify"
            id="reviewContent"
            class="item-modify"
            rows="6"
            v-model="updateContent"
            ref="reviewContent"
            >
          </textarea>
          <div v-show="isModify" class="limit">현재 {{ this.updateContent.length }} / 300 자 입니다.</div>
        </div>
      </div>
      <div class='btn-group'>
        <div v-if="!isModify">
          <button class='ae-btn' @click="modifyReview">수정</button>
          <button class='ae-btn btn-red' @click="deleteReview">삭제</button>
        </div>
        <div v-if="isModify">
          <button class='ae-btn' @click="checkValue">완료</button>
          <button class='ae-btn btn-red' @click="cancelModify">취소</button>
        </div>
      </div>
  </div>
</template>

<script>
import { mapActions, mapState, mapGetters } from 'vuex'
import ReviewScoreView from './ReviewScoreView.vue'
import ReviewModifyScoreView from './ReviewModifyScoreView.vue'

const reviewStore = 'reviewStore'
const bookStore = 'bookStore'

export default {
  name: 'ReviewMyItemView',
  components: {
    ReviewScoreView,
    ReviewModifyScoreView
  },
  props: ['review', 'page'],
  data: function () {
    return {
      isModify: false,
      updateScore: this.review.score,
      updateContent: this.review.content,
      userInfo: JSON.parse(sessionStorage.getItem('userInfo')),

      // 더보기
      isTruncated: false,
      isExpanded: false,
      reviewMyPageSetting: null
    }
  },
  computed: {
    ...mapState(bookStore, ['book']),
    ...mapGetters(reviewStore, ['getReviewMyPageSetting'])
  },
  methods: {
    ...mapActions(reviewStore, ['modifyReviewAction', 'deleteReviewAction']),
    ...mapActions(bookStore, ['getBookDetail']),
    async checkValue () {
      let err = true
      let msg = ''

      if (!this.updateContent) {
        msg = '리뷰 내용을 입력해주세요'
        err = false
        this.$refs.reviewContent.focus()
      }

      if (this.updateContent.length > 300) {
        msg = '리뷰 입력은 300자 내로 가능합니다. \n' + '현재 입력된 글자는 ' + this.updateContent.length + '자 입니다.'
        err = false
        this.$refs.reviewContent.focus()
      }

      // 에러가 있으면 메세지 찍기
      if (!err) alert(msg)
      // 만약, 내용이 다 입력되어 있다면 리뷰 수정 후 getReviewBookListAction(request)
      else {
        this.getBookDetail(this.review.isbn)
        const payload = {
          reviewId: this.review.id,
          data: {
            content: this.updateContent,
            score: this.updateScore
          }
        }

        // 1. 댓글 수정
        await this.modifyReviewAction(payload)
        await this.$emit('paging', this.page + 1)

        this.book.scoreSum += this.updateScore - this.review.score
        // 2. 수정 반영해서 리스트 가져오기 : emit 완료보다 상태변경이 빨라서 딜레이 설정
        setTimeout(() => {
          this.truncateContent()
          this.isModify = false
          this.isExpanded = false
        }, 400)
      }
    },
    modifyReview () {
      if (!this.isModify) {
        this.isModify = true
        this.isExpanded = true
      }
    },
    cancelModify () {
      this.isModify = false
      this.isExpanded = false
      this.updateScore = this.review.score
      this.updateContent = this.review.content
    },
    modifyScore (newScore) {
      this.updateScore = newScore
    },
    async deleteReview () {
      if (confirm('삭제하시겠습니까?')) {
        await this.deleteReviewAction(this.review.id)
        this.getBookDetail(this.review.isbn)
        this.book.scoreSum -= this.review.score
        this.book.reviewCount -= 1
      }
      this.reviewMyPageSetting = this.getReviewMyPageSetting

      if (this.reviewMyPageSetting.numberOfElements === 1) {
        this.$emit('paging', this.page)
      } else {
        this.$emit('paging', this.page + 1)
      }
    },
    check (index) {
      this.review.score = index
    },

    // 더보기
    truncateContent () {
      if (this.getContentLength() < 100) {
        this.isTruncated = false
        this.isExpanded = false
        return
      }
      this.isTruncated = true
    },
    expandContent () {
      this.isExpanded = true
    },
    shrinkContent () {
      this.isExpanded = false
    },
    getContentLength () {
      return this.review.content.length
    }
  },
  mounted () {
    this.truncateContent()
  }

}
</script>

<style scoped>
.router-font:hover {
  text-decoration: underline;
}
.router-font {
  font-weight: 900;
  color: var(--ae-navy);
}
.limit {
  text-align: right;
  margin-right: 1px;
}
.item-content.is-expanded {
  height: auto !important;
  overflow: visible !important;
}
.item-content {
  padding: 10px 0px;
  text-align: left;
  padding: 6px 12px;
  font-size: 1em;
  width: 97.5%;
  overflow: hidden;
  display: flex;
  word-break: break-all; /* 단어 단위가 아닌 문자 단위로 줄 바꿈 */
}
.more-content {
  border: none;
}
.book-title {
  color: var(--ae-navy);
  font-size: 1.2em;
  margin-top: 4px;
  margin-bottom: 2px;
  text-align: left;
  display: flex;
}
.item-updated-at {
  font-size: 11px;
  color: var(--font-gray);
  text-align: center;
  margin-top: 8px;
  margin-left: 10px;
}
.item-group-1 {
  display: flex;
  align-items: center;
  margin-left: 13px;
  margin-bottom: 5px;
}
.item-info {
  display: flex;
  justify-content: flex-start;
  margin-right: 10px;
}
.item-group-2 {
  margin-left: 5px;
}
.item-modify {
  width: 534px;
  margin-left: 9px;
  margin-top: 3px;
  font-size: 1em;
  resize: none;
  height: auto;
}
.item-score {
  display: flex;
  margin-left: auto;
}
.review-item {
  background-color: white;
  padding : 13px 20px 0px 20px;
  border: 0.5px solid var(--stroke-gray);
  height: 180px;
  width: 700px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
}
.review-group {
  width: 85%;
  margin-right: 10px;
}
.btn-group {
  margin-top: 35px;
  width: 15%;
}

.ae-btn {
  border-width: 1.5px;
  border-style: solid;
  border-radius: 10px;
  padding: 0.3rem 1.8rem;
  font-size: 0.95rem;
  font-weight: bold;
  margin: 3px 0px;
}

.orange-btn {
  color: var(--ae-red);;
  border: none;
  background: none;
  font-weight: bold;
  font-size: 0.9em;
}
</style>
