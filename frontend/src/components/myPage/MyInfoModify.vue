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
        </div>
      </form>

    </div>

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
