<template>
  <div class="menu">
    <div class="user-img-box"><img class="user-img" :src=user.profileUrl></div>
    <div class="nickname"><span style="font-weight:800">{{ user.nickname }}</span> 님</div>
    <ul>
      <li><router-link class="mypage-btn" :to="{ name: 'MyInfoModify' }" replace>사용자 정보 수정</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyNotification' }" replace>알림 설정</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyPainting' }" replace>그림장</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyStoryListView' }" replace>동화책</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyReview' }" replace>작성한 리뷰</router-link></li>
      <li><button type="button" class="mypage-btn user-delete" v-b-modal.modal-delete-user>사용자 탈퇴</button></li>
    </ul>

    <b-modal
      id="modal-delete-user"
      ref="modal"
      title="회원 탈퇴"
      @show="resetModal"
      @hidden="resetModal"
      @ok="handleOk"
      ok-title="확인"
      ok-variant="danger"
      cancel-title="취소"
    >
      <form ref="form" @submit.stop.prevent="handleSubmit">
        <b-form-group
          label="회원 탈퇴를 원하신다면 아래의 문구를 입력해주세요."
          label-for="msg-input"
          invalid-feedback="문구를 올바르게 입력해주세요"
          :state="msgState"
        >
          <h6>&lt; 회원 탈퇴를 진행합니다 &gt;</h6><br>
          <b-form-input
            id="msg-input"
            v-model="msg"
            :state="msgState"
            required
          ></b-form-input>
        </b-form-group>
      </form>
    </b-modal>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions } from 'vuex'
const userStore = 'userStore'

export default {
  name: 'MyPageMenu',
  data () {
    return {
      msg: '',
      msgState: null
    }
  },
  created () {
    this.isLoginUser = sessionStorage.getItem('isLoginUser')
    if (this.user.nickname || this.user.profileUrl) {
      this.nickname = this.user.nickname
    } else {
      const userInfo = JSON.parse(sessionStorage.getItem('userInfo'))
      this.user.nickname = userInfo.nickname
      this.user.profileUrl = userInfo.profileUrl
    }
  },
  computed: {
    ...mapState(userStore, ['isLogin', 'isLoginError', 'user']),
    ...mapGetters(userStore, ['getUserInfo'])
  },
  methods: {
    ...mapActions(userStore, ['userUpdate', 'userDelete', 'userLogout']),
    goTo (componentName) {
      this.$emit('goTo', componentName)
    },
    deleteUserInfo () {
      if (confirm('정말로 탈퇴하시겠습니까?')) {
        this.userDelete()
          .then(() => {
            return this.userLogout()
          })
          .then(() => {
            alert('그동안 서비스를 이용해주셔서 감사합니다')
            this.$router.push({ name: 'Main' })
          })
          .catch((error) => {
            console.error(error)
          })
      }
    },
    checkFormValidity () {
      const valid = this.$refs.form.checkValidity()
      this.msgState = valid
      return valid
    },
    resetModal () {
      this.msg = ''
      this.msgState = null
    },
    handleOk (bvModalEvent) {
      bvModalEvent.preventDefault()
      this.handleSubmit()
    },
    handleSubmit () {
      if (!this.checkFormValidity()) {
        return
      }

      if (this.msg === '회원 탈퇴를 진행합니다') {
        this.deleteUserInfo()
        this.$nextTick(() => {
          this.$bvModal.hide('modal-delete-user')
        })
      } else {
        this.msgState = false
        this.$refs.form.reportValidity()
      }
    }
  }
}
</script>

<style scoped>
.menu {
  background-color: var(--main-yellow);
  width: 243px;
  height: 505px;
  border: 5px solid white;
  border-radius: 15px;
  box-shadow: 2px 4px 4px 0 rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.mypage-btn {
  width: 149px;
  height: 36px;
  border-radius: 30px;
  background-color: white;
  margin-top: 10px;
  font-weight: 800;
  color: var(--ae-red);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-img-box {
  width: 130px;
  height: 130px;
  border: 5px solid white;
  margin: 0px auto;
}

.user-img{
  width: 130px;
}

.nickname{
  margin-top: 2px;
  font-size: 20px;
}

.user-delete{
  border : none;
}
</style>
