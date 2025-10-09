<template>
  <nav class="fixed bottom-0 left-0 right-0 px-2 pb-4 z-50 max-w-full">
    <div 
      class="flex items-center justify-around px-2 py-3 mx-auto max-w-full"
      :style="{
        height: '56px',
        background: 'rgba(201, 188, 255, 0.2)',
        backdropFilter: 'blur(8px)',
        borderRadius: '16px'
      }"
    >
      <router-link
        v-for="item in navItems"
        :key="item.href"
        :to="item.to"
        class="flex flex-col items-center gap-1 flex-1 min-w-0 max-w-[80px]"
      >
        <div
          :class="[
            'p-1 rounded-2xl transition-all'
          ]"
        >
          <img
            :src="item.icon"
            :alt="item.label"
            class="w-6 h-6"
          />
        </div>
        <span
          :class="[
            'text-[10px] font-semibold uppercase text-center',
            $route.path === item.href ? 'text-[#A08AFF]' : 'text-white opacity-50'
          ]"
          :style="{
            fontFamily: 'PingFang HK',
            fontWeight: 600,
            fontSize: '10px',
            lineHeight: '14px',
            textAlign: 'center',
            textTransform: 'uppercase'
          }"
        >
          {{ item.label }}
        </span>
      </router-link>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUrlParams } from '@/composables/useUrlParams'

const route = useRoute()
const { savedParams } = useUrlParams()

const navItems = computed(() => {
  const items = [
    { 
      href: '/', 
      icon: route.path === '/' ? '/home-active.png' : '/home.png', 
      label: 'HOME' 
    },
    { 
      href: '/transaction', 
      icon: route.path === '/transaction' ? '/Transaction-active.png' : '/Transaction.png', 
      label: 'TRANSACTION' 
    },
    { 
      href: '/share', 
      icon: route.path === '/share' ? '/share-active.png' : '/share.png', 
      label: 'SHARE' 
    },
    { 
      href: '/account', 
      icon: route.path === '/account' ? '/account-active.png' : '/account.png', 
      label: 'ACCOUNT' 
    },
  ]

  // 为每个导航项添加查询参数
  return items.map(item => ({
    ...item,
    to: {
      path: item.href,
      query: savedParams.value
    }
  }))
})
</script>
