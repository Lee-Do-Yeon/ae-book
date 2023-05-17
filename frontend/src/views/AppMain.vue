<template>
    <div>
    <header-navbar-main></header-navbar-main>
    <!-- 메인 메뉴 -->
    <div class="menu-container">
      <router-link :to="{ name: 'Story' }">
        <div class="main-menu first">
          <div class="menu-text">내가<br>만드는<br>동화<br><img src="../assets/images/duck.png" id="duckIcon"></div>
        </div>
      </router-link>
      <router-link :to="{ name: 'Painting' }">
        <div class="main-menu second">
          <div class="menu-text">색칠공부<br><br><br><img src="../assets/images/tiger.png" id="paintingIcon"></div>
        </div>
      </router-link>
      <router-link :to="{ name: 'BookSearchByPicture' }">
        <div class="main-menu third">
          <div class="menu-text">사진으로<br>도서 검색<br><br><img src="../assets/images/photoCameraIcon.png" id="photoIcon"></div>
        </div>
      </router-link>
      <router-link :to="{ name: 'BookSearch' }">
        <div class="main-menu fourth" @click="RESET_BOOK_SEARCH()">
          <div class="menu-text">제목으로<br>도서 검색<br><br><img src="../assets/images/bookIcon.png" id="bookIcon"></div>
        </div>
      </router-link>
    </div>
    <!-- /메인 메뉴 -->
    <div class="clear"></div>
    <div class="subject-line"><div class="subject">새로 들어온 책</div><div class="red-bar"></div></div>
    <div class="carousel-container">
      <book-carousel-view :items="bookMainList" :chunkSize="4"></book-carousel-view>
    </div>
    <!-- TODO : 각기 다음에 필요한 컴포넌트는 components/{domain} 하위에 작성 -->
    <div class="subject-line"><div class="subject">최신 리뷰</div><div class="red-bar"></div></div>
     <div class="review-container">
      <div v-show="reviewMainList.length == 0">
        <p class="p-margin p-font">등록된 리뷰가 없습니다</p>
      </div>
      <div v-show="reviewMainList.length > 0">
        <review-carousel-view :items="reviewMainList"></review-carousel-view>
      </div>
     </div>
    <!-- TODO : 우리 아이들 작품(만든 동화) 리스트 컴포넌트 -->
    <div class="subject-line painting-slide"><div class="subject">우리 <span style="color:#FF0000">아</span><span style="color:#3F80FF">이</span><span style="color:#41B82D">들</span> 작품</div><div class="red-bar"></div></div>
    <main-painting-slide :items="mainPaintingList"></main-painting-slide>
  </div>
</template>

<script>
import HeaderNavbarMain from '@/components/HeaderNavbarMain'
import BookCarouselView from '@/components/common/list/BookCarouselView.vue'
import PaintingCarouselView from '@/components/common/list/PaintingCarouselView.vue'
import { mapState, mapActions, mapMutations } from 'vuex'
import ReviewCarouselView from '../components/common/list/ReviewCarouselView.vue'
import MainPaintingSlide from '../components/painting/MainPaintingSlide.vue'

const reviewStore = 'reviewStore'
const bookStore = 'bookStore'
const paintingStore = 'paintingStore'

export default {
  name: 'AppMain',
  components: {
    HeaderNavbarMain,
    ReviewCarouselView,
    BookCarouselView,
    PaintingCarouselView,
    MainPaintingSlide
  },
  methods: {
    ...mapActions(reviewStore, ['getReviewMainListAction']),
    ...mapActions(bookStore, ['getNewBookList']),
    ...mapActions(paintingStore, ['getNewPainting']),
    ...mapMutations(bookStore, ['RESET_BOOK_SEARCH'])
  },
  computed: {
    ...mapState(reviewStore, ['reviewMainList']),
    ...mapState(bookStore, ['bookMainList']),
    ...mapState(paintingStore, ['mainPaintingList'])
  },
  mounted () {
    this.getReviewMainListAction()
    this.getNewBookList()
    this.getNewPainting()
  }
}

</script>

<style scoped>
.p-margin {
  margin-top: 60px;
}
.carousel-container {
  margin: auto;
  height: 500px;
  width: 1000px;
}

.menu-container{
  width: 1000px;
  margin: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
}

.main-menu{
  width: 230px;
  height: 340px;
  background-color: red;
  margin: 7px;
  border-radius: 50px 10px 50px 10px;
  border: 10px solid white;
  position:relative;
  text-align: left;
}

.main-menu.first {
  background-color: var(--menu-blue);
}

.main-menu.second {
  background-color: var(--menu-yellow);
}

.main-menu.third {
  background-color: var(--menu-red);
}

.main-menu.fourth {
  background-color: var(--menu-green);
}

.menu-text{
  position: absolute;
  color: white;
  font-size: 32px;
  font-weight: 800;
  text-shadow: 0px 2px 4px rgba(0, 0, 0, 0.25);
  margin: 30px;
}

.clear{
  clear:both;
}

.subject{
  text-align: left;
  font-size: 24px;
  font-weight: 800;
  margin-right: 15px;
}

.subject-line{
  width: 1000px;
  margin: auto;
  margin-top: 70px;
  display: flex;
  justify-content: left;
  align-items: center;
}

.red-bar {
  height:2px;
  background-color: var(--ae-red);
  width:141px;
}

#duckIcon, #bookIcon, #paintingIcon, #photoIcon{
  height: 140px;
}

.painting-slide{
  margin-bottom:50px;
}

</style>
