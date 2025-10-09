<template>
  <div class="min-h-screen w-full bg-[#1a1333] pb-24 overflow-x-hidden">
    <Header />
    <ChatButton />

    <main class="px-4 space-y-6 overflow-x-hidden max-w-full pt-6">
      <!-- Tab Switcher -->
      <div class="w-full max-w-[345px] h-[44px] relative mx-auto">
        <!-- Background -->
        <div
          class="w-full h-[44px] bg-[#d9d9d9] rounded-[22px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2"
        ></div>
        
        <!-- SWAP Selected Background -->
        <div
          v-if="activeTab === 'swap'"
          class="w-[160px] h-[40px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/NgmbQZO7jR.png)] bg-cover bg-no-repeat rounded-[16px] absolute top-[2px] z-[2] cursor-pointer"
          style="left: 2px;"
          @click="setActiveTab('swap')"
        ></div>
        
        <!-- WITHDRAW Selected Background -->
        <div
          v-if="activeTab === 'withdraw'"
          class="w-[172px] h-[40px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/dCLM1LNMJq.png)] bg-cover bg-no-repeat rounded-[16px] absolute top-[2px] z-[1] cursor-pointer"
          style="right: 2px;"
          @click="setActiveTab('withdraw')"
        ></div>
        
        <!-- SWAP Icon (when SWAP is selected) -->
        <div
          v-if="activeTab === 'swap'"
          class="w-[16px] h-[16px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/FeHabuR8CA.png)] bg-cover bg-no-repeat absolute top-[14px] left-[49px] overflow-hidden z-[4]"
        ></div>
        
        <!-- WITHDRAW Icon (when WITHDRAW is selected) -->
        <div
          v-if="activeTab === 'withdraw'"
          class="w-[16px] h-[16px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/7E4bgrFMv1.png)] bg-cover bg-no-repeat absolute top-[14px] left-[49px] overflow-hidden z-[4]"
        ></div>
        
        <!-- SWAP Text Icon (only visible when SWAP is selected) -->
        <div
          v-if="activeTab === 'swap'"
          class="w-[20px] h-[20px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/SOvzinbwUK.png)] bg-cover bg-no-repeat absolute top-[12px] left-[46px] overflow-hidden z-[1]"
        ></div>
        
        <!-- WITHDRAW Text Icon (always visible) -->
        <div
          class="w-[20px] h-[20px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/u299nfZ76H.png)] bg-cover bg-no-repeat absolute top-[12px] left-[197px] overflow-hidden z-[2]"
        ></div>
        
        <!-- SWAP Text -->
        <span
          class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#fff] absolute top-[12px] text-left uppercase whitespace-nowrap z-[3] cursor-pointer"
          :style="{
            opacity: activeTab === 'swap' ? 1 : 0.8,
            left: activeTab === 'swap' ? '70px' : '70px',
          }"
          @click="setActiveTab('swap')"
          >SWAP</span
        >
        
        <!-- WITHDRAW Text -->
        <span
          class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#fff] absolute top-[12px] text-left uppercase whitespace-nowrap z-[1] cursor-pointer"
          :style="{
            opacity: activeTab === 'withdraw' ? 1 : 0.8,
            left: activeTab === 'withdraw' ? '222px' : '222px',
          }"
          @click="setActiveTab('withdraw')"
          >WITHDRAW</span
        >
      </div>

      <!-- SWAP Content -->
      <div v-if="activeTab === 'swap'" class="space-y-6 animate-fadeIn">
        <!-- Swap Card -->
        <div
          class="w-full max-w-[335px] h-[308px] bg-[#fff] rounded-[20px] relative z-[1] mx-auto pt-[5px]"
        >
          <span
            class="block h-[21px] font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] relative text-center uppercase whitespace-nowrap z-[2] mt-[12px] mx-auto"
            >ETH to usdc</span
          >
          <div
            class="flex w-full max-w-[295px] h-[14px] justify-between items-center relative z-[3] mt-[19px] mx-auto px-[10px]"
          >
            <span
              class="h-[14px] shrink-0 font-['PingFang_HK'] text-[10px] font-semibold opacity-30 leading-[14px] text-[#000] relative text-left uppercase whitespace-nowrap z-[3]"
              >you pay</span
            ><span
              class="flex w-[137px] h-[14px] justify-end items-start shrink-0 font-['PingFang_HK'] text-[10px] font-semibold opacity-30 leading-[14px] text-[#000] relative text-right uppercase whitespace-nowrap z-[3]"
              >eth balance: 999,999,999</span
            >
                </div>
          <div
            class="w-full max-w-[315px] h-[44px] bg-[#f6f6f6] rounded-[4px] relative z-[4] mt-[2px] mx-auto"
          >
            <div
              class="w-[68px] h-[32px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/efZcz289Yk.png)] bg-cover bg-no-repeat absolute top-[6px] z-[5] cursor-pointer hover:opacity-80 transition-opacity"
              style="right: 10px;"
              @click="setMaxEth"
            >
              <span
                class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#7354ff] absolute top-[5px] left-[16px] text-left uppercase whitespace-nowrap z-[1]"
                >max</span
              >
            </div>
            <div
              class="w-[24px] h-[24px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/hPDX8EZtQJ.png)] bg-cover bg-no-repeat rounded-[150px] absolute top-[10px] left-[10px] z-50"
            ></div>
            <span
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[41px] text-left uppercase whitespace-nowrap z-[3]"
              >ETH：</span
            ><input
              v-model="ethAmount"
              @input="handleEthAmountChange"
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[99px] text-left uppercase whitespace-nowrap z-[4] bg-transparent outline-none w-[100px]"
              type="number"
            />
          </div>
          <div
            class="w-full max-w-[325px] h-[48px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/0xKHizybGG.png)] bg-cover bg-no-repeat relative z-[2] mt-[5px] mx-auto cursor-pointer hover:opacity-80 transition-opacity"
            @click="swapDirection"
          ></div>
          <span
            class="block h-[14px] font-['PingFang_HK'] text-[10px] font-semibold opacity-30 leading-[14px] text-[#000] relative text-left uppercase whitespace-nowrap z-[5] mt-0 px-[10px]"
            >you get</span
          >
          <div
            class="w-full max-w-[315px] h-[44px] bg-[#f6f6f6] rounded-[4px] relative z-40 mt-[2px] mx-auto"
          >
            <div
              class="w-[24px] h-[24px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/HhUn9dE0PA.png)] bg-cover bg-no-repeat rounded-[12px] absolute top-[10px] left-[10px] z-[1]"
            ></div>
            <span
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[40px] text-left uppercase whitespace-nowrap z-[2]"
              >USDC:</span
            ><span
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[98px] text-left uppercase whitespace-nowrap z-[3]"
              >{{ usdcAmount }}</span
            >
          </div>
          <span
            class="flex w-full max-w-[200px] h-[14px] justify-center items-start font-['PingFang_HK'] text-[10px] font-semibold opacity-50 leading-[14px] text-[#264b50] relative text-center uppercase whitespace-nowrap z-[4] mt-[9px] mx-auto"
            >{{ ethAmount }} ETH = {{ usdcAmount }} USDC</span
          >
          <div
            class="flex w-full max-w-[335px] h-[44px] justify-center items-center bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/n5xV8EYBHb.png)] bg-cover bg-no-repeat rounded-[16px] relative z-[5] mt-[16px] mx-auto cursor-pointer hover:opacity-90 transition-opacity"
            @click="connectWallet"
          >
            <span
              class="h-[21px] shrink-0 font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#fff] relative text-left uppercase whitespace-nowrap z-[5]"
              >Connect wallet to swap</span
            >
          </div>
        </div>

        <!-- Swap History -->
        <div
          class="w-full max-w-[347.5px] h-[258px] relative z-[1] mx-auto px-2"
        >
          <div
            class="w-full max-w-[335px] h-[258px] bg-[rgba(24,25,46,0.8)] rounded-[8px] absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_4px_0_rgba(243,226,255,0.2)_inset] z-[4]"
          >
            <div
              class="w-full h-[40px] relative z-[1] mt-0 mr-0 mb-0 ml-0"
            >
              <span
                class="flex w-[93px] h-[17px] justify-center items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-80 leading-[42px] text-[#c9beff] relative text-center uppercase whitespace-nowrap z-[1] mx-auto"
                >Swap history</span
              >
              <div
                class="w-full h-[40px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/V4zJwFHfwX.png)] bg-cover bg-no-repeat rounded-[8px] absolute top-0 left-1/2 -translate-x-1/2 z-30"
              ></div>
            </div>
            <div
              class="w-full h-px bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/njnh4AJL0G.png)] bg-cover bg-no-repeat absolute top-[39.5px] left-0 z-[2]"
            ></div>
            <div
              class="w-full max-w-[315px] h-[40px] relative z-[28] mt-[6px] mx-auto px-[10px]"
            >
              <div
                class="w-full h-[40px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-[25]"
              ></div>
              <span
                class="flex h-[17px] justify-start items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] left-[10px] text-left uppercase whitespace-nowrap z-[26]"
                >Date(UTC)</span
              ><span
                class="flex h-[17px] justify-start items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] text-left uppercase whitespace-nowrap z-[27]"
                style="left: 40%;"
                >From eth</span
              ><span
                class="flex w-[55px] h-[17px] justify-end items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] text-right uppercase whitespace-nowrap z-[28]"
                style="right: 10px;"
                >To usdc</span
              >
            </div>
          <div
            v-for="(item, index) in swapHistory"
            :key="index"
              class="w-full max-w-[315px] h-[48px] relative z-[4] mt-[5px] mx-auto px-[10px]"
            >
              <div
                class="w-full h-[48px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-10"
              ></div>
              <span
                class="flex h-[21px] justify-start items-start font-['Akshar'] text-[15px] font-normal opacity-80 leading-[20.7px] text-[#fff] absolute top-[14px] left-[10px] text-left uppercase whitespace-nowrap z-[2]"
                >{{ item.date }}</span
              ><span
                class="flex w-[50px] h-[21px] justify-end items-start font-['Akshar'] text-[15px] font-semibold opacity-80 leading-[20.7px] text-[#fff] absolute top-[15px] text-right uppercase whitespace-nowrap z-[4]"
                style="left: 40%;"
                >{{ item.fromEth }}</span
              ><span
                class="flex w-[23px] h-[21px] justify-end items-start font-['Akshar'] text-[15px] font-semibold opacity-80 leading-[20.7px] text-[#fff] absolute top-[15px] text-right uppercase whitespace-nowrap z-[3]"
                style="right: 10px;"
                >{{ item.toUsdc }}</span
              >
            </div>
          </div>
        </div>
      </div>

      <!-- WITHDRAW Content -->
      <div v-if="activeTab === 'withdraw'" class="space-y-6 animate-fadeIn">
        <!-- Withdraw Card -->
        <div
          class="w-full max-w-[335px] h-[280px] text-[0px] relative mx-auto my-0 bg-[url(/whitdraw.png)] bg-cover bg-no-repeat bg-center rounded-[20px] pt-[25px]"
        >
          <span
            class="block h-[42px] font-['DIN_Alternate'] text-[36px] font-bold leading-[41.906px] text-[#fff] relative text-left uppercase whitespace-nowrap z-10 mt-[16px] px-[15px]"
            >1,299,999.00</span
          ><span
            class="block h-[18px] font-['PingFang_HK'] text-[13px] font-normal opacity-80 leading-[18px] text-[#fff] relative text-left uppercase whitespace-nowrap z-[2] mt-[12px] px-[15px]"
            >available amount</span
          >
          <div
            class="w-full max-w-[315px] h-[44px] bg-[#f6f6f6] rounded-[4px] relative z-[1] mt-[21px] mx-auto"
          >
            <div
              class="w-[68px] h-[32px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/FYX4FUdZUo.png)] bg-cover bg-no-repeat absolute top-[6px] z-[4] cursor-pointer hover:opacity-80 transition-opacity"
              style="right: 10px;"
              @click="setMaxUsdc"
            >
              <span
                class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#7354ff] absolute top-[5px] left-[16px] text-left uppercase whitespace-nowrap z-[5]"
                >max</span
              >
            </div>
            <span
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[10px] text-left uppercase whitespace-nowrap z-[2]"
              >usdc:</span
            ><input
              v-model="withdrawAmount"
              @input="handleWithdrawAmountChange"
              class="flex h-[21px] justify-start items-start font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#000] absolute top-[11px] left-[68px] text-left uppercase whitespace-nowrap z-[3] bg-transparent outline-none w-[100px]"
              type="number"
            />
          </div>

          <!-- Connect Wallet Button inside card -->
          <div
            class="flex w-full max-w-[335px] h-[44px] justify-center items-center bg-gradient-to-r from-purple-500 to-orange-500 rounded-[16px] relative z-[5] mt-[58px] mx-auto cursor-pointer hover:opacity-90 transition-opacity absolute bottom-0 left-1/2 -translate-x-1/2"
            @click="connectWallet"
          >
            <span
              class="h-[21px] shrink-0 font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-white relative text-left uppercase whitespace-nowrap z-[5]"
              >CONNECT WALLET TO WITHDRAW</span
            >
          </div>
        </div>

        <!-- Withdraw History -->
        <div
          class="w-full max-w-[347.5px] h-[258px] relative z-[1] mx-auto px-2"
        >
          <div
            class="w-full max-w-[335px] h-[258px] bg-[rgba(24,25,46,0.8)] rounded-[8px] absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_4px_0_rgba(243,226,255,0.2)_inset] z-[4]"
          >
            <div
              class="w-full h-[40px] relative z-[1] mt-0 mr-0 mb-0 ml-0"
            >
              <span
                class="flex w-[93px] h-[17px] justify-center items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-80 leading-[42px] text-[#c9beff] relative text-center uppercase whitespace-nowrap z-[1] leading-[42px] mx-auto"
                >Withdraw history</span
              >
              <div
                class="w-full h-[40px] bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/V4zJwFHfwX.png)] bg-cover bg-no-repeat rounded-[8px] absolute top-0 left-1/2 -translate-x-1/2 z-30"
              ></div>
            </div>
            <div
              class="w-full h-px bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/njnh4AJL0G.png)] bg-cover bg-no-repeat absolute top-[39.5px] left-0 z-[2]"
            ></div>
            <div
              class="w-full max-w-[315px] h-[40px] relative z-[28] mt-[6px] mx-auto px-[10px]"
            >
              <div
                class="w-full h-[40px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-[25]"
              ></div>
              <span
                class="flex h-[17px] justify-start items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] left-[10px] text-left uppercase whitespace-nowrap z-[26]"
                >Date(UTC)</span
              ><span
                class="flex h-[17px] justify-start items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] text-left uppercase whitespace-nowrap z-[27]"
                style="left: 40%;"
                >From usdc</span
              ><span
                class="flex w-[55px] h-[17px] justify-end items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] text-right uppercase whitespace-nowrap z-[28]"
                style="right: 10px;"
                >To eth</span
              >
            </div>
          <div
            v-for="(item, index) in withdrawHistory"
            :key="index"
              class="w-full max-w-[315px] h-[48px] relative z-[4] mt-[5px] mx-auto px-[10px]"
            >
              <div
                class="w-full h-[48px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-10"
              ></div>
              <span
                class="flex h-[21px] justify-start items-start font-['Akshar'] text-[15px] font-normal opacity-80 leading-[20.7px] text-[#fff] absolute top-[14px] left-[10px] text-left uppercase whitespace-nowrap z-[2]"
                >{{ item.date }}</span
              ><span
                class="flex w-[50px] h-[21px] justify-end items-start font-['Akshar'] text-[15px] font-semibold opacity-80 leading-[20.7px] text-[#fff] absolute top-[15px] text-right uppercase whitespace-nowrap z-[4]"
                style="left: 40%;"
                >{{ item.usdc }}</span
              ><span
                class="flex w-[23px] h-[21px] justify-end items-start font-['Akshar'] text-[15px] font-semibold opacity-80 leading-[20.7px] text-[#fff] absolute top-[15px] text-right uppercase whitespace-nowrap z-[3]"
                style="right: 10px;"
                >{{ item.eth }}</span
              >
            </div>
          </div>
        </div>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import ChatButton from '../components/ChatButton.vue'

const activeTab = ref<'swap' | 'withdraw'>('swap')
const ethAmount = ref('100')
const usdcAmount = ref('1')
const withdrawAmount = ref('100')
const ethWithdrawAmount = ref('10000')

// 计算汇率 - SWAP: ETH to USDC
const exchangeRate = computed(() => {
  const eth = parseFloat(ethAmount.value) || 0
  return eth / 100 // 100 ETH = 1 USDC
})

// 计算withdraw汇率 - WITHDRAW: USDC to ETH
const withdrawExchangeRate = computed(() => {
  const usdc = parseFloat(withdrawAmount.value) || 0
  return usdc * 100 // 1 USDC = 100 ETH
})

// 更新USDC数量 (SWAP)
const updateUsdcAmount = () => {
  const calculated = exchangeRate.value
  usdcAmount.value = calculated > 0 ? calculated.toFixed(0) : '0'
}

// 更新ETH withdraw数量 (WITHDRAW)
const updateEthWithdrawAmount = () => {
  const calculated = withdrawExchangeRate.value
  ethWithdrawAmount.value = calculated > 0 ? calculated.toFixed(0) : '0'
}

// 设置最大ETH数量 (SWAP)
const setMaxEth = () => {
  ethAmount.value = '999999999'
  updateUsdcAmount()
}

// 设置最大USDC数量 (WITHDRAW)
const setMaxUsdc = () => {
  withdrawAmount.value = '1299999'
  updateEthWithdrawAmount()
}

// 交换方向 (SWAP)
const swapDirection = () => {
  const temp = ethAmount.value
  ethAmount.value = usdcAmount.value
  usdcAmount.value = temp
  updateUsdcAmount()
}

// 连接钱包
const connectWallet = () => {
  console.log('Connecting wallet...')
  // 这里可以添加连接钱包的逻辑
}

const swapHistory = [
  { date: "2025/08/10", fromEth: "999,999", toUsdc: "9,999" },
  { date: "2025/08/10", fromEth: "500,000", toUsdc: "5,000" },
  { date: "2025/08/10", fromEth: "100,000", toUsdc: "1,000" },
]

const withdrawHistory = [
  { date: "2025/08/10", usdc: "999,999", eth: "99,999,900" },
  { date: "2025/08/10", usdc: "500,000", eth: "50,000,000" },
  { date: "2025/08/10", usdc: "100,000", eth: "10,000,000" },
]

const setActiveTab = (tab: 'swap' | 'withdraw') => {
  activeTab.value = tab
}

// 监听ETH数量变化 (SWAP)
const handleEthAmountChange = () => {
  updateUsdcAmount()
}

// 监听withdraw数量变化 (WITHDRAW)
const handleWithdrawAmountChange = () => {
  updateEthWithdrawAmount()
}

// 初始化时更新数值
updateUsdcAmount()
updateEthWithdrawAmount()

// 监听数值变化，确保实时更新
watch(ethAmount, () => {
  updateUsdcAmount()
})

watch(withdrawAmount, () => {
  updateEthWithdrawAmount()
})

// 调试信息
watch([ethAmount, usdcAmount], ([eth, usdc]) => {
  console.log(`SWAP: ${eth} ETH = ${usdc} USDC`)
})

watch([withdrawAmount, ethWithdrawAmount], ([usdc, eth]) => {
  console.log(`WITHDRAW: ${usdc} USDC = ${eth} ETH`)
})
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
</style>
