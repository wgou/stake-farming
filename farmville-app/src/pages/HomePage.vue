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
            ETH
            <br />
            REWARD
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
            800.0000
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
              615193
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
              NOOES
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
              156
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
              854.778M
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

      <button
        class="w-full rounded-2xl shadow-lg font-display"
        :style="{
          height: '56px',
          background: 'linear-gradient(94.73deg, #B660D5 3.6%, #FD9379 101.51%)',
          fontWeight: 900,
          fontSize: '15px',
          lineHeight: '21px',
          textTransform: 'uppercase',
          color: '#E8FFF7',
        }"
      >
        CONNECT FARMVILLE
      </button>

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
                maxHeight: expandedFaqItems.includes(index) ? '300px' : '0px',
                opacity: expandedFaqItems.includes(index) ? 1 : 0,
              }"
            >
              <p
                class="pl-6 pr-6 pt-3 pb-4 font-sans"
                :style="{
                  fontWeight: 400,
                  fontSize: '12px',
                  lineHeight: '17px',
                  textTransform: 'capitalize',
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

      <div v-else class="space-y-3 pb-4">
        <div class="relative rounded transition-all duration-300">
          <div
            class="absolute inset-0"
            :style="{
              background: 'rgba(116, 84, 255, 0.6)',
              opacity: 0.2,
              boxShadow: 'inset 0px 0px 6px rgba(255, 255, 255, 0.3)',
            }"
          />
          <div class="relative p-4">
            <div class="grid grid-cols-2 gap-4">
              <div
                class="font-sans"
                :style="{
                  fontWeight: 600,
                  fontSize: '14px',
                  lineHeight: '20px',
                  textTransform: 'uppercase',
                  color: '#FFFFFF',
                  opacity: 0.6,
                }"
              >
                FARMVILLE ADDRESS
              </div>
              <div
                class="text-right font-sans"
                :style="{
                  fontWeight: 600,
                  fontSize: '14px',
                  lineHeight: '20px',
                  textTransform: 'uppercase',
                  color: '#FFFFFF',
                  opacity: 0.6,
                }"
              >
                ETH REWARD
              </div>
            </div>
          </div>
        </div>
        <div 
          v-for="(item, index) in rewardsData" 
          :key="index" 
          class="relative rounded transition-all duration-300 hover:scale-[1.02] cursor-pointer"
          @click="selectRewardItem(index)"
        >
          <div
            class="absolute inset-0 transition-all duration-300"
            :style="{
              background: 'rgba(116, 84, 255, 0.6)',
              opacity: selectedRewardIndex === index ? 0.3 : 0.2,
              boxShadow: 'inset 0px 0px 6px rgba(255, 255, 255, 0.3)',
            }"
          />
          <div class="relative p-4">
            <div class="grid grid-cols-2 gap-4">
              <div
                class="font-sans transition-all duration-300"
                :style="{
                  fontWeight: 400,
                  fontSize: '12px',
                  lineHeight: '17px',
                  color: '#FFFFFF',
                  opacity: selectedRewardIndex === index ? 1 : 0.8,
                }"
              >
                {{ item.address }}
              </div>
              <div
                class="text-right font-sans transition-all duration-300"
                :style="{
                  fontWeight: 400,
                  fontSize: '12px',
                  lineHeight: '17px',
                  color: '#FFFFFF',
                  opacity: selectedRewardIndex === index ? 1 : 0.8,
                }"
              >
                {{ item.reward }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import ChatButton from '../components/ChatButton.vue'

const activeTab = ref<'rewards' | 'faq'>('faq')
const expandedFaqItems = ref<number[]>([])
const selectedRewardIndex = ref<number | null>(null)

const partners = [
  { name: "CoinMarketCap", icon: "/CoinMarketCap.png" },
  { name: "CoinGecko", icon: "/CoinGecko.png" },
  { name: "TrustWallet", icon: "/TrustWallet.png" },
  { name: "Crypto.Com", icon: "/CryptoCom.png" },
]

const faqItems = [
  {
    question: "WHAT IS THE RETURN OFINVESTMENT(ROD?",
    answer: "After Successfully Joining, The System Will Start...",
    dotColor: "#7454FF",
  },
  {
    question: "HOW TO EARN REWARD?",
    answer: "The Cryptocurrency Mined Every Day Generates ETH...",
    dotColor: "#FCBA05",
  },
  {
    question: "IS THERE A REWRRO FOR INVITING FRIENDS?",
    answer: "Yes, You Can Invite Your Friends To Join The Mini...",
    dotColor: "#49BCFC",
  },
]

const rewardsData = [
  { address: "8XA1055A...163D7844", reward: "0.07445" },
  { address: "8XA1055A...163D7844", reward: "0.07445" },
  { address: "8XA1055A...163D7844", reward: "0.07445" },
  { address: "8XA1055A...163D7844", reward: "0.07445" },
  { address: "8XA1055A...163D7844", reward: "0.07445" },
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
