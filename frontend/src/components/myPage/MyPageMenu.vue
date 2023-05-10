<template>
  <div class="menu">
    <div class="user-img-box"><img class="user-img" :src=user.profileUrl></div>
    <div class="nickname"><span style="font-weight:800">{{ user.nickname }}</span> 님</div>
    <ul>
      <li class="mypage-btn" @click="goTo('MyInfoModify')">사용자 정보 수정</li>
      <li class="mypage-btn" @click="goTo('MyNotification')">알림 설정</li>
      <li class="mypage-btn" @click="goTo('MyPainting')">그림장</li>
      <li class="mypage-btn" @click="goTo('MyStoryListView')">동화책</li>
      <li class="mypage-btn" @click="goTo('MyReview')">작성한 서평</li>
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
    this.user = this.getUserInfo
    console.log(this.user)
  },
  computed: {
    ...mapState(userStore, ['isLogin', 'isLoginError', 'user']),
    ...mapGetters(userStore, ['getUserInfo']),
    user () {
      return JSON.parse(sessionStorage.getItem('userInfo'))
    }
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
  position: relative;
  width: 130px;
  height: 130px;
  overflow: hidden;
  border-radius: 100%;
  border: 5px solid white;
  margin: 0px auto;
  background-color: white;
}

.user-img{
  position: absolute;
  width: 300px;
  transform: translate(-50%, 0%);
}

.nickname{
  margin-top: 2px;
  font-size: 20px;
}
</style>
