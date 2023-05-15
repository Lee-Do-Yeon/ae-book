<template>
  <div>
    <h1 class="subject">사용자 정보 수정</h1>
    <div style="height:2px; background-color: #E0E0E0;"></div>
    <div>
      <form id="form-modifyInfo" method="post" action="">
        <div class="form-input">
          <div class="input-label">닉네임</div>
          <input
            type="text"
            name="nickname"
            id="nickname"
            v-model="nickname"
            placeholder="새로운 닉네임을 입력해주세요"
          />
        </div>

        <div class="form-input">
          <div class="input-label">이미지</div>
          <label for="file-upload" class="photo">
            <img class="profile-img" :src="profileUrl" v-if="profileUrl"  />
            <img v-else src="https://img.icons8.com/ios/100/camera--v4.png" alt="camera--v4" class="camera"/>
          </label>
          <input id="file-upload" type="file" name="profileUrl" @change="onFileChange"  ref="fileInput" accept=".jpg, .png, .jpeg"/>
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
      profileUrl: ''
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
      if (!this.nickname) {
        alert('닉네임은 입력은 필수입니다!')
        return
      }

      let data = {
        nickname: this.nickname
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
      this.profileUrl = ''
    },
    onFileChange (e) {
      const file = e.target.files[0]
      this.previewImage(file)
    },
    previewImage (file) {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = e => {
        this.profileUrl = e.target.result
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
  /* margin: 30px auto; */
  margin-left: 20px;
  margin-top: 30px;
  margin-bottom: 30px;
  width:80%;
  text-align: left;
  font-size: 15px;
  font-weight: bold;
  display: flex;
}

.invalid-feedback {
  text-align: left;
}

.photo {
  height: 255px;
  width: 40%;
  /* margin: auto; */
  border-radius: 10px;
  border: 1px solid var(--ae-navy);
  display: flex;
  justify-content: center;
  align-items: center;
}

.ae-btn {
  cursor: pointer;
}

#file-upload {
  display: none;
}

.camera {
  width: 100px;
  margin: auto;
}

img {
  max-width: 100%;
  max-height: 100%;
}

.profile-img{
  width:100%;
  border-radius: 10px;
}

.input-label{
  font-weight: 700;
  margin-right: 20px;
  font-size: medium;
}
</style>
