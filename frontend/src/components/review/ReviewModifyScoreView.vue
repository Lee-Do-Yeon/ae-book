<template>
  <div class="star-rating">
    <div
      class="star"
      v-for="index in 5"
      :key="index"
      @click="check(index)"
    >
      <span v-if="index <= starScore">🧡</span>
      <span v-if="index > starScore">🤍</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReviewScoreView',
  props: ['score', 'isModify'],
  data () {
    return {
      starScore: this.score
    }
  },
  methods: {
    check (index) {
      if (this.isModify) {
        this.starScore = index
      }
      this.$emit('modify-score', index)
    }
  }
}
</script>

<style scoped>
.star {
  display: inline-block;
  font-size: 1.3em;
}
</style>
