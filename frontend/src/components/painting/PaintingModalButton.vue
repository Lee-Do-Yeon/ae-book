<template #modal-footer>
  <div class="modal-footer">
    <button class="ae-btn btn-red" @click="downloadPainting(painting.id)">다운로드</button>
    <button v-if="painting.type === 'LINE'" class="ae-btn btn-red" @click="coloring">색칠하기</button>
    <button class="ae-btn btn-navy" @click="clickDelete(painting.id)">삭제</button>
    <button class="ae-btn" @click="closeModal">닫기</button>
  </div>
</template>
<script>
import { mapActions, mapState, mapMutations } from 'vuex'
const paintingStore = 'paintingStore'

export default {
  name: 'PaintingModalButton',
  computed: {
    ...mapState(paintingStore, ['painting'])
  },
  methods: {
    ...mapActions('paintingStore', ['downloadPainting', 'deletePainting', 'updatePaintingTitle']),
    ...mapMutations('paintingStore', ['SET_SKETCH']),
    closeModal () {
      this.$emit('close')
    },
    clickDelete (id) {
      this.deletePainting(id)
      this.closeModal()
    },
    coloring () {
      this.SET_SKETCH({url: this.painting, type: 'painting'})
      this.$router.push('/painting/board')
    }
  }
}
</script>

<style>

</style>
