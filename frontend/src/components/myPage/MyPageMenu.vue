<template>
  <div class="menu">
    <div class="user-img-box"><img class="user-img" :src=user.profileUrl></div>
    <div class="nickname"><span style="font-weight:800">{{ user.nickname }}</span> 님</div>
    <ul>
      <li><router-link class="mypage-btn" :to="{ name: 'MyInfoModify' }" replace>사용자 정보 수정</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyNotification' }" replace>알림 설정</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyPainting' }" replace>그림장</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyStoryListView' }" replace>동화책</router-link></li>
      <li><router-link class="mypage-btn" :to="{ name: 'MyReview' }" replace>작성한 서평</router-link></li>
    </ul>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
const userStore = 'userStore'

export default {
  name: 'MyPageMenu',
  data () {
    return {
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
    goTo (componentName) {
      this.$emit('goTo', componentName)
    }
  }
}
</script>

<style scoped>
.menu {
  background-color: var(--main-yellow);
  width: 243px;
  height: 475px;
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
</style>
