<template>
    <div>
        <b-navbar toggleable="lg" id="headerNav">
            <b-navbar-brand href="#">
                <router-link to="/">
                  <img src="../assets/images/main_logo.png" width="100" height="60">
                </router-link>
            </b-navbar-brand>

            <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

            <b-collapse id="nav-collapse" is-nav>
            <b-navbar-nav id="navbarNav">
                <b-nav-item>
                    <router-link to="/story">
                      <div class="nav-menu first">
                        <span>내가 만드는 동화</span>
                      </div>
                    </router-link>
                </b-nav-item>
                <b-nav-item>
                    <router-link to="/painting">
                      <div class="nav-menu second">
                        <span>색칠공부</span>
                      </div>
                    </router-link>
                </b-nav-item>
                <b-nav-item>
                    <router-link to="/book/searchbypicture">
                      <div class="nav-menu third">
                        <span>사진으로 책 검색</span>
                      </div>
                    </router-link>
                </b-nav-item>
                <b-nav-item>
                    <router-link to="/book/search">
                      <div class="nav-menu fourth" @click="RESET_BOOK_SEARCH()">
                        <span>제목으로 책 검색</span>
                      </div>
                    </router-link>
                </b-nav-item>
            </b-navbar-nav>
            <!-- 로그인 후 -->
            <b-navbar-nav class="ml-auto" v-if=isLoginUser>
                    <b-nav-form>
                        <router-link :to="{ name: 'MyPage' }" class="link">
                          <b-button size="sm" class="btn-white"><img src="https://img.icons8.com/material-rounded/24/null/person-male.png"/><span class="to-my-page">마이페이지</span></b-button></router-link>
                          <b-button size="sm" @click="logout" class="btn-white"><img width="25" height="25" src="https://img.icons8.com/external-outline-kendis-lasman/64/external-logout-user-interface-outline-kendis-lasman.png"/><span class="to-my-page">&nbsp;로그아웃</span></b-button>
                    </b-nav-form>
            </b-navbar-nav>
            <!-- 로그인 전 -->
            <b-navbar-nav class="ml-auto" v-else>
                <b-nav-form>
                    <router-link :to="{ name: 'Login' }" class="link"><b-button size="sm" class="btn-white"><img width="25" height="25" src="https://img.icons8.com/external-outline-kendis-lasman/25/external-login-user-interface-outline-kendis-lasman.png"/>&nbsp;로그인</b-button></router-link>
                </b-nav-form>
            </b-navbar-nav>
            </b-collapse>
        </b-navbar>
    </div>
</template>

<script>
// TODO: 메인페이지 Navbar 배치 변경.
import { mapState, mapActions, mapMutations } from 'vuex'
const userStore = 'userStore'
const bookStore = 'bookStore'

export default {
  name: 'HeaderNavbar',
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
    ...mapMutations(bookStore, ['RESET_BOOK_SEARCH']),
    logout () {
      if (window.confirm('로그아웃을 하시겠습니까?')) {
        this.userLogout().then(() => {
          window.location.href = this.kakaoLogOutLink
        })
      }
    }
  }
}
</script>

<style scoped>

#headerNav{
  background-color: var(--main-yellow);
  text-decoration: none;
  font-weight: bold;
}

.nav-menu{
  width: 200px;
  height: 50px;
  background-color: red;
  border-radius: 30px 30px 0px 0px;
  border: 5px solid white;
  border-bottom: 0px;
  text-align: center;
  position:relative;
  padding : 10px 0;
}

#navbarNav{
  margin-bottom: -35px;
}

.nav-menu.first {
  background-color: var(--menu-blue);
}

.nav-menu.second {
  background-color: var(--menu-yellow);
}

.nav-menu.third {
  background-color: var(--menu-red);
}

.nav-menu.fourth {
  background-color: var(--menu-green);
}

.to-my-page {
  font-size: small;
}

</style>
