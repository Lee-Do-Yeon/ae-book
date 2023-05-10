<template>
  <div class="star-rating">
    <div
      class="star"
      v-for="index in 5"
      :key="index"
    >
      <span v-if="index <= score">🧡</span>
      <span v-if="index > score">🤍</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReviewScoreView',
  props: ['score']
}
</script>

<style scoped>
.star {
  display: inline-block;
  font-size: 1.3em;
}
</style>
