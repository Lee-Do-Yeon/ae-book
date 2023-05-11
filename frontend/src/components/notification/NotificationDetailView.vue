<template>
  <div>
    <div>&nbsp;</div>
    <div class="detail" v-if="notification">
      <div class="detail-img">
        <img class="book-cover" v-bind:src="notification.coverImageUrl">
      </div>

      <!-- 수정할 화면 -->
      <div class="detail-content" v-if="modify">
        <p class="detail-content-title">{{ notification.title }}</p>
        <div v-if="notification.notificationType === 'S'">
          <p>알림타입</p>
          <b-form-group>
            <b-form-group>
              <b-form-radio v-model="selected" name="some-radios" value="D">기본 최저가(현재 최저가 : {{ book.price }}원)</b-form-radio>
            </b-form-group>
            <b-form-group>
              <b-form-radio v-model="selected" name="some-radios" value="S" checked>사용자 지정 최저가</b-form-radio>
              <div class="price" v-if="selected === 'S'">
                지정가격 : <input type="text" v-model="upperLimit">
              </div>
            </b-form-group>
          </b-form-group>
        </div>
        <div v-else >
          <p>알림타입</p>
          <b-form-group>
            <b-form-group>
              <b-form-radio v-model="selected" name="some-radios" value="D">기본 최저가(현재 최저가 : {{ book.price }}원)</b-form-radio>
            </b-form-group>
            <b-form-group>
              <b-form-radio v-model="selected" name="some-radios" value="S" checked>사용자 지정 최저가</b-form-radio>
              <div class="price" v-if="selected === 'S'">
                지정가격 : <input type="text" v-model="upperLimit">
              </div>
            </b-form-group>
          </b-form-group>
        </div>
        <p>알림 신청일시 : {{ formatDate(notification.createdAt) }}</p>
        <p>알림 수정일시 : {{ formatDate(notification.updatedAt) }}</p>
        <button class="ae-btn btn-modifiy" @click="cancelModify">취소</button> &nbsp;
        <button class="ae-btn btn-navy btn-modifiy" @click="submitNotification(upperLimit)">수정</button> &nbsp;
      </div>

      <!-- 일반적으로 보여지는 화면 -->
      <div class="detail-content" v-else>
        <p class="detail-content-title">{{ notification.title }}</p>
        <div v-if="notification.notificationType === 'S'">
          <p>알림타입 : 사용자 지정 최저가</p>
          <p>지정가격 : {{ notification.upperLimit }}원</p>
        </div>
        <p v-else>알림타입 : 기본 최저가(신청 최저가 : {{ notification.price }}원)</p>
        <p>알림 신청일시 : {{ formatDate(notification.createdAt) }}</p>
        <p>알림 수정일시 : {{ formatDate(notification.updatedAt) }}</p>
        <button class="ae-btn btn-navy btn-modifiy" @click="modifyNotification">수정</button>
      </div>
    </div>
    <div>&nbsp;</div>
  </div>
  </template>

<script>
import { mapState, mapActions } from 'vuex'
const notificationStore = 'notificationStore'
const bookStore = 'bookStore'

export default {
  name: 'NotificationDetailView',
  props: ['notificationId', 'page', 'isbn'],
  data () {
    return {
      modify: false,
      upperLimit: 0,
      selected: ''
    }
  },
  created () {
    this.getBookDetail(this.isbn)
  },
  mounted () {
    this.getBookNotificationDetail(this.notificationId)
  },
  computed: {
    ...mapState(bookStore, ['book']),
    ...mapState(notificationStore, ['notification'])
  },
  methods: {
    ...mapActions(bookStore, ['getBookDetail']),
    ...mapActions(notificationStore, ['notificationUpdate', 'getBookNotificationDetail']),
    formatDate (dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear().toString().substr(-2)
      const month = ('0' + (date.getMonth() + 1)).slice(-2)
      const day = ('0' + date.getDate()).slice(-2)
      const hour = ('0' + date.getHours()).slice(-2)
      const minute = ('0' + date.getMinutes()).slice(-2)
      return `${year}.${month}.${day} ${hour}:${minute}`
    },
    modifyNotification () {
      this.upperLimit = this.notification.upperLimit
      this.modify = true
    },
    cancelModify () {
      this.upperLimit = this.notification.upperLimit
      this.selected = ''
      this.modify = false
    },
    submitNotification (price) {
      if (this.selected === '') {
        alert('알림 신청 타입을 선택해주세요!')
        return
      }

      if (price > this.book.price) {
        alert('도서의 최저가보다 적은 가격만 알림 신청이 가능합니다! 가격을 변경해주세요!')
        return
      }

      if (!isNaN(price)) {
        if (this.selected === 'D') {
          price = 0
        }

        const payload = {
          notificationId: this.notification.id,
          content: {
            upperLimit: price,
            notificationType: this.selected
          }
        }
        this.notificationUpdate(payload)
          .then(() => {
            // alert('알림이 성공적으로 수정되었습니다.')
            this.getBookNotificationDetail(this.notificationId).then(() => {
              this.selected = ''
              this.modify = false
            })
          })
      } else {
        alert('숫자만 입력할 수 있습니다.')
      }
    }
  }
}
</script>

<style>
.detail {
  display:flex;
}
.detail-img {
  flex-basis: 40%;
  margin-left: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-content {
  flex-basis: 60%;
  text-align: left;
  margin-right: 30px;
}
.book-cover {
  box-shadow: 2px 4px 11px 0 rgba(0, 0, 0, 0.25);
}
.detail-content-title {
  font-weight: 800;
  font-size: 22px;
}
.btn-modifiy{
  float: right;
  font-size: 0.8em;
  padding: 0.4em 0.6em;
}
.price{
  margin-top: 0.8em;
  margin-bottom: 0;
  margin-left: 1.5em;
}
</style>
