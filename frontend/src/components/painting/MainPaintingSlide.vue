<template>
  <div class="rolling-container">
    <div class="rolling-list original" id="roller1">
      <ul>
        <li v-for="painting in items" :key="painting.id">
          <img :src="painting.fileUrl">
        </li>
      </ul>
    </div>
    <div class="rolling-list clone" id="roller2">
      <ul>
        <li v-for="painting in items" :key="painting.id">
          <img :src="painting.fileUrl">
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MainPaintingSilde',
  props: {
    items: {
      type: Array,
      required: true
    }
  },
  mounted () {
    document.querySelector('#roller1').style.left = '0px'
    document.querySelector('#roller2').style.left = document.querySelector('.rolling-list ul').offsetWidth + 'px'
  }
}
</script>

<style scoped>

ul {
  padding: 0px;
}
.rolling-container {
  width: 100%;
  display: flex;
  overflow: hidden;
}
.rolling-container .rolling-list ul {
  display: flex;
}
.rolling-container .rolling-list ul li {
  width: 250px;
  box-sizing: border-box;
  margin-right: 25px;
}
.rolling-list.original {
  animation: rollingleft1 40s linear infinite;
}
.rolling-list.clone {
  animation: rollingleft2 40s linear infinite;
}

@keyframes rollingleft1 {
  0% { transform: translateX(0); }
  50% { transform: translateX(-100%); }
  50.001% { transform: translateX(100%); }
  100% { transform: translateX(0); }
}

@keyframes rollingleft2 {
  0% { transform: translateX(0); }
  100% { transform: translateX(-200%); }
}

img {
  width: 250px;
}

.rolling-container .rolling-list ul li:nth-child(even) {
  margin-top: 70px;
}

</style>
