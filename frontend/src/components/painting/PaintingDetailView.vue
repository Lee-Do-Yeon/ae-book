<template>
<div>
  <div v-if="painting !== null">
    <input class="input-box" type="text" v-model="painting.title">
    <button type="button" class="ae-btn btn-red" @click="onClickUpdate">수정</button>
    <br>
    <img class="painting-img" v-bind:src="painting.fileUrl">
  </div>
</div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
const paintingStore = 'paintingStore'

export default {
  name: 'PaintingDetailView',
  computed: {
    ...mapState(paintingStore, ['painting'])
  },
  methods: {
    ...mapActions('paintingStore', ['updatePaintingTitle']),
    onClickUpdate () {
      this.updatePaintingTitle({paintingId: this.painting.id, title: {title: this.painting.title}})
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
.painting-img {
  max-width: 700px;
  margin: 50px 0px;
}

.input-box {
  border-radius: 30px;
  border: 1px solid var(--ae-red);
  height: 35px;
  padding-left: 10px;
}

</style>
