<template #modal-footer>
  <div class="modal-footer">
    <!-- Emulate built in modal footer ok and cancel button actions -->
    <b-button size="sm" variant="danger" @click="deleteStoryModal()">
      삭제
    </b-button>
    <b-button @click="closeModal" size="sm" variant="dark">닫기</b-button>
  </div>
</template>
<script>
import { mapGetters, mapActions } from 'vuex'

const storyStore = 'storyStore'

export default {

  components: {
  },
  data () {
    return {
      audio: Object,
      id: 0
    }
  },
  props: {
    voiceUrl: String
  },

  mounted () {
    this.audio = new Audio(this.voiceUrl)
  },

  methods: {
    ...mapActions(storyStore, ['deleteStoryById']),

    closeModal () {
      if (this.audio) {
        this.audio.pause()
        this.audio.currentTime = 0
      }
      this.$emit('close-modal')
    },

    deleteStoryModal () {
      this.id = this.getStoryId
      this.deleteStoryById(this.id)
      this.closeModal()
    }

  },

  computed: {
    ...mapGetters(storyStore, ['getStoryId'])
  }
}
</script>

<style>

</style>
