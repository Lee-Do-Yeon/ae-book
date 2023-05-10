<template>
    <div class="container">
    <div class="left">
      <router-link :to="{ name: 'BookDetail', params: { isbn: book.isbn } }">
        <img v-bind:src="book.coverImageUrl" class="book-image">
      </router-link>
    </div>
    <div class="right">
      <div class="info">
        <router-link :to="{ name: 'BookDetail', params: { isbn: book.isbn } }">
          <span class="title">{{ book.title | removeTitlePrefix }}</span><br>
        </router-link>
        <span>현재 최저가 </span><span class="price">{{ book.price | pricePoint  }}</span><span>원</span><br>
        {{ book.author }} | {{ book.publisher }} | {{ book.publishDate }}
      </div>
      <div class="button">
        <button type="button" class="ae-btn btn-red" @click="onClickRedirect(book.aladinUrl)">구매하러 가기</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BookSearchItem',
  props: {
    book: Object
  },
  methods: {
    onClickRedirect (url) {
      window.open(url, 'blank')
    }
  }
}
</script>

<style scoped>

.container {
  display: flex;
  flex-direction: row;
  margin-top: 40px;
  margin-bottom: 60px;
  justify-content: space-between;
}

.left {
  margin-right: 30px;
}

.right {
  width: 80%;
  display: flex;
  flex-direction: column;
}

.right > .info {
  height: 80%;
  text-align: left;
  padding: 30px;
}

.right > .button {
  height: 20%;
  display: flex;
  justify-content: end;
  align-items: center;
}

.book-image {
  width: 200px;
  box-shadow: 2px 4px 11px 0 rgba(0, 0, 0, 0.25);
}

.title {
  color: var(--ae-navy);
  font-weight: 800;
  font-size: 22px;
  margin-top: 3px;
}

.subinfo {
  color: #888888;
  font-size: 15px;
  font-weight: 100;
}

.price {
  color: var(--ae-red);
  font-weight: 800;
  font-size: 32px;
}

span {
  margin-bottom: 0;
  font-weight: 800;
  font-size: 18px;
}
</style>
