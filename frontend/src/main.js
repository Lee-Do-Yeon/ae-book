// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from '@/store/index.js'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import './assets/app.css'
import { shortText, pricePoint, removeTitlePrefix } from '@/assets/filters'

Vue.use(BootstrapVue)
Vue.config.productionTip = false
Vue.filter('shortText', shortText)
Vue.filter('pricePoint', pricePoint)
Vue.filter('removeTitlePrefix', removeTitlePrefix)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  store,
  router,
  components: { App },
  template: '<App/>'
})
