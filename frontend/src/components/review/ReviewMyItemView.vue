<template>
  <div class="review-item" :style="{ height: isExpanded ? 'auto' : '150px' }">
    <div class="review-group">
      <div class="item-group-1">
        <div class="item-info">
          <div>
            <div class="book-title">
            {{ review.title.slice(5, review.title.length)}}
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
        <textarea v-show="isModify"
          id="reviewContent"
          class="item-modify"
          rows="3"
          v-model="updateContent">
        </textarea>
      </div>
      <div class='btn-group'>
        <div v-if="!isModify">
          <button class='ae-btn' @click="modifyReview">수정</button>
          <button class='ae-btn btn-navy' @click="deleteReview">삭제</button>
        </div>
        <div v-if="isModify">
          <button class='ae-btn' @click="checkValue">완료</button>
          <button class='ae-btn btn-navy' @click="cancelModify">취소</button>
        </div>
      </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import ReviewScoreView from './ReviewScoreView.vue'
import ReviewModifyScoreView from './ReviewModifyScoreView.vue'

const reviewStore = 'reviewStore'

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
      isExpanded: false
    }
  },
  methods: {
    ...mapActions(reviewStore, ['modifyReviewAction', 'deleteReviewAction']),
    async checkValue () {
      let err = true
      let msg = ''

      if (!this.updateContent) {
        msg = '내용을 입력해주세요'
        err = false
        this.$refs.reviewContent.focus()
      }

      // 에러가 있으면 메세지 찍기
      if (!err) alert(msg)
      // 만약, 내용이 다 입력되어 있다면 리뷰 수정 후 getReviewBookListAction(request)
      else {
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

        // 2. 수정 반영해서 리스트 가져오기 : emit 완료보다 상태변경이 빨라서 딜레이 설정
        setTimeout(() => {
          this.truncateContent()
          this.isModify = false
        }, 400)
      }
    },
    modifyReview () {
      if (!this.isModify) {
        this.isModify = true
      }
    },
    cancelModify () {
      this.isModify = false
    },
    modifyScore (newScore) {
      this.updateScore = newScore
    },
    async deleteReview () {
      if (confirm('리얼루다가 삭제하시것슴니까 ?!?!?!?!!!?')) {
        await this.deleteReviewAction(this.review.id)
      }

      this.$emit('paging', this.page + 1)
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
}
.more-content {
  border: none;
}
.book-title {
  color: var(--ae-navy);
  font-weight: 900;
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
  width: 506px;
  margin-left: 2px;
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
  border-color: var(--ae-navy);
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