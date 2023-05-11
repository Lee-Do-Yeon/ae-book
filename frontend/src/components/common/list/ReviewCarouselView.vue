<template>
<!-- 상단 페이지 넘김 버튼 -->
  <div>
    <div class="my-controls">
      <button @click="prev" class="carousel-btn">&lt;</button>
      <button @click="next" class="carousel-btn right">></button>
    </div>
    <!-- 캐러셀 창 설정 -->
    <b-carousel
      ref="myCarousel"
      id="carousel-1"
      v-model="slide"
      :interval="0"
      background="#FFF4BB"
      img-width="100%"
      img-height="440"
      @sliding-start="onSlideStart"
      @sliding-end="onSlideEnd"
      style="height: 100%;"
    >
    <!-- 여기에 보여줄 리스트 넣으면 됨 -->
    <!-- 캐러셀 한 페이지에 N개의 요소를 보여주려고 chunk,,, chunkeItems()에서 chunkSize 변경 가능 -->
      <template v-for="(itemsChunk, index) in chunkedItems">
        <!-- 캐러셀 배경 필요 ... 현재는 img-blank -->
        <b-carousel-slide :key="index" img-blank img-alt="Blank image" style="width:100%; height:440px">
          <div class="d-flex">
            <!-- key를 요소에 맞춰 설정 -->
            <div v-for="item in itemsChunk" :key="item.id" class="chunk w-34">
              <!-- 리뷰를 나타내는 내용 추가 -->
              <review-carousel-item-view :item="item" />
              <router-link :to="{ name: 'BookDetail', params: { isbn: item.isbn } }">
                <button class="move-book btn-red">
                    책 보러가기 →
                </button>
              </router-link>
            </div>
          </div>
        </b-carousel-slide>
      </template>
    </b-carousel>
    <div class="custom-indicators mt-2">
      <ol>
        <li
          v-for="(slide, index) in slides"
          :key="index"
          :class="{ active: index === activeIndex }"
          @click="slideTo(index)"
        ></li>
      </ol>
    </div>
  </div>
</template>

<script>
import ReviewCarouselItemView from '../../review/ReviewCarouselItemView.vue'
export default {
  components: { ReviewCarouselItemView },
  name: 'ReviewCarousel',
  props: {
    items: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      slide: 0,
      sliding: null,
      activeIndex: 0
    }
  },
  methods: {
    onSlideStart (slide) {
      this.sliding = true
      this.activeIndex = slide
    },
    onSlideEnd (slide) {
      this.sliding = false
      this.activeIndex = slide
    },
    prev () {
      this.$refs.myCarousel.prev()
    },
    next () {
      this.$refs.myCarousel.next()
    },
    slideTo (index) {
      this.activeIndex = index
      this.slide = index % this.chunkedItems.length
      this.$refs.myCarousel.goToSlide(index)
    }
  },
  computed: {
    chunkedItems () {
      // 리뷰 리스트를 3개씩 자르기
      const chunkSize = 3
      return this.items.reduce((resultArray, item, index) => {
        const chunkIndex = Math.floor(index / chunkSize)
        if (!resultArray[chunkIndex]) {
          resultArray[chunkIndex] = [] // start a new chunk
        }
        resultArray[chunkIndex].push(item)
        return resultArray
      }, [])
    },
    slides () {
      return this.chunkedItems.length || 1
    }
  }
}
</script>
<style scoped>
.custom-indicators {
  text-align: center;
}

.custom-indicators ol {
  display: inline-block;
  margin: 0;
  padding: 0;
  list-style: none;
}

.custom-indicators li {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #E5E3DA;
  margin: 0 5px;
  cursor: pointer;
}

.custom-indicators li.active {
  background-color: var(--ae-red);
}

.move-book {
  margin-top: 30px;
  border-radius: 30px;
  color: white;
  padding: 0.5rem 1.5rem 0.4rem 1.5rem;
  font-size: 0.95rem;
}

.d-flex {
  justify-content: center;
}

.my-controls {
  width: 1000px;
  margin: auto;
  display: flex;
  justify-content: right;
  margin-bottom: 20px;
}

.carousel-btn{
  width: 40px;
  height: 40px;
  border-radius: 100%;
  color: var(--ae-navy);
  border: 1px solid #E5E3DA;
  background-color: white;
  margin-right: 5px;
}

.right{
  border: 1px solid var(--ae-red);
  background-color: var(--ae-red);
  color: white;
}
</style>
