<template>
    <div class="navbar">
      <div class="logo-box">
        <router-link to="/">
          <img src="../assets/images/main_logo.png" width="250" height="170">
        </router-link>
      </div>
        <!-- 로그인 후 -->
        <div class="after-login" style="margin-left: auto;" v-if=isLoginUser>
          <router-link :to="{ name: 'MyPage' }" class="link"><b-button size="sm" class="btn-white"><img src="https://img.icons8.com/material-rounded/24/null/person-male.png"/><span class="to-my-page">마이페이지</span></b-button></router-link>
          <b-button size="sm" @click="logout" class="btn-white"><img width="25" height="25" src="https://img.icons8.com/external-outline-kendis-lasman/64/external-logout-user-interface-outline-kendis-lasman.png"/><span class="to-my-page">&nbsp;로그아웃</span></b-button>
        </div>
        <!-- 로그인 전 -->
        <div class="before-login" style="margin-left: auto;" v-else>
          <router-link :to="{ name: 'Login' }" class="link"><b-button size="sm" class="btn-white"><img width="25" height="25" src="https://img.icons8.com/external-outline-kendis-lasman/25/external-login-user-interface-outline-kendis-lasman.png"/>&nbsp;로그인</b-button></router-link>
        </div>
    </div>
</template>

<script>
// TODO: 메인페이지 Navbar 배치 및 디자인 변경
import { mapState, mapActions } from 'vuex'
const userStore = 'userStore'

export default {
  name: 'HeaderNavbarMain',
  data () {
    return {
      kakaoLogOutLink: `https://kauth.kakao.com/oauth/logout?client_id=${process.env.VUE_APP_REST_API_KEY}&logout_redirect_uri=${process.env.VUE_APP_LOGOUT_REDIRECT_URI}`,
      isLoginUser: false
    }
  },
  created () {
    this.isLoginUser = sessionStorage.getItem('isLoginUser')
  },
  computed: {
    ...mapState(userStore, ['isLogin', 'isLoginError', 'user']),
    user () {
      return JSON.parse(sessionStorage.getItem('userInfo'))
    }
  },

  methods: {
    ...mapActions(userStore, ['userLogout']),
    logout () {
      if (window.confirm('로그아웃  하시겠습니까?')) {
        this.userLogout().then(() => {
          window.location.href = this.kakaoLogOutLink
        })
      }
    }
  }
}
</script>

<style scoped>
.navbar{
  height: 150px;
  background-image: url("../assets/images/main_header.png");
  background-size: 1700px 150px;
  position: relative;
  display: flex;
  justify-content: center;
}

.logo-box {
  position: absolute;
  margin: auto;
}

.after-login .before-login {
  position: absolute;
}

.to-my-page {
  font-size: small;
}
</style>
