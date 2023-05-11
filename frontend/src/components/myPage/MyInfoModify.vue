<template>
  <div>
    <h1 class="subject">사용자 정보 수정</h1>
    <div style="height:2px; background-color: #E0E0E0;"></div>
    <div>
      <form id="form-modifyInfo" method="post" action="">
        <div class="form-input">닉네임
          <input
            type="text"
            name="nickname"
            id="nickname"
            v-model="user.nickname"
            placeholder="새로운 닉네임을 입력해주세요"
          />
        </div>

        <div class="form-input">이미지
          <input type="file"
            name="profileUrl"
            id="profileUrl"
            ref="fileInput"
            accept="image/*"
          />
        </div>

        <div>
          <button
            type="submit"
            @click.prevent="modifyUser"
            class="ae-btn btn-red"
          >
            수정
          </button>
          <button type="button" class="ae-btn" v-b-modal.modal-delete-user>사용자 탈퇴</button>
        </div>
      </form>

    </div>

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
import { mapState, mapActions, mapGetters } from 'vuex'
const userStore = 'userStore'

export default {
  name: 'MyInfoModify',
  data () {
    return {
      msg: '',
      msgState: null,
      nickname: '',
      profileUrl: null
    }
  },
  created () {
    this.isLoginUser = sessionStorage.getItem('isLoginUser')
    if (this.user.nickname) {
      this.nickname = this.user.nickname
    } else {
      const userInfo = JSON.parse(sessionStorage.getItem('userInfo'))
      this.user.nickname = userInfo.nickname
      // this.user = userInfo
    }
  },
  computed: {
    ...mapState(userStore, ['isLogin', 'isLoginError', 'user']),
    ...mapGetters(userStore, ['getUserInfo'])
  },
  methods: {
    ...mapActions(userStore, ['userUpdate', 'userDelete', 'userLogout']),
    async modifyUser () {
      if (!this.user.nickname) {
        alert('닉네임은 입력은 필수입니다!')
        return
      }

      let data = {
        nickname: this.user.nickname
      }

      let formData = new FormData()
      if (this.$refs.fileInput.files[0]) {
        formData.append('imgUrl', this.$refs.fileInput.files[0])
      } else {
        formData.append('imgUrl', null)
      }
      formData.append('content', new Blob([JSON.stringify(data)], {type: 'application/json'}))

      await this.userUpdate(formData)
      alert('수정이 완료되었습니다.')
    },
    deleteUserInfo () {
      console.log('사용자 탈퇴')
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
      // Exit when the form isn't valid
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
.subject{
  text-align: left;
  font-size: 24px;
  font-weight: 800;
}

.form-input {
  margin: 30px auto;
  width:80%;
  text-align: left;
  font-size: 15px;
  font-weight: bold;
}

.invalid-feedback {
  text-align: left;
}
</style>
