<template>
  <el-submenu v-if="menu.list && menu.list.length >= 1" :index="menu.menuId + ''"
    :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
    <template slot="title">
      <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
      <span>{{ menu.name }}</span>
    </template>
    <sub-menu v-for="item in menu.list" :key="item.menuId" :menu="item" :dynamicMenuRoutes="dynamicMenuRoutes">
    </sub-menu>
  </el-submenu>
  <el-menu-item v-else :index="menu.menuId + ''" @click="gotoRouteHandle(menu)">
    <icon-svg :name="menu.icon || ''" class="site-sidebar__menu-icon"></icon-svg>
    <span>{{ menu.name }}</span>
    <span v-if="menu.name === '客服管理' && unRead > 0" class="unread-badge">{{ unRead }}</span>
  </el-menu-item>
</template>

<script>
import SubMenu from './main-sidebar-sub-menu'
export default {
  name: 'sub-menu',
  props: {
    unRead: {
      type: Number,
      default: 0
    },
    menu: {
      type: Object,
      required: true
    },
    dynamicMenuRoutes: {
      type: Array,
      required: true
    }
  },
  components: {
    SubMenu
  },
  computed: {
    sidebarLayoutSkin: {
      get() { return this.$store.state.common.sidebarLayoutSkin }
    }
  },
  methods: {
    // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
    gotoRouteHandle(menu) {
      var route = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.menuId)
      if (route.length >= 1) {
        if (this.$route.name !== route[0].name) {
          this.$router.push({ name: route[0].name }).catch(err => {})
        }
      }
    }
  }
}
</script>

<style scoped>
.unread-badge {
  color: white;
  background-color: red;
  border-radius: 10px;
  padding: 2px 6px;
  margin-left: 5px;
  font-size: 12px;
}
</style>