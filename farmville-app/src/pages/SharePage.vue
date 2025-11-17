<template>
  <div class="min-h-screen w-full text-white relative overflow-x-hidden" style="background-color: #2D1B69;">
    <!-- Background Effects -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-0 left-1/4 -translate-x-1/2 w-64 sm:w-96 h-64 sm:h-96 bg-purple-600/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-1/4 right-1/4 translate-x-1/2 w-64 sm:w-96 h-64 sm:h-96 bg-blue-600/20 rounded-full blur-3xl"></div>
    </div>

    <Header />
    <ChatButton />

    <div class="relative z-10 flex flex-col min-h-screen pb-24 overflow-x-hidden max-w-full">
      <div class="flex-1 px-4 py-16 max-w-md mx-auto w-full">
        <!-- Main Title -->
        <h1 class="text-center leading-[30px] text-[#FFFFFF]" style="font-family: 'Tilt Warp'; font-style: normal; font-weight: 400; font-size: 24px; text-transform: capitalize;">
          Earn While You Share
        </h1>

        <!-- Description -->
        <p class="text-center leading-[20px] text-[#FFFFFF]" style="font-family: 'PingFang HK'; font-style: normal; font-weight: 600; font-size: 14px; text-transform: capitalize; opacity: 0.9; margin-top: 8px;">
          Share Your Referral Link With Friends And Earn 30% Of Their Rewards
        </p>

        <!-- Reward Rate Display -->
        <div class="text-center" style="margin-top: 23px;">
          <span class="leading-[30px]" style="font-family: 'Tilt Warp'; font-size: 24px; font-style: normal; font-weight: bold; line-height: normal; text-transform: capitalize; background: linear-gradient(128deg, #B660D5 15.67%, #FD9379 85.42%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;">
            30%
          </span>
          <span class="leading-[20px]" style="font-family: 'PingFang HK'; font-style: normal; font-weight: 600; font-size: 14px; text-transform: capitalize; opacity: 0.9; color: #7454FF !important;">
            Reward Rate
          </span>
        </div>

        <!-- Invitation Link Section -->
        <div style="margin-top: 38px;">
          <label class="leading-[14px] text-[#FFFFFF]" style="font-family: 'PingFang HK'; font-style: normal; font-weight: 600; font-size: 10px; text-transform: uppercase; opacity: 0.6;">
            Your Invitation Link
          </label>
          <div
            class="w-full h-[44px] bg-[#f6f6f6] rounded-[4px] relative z-[1] mt-[2px] mx-auto"
          >
            <button
              class="w-[75px] h-[28px] absolute top-[8px] right-[8px] z-[4] cursor-pointer hover:opacity-90 transition-all flex items-center justify-center rounded-[4px] border-2 border-[#7354ff] bg-white"
              @click="handleCopy"
            >
              <span
                class="font-['PingFang_HK'] text-[13px] font-semibold leading-[16px] text-[#7354ff] uppercase whitespace-nowrap"
              >
                {{ copied ? "COPIED" : "COPY" }}
              </span>
            </button>
            <input
              v-model="shareLink"
              :title="shareLink"
              class="h-[21px] font-['PingFang_HK'] text-[11px] font-medium leading-[21px] text-[#000] absolute top-[11px] left-[10px] text-left z-[3] bg-transparent outline-none"
              style="width: calc(100% - 93px); overflow-x: auto; overflow-y: hidden; white-space: nowrap;"
              type="text"
              readonly
            />
          </div>
        </div>

        <!-- Steps Section -->
        <div class="space-y-8 pt-6">
          <!-- Step 01 -->
          <div class="flex items-start gap-4">
            <div class="flex flex-col items-center gap-2 min-w-[90px]">
              <div class="w-6 h-6 flex items-center justify-center">
                <img src="/step01-icon.svg" alt="Step 01" class="w-6 h-6" />
              </div>
              <span class="text-sm font-bold bg-gradient-to-r from-cyan-400 to-blue-400 bg-clip-text text-transparent step-01-title italic" style="background: linear-gradient(to right, #22d3ee, #3b82f6) !important; -webkit-background-clip: text !important; background-clip: text !important; color: transparent !important;">
                STEP 01
              </span>
            </div>
            <div class="flex-1 pt-3">
              <p class="leading-[17px] text-[#FFFFFF]" style="font-family: 'PingFang HK'; font-style: normal; font-weight: 600; font-size: 12px; text-transform: capitalize;">Share Your Unique Referral Link With Friends</p>
            </div>
          </div>

          <!-- Step 02 -->
          <div class="flex items-start gap-4">
            <div class="flex flex-col items-center gap-2 min-w-[90px]">
              <div class="w-6 h-6 flex items-center justify-center">
                <img src="/step02-icon.svg" alt="Step 02" class="w-6 h-6" />
              </div>
              <span class="text-sm font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent step-02-title italic">
                STEP 02
              </span>
            </div>
            <div class="flex-1 pt-3">
              <p class="leading-[17px] text-[#FFFFFF]" style="font-family: 'PingFang HK'; font-style: normal; font-weight: 600; font-size: 12px; text-transform: capitalize;">Friends Join Using Your Link And Start Farming</p>
            </div>
          </div>

          <!-- Step 03 -->
          <div class="flex items-start gap-4">
            <div class="flex flex-col items-center gap-2 min-w-[90px]">
              <div class="w-6 h-6 flex items-center justify-center">
                <img src="/step03-icon.svg" alt="Step 03" class="w-6 h-6" />
              </div>
              <span class="text-sm font-bold bg-gradient-to-r from-yellow-400 to-orange-400 bg-clip-text text-transparent step-03-title italic" style="background: linear-gradient(to right, #facc15, #fb923c) !important; -webkit-background-clip: text !important; background-clip: text !important; color: transparent !important;">
                STEP 03
              </span>
            </div>
            <div class="flex-1 pt-3">
              <p class="leading-[17px] text-[#FFFFFF]" style="font-family: 'PingFang HK'; font-style: normal; font-weight: 600; font-size: 12px; text-transform: capitalize;">Earn 30% Of Their Rewards Automatically!</p>
            </div>
          </div>
        </div>

        <!-- Referral Rewards Table -->
        <div class="w-full max-w-[347.5px] relative z-[1] mx-auto mt-8 px-2">
          <div class="w-full max-w-[335px] bg-[rgba(24,25,46,0.8)] rounded-[8px] shadow-[0_0_4px_0_rgba(243,226,255,0.2)_inset] z-[4] mx-auto pb-2">
            <div class="w-full h-[40px] relative z-[1] mt-0 mr-0 mb-0 ml-0">
              <span class="flex w-[93px] h-[17px] justify-center items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-80 leading-[42px] text-[#c9beff] relative text-center uppercase whitespace-nowrap z-[1] leading-[42px] mx-auto">
                Referral Rewards
              </span>
              <div class="w-full h-[40px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/V4zJwFHfwX.png)] bg-cover bg-no-repeat rounded-[8px] absolute top-0 left-1/2 -translate-x-1/2 z-30"></div>
            </div>
            <div class="w-full h-px bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/njnh4AJL0G.png)] bg-cover bg-no-repeat absolute top-[39.5px] left-0 z-[2]"></div>
            <div class="w-full max-w-[315px] h-[40px] relative z-[28] mt-[6px] mx-auto px-[10px]">
              <span class="flex h-[17px] justify-start items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] left-[10px] text-left uppercase whitespace-nowrap z-[26]">
                Date(UTC)
              </span>
              <span class="flex h-[17px] justify-end items-start font-['PingFang_HK'] text-[12px] font-semibold leading-[16.8px] absolute top-[12px] text-right uppercase whitespace-nowrap z-[27]" style="right: 10px; color: rgba(255, 255, 255, 0.3);">
                ETH Remark
              </span>
            </div>
            
            <!-- 可滚动内容区域 -->
            <div class="w-full max-h-[240px] overflow-y-auto">
              <!-- 空状态 -->
              <div v-if="rewardData.length === 0" class="w-full max-w-[315px] h-[48px] relative z-[4] mt-[5px] mx-auto px-[10px] flex items-center justify-center">
                <span class="text-[12px] text-white opacity-50">No referral rewards yet</span>
              </div>
              <!-- 奖励列表 -->
              <div v-for="(item, index) in rewardData" :key="item.id" class="w-full max-w-[315px] h-[48px] relative z-[4] mt-[5px] mx-auto px-[10px]">
                <div class="w-full h-[48px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-10"></div>
                <span class="flex h-[21px] justify-start items-start font-['Akshar'] text-[15px] font-normal opacity-80 leading-[20.7px] text-[#fff] absolute top-[14px] left-[10px] text-left uppercase whitespace-nowrap z-[2]">
                  {{ formatDate(item.created) }}
                </span>
                <span class="flex w-[50px] h-[21px] justify-end items-start font-['Akshar'] text-[15px] font-semibold opacity-80 leading-[20.7px] text-[#fff] absolute top-[15px] text-right uppercase whitespace-nowrap z-[3]" style="right: 10px;">
                  {{ formatReward(item.rewardEth) }}
                </span>
              </div>
              
              <!-- Show More 按钮 -->
              <div
                v-if="rewardHasMore"
                class="w-full flex items-center justify-center relative z-[4] mt-[6px] mb-[8px] mx-auto"
              >
                <button
                  @click="loadMoreRewards"
                  :disabled="isLoadingMoreReward"
                  class="text-[13px] font-semibold text-blue-400 hover:text-blue-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors py-2"
                >
                  {{ isLoadingMoreReward ? 'Loading...' : 'Show more ▼' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <BottomNav />
    <Toast />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Header from '../components/Header.vue'
import ChatButton from '../components/ChatButton.vue'
import BottomNav from '../components/BottomNav.vue'
import Toast from '../components/Toast.vue'
import { getShareLink, getReferralRewards } from '@/lib/api'
import type { FarmingReward } from '@/lib/api'
import { useToast } from '@/composables/useToast'

const toast = useToast()
const copied = ref(false)
const shareLink = ref('')
const rewardData = ref<FarmingReward[]>([])
const rewardCurrentPage = ref(1)
const rewardHasMore = ref(false)
const isLoadingMoreReward = ref(false)

// 获取分享链接
const fetchShareLink = async () => {
  try {
    console.log('🔄 获取分享链接...')
    const response = await getShareLink()
    if (response.success && response.data) {
      shareLink.value = response.data
      console.log('✅ 分享链接获取成功:', response.data)
    } else {
      console.log('⚠️ 分享链接为空')
    }
  } catch (error) {
    console.error('❌ 获取分享链接失败:', error)
  }
}

// 获取推荐奖励列表
const fetchReferralRewards = async (isFirstPage = true) => {
  if (isLoadingMoreReward.value) return
  
  try {
    isLoadingMoreReward.value = true
    const pageToFetch = isFirstPage ? 1 : rewardCurrentPage.value + 1
    console.log('🔄 获取推荐奖励列表，页码:', pageToFetch)
    
    const response = await getReferralRewards(pageToFetch, 4) // 每页4条记录
    
    if (response.success && response.data && response.data.records) {
      if (isFirstPage) {
        // 第一页，替换数据
        rewardData.value = response.data.records
        rewardCurrentPage.value = 1
      } else {
        // 加载更多，追加数据
        rewardData.value = [...rewardData.value, ...response.data.records]
        rewardCurrentPage.value = pageToFetch
      }
      
      // 判断是否还有更多数据
      rewardHasMore.value = rewardData.value.length < response.data.total
      
      console.log('✅ 推荐奖励列表获取成功:', response.data.records.length, '条记录')
      console.log('总记录数:', response.data.total, '当前已加载:', rewardData.value.length)
    } else {
      console.log('⚠️ 推荐奖励列表为空')
      if (isFirstPage) {
        rewardData.value = []
      }
      rewardHasMore.value = false
    }
  } catch (error) {
    console.error('❌ 获取推荐奖励列表失败:', error)
    if (isFirstPage) {
      rewardData.value = []
    }
    rewardHasMore.value = false
  } finally {
    isLoadingMoreReward.value = false
  }
}

// 加载更多推荐奖励
const loadMoreRewards = () => {
  fetchReferralRewards(false)
}

// 格式化日期（显示 YYYY/MM/DD 格式）
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}/${month}/${day}`
}

// 格式化奖励数字（保留4位小数）
const formatReward = (num: number) => {
  return num.toFixed(4)
}

// 复制到剪贴板（兼容多种方法）
const handleCopy = async () => {
  if (!shareLink.value) {
    toast.warning('No link to copy')
    return
  }

  try {
    // 方法 1: 使用现代 Clipboard API
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(shareLink.value)
      console.log('✅ 使用 Clipboard API 复制成功')
      toast.success('Link copied to clipboard!')
      copied.value = true
      setTimeout(() => {
        copied.value = false
      }, 2000)
      return
    }

    // 方法 2: 使用传统的 execCommand 方法（后备方案）
    const textArea = document.createElement('textarea')
    textArea.value = shareLink.value
    textArea.style.position = 'fixed'
    textArea.style.left = '-999999px'
    textArea.style.top = '-999999px'
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()
    
    const successful = document.execCommand('copy')
    document.body.removeChild(textArea)
    
    if (successful) {
      console.log('✅ 使用 execCommand 复制成功')
      toast.success('Link copied to clipboard!')
      copied.value = true
      setTimeout(() => {
        copied.value = false
      }, 2000)
    } else {
      throw new Error('execCommand copy failed')
    }
  } catch (err) {
    console.error('❌ 复制失败:', err)
    toast.error('Failed to copy link')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchShareLink()
  fetchReferralRewards()
})
</script>

<style scoped>
/* Ensure the page has the correct background */
:deep(body) {
  background-color: #2D1B69 !important;
}

/* Step text colors */
.step-description {
  color: white !important;
}

/* Step titles with italic effect */
.step-01-title, .step-02-title, .step-03-title {
  font-style: italic !important;
}

/* Step colors */
.step-01-icon {
  background-color: rgba(6, 182, 212, 0.2) !important;
  border-color: rgba(6, 182, 212, 0.4) !important;
}

.step-01-icon svg {
  color: #22d3ee !important;
}

.step-01-title {
  background: linear-gradient(to right, #22d3ee, #3b82f6) !important;
  -webkit-background-clip: text !important;
  background-clip: text !important;
  color: transparent !important;
}

.step-03-icon {
  background-color: rgba(234, 179, 8, 0.2) !important;
  border-color: rgba(234, 179, 8, 0.4) !important;
}

.step-03-icon svg {
  color: #facc15 !important;
}

.step-03-title {
  background: linear-gradient(to right, #facc15, #fb923c) !important;
  -webkit-background-clip: text !important;
  background-clip: text !important;
  color: transparent !important;
}

/* Referral Rewards container colors */
.referral-rewards-container {
  background-color: rgba(31, 19, 71, 0.6) !important;
}

.referral-rewards-header {
  background-color: rgba(26, 15, 62, 0.4) !important;
}

/* Custom scrollbar for better UX */
::-webkit-scrollbar {
  width: 6px;
  height: 4px; /* 水平滚动条高度 */
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(115, 84, 255, 0.5);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(115, 84, 255, 0.7);
}

/* Input 水平滚动条样式 */
input::-webkit-scrollbar {
  height: 3px;
}

input::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}

input::-webkit-scrollbar-thumb {
  background: rgba(115, 84, 255, 0.3);
  border-radius: 2px;
}

input::-webkit-scrollbar-thumb:hover {
  background: rgba(115, 84, 255, 0.5);
}
</style>
