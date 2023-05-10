<template>
  <div id="app">
    <div id="wrap">
      <router-view/>
    </div>
    <the-footer></the-footer>
  </div>
</template>

<script>
import TheFooter from '@/components/TheFooter'
export default {
  name: 'App',
  components: {
    TheFooter
  }
}
</script>

<style>
#app {
  position: relative;
  min-height: 100%;
  padding-bottom: 500px;
}

</style>
