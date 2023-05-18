<template #modal-footer>
  <div v-if="painting !== null" class="modal-footer">
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
    ...mapState(paintingStore, ['painting', 'paintingPageSetting'])
  },
  props: {
    nowType: {
      type: String
    },
    nowPage: {
      type: Number
    }
  },
  methods: {
    ...mapActions('paintingStore', ['downloadPainting', 'deletePainting', 'updatePaintingTitle']),
    ...mapMutations('paintingStore', ['SET_SKETCH']),
    closeModal () {
      this.$emit('close')
    },
    clickDelete (id) {
      if (this.paintingPageSetting.numberOfElements === 1) {
        this.nowPage = this.nowPage - 1
      }
      let request = {
        paintingId: id,
        type: this.nowType,
        page: this.nowPage
      }
      this.deletePainting(request)
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
