import Vue from 'vue'
import App from '@/App'
import router from '@/router'                 // api: https://github.com/vuejs/vue-router
import store from '@/store'                   // api: https://github.com/vuejs/vuex
import VueCookie from 'vue-cookie'            // api: https://github.com/alfhen/vue-cookie
import '@/element-ui'                         // api: https://github.com/ElemeFE/element
import '@/icons'                              // api: http://www.iconfont.cn/
import '@/element-ui-theme'
import '@/assets/scss/index.scss'
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import { isAuth, postJson } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import moment from "moment";
import {formatterColumn} from "@/utils/index";
import { modal,openWallet,openHash} from '@/utils/index'
import  i18n  from '@/language/index.js'
Vue.use(VueCookie)
Vue.config.productionTip = false

// 非生产环境, 适配mockjs模拟数据已移除
// if (process.env.NODE_ENV !== 'production') {
//   require('@/mock')
// }
import appLayout from '@/components/appLayout/index'
import editLayout from '@/components/editLayout/index'
import cardSearch from '@/components/cardSearch/index'
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
import {isMobile} from "@/utils/index";

// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar"
Vue.component('appLayout', appLayout)
Vue.component('editLayout', editLayout)
Vue.component('cardSearch', cardSearch)
Vue.component('FileUpload', FileUpload)
Vue.component('ImageUpload', ImageUpload)
Vue.component('ImagePreview', ImagePreview)
Vue.component('RightToolbar', RightToolbar)
// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.isAuth = isAuth     // 权限方法
Vue.prototype.postJson = postJson     // 权限方法
Vue.prototype.$moment = moment
Vue.prototype.formatterColumn = formatterColumn
Vue.prototype.$modal = modal
Vue.prototype.openWallet = openWallet
Vue.prototype.openHash = openHash
Vue.prototype.isMobile = isMobile()
// 保存整站vuex本地储存初始状态
window.SITE_CONFIG = window.SITE_CONFIG || {};
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
})
