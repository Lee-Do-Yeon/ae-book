d<template>
  <div class='item-container'>
    <div class='item-nickname'>
      {{ item.reviewerNickname }}
    </div>
    <div class='item-content'>
      {{ item.content | shortText(59, '...') }}
    </div>
    <div class='item-score'>
      <review-score-view :score="item.score" />
    </div>
  </div>
</template>

<script>
import ReviewScoreView from './ReviewScoreView.vue'
export default {
  components: { ReviewScoreView },
  name: 'ReviewCarouselItemView',
  props: ['item']

}
</script>

<style scoped>
.item-container {
  border-radius: 30px;
  background-color: white;
  margin: 0px 15px;
  width: 350px;
  height: 300px;
  display: flex;
  flex-direction: column;
  background-image: url("data:image/svg+xml,%3csvg width='100%25' height='100%25' xmlns='http://www.w3.org/2000/svg'%3e%3crect width='100%25' height='100%25' fill='none' rx='30' ry='30' stroke='%23ED553BFF' stroke-width='2' stroke-dasharray='4' stroke-dashoffset='0' stroke-linecap='round'/%3e%3c/svg%3e");
}

.item-nickname {
  color: var(--ae-red);
  font-size: 1.5em;
  font-weight: bold;
  margin-top : 1.5em;
}
.item-content {
  color: var(--ae-navy);
  font-size: 1.1em;
  margin: 1.3em 2.5em;
  word-break: break-all; /* 단어 단위가 아닌 문자 단위로 줄 바꿈 */
}
.item-score {
  margin-bottom: 1.5em;
  margin-top: auto;
}
</style>
