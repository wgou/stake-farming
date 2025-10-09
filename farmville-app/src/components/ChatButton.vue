<template>
  <div class="fixed bottom-20 right-2 z-50">
    <button
      class="w-14 h-14 rounded-full shadow-lg hover:scale-105 transition-transform"
      :style="{
        backgroundImage: 'url(/kf.png)',
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center'
      }"
      @click="goToChat"
    >
    </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToChat = () => {
  router.push('/chat')
}
</script>
