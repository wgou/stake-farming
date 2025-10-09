<template>
  <div class="fixed bottom-20 right-2 z-50">
    <button
      class="w-14 h-14 rounded-full shadow-lg hover:scale-105 transition-transform relative"
      :style="{
        backgroundImage: 'url(/kf.png)',
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center'
      }"
      @click="goToChat"
    >
      <!-- 未读消息红点 -->
      <div
        v-if="unreadCount > 0"
        class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full border-2 border-white flex items-center justify-center"
        :style="{
          boxShadow: '0 0 8px rgba(239, 68, 68, 0.8)',
          animation: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite'
        }"
      >
        <span class="text-white text-[10px] font-bold leading-none">{{ unreadCount > 9 ? '9+' : unreadCount }}</span>
      </div>
    </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUnreadMessages } from '@/composables/useUnreadMessages'
import { useUrlParams } from '@/composables/useUrlParams'

const router = useRouter()
const { unreadCount } = useUnreadMessages()
const { savedParams } = useUrlParams()

const goToChat = () => {
  router.push({
    path: '/chat',
    query: savedParams.value
  })
}
</script>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}
</style>
