import Vue from 'vue'
import router from '@/router'
import store from '@/store'
import moment from "moment";
/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16 | 0) : ('r&0x3' | '0x8')).toString(16)
  })
}

/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth (key) {
  return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}
export function postJson (url, data, callback) {
  this.$http({
    url: this.$http.adornUrl(url),
    method: `post`,
    data: this.$http.adornParams(data)
  }).then(({ data }) => {
    callback(data)
  })
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId') {
  var res = []
  var temp = {}
  for (var i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (var k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]]['children']) {
        temp[data[k][pid]]['children'] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      temp[data[k][pid]]['children'].push(data[k])
    } else {
      res.push(data[k])
    }
  }
  return res
}

/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  Vue.cookie.delete('token');
  store.commit('resetStore');
  router.options.isAddDynamicMenuRoutes = false
}
export const timeList={
  1: [moment().format("YYYY-MM-DD")+" 00:00:00", moment().format("YYYY-MM-DD")+" 23:59:59"],
  2:[moment().subtract(1, "days").format("YYYY-MM-DD")+" 00:00:00",moment().subtract(1, "days").format("YYYY-MM-DD")+" 23:59:59"],
  3:[moment().subtract(7, "days").format("YYYY-MM-DD")+" 00:00:00",moment().format("YYYY-MM-DD")+" 23:59:59"],
  4:[moment().subtract(30, "days").format("YYYY-MM-DD")+" 00:00:00",moment().format("YYYY-MM-DD")+"2 3:59:59"],
  5:[moment().startOf('month').format('YYYY-MM-DD')+"00:00:00", moment().endOf('month').format('YYYY-MM-DD')+"23:59:59"],
}

//替换table空数据
export function formatterColumn( row, column, cellValue, index ) {
  if(cellValue!==''&&cellValue!==undefined &&cellValue!==null){
    return cellValue
  }else{
    return '-'
  }
}

/**
 * loading
 */
 export const modal={
  loadFun:null,
  loading(text){
    this.loadFun= Loading.service({
      lock: true,
      text: text,
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    });
  },
  closeLoading(){
    this.loadFun.close();
  }
}
export function objecttoArr(obj){
  return Object.entries(obj).map(([key, value]) => ({ label: value, value:key  }))
}

export function openWallet(wallet){
  window.open("https://etherscan.io/address/" + wallet)
}
export function openHash(hash){
  window.open("https://etherscan.io/tx/" + hash)
}
 
export function isMobile() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
}