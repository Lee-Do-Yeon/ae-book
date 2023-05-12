<template>
  <div class="container">
    <div class="search-container">
        <input  @blur="clickOutsideAutoKeyword" v-model="keyword" placeholder="검색어를 입력하세요" @keyup.enter="onClickSearch"  @input="autoComplete" class="search-input">
        <button @click="onClickSearch" class="ae-btn btn-red">검색</button>
        <ul class="autocomplete" v-if="keyword !== ''">
          <li v-for="(keyword, index) in autoCompleteList" :key="index" @mousedown="clickKeyword(keyword)">{{ keyword | removeTitlePrefix }}</li>
        </ul>
    </div>
    <div class="content-container">
      <div class="search-option-container">
        <div class="search-option">
          <div class="search-option-title">검색옵션</div>
          <div style="height:1px; background-color: white;"></div>
          <div><input type="checkbox" v-model="searchTargets" value="TITLE">제목</div>
          <div><input type="checkbox" v-model="searchTargets" value="AUTHOR">지은이</div>
          <div><input type="checkbox" v-model="searchTargets" value="PUBLISHER">출판사</div>
        </div>
      </div>
      <div class="search-result-container">
        <div v-if="searchKeyword === ''" class="no-search">
          검색어를 입력하세요.
        </div>
        <div v-else>
          <div class="search-result">
            '<span style="color:var(--ae-red); font-weight: 700;">{{ searchKeyword }}</span>' 검색 결과 전체 <span style="font-size:30px">{{totalSearchCount | pricePoint}}</span>건
          </div>
          <div class="bar"></div>
          <div v-if="totalSearchCount === 0">
            <div class="no-search">'<span style="color:var(--ae-red)">{{ searchKeyword }}</span>' 검색 결과를 찾을 수 없습니다.</div>
            <div>입력한 검색어의 철자 또는 띄어쓰기가 정확한지 다시 한번 확인해 주세요.<br>검색어의 단어 수를 줄이거나, 보다 일반적인 검색어를 사용하여 검색해 보세요.</div>
          </div>
          <div>
            <book-search-item v-for="book in bookList" :key="book.isbn" :book="book"></book-search-item>
          </div>
          <div class="pagination-container" v-if="totalSearchCount !== 0">
            <pagination :pageSetting="bookPageSetting" @paging="paging"></pagination>
          </div>
      </div>

      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters, mapState, mapMutations } from 'vuex'
import Pagination from '../common/Pagination.vue'
import BookSearchItem from './BookSearchItem.vue'
import axios from 'axios'
const bookStore = 'bookStore'

export default {
  components: { Pagination, BookSearchItem },
  name: 'BookSearchView',
  data () {
    return {
      keyword: '', // 입력중인 키워드
      searchTargets: ['TITLE', 'AUTHOR', 'PUBLISHER'],
      request: null,
      autoCompleteList: []
    }
  },
  computed: {
    ...mapState(bookStore, ['bookList', 'bookPageSetting', 'totalSearchCount', 'searchKeyword']),
    ...mapGetters(bookStore, ['getBookList'])
  },
  methods: {
    ...mapMutations(bookStore, ['RESET_BOOK_SEARCH', 'SET_SEARCH_KEYWORD']),
    ...mapActions(bookStore, ['getSearchList']),
    onClickSearch () {
      // 검색어의 양끝 공백 제거
      this.keyword = this.keyword.trim()

      // 공백 검색이라면 검색 결과 출력 X.
      if (this.keyword === '') {
        this.RESET_BOOK_SEARCH()
        this.SET_SEARCH_KEYWORD('')
        return
      }

      // 검색이 되면 자동 완성 창이 꺼지도록 리셋.
      this.autoCompleteList = []

      // 아무 검색 조건도 쓰지 않는다면 디폴트 설정으로 가도록.
      if (this.searchTargets.length === 0) {
        this.searchTargets = ['TITLE', 'AUTHOR', 'PUBLISHER']
      }

      // 특수문자 인코딩
      const encodedChar = encodeURIComponent(this.keyword)

      this.request = {
        keyword: encodedChar,
        searchTargets: this.searchTargets
      }
      this.SET_SEARCH_KEYWORD(this.keyword)
      this.getSearchList(this.request)
    },
    paging (page) {
      this.request['page'] = page - 1
      this.getSearchList(this.request)
    },
    async autoComplete () {
      if (this.keyword.length < 2) {
        this.autoCompleteList = []
        return
      }
      await axios.get(`/api/books/autocomplete?keyword=${this.keyword}`)
        .then(data => {
          this.autoCompleteList = data.data.result
        })
        .catch(error => {
          console.error(error)
        })
    },
    clickKeyword (keyword) {
      // 특수문자를 검색을 못하기 때문에 앞의 [중고] 말머리를 제외.
      this.keyword = keyword.slice(5)
      this.onClickSearch()
    },
    clickOutsideAutoKeyword () {
      this.autoCompleteList = []
    }
  }
}
</script>

<style scoped>
.container {
  width: 100%;
}

.content-container{
  display: flex;
  flex-direction: row;
  margin-top: 60px;
}

.search-option-container {
  width: 20%;
  margin-right: 30px;
}

.search-result-container {
  width: 80%;
  display: flex;
  flex-direction: column;
  /* background-color: rgb(215, 163, 163); */
}

.book-image {
  width: 160px;
}

.pagination-container {
  display:flex;
  justify-content: center;
}

.search-option{
  width: 80%;
  background-color: var(--main-yellow);
  border-radius: 20px;
  padding: 20px;
  text-align: left;
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
}

.search-option > * {
  margin: 15px 0px;
}

.search-option > div > input {
  margin-right: 5px;
}

.input-group{
  width: 500px;
  margin: auto;
  margin-top: 50px;
  margin-bottom: 50px;
}

.bar {
  height:2px;
  background-color: var(--ae-navy);
}

.search-result{
  font-size: 25px;
  text-align: left;
  font-weight: 700;
}

.no-search{
  font-size: 35px;
  margin: 50px;
}

.auto-list {
  background-color: red;
}

.autocomplete {
  position: absolute;
  z-index: 5;
  left: 0;
  max-height: 200px;
  overflow-y: scroll;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
  padding: 0px;
}

.autocomplete li {
  padding: 10px;
  cursor: pointer;
}

.autocomplete li:hover {
  background-color: #f2f2f2;
}

.search-container{
  position: relative;
  width: 480px;
  margin: auto;
  margin-top: 50px;
}

.search-input{
  width: 400px;
  height: 35px;
  border: #ccc 1px solid;
  border-radius: 1%;
}

.search-option-title {
  margin: 0px;
  font-size: 20px;
  font-weight: 800;
}
</style>
