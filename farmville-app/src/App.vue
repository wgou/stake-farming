<template>
  <div id="app">
    <router-view />
    <Toast />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { checkReferralCode } from '@/lib/api'
import { useToast } from '@/composables/useToast'
import { useUrlParams } from '@/composables/useUrlParams'
import Toast from '@/components/Toast.vue'

const toast = useToast()
const { initializeParams } = useUrlParams()

// 在应用启动时检查 URL 中的 code 参数
onMounted(async () => {
  try {
    // 初始化并保存 URL 参数（用于后续页面导航）
    initializeParams()
    
    // 获取 URL 中的查询参数
    const urlParams = new URLSearchParams(window.location.search)
    const code = urlParams.get('code')
    
    // 如果存在 code 参数，调用接口检查
    if (code) {
      console.log('检测到邀请码，准备调用 checkReferralCode:', code)
      
      try {
        const response = await checkReferralCode(code)
        
        if (response.success) {
          console.log('✅ 邀请码验证成功')
          // 保存有效的邀请码到 localStorage
          localStorage.setItem('referralCode', code)
        } else {
          console.warn('❌ 邀请码验证失败:', response.msg)
          toast.error(response.msg || 'Invalid referral code')
        }
      } catch (error) {
        console.error('❌ 检查邀请码时出错:', error)
        toast.error('Failed to verify referral code')
      }
    } else {
      console.log('URL 中没有 code 参数')
      toast.warning('Invalid URL: Missing referral code parameter')
    }
  } catch (error) {
    console.error('❌ URL 参数解析出错:', error)
  }
})
</script>

<style>
#app {
  font-family: 'PingFang HK', 'PingFang SC', system-ui, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow-x: hidden;
  max-width: 100vw;
  width: 100%;
  min-height: 100vh;
}
</style>
