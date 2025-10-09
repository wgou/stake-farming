import { ref, onMounted, onUnmounted, watch } from 'vue'
import { getUnreadMessageCount } from '@/lib/api'
import { useWallet } from '@/composables/useWallet'

// 全局状态
const unreadCount = ref(0)
let pollingInterval: ReturnType<typeof setInterval> | null = null
let isPolling = false

// 获取未读消息数量
const fetchUnreadCount = async () => {
  try {
    const response = await getUnreadMessageCount()
    if (response.success && response.data) {
      unreadCount.value = response.data.unRead || 0
      console.log('📬 未读消息数量:', unreadCount.value)
    }
  } catch (error) {
    console.error('获取未读消息数量失败:', error)
    // 静默处理错误，不影响用户体验
  }
}

// 启动轮询
const startPolling = () => {
  if (isPolling) return
  
  console.log('🔄 启动未读消息轮询（每5秒）')
  isPolling = true
  
  // 立即获取一次
  fetchUnreadCount()
  
  // 每5秒轮询一次
  pollingInterval = setInterval(fetchUnreadCount, 5000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingInterval) {
    clearInterval(pollingInterval)
    pollingInterval = null
    isPolling = false
    unreadCount.value = 0
    console.log('⏹️ 停止未读消息轮询')
  }
}

export const useUnreadMessages = () => {
  const { isConnected } = useWallet()
  
  // 监听钱包连接状态
  watch(isConnected, (newVal) => {
    if (newVal) {
      startPolling()
    } else {
      stopPolling()
    }
  }, { immediate: true })
  
  // 组件卸载时清理
  onUnmounted(() => {
    // 不在这里停止轮询，因为这是全局状态
    // 轮询应该在整个应用生命周期内持续
  })
  
  return {
    unreadCount,
    fetchUnreadCount,
    startPolling,
    stopPolling
  }
}

