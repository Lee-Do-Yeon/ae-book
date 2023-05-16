<template>
  <div class="modal-overlay">
    <b-modal v-model="visible" size="xl" @hidden="onHidden" hide-footer>
      <slot></slot>
    </b-modal>
  </div>
</template>

<script>
export default {
  name: 'ModalView',
  props: {
    modalShow: {
      type: Boolean,
      required: true
    },
    audio: Object
  },
  components: {
  },
  data () {
    return {

    }
  },
  methods: {
    onHidden () {
      if (this.audio) {
        this.audio.pause()
        this.audio.currentTime = 0
      }
      this.$emit('close-modal')
    }
  },
  watch: {
    modalShow (newValue) {
      this.visible = newValue
    }
  },
  computed: {
    visible: {
      get () {
        return this.modalShow
      },
      set (newValue) {
        this.$emit('update:modalShow', newValue)
      }
    }
  }
}
</script>

<style scoped>
</style>
