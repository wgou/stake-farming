<template>
  <aside class="site-sidebar" :class="'site-sidebar--' + sidebarLayoutSkin">
    <div class="site-sidebar__inner">
      <el-menu :default-active="menuActiveName || 'home'" :collapse="sidebarFold" :collapseTransition="false"
        class="site-sidebar__menu">
        <!-- <el-menu-item index="home" @click="$router.push({ name: 'home' })">
        </el-menu-item> -->

        <sub-menu v-for="menu in menuList" :key="menu.menuId" :menu="menu" :dynamicMenuRoutes="dynamicMenuRoutes"
          :unRead="unRead">
        </sub-menu>
        <!-- 如果菜单是: 客服管理  并且 this.unRead > 0 则在菜单后面显示 this.unRead,并标记为红色 -->
      </el-menu>
        <audio ref="music" :src="require('@/assets/y1478.wav')" type="audio/mpeg" hidden></audio>

    </div>
  </aside>
</template>

<script>
import SubMenu from './main-sidebar-sub-menu'
import { isURL } from '@/utils/validate'

export default {
  data() {
    return {
      dynamicMenuRoutes: [],
      unReadTimer: null,
      unRead: 0,
      noUnReadCounter:0
    }
  },
  components: {
    SubMenu
  },
  computed: {
    sidebarLayoutSkin: {
      get() { return this.$store.state.common.sidebarLayoutSkin }
    },
    sidebarFold: {
      get() { return this.$store.state.common.sidebarFold }
    },
    menuList: {
      get() { return this.$store.state.common.menuList },
      set(val) { this.$store.commit('common/updateMenuList', val) }
    },
    menuActiveName: {
      get() { return this.$store.state.common.menuActiveName },
      set(val) { this.$store.commit('common/updateMenuActiveName', val) }
    },
    mainTabs: {
      get() { return this.$store.state.common.mainTabs },
      set(val) { this.$store.commit('common/updateMainTabs', val) }
    },
    mainTabsActiveName: {
      get() { return this.$store.state.common.mainTabsActiveName },
      set(val) { this.$store.commit('common/updateMainTabsActiveName', val) }
    }
  },
  watch: {
    $route: 'routeHandle'
  },
  created() {
    this.menuList = JSON.parse(sessionStorage.getItem('menuList') || '[]')
    this.dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
    this.routeHandle(this.$route)
  },


  mounted() {
    this.unReadTimer = setInterval(() => {
      this.getUnRead();
    }, 5000); // 每5秒刷新一次
  },

  beforeDestroy() {
    if (this.unReadTimer) {
      clearInterval(this.unReadTimer); // 组件销毁前清除定时器
      this.stopMusic()
      this.unReadTimer = null;
    }
  },

  methods: {

    getUnRead() {
      this.$http({
        url: this.$http.adornUrl("/admin/message/unRead"),
        method: "post",
        data: {}
      }).then(({ data }) => {
        if (data.success) {
         
          if(data.data > 0){
            this.noUnReadCounter ++;
          }
          console.log("this.unRead:" + this.unRead  + " data:" + data.data + " noUnReadCounter:" + this.noUnReadCounter)
          if (data.data > 0 && (this.noUnReadCounter >= 3 || this.unRead != data.data)) {
            this.playMusic(); // 播放音乐
            this.noUnReadCounter = 0;
          }
          
          this.unRead = data.data;

        } else {
          this.unRead = 0;
        }
      })
    },
    // 点击音乐图标
    playMusic() {
      this.stopMusic()
      this.$refs.music.play() // 播放音乐
    },
    stopMusic() {
      this.$refs.music.pause()
      this.$refs.music.load()
    },
    // 路由操作
    routeHandle(route) {
      if (route.meta.isTab) {
        // tab选中, 不存在先添加
        var tab = this.mainTabs.filter(item => item.name === route.name)[0]
        if (!tab) {
          if (route.meta.isDynamic) {
            route = this.dynamicMenuRoutes.filter(item => item.name === route.name)[0]
            if (!route) {
              return console.error('未能找到可用标签页!')
            }
          }
          tab = {
            menuId: route.meta.menuId || route.name,
            name: route.name,
            title: route.meta.title,
            type: isURL(route.meta.iframeUrl) ? 'iframe' : 'module',
            iframeUrl: route.meta.iframeUrl || ''
          }
          this.mainTabs = this.mainTabs.concat(tab)
        }
        this.menuActiveName = tab.menuId + ''
        this.mainTabsActiveName = tab.name
      }
    }
  }
}
</script>
