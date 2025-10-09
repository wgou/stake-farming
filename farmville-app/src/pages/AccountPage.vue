<template>
  <div class="min-h-screen w-full pb-24 overflow-x-hidden" style="background: linear-gradient(191.82deg, #1F1D35 1.41%, #291C8B 52.85%, #25204B 100%), #0B0914;">
    <Header />
    <ChatButton />

    <main class="px-4 overflow-x-hidden max-w-full pt-6">
      <div style="color: #CABEFF; text-align: left; font-family: 'PingFang HK'; font-size: 12px; font-style: normal; font-weight: 600; line-height: normal; text-transform: uppercase; opacity: 0.8;">My wallet</div>
      <div class="mt-[10px] w-full h-[127px] rounded-2xl relative flex items-center bg-cover bg-center" style="background: url('/mask-bg.png');">
        <div class="absolute w-[135px] h-[131px] right-[10px] top-[10px] bg-cover bg-center" style="background-image: url('/card.png');"></div>
        <div class="flex flex-col items-center justify-center ml-[30px]">
          <div class="text-white/80 text-xs font-normal" style="font-family: 'PingFang HK';">Eth</div>
          <div class="text-white text-center text-2xl font-bold" style="font-family: 'DIN Alternate';">{{ walletData.eth }}</div>
          <div class="text-white/80 text-xs font-normal" style="font-family: 'PingFang HK';">Balance</div>
        </div>
        <div class="flex flex-col items-center justify-center ml-[60px]">
          <div class="text-white/80 text-xs font-normal" style="font-family: 'PingFang HK';">USDC</div>
          <div class="text-white text-center text-2xl font-bold" style="font-family: 'DIN Alternate';">{{ walletData.usdc }}</div>
          <div class="text-white/80 text-xs font-normal" style="font-family: 'PingFang HK';">Balance</div>
        </div>
      </div>
      <div class="mt-[10px] text-left text-xs font-semibold uppercase opacity-80" style="color: #CABEFF; font-family: 'PingFang HK';">Assets</div>
      <div class="w-full h-16 rounded-lg bg-[#010506] mt-[5px] flex items-center" v-for="item in assets" :key="item.name">
        <div class="w-11 h-11 rounded-full ml-[9px]">
          <img :src="item.icon" class="w-full">
        </div>
        <div class="ml-[17px] text-white text-xs font-semibold mr-auto" style="font-family: 'PingFang HK';">{{ item.name }}</div>
        <div class="text-right text-[15px] font-semibold mr-[10px]" :style="{ color: item.color, fontFamily: 'Akshar' }">{{ item.value }}</div>
      </div>
      <div class="w-full h-10 mt-[10px] text-center text-xs font-semibold uppercase flex justify-center items-end pb-[5px] bg-cover bg-no-repeat bg-center" style="color: #CABEFF; font-family: 'PingFang HK'; background-image: url('/account-bgs.png');">Farming rewards</div>
      <div class="w-full h-12 rounded-lg bg-[#010506] mt-[5px] flex items-center">
        <div class="ml-[17px] text-white/30 text-[12px] mr-auto flex-shrink-0" style="font-family: 'PingFang HK';">Date(UTC)</div>
        <div class="text-white/30 text-right text-[15px] mr-[10px] flex-shrink-0" style="font-family: Akshar;">REWARD</div>
      </div>
      
      <!-- 空状态显示 -->
      <div v-if="rewards.length === 0" class="w-full h-12 rounded-lg bg-[#010506] mt-[5px] flex items-center justify-center">
        <div class="text-white/50 text-[13px]" style="font-family: 'PingFang HK';">No farming rewards yet</div>
      </div>
      
      <!-- 奖励列表 -->
      <div class="w-full h-12 rounded-lg bg-[#010506] mt-[5px] flex items-center" v-for="(item, index) in rewards" :key="`${item.date}-${index}`">
        <div class="ml-[17px] text-white/70 text-[12px] mr-auto flex-shrink-0" style="font-family: 'PingFang HK';">{{ item.date }}</div>
        <div class="text-white text-right text-[15px] mr-[10px] flex-shrink-0" style="font-family: Akshar;">{{ item.reward }}</div>
      </div>
      
      <!-- Show More 按钮 -->
      <div v-if="hasMore && rewards.length > 0" class="w-full flex items-center justify-center mt-[10px] mb-[10px]">
        <button
          @click="loadMoreRewards"
          :disabled="isLoadingMore"
          class="text-[13px] font-semibold text-blue-400 hover:text-blue-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors py-2 px-4"
          style="font-family: 'PingFang HK';"
        >
          {{ isLoadingMore ? 'Loading...' : 'Show more ▼' }}
        </button>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import ChatButton from '../components/ChatButton.vue'
import { getWalletIndex, getFarmingRewards, type FarmingReward } from '@/lib/api'

// 钱包数据
const walletData = ref({
  usdc: '0',
  eth: '0',
  totalReward: '0',
  exchangeable: '0',
  accountBalance: '0'
})

// 资产列表
const assets = ref([
  { 
    name: "ETH REVENUE", 
    value: "0", 
    icon: "/account-eth.png",
    color: "rgba(133, 238, 173, 0.9)"
  },
  { 
    name: "EXCHANGEABLE", 
    value: "0", 
    icon: "/account-exchange.png",
    color: "#A9CCFF"
  },
  {
    name: "ACCOUNT BALANCE",
    value: "0",
    icon: "/account-balance.png",
    color: "#A9CCFF"
  },
])

// Farming Rewards 列表数据
const rewards = ref<{ date: string; reward: string }[]>([])
const currentPage = ref(1)
const hasMore = ref(true)
const isLoadingMore = ref(false)

// 获取钱包数据
const fetchWalletData = async () => {
  try {
    console.log('📊 获取钱包数据...')
    const response = await getWalletIndex()
    
    if (response.success && response.data) {
      console.log('✅ 钱包数据获取成功:', response.data)
      
      // 更新 walletData
      walletData.value = {
        usdc: response.data.usdc || '0',
        eth: response.data.eth || '0',
        totalReward: response.data.totalReward || '0',
        exchangeable: response.data.exchangeable || '0',
        accountBalance: response.data.accountBalance || '0'
      }
      
      // 更新资产列表
      assets.value[0].value = walletData.value.totalReward  // ETH REVENUE
      assets.value[1].value = walletData.value.exchangeable  // EXCHANGEABLE
      assets.value[2].value = walletData.value.accountBalance  // ACCOUNT BALANCE
      
      console.log('✅ 数据已更新:', {
        usdc: walletData.value.usdc,
        eth: walletData.value.eth,
        totalReward: walletData.value.totalReward,
        exchangeable: walletData.value.exchangeable,
        accountBalance: walletData.value.accountBalance
      })
    } else {
      console.error('❌ 钱包数据获取失败:', response)
    }
  } catch (error) {
    console.error('❌ 获取钱包数据时出错:', error)
  }
}

// 格式化日期 - 显示完整日期时间
const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`
}

// 格式化奖励数字（保留5位小数）
const formatReward = (reward: number): string => {
  // 先截取5位小数（不四舍五入）
  const truncated = Math.floor(reward * 100000) / 100000
  // 转换为字符串并确保有5位小数
  return truncated.toFixed(5)
}

// 获取 Farming Rewards 列表
const fetchFarmingRewards = async (reset = true) => {
  try {
    if (reset) {
      currentPage.value = 1
      rewards.value = []
      console.log('🔄 重新加载 Farming Rewards...')
    } else {
      if (isLoadingMore.value) return
      isLoadingMore.value = true
      console.log(`📄 加载更多 Farming Rewards (第 ${currentPage.value} 页)...`)
    }

    const response = await getFarmingRewards(currentPage.value, 4)
    
    if (response.success && response.data) {
      console.log('✅ Farming Rewards 获取成功:', response.data)
      
      const newRewards = response.data.records.map(item => ({
        date: formatDate(item.created),
        reward: formatReward(item.rewardEth)
      }))
      
      if (reset) {
        rewards.value = newRewards
      } else {
        rewards.value = [...rewards.value, ...newRewards]
      }
      
      // 检查是否还有更多数据
      hasMore.value = currentPage.value < response.data.pages
      
      console.log('✅ Farming Rewards 已更新:', {
        当前页: currentPage.value,
        总页数: response.data.pages,
        当前数据量: rewards.value.length,
        总数据量: response.data.total,
        还有更多: hasMore.value
      })
    } else {
      console.error('❌ Farming Rewards 获取失败:', response)
    }
  } catch (error) {
    console.error('❌ 获取 Farming Rewards 时出错:', error)
  } finally {
    isLoadingMore.value = false
  }
}

// 加载更多
const loadMoreRewards = () => {
  if (hasMore.value && !isLoadingMore.value) {
    currentPage.value++
    fetchFarmingRewards(false)
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchWalletData()
  fetchFarmingRewards()
})
</script>
