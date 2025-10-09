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
          Share Your Referral Link With Friends And Earn 38% Of Their Rewards
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
            <div
              class="w-[68px] h-[32px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/FYX4FUdZUo.png)] bg-cover bg-no-repeat absolute top-[6px] z-[4] cursor-pointer hover:opacity-80 transition-opacity"
              style="right: 14px;"
              @click="handleCopy"
            >
              <span
                class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#7354ff] absolute top-[5px] left-[16px] text-left uppercase whitespace-nowrap z-[5]"
              >
                {{ copied ? "COPIED" : "COPY" }}
              </span>
            </div>
            ><input
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[68px] text-left uppercase whitespace-nowrap z-[3] bg-transparent outline-none w-[100px]"
              type="number"
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
        <div class="w-full max-w-[347.5px] h-[258px] relative z-[1] mx-auto mt-8 px-2">
          <div class="w-full max-w-[335px] h-[258px] bg-[rgba(24,25,46,0.8)] rounded-[8px] absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_4px_0_rgba(243,226,255,0.2)_inset] z-[4]">
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
            <div v-for="(item, index) in rewardData" :key="index" class="w-full max-w-[315px] h-[48px] relative z-[4] mt-[5px] mx-auto px-[10px]">
              <div class="w-full h-[48px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-10"></div>
              <span class="flex h-[21px] justify-start items-start font-['Akshar'] text-[15px] font-normal opacity-80 leading-[20.7px] text-[#fff] absolute top-[14px] left-[10px] text-left uppercase whitespace-nowrap z-[2]">
                {{ item.date }}
              </span>
              <span class="flex w-[50px] h-[21px] justify-end items-start font-['Akshar'] text-[15px] font-semibold opacity-80 leading-[20.7px] text-[#fff] absolute top-[15px] text-right uppercase whitespace-nowrap z-[3]" style="right: 10px;">
                {{ item.amount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Header from '../components/Header.vue'
import ChatButton from '../components/ChatButton.vue'
import BottomNav from '../components/BottomNav.vue'

const copied = ref(false)
const rewardData = ref([
  { date: '2025/08/10', amount: '100' },
  { date: '2025/08/10', amount: '100' },
  { date: '2025/08/10', amount: '100' }
])

const handleCopy = () => {
  copied.value = true
  setTimeout(() => {
    copied.value = false
  }, 2000)
}
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
</style>
