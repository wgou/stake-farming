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
          <div class="text-white text-center text-2xl font-bold" style="font-family: 'DIN Alternate';">1,288</div>
          <div class="text-white/80 text-xs font-normal" style="font-family: 'PingFang HK';">Balance</div>
        </div>
        <div class="flex flex-col items-center justify-center ml-[60px]">
          <div class="text-white/80 text-xs font-normal" style="font-family: 'PingFang HK';">USDC</div>
          <div class="text-white text-center text-2xl font-bold" style="font-family: 'DIN Alternate';">99</div>
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
        <div class="ml-[17px] text-white/30 text-[15px] mr-auto" style="font-family: 'PingFang HK';">DATE</div>
        <div class="text-white/30 text-right text-[15px] mr-[10px]" style="font-family: Akshar;">REWARD</div>
      </div>
      <div class="w-full h-12 rounded-lg bg-[#010506] mt-[5px] flex items-center" v-for="item in rewards" :key="item.date">
        <div class="ml-[17px] text-white/70 text-[15px] mr-auto" style="font-family: 'PingFang HK';">{{ item.date }}</div>
        <div class="text-white text-right text-[15px] mr-[10px]" style="font-family: Akshar;">{{ item.reward }}</div>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import ChatButton from '../components/ChatButton.vue'

const assets = [
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
]

const rewards = [
  { date: "2025/08/10", reward: "999,999" },
  { date: "2025/08/10", reward: "999,999" },
]
</script>
