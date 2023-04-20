import Vue from 'vue'
import App from './App.vue'
import axios from 'axios'
<<<<<<< HEAD
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.prototype.$http = axios
Vue.config.productionTip = false
Vue.use(BootstrapVue)    
Vue.use(IconsPlugin)

new Vue({
	render: h => h(App),
	axios
=======

Vue.prototype.$http = axios
Vue.config.productionTip = false

new Vue({
	render: h => h(App),
>>>>>>> branch 'mentoring/devops/S08P31C201-433-2-spring-and-vue-js' of https://lab.ssafy.com/s08-final/S08P31C201.git
}).$mount('#app')
