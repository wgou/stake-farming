<template>
  <nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
    <div class="site-navbar__header" style="border-right: 1px solid #161c25;border-bottom: 1px solid #161c25;">
      <h1 class="site-navbar__brand" @click="$router.push({ name: 'home' })">
        <a class="site-navbar__brand-lg" href="javascript:;">ETH FARMING</a>
        <a class="site-navbar__brand-mini" href="javascript:;">ETH FARMING</a>
      </h1>
    </div>
    <div class="site-navbar__body clearfix">
      <el-menu
        class="site-navbar__menu"
        mode="horizontal">
        <el-menu-item class="site-navbar__switch" index="0" @click="sidebarFold = !sidebarFold">
          <icon-svg name="zhedie"></icon-svg>
        </el-menu-item>
      </el-menu>
      <el-menu
        class="site-navbar__menu site-navbar__menu--right"
        mode="horizontal">
        <el-menu-item index="1">
          <template slot="title">
            <el-badge>
              <span> 当前UTC时间: {{currentTime}} </span>
            </el-badge>
          </template>
        </el-menu-item> 
       
        <el-menu-item class="site-navbar__avatar" index="3">
          
          <el-dropdown :show-timeout="0" placement="bottom">
           
            <span class="el-dropdown-link">
              <img src="~@/assets/img/avatar.png" :alt="userName">{{ userName }}
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="buildGoogleAuth()">Google令牌</el-dropdown-item>
              <el-dropdown-item @click.native="updatePasswordHandle()">修改密码</el-dropdown-item>
              <el-dropdown-item @click.native="logoutHandle()">退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>
      </el-menu>
    </div>
    <!-- 弹窗, 修改密码 -->
    <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
       <!-- 新增 -->
    <google-auth ref="googleAuth"></google-auth>
  </nav>
</template>

<script>
  import UpdatePassword from './main-navbar-update-password'
  import { clearLoginInfo } from '@/utils'
  import GoogleAuth from "./common/google-auth.vue";

  export default {
    data () {
      return {
        updatePassowrdVisible: false,
        settingVisible: false,
        currentTime:''
      }
    },
    components: {
      UpdatePassword,
      GoogleAuth
    },
    
  mounted(){
        // 初始化时显示当前 UTC 时间
        this.updateTime();
        // 每秒更新一次时间
        this.timer = setInterval(this.updateTime, 1000);
    },
    beforeDestroy() {
    // 组件销毁前清除定时器
    clearInterval(this.timer);
  },
    computed: {
      navbarLayoutType: {
        get () { return this.$store.state.common.navbarLayoutType }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold },
        set (val) { this.$store.commit('common/updateSidebarFold', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      },
      userName: {
        get () { return this.$store.state.user.name }
      },
      userId: {
        get () { return this.$store.state.user.id }
      }
    },
    methods: {
      updateTime() {
        // 获取当前 UTC 时间
        const now = new Date();
        // 格式化为 YYYY-MM-DD HH:mm:ss
        const formattedTime = this.formatDate(now);
        this.currentTime = formattedTime;
    },
    formatDate(date) {
      const year = date.getUTCFullYear();
      const month = String(date.getUTCMonth() + 1).padStart(2, '0'); // 月份从0开始
      const day = String(date.getUTCDate()).padStart(2, '0');
      const hours = String(date.getUTCHours()).padStart(2, '0');
      const minutes = String(date.getUTCMinutes()).padStart(2, '0');
      const seconds = String(date.getUTCSeconds()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
      buildGoogleAuth(){
        this.$nextTick(() => {
          this.$refs.googleAuth.bind();
        })
      },
      // 修改密码
      updatePasswordHandle () {
        this.updatePassowrdVisible = true
        this.$nextTick(() => {
          this.$refs.updatePassowrd.init()
        })
      },
      // 退出
      logoutHandle () {
        this.$confirm(`确定进行[退出]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/logout'),
            method: 'post',
            data: this.$http.adornData()
          }).then(({data}) => {
            if (data && data.code === 0) {
              clearLoginInfo()
              this.$router.push({ name: 'login' })
            }
          })
        }).catch(() => {})
      }
    }
  }
</script>
