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
      id: 0,
      storyPageSetting: null
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

      this.storyPageSetting = this.getStoryPageSetting

      if (this.storyPageSetting.numberOfElements === 1) {
        this.deleteStoryById({id: this.id, flag: true})
      } else {
        this.deleteStoryById({id: this.id, flag: false})
      }
      this.closeModal()
    }

  },

  computed: {
    ...mapGetters(storyStore, ['getStoryId', 'getStoryPageSetting'])
  }
}
</script>

<style>

</style>
