<template>
<div>
  <input class="input-box" type="text" v-model="painting.title">
  <button class="ae-btn btn-red" @click="updatePaintingTitle({paintingId: painting.id, title: {title:painting.title}})">수정</button>
  <br>
  <img class="painting-img" v-bind:src="painting.fileUrl">
</div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
const paintingStore = 'paintingStore'

export default {
  name: 'PaintingDetailView',
  data () {
    return {

    }
  },
  computed: {
    ...mapState(paintingStore, ['painting'])
  },
  methods: {
    ...mapActions('paintingStore', ['updatePaintingTitle'])
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
