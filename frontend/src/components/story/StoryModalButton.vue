<template #modal-footer>
  <div class="modal-footer">
    <!-- Emulate built in modal footer ok and cancel button actions -->
    <b-button size="sm" variant="danger" @click="deleteStoryById()">
      삭제
    </b-button>
    <b-button @click="closeModal" size="sm" variant="dark">닫기</b-button>
  </div>
</template>
<script>

import { deleteStory } from '@/api/story'
import { mapGetters } from 'vuex'

const storyStore = 'storyStore'

export default {

  components: {
  },
  data () {
    return {
      audio: Object
    }
  },
  props: {
    voiceUrl: String
  },

  mounted () {
    this.audio = new Audio(this.voiceUrl)
  },

  methods: {
    closeModal () {
      this.audio.pause()
      this.$emit('close')
    },

    deleteStoryById () {
      this.storyId = this.getStoryId
      deleteStory(this.storyId)
        .then(response => {
          if (response.data.resultCode === 200) {
            alert('정상적으로 삭제했습니다.')
            this.closeModal()
          }
        })
        .catch(error => {
          alert('정상적으로 삭제하지 못했습니다. ' + error)
        })
    }

  },

  computed: {
    ...mapGetters(storyStore, ['getStoryId'])
  }
}
</script>

<style>

</style>
