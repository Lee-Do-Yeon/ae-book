<template #modal-footer>
  <div class="modal-footer">
    <b-button class="ae-btn" @click="closeNotificationModal">닫기</b-button>
    <b-button class="ae-btn btn-red" @click="saveUserNotification">신청</b-button>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import notificationStore from '../../store/modules/notificationStore'

export default {
  name: 'NotificationCreateModalButton',
  data () {
    return {

    }
  },
  methods: {
    ...mapActions(notificationStore, ['saveNotification']),
    closeNotificationModal () {
      this.$emit('close')
    },
    saveUserNotification () {
      // const payload = {
      //   data: {
      //     isbn: this.isbn,
      //     price: this.price
      //   }
      // }

      // this.saveNotification()
      this.closeNotificationModal()
    }
  }
}
</script>

<style>

</style>
