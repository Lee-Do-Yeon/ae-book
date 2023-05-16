<template>
  <div>
    <div v-if="item !== null">
      <div class="item-container">
        <img v-if="item.fileUrl" v-bind:src="item.fileUrl" class="item-img">
        <img v-if="item.imgUrl" v-bind:src="item.imgUrl" class="item-img">
        <img v-if="item.coverImageUrl" v-bind:src="item.coverImageUrl" class="item-img">
      </div>
      <div class="info-container">
        <p class="title">{{ item.title | removeTitlePrefix | shortText(9, '...') }}</p>
        <p class="author" v-if="item.author">{{ item.author | shortText(16, '...') }}</p>
        <p class="price" v-if="item.price">{{ item.price | pricePoint }}원</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ListItem',
  props: {
    item: Object
  }
}
</script>

<style scoped>
.item-container {
  width: 200px;
  height: 285px;
  background-color: white;
  border: 1px solid #EAE8DF;
  box-shadow: 0 4px 10px 0 rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.item-img {
  width: 145px;
  box-shadow: 2px 4px 11px 0 rgba(0, 0, 0, 0.25);
}

.title {
  color: var(--ae-navy);
  font-weight: 800;
  font-size: 22px;
  margin-top: 3px;
}

.author {
  color: #888888;
  font-size: 14px;
}

.price {
  color: var(--ae-red);
  font-weight: 800;
  font-size: 18px;
}

p {
  margin-bottom: 0;
}
</style>
