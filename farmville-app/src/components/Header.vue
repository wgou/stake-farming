<template>
  <header class="flex items-center justify-between px-4 pt-6 max-w-full">
    <div class="flex items-center gap-2 flex-1 min-w-0">
      <div class="w-9 h-9 rounded-full overflow-hidden flex-shrink-0">
        <img
          src="/head.png"
          alt="Farmville Logo"
          class="w-full h-full object-cover"
        />
      </div>
      <span class="truncate" style="color: #FFF; font-family: 'PingFang SC'; font-size: 15px; font-style: normal; font-weight: 600; line-height: normal; text-transform: uppercase;">FARMVILLE</span>
    </div>
    
    <!-- 连接钱包按钮 -->
    <button 
      v-if="!isConnected"
      @click="handleConnect"
      :disabled="isConnecting"
      class="hover:bg-purple-400/10 transition-colors flex-shrink-0 ml-2 flex items-center justify-center"
      style="width: 79px; height: 25px; border-radius: 2px; border: 1px solid #7454FF; color: #7454FF; font-family: 'PingFang HK'; font-size: 14px; font-style: normal; font-weight: 600; line-height: normal; text-transform: uppercase;"
    >
      <span v-if="!isConnecting">CONNECT</span>
      <span v-else class="text-xs">CONNECT...</span>
    </button>

    <!-- 已连接 - 显示地址 -->
    <button 
      v-else
      @click="copyAddress"
      class="hover:bg-purple-400/10 transition-colors flex-shrink-0 ml-2 flex items-center justify-center gap-1 px-2"
      style="min-width: 79px; height: 25px; border-radius: 2px; border: 1px solid #7454FF; color: #7454FF; font-family: 'PingFang HK'; font-size: 12px; font-style: normal; font-weight: 600; line-height: normal;"
    >
      <span class="w-1.5 h-1.5 rounded-full bg-green-400"></span>
      <span>{{ copied ? 'copied!' : formatAddress(address) }}</span>
    </button>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useWallet } from '@/composables/useWallet'
import { useToast } from '@/composables/useToast'

const { address, isConnecting, isConnected, connectWallet, autoConnect, formatAddress: formatAddr } = useWallet()
const toast = useToast()
const copied = ref(false)

// 格式化地址
const formatAddress = (addr: string) => {
  return formatAddr(addr)
}

// 处理连接
const handleConnect = async () => {
  await connectWallet(
    () => {
      // 成功回调 - 静默处理，不显示toast
      console.log('✅ Wallet connected successfully')
    },
    (error) => {
      // 错误回调 - 显示错误toast
      toast.error(error)
    }
  )
}

// 复制地址
const copyAddress = async () => {
  if (!address.value) return
  
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(address.value)
      copied.value = true
      setTimeout(() => {
        copied.value = false
      }, 2000)
    } else {
      // 回退方案
      const textArea = document.createElement('textarea')
      textArea.value = address.value
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      
      try {
        document.execCommand('copy')
        copied.value = true
        setTimeout(() => {
          copied.value = false
        }, 2000)
      } catch (err) {
        console.error('复制失败:', err)
      } finally {
        document.body.removeChild(textArea)
      }
    }
  } catch (err) {
    console.error('复制到剪贴板失败:', err)
  }
}

// 组件挂载时尝试自动连接
onMounted(() => {
  autoConnect(
    () => {
      // 成功回调 - 静默处理
      console.log('✅ Auto-connected successfully')
    },
    (error) => {
      // 错误回调 - 显示错误toast
      toast.error(error)
    }
  )
})
</script>
