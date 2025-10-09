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
              >eth balance: {{ ethBalance }}</span
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
          <div
            class="flex w-full max-w-[335px] h-[44px] justify-center items-center bg-[url(https://codia-f2c.s3.us-west-1.amazonaws.com/image/2025-10-07/n5xV8EYBHb.png)] bg-cover bg-no-repeat rounded-[16px] relative z-[5] mt-[16px] mx-auto cursor-pointer hover:opacity-90 transition-opacity"
            @click="handleSwap"
          >
            <span
              class="h-[21px] shrink-0 font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-[#fff] relative text-left uppercase whitespace-nowrap z-[5]"
              >swap</span
            >
          </div>
        </div>

        <!-- Swap History -->
        <div
          class="w-full max-w-[347.5px] relative z-[1] mx-auto px-2"
        >
          <div
            class="w-full max-w-[335px] bg-[rgba(24,25,46,0.8)] rounded-[8px] shadow-[0_0_4px_0_rgba(243,226,255,0.2)_inset] z-[4] mx-auto pb-2"
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
          
          <!-- 可滚动内容区域 -->
          <div class="w-full max-h-[240px] overflow-y-auto">
            <!-- 加载中或空状态 -->
            <div
              v-if="swapHistory.length === 0"
              class="w-full max-w-[315px] h-[36px] relative z-[4] mt-[3px] mx-auto px-[10px] flex items-center justify-center"
            >
              <span class="text-[12px] text-white opacity-50">No swap history yet</span>
            </div>
            <!-- 历史记录列表 -->
            <div
              v-for="(item, index) in swapHistory"
              :key="item.id"
                class="w-full max-w-[315px] h-[36px] relative z-[4] mt-[3px] mx-auto px-[10px]"
              >
                <div
                  class="w-full h-[36px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-10"
                ></div>
                <span
                  class="flex h-[18px] justify-start items-start font-['Akshar'] text-[11px] font-normal opacity-80 leading-[18px] text-[#fff] absolute top-[9px] left-[10px] text-left whitespace-nowrap z-[2]"
                  >{{ formatDate(item.created) }}</span
                ><span
                  class="flex w-[50px] h-[18px] justify-end items-start font-['Akshar'] text-[12px] font-semibold opacity-80 leading-[18px] text-[#fff] absolute top-[9px] text-right uppercase whitespace-nowrap z-[4]"
                  style="left: 40%;"
                  >{{ formatNumber(item.eth) }}</span
                ><span
                  class="flex w-[23px] h-[18px] justify-end items-start font-['Akshar'] text-[12px] font-semibold opacity-80 leading-[18px] text-[#fff] absolute top-[9px] text-right uppercase whitespace-nowrap z-[3]"
                  style="right: 10px;"
                  >{{ formatNumber(item.usdc) }}</span
                >
              </div>
            </div>
            
            <!-- Show More 按钮 - 作为表格内的最后一行 -->
            <div
              v-if="swapHasMore"
              class="w-full flex items-center justify-center relative z-[4] mt-[6px] mb-[8px] mx-auto"
            >
              <button
                @click="loadMoreSwapHistory"
                :disabled="isLoadingMoreSwap"
                class="text-[13px] font-semibold text-blue-400 hover:text-blue-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors py-2"
              >
                {{ isLoadingMoreSwap ? 'Loading...' : 'Show more ▼' }}
              </button>
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
            >{{ availableAmount }}</span
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

          <!-- Withdraw Button inside card -->
          <div
            class="flex w-full max-w-[335px] h-[44px] justify-center items-center bg-gradient-to-r from-purple-500 to-orange-500 rounded-[16px] relative z-[5] mt-[58px] mx-auto cursor-pointer hover:opacity-90 transition-opacity absolute bottom-0 left-1/2 -translate-x-1/2"
            @click="handleWithdraw"
          >
            <span
              class="h-[21px] shrink-0 font-['PingFang_HK'] text-[15px] font-semibold leading-[21px] text-white relative text-left uppercase whitespace-nowrap z-[5]"
              >WITHDRAW</span
            >
          </div>
        </div>

        <!-- Withdraw History -->
        <div
          class="w-full max-w-[347.5px] relative z-[1] mx-auto px-2"
        >
          <div
            class="w-full max-w-[335px] bg-[rgba(24,25,46,0.8)] rounded-[8px] shadow-[0_0_4px_0_rgba(243,226,255,0.2)_inset] z-[4] mx-auto pb-2"
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
                style="left: 35%;"
                >USDC</span
              ><span
                class="flex w-[55px] h-[17px] justify-end items-start font-['PingFang_HK'] text-[12px] font-semibold opacity-30 leading-[16.8px] text-[#fff] absolute top-[12px] text-right uppercase whitespace-nowrap z-[28]"
                style="right: 10px;"
                >Status</span
              >
            </div>
          
          <!-- 可滚动内容区域 -->
          <div class="w-full max-h-[240px] overflow-y-auto">
            <!-- 加载中或空状态 -->
            <div
              v-if="withdrawHistory.length === 0"
              class="w-full max-w-[315px] h-[36px] relative z-[4] mt-[3px] mx-auto px-[10px] flex items-center justify-center"
            >
              <span class="text-[12px] text-white opacity-50">No withdraw history yet</span>
            </div>
            <!-- 历史记录列表 -->
            <div
              v-for="(item, index) in withdrawHistory"
              :key="item.id"
                class="w-full max-w-[315px] h-[36px] relative z-[4] mt-[3px] mx-auto px-[10px]"
              >
                <div
                  class="w-full h-[36px] bg-[rgba(115,84,255,0.6)] rounded-[4px] opacity-20 absolute top-0 left-1/2 -translate-x-1/2 shadow-[0_0_6px_0_rgba(255,255,255,0.3)_inset] z-10"
                ></div>
                <span
                  class="flex h-[18px] justify-start items-start font-['Akshar'] text-[11px] font-normal opacity-80 leading-[18px] text-[#fff] absolute top-[9px] left-[10px] text-left whitespace-nowrap z-[2]"
                  >{{ formatDate(item.created) }}</span
                ><span
                  class="flex w-[50px] h-[18px] justify-end items-start font-['Akshar'] text-[12px] font-semibold opacity-80 leading-[18px] text-[#fff] absolute top-[9px] text-right uppercase whitespace-nowrap z-[4]"
                  style="left: 35%;"
                  >{{ formatNumber(item.usdc) }}</span
                ><span
                  :class="getStatusClass(item.status)"
                  class="flex h-[18px] px-2 items-center justify-center font-['PingFang_HK'] text-[9px] font-semibold rounded-[4px] absolute top-[9px] z-[3]"
                  style="right: 10px;"
                  >{{ getStatusText(item.status) }}</span
                >
              </div>
            </div>
            
            <!-- Show More 按钮 - 作为表格内的最后一行 -->
            <div
              v-if="withdrawHasMore"
              class="w-full flex items-center justify-center relative z-[4] mt-[6px] mb-[8px] mx-auto"
            >
              <button
                @click="loadMoreWithdrawHistory"
                :disabled="isLoadingMoreWithdraw"
                class="text-[13px] font-semibold text-blue-400 hover:text-blue-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors py-2"
              >
                {{ isLoadingMoreWithdraw ? 'Loading...' : 'Show more ▼' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import ChatButton from '../components/ChatButton.vue'
import { getWalletIndex, toUsdc, submitExchange, getSwapHistory, getWithdrawAvailable, submitWithdraw, getWithdrawHistory } from '@/lib/api'
import { useToast } from '@/composables/useToast'
import type { SwapHistoryItem, WithdrawHistoryItem } from '@/lib/api'

const toast = useToast()

const activeTab = ref<'swap' | 'withdraw'>('swap')
const ethAmount = ref('0')
const usdcAmount = ref('0')
const withdrawAmount = ref('0')
const ethBalance = ref('0') // 可兑换的 ETH 余额
const swapHistory = ref<SwapHistoryItem[]>([]) // Swap 历史记录
const swapCurrentPage = ref(1) // Swap 当前页码
const swapHasMore = ref(false) // Swap 是否有更多数据
const isLoadingMoreSwap = ref(false) // Swap 是否正在加载更多
const availableAmount = ref('0') // Withdraw 可用金额
const withdrawHistory = ref<WithdrawHistoryItem[]>([]) // Withdraw 历史记录
const withdrawCurrentPage = ref(1) // Withdraw 当前页码
const withdrawHasMore = ref(false) // Withdraw 是否有更多数据
const isLoadingMoreWithdraw = ref(false) // Withdraw 是否正在加载更多
 

// 更新USDC数量 (SWAP) - 调用 API
const updateUsdcAmount = async () => {
  try {
    console.log('🔄 调用 toUsdc API...')
    const response = await toUsdc()
    if (response.success) {
      usdcAmount.value = response.data.toString()
      console.log('✅ toUsdc API 调用成功:', response.data)
    } else {
      console.error('❌ toUsdc API 调用失败')
    }
  } catch (error) {
    console.error('❌ 调用 toUsdc API 出错:', error)
  }
}



// 设置最大ETH数量 (SWAP)
const setMaxEth = async () => {
  ethAmount.value = ethBalance.value
  await updateUsdcAmount()
}

// 设置最大USDC数量 (WITHDRAW)
const setMaxUsdc = () => {
  withdrawAmount.value = availableAmount.value

}

// 获取钱包数据
const fetchWalletData = async () => {
  try {
    const response = await getWalletIndex()
    if (response.success && response.data) {
      // 设置可兑换的 ETH 余额
      ethBalance.value = response.data.exchangeable.toLocaleString()
 
      console.log('✅ 钱包数据加载成功:', response.data)
      console.log('可兑换 ETH 余额:', response.data.exchangeable)
    }
  } catch (error) {
    console.error('❌ 获取钱包数据失败:', error)
  }
}

// 获取 Swap 历史记录
const fetchSwapHistory = async (isFirstPage = true) => {
  if (isLoadingMoreSwap.value) return
  
  try {
    isLoadingMoreSwap.value = true
    const pageToFetch = isFirstPage ? 1 : swapCurrentPage.value + 1
    console.log('🔄 获取 Swap 历史记录，页码:', pageToFetch)
    
    const response = await getSwapHistory(pageToFetch, 4) // 每页4条记录
    
    if (response.success && response.data && response.data.records) {
      if (isFirstPage) {
        // 第一页，替换数据
        swapHistory.value = response.data.records
        swapCurrentPage.value = 1
      } else {
        // 加载更多，追加数据
        swapHistory.value = [...swapHistory.value, ...response.data.records]
        swapCurrentPage.value = pageToFetch
      }
      
      // 判断是否还有更多数据
      swapHasMore.value = swapHistory.value.length < response.data.total
  
    } else {
      console.log('⚠️ Swap 历史记录为空')
      if (isFirstPage) {
        swapHistory.value = []
      }
      swapHasMore.value = false
    }
  } catch (error) {
    console.error('❌ 获取 Swap 历史记录失败:', error)
    if (isFirstPage) {
      swapHistory.value = []
    }
    swapHasMore.value = false
  } finally {
    isLoadingMoreSwap.value = false
  }
}

// 加载更多 Swap 历史
const loadMoreSwapHistory = () => {
  fetchSwapHistory(false)
}

// 获取 Withdraw 可用金额
const fetchWithdrawAvailable = async () => {
  try {
    console.log('🔄 获取 Withdraw 可用金额...')
    const response = await getWithdrawAvailable()
    if (response.success && response.data !== undefined) {
      availableAmount.value = response.data.toFixed(2)
      console.log('✅ Withdraw 可用金额加载成功:', response.data)
    } else {
      console.log('⚠️ Withdraw 可用金额为空')
      availableAmount.value = '0'
    }
  } catch (error) {
    console.error('❌ 获取 Withdraw 可用金额失败:', error)
    availableAmount.value = '0'
  }
}

// 格式化日期（显示 YY-MM-DD HH:MM:SS 格式）
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const year = String(date.getFullYear()).slice(-2) // 取后两位年份
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化数字（截断到4位小数，添加千位分隔符）
const formatNumber = (num: number) => {
  // 截断到4位小数（不四舍五入）
  const truncated = Math.floor(num * 10000) / 10000
  // 添加千位分隔符，保留最多4位小数
  return truncated.toLocaleString('en-US', { 
    minimumFractionDigits: 0,
    maximumFractionDigits: 4 
  })
}

// 获取状态文本
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    3: 'Completed',
    '-1': 'Failed'
  }
  return statusMap[status] || 'Pending'
}

// 获取状态样式类
const getStatusClass = (status: number) => {
  const classMap: Record<number, string> = {
    3: 'bg-green-100 text-green-700 border border-green-300',
    '-1': 'bg-red-100 text-red-700 border border-red-300'
  }
  return classMap[status] || 'bg-yellow-100 text-yellow-700 border border-yellow-300'
}

// 提交 SWAP 交易
const handleSwap = async () => {
  try {
    // 验证输入
    if (!ethAmount.value || parseFloat(ethAmount.value) <= 0) {
      toast.warning('Please enter a valid ETH amount')
      return
    }
    
    if (!usdcAmount.value || parseFloat(usdcAmount.value) <= 0) {
      toast.warning('Please enter a valid USDC amount')
      return
    }
    
    console.log('🔄 调用 submitExchange API...')
    toast.info('Submitting swap transaction...')
    
    const response = await submitExchange()
    
    if (response.success) {
      console.log('✅ Swap 交易提交成功')
      toast.success('🎉 Swap transaction submitted successfully!')
      
      // 刷新钱包数据（更新 ETH Balance）
      console.log('🔄 刷新钱包数据，更新 ETH Balance...')
      await fetchWalletData()
      console.log('✅ ETH Balance 已更新:', ethBalance.value)
      
      // 刷新 Swap 历史记录（重置到第一页）
      console.log('🔄 重置 Swap 历史记录到第一页...')
      swapCurrentPage.value = 0
      await fetchSwapHistory(true)
      console.log('📊 Swap 历史刷新完成，swapHasMore:', swapHasMore.value)
      
      // 重置输入
      ethAmount.value = '0'
      usdcAmount.value = '0'
    } else {
      console.error('❌ Swap 交易提交失败:', response.msg)
      toast.error(response.msg || 'Swap transaction failed')
    }
  } catch (error) {
    console.error('❌ 提交 Swap 交易出错:', error)
    const errorMessage = error instanceof Error ? error.message : 'An error occurred during swap'
    toast.error(errorMessage)
  }
}

// 提交 WITHDRAW 交易
const handleWithdraw = async () => {
  try {
    // 验证输入
    if (!withdrawAmount.value || parseFloat(withdrawAmount.value) <= 0) {
      toast.warning('Please enter a valid USDC amount')
      return
    }
    
    console.log('🔄 调用 submitWithdraw API...')
    toast.info('Submitting withdraw transaction...')
    
    const response = await submitWithdraw(withdrawAmount.value)
    
    if (response.success) {
      console.log('✅ Withdraw 交易提交成功')
      toast.success('🎉 Withdraw transaction submitted successfully!')
      
      // 刷新可用金额
      await fetchWithdrawAvailable()
      
      // 刷新 Withdraw 历史记录（重置到第一页）
      console.log('🔄 重置 Withdraw 历史记录到第一页...')
      withdrawCurrentPage.value = 0
      await fetchWithdrawHistory(true)
      console.log('📊 Withdraw 历史刷新完成，withdrawHasMore:', withdrawHasMore.value)
      
      // 重置输入
      withdrawAmount.value = '0'
    } else {
      console.error('❌ Withdraw 交易提交失败:', response.msg)
      toast.error(response.msg || 'Withdraw transaction failed')
    }
  } catch (error) {
    console.error('❌ 提交 Withdraw 交易出错:', error)
    const errorMessage = error instanceof Error ? error.message : 'An error occurred during withdraw'
    toast.error(errorMessage)
  }
}

// 获取 Withdraw 历史记录
const fetchWithdrawHistory = async (isFirstPage = true) => {
  if (isLoadingMoreWithdraw.value) return
  
  try {
    isLoadingMoreWithdraw.value = true
    const pageToFetch = isFirstPage ? 1 : withdrawCurrentPage.value + 1
    console.log('🔄 获取 Withdraw 历史记录，页码:', pageToFetch)
    
    const response = await getWithdrawHistory(pageToFetch, 4) // 每页4条记录
    
    if (response.success && response.data && response.data.records) {
      if (isFirstPage) {
        // 第一页，替换数据
        withdrawHistory.value = response.data.records
        withdrawCurrentPage.value = 1
      } else {
        // 加载更多，追加数据
        withdrawHistory.value = [...withdrawHistory.value, ...response.data.records]
        withdrawCurrentPage.value = pageToFetch
      }
      
      // 判断是否还有更多数据
      withdrawHasMore.value = withdrawHistory.value.length < response.data.total
      
      console.log('✅ Withdraw 历史记录加载成功:', response.data.records.length, '条记录')
      console.log('总记录数:', response.data.total, '当前已加载:', withdrawHistory.value.length)
      console.log('withdrawHasMore 判断结果:', withdrawHasMore.value, '(', withdrawHistory.value.length, '<', response.data.total, ')')
    } else {
      console.log('⚠️ Withdraw 历史记录为空')
      if (isFirstPage) {
        withdrawHistory.value = []
      }
      withdrawHasMore.value = false
    }
  } catch (error) {
    console.error('❌ 获取 Withdraw 历史记录失败:', error)
    if (isFirstPage) {
      withdrawHistory.value = []
    }
    withdrawHasMore.value = false
  } finally {
    isLoadingMoreWithdraw.value = false
  }
}

// 加载更多 Withdraw 历史
const loadMoreWithdrawHistory = () => {
  fetchWithdrawHistory(false)
}

const setActiveTab = (tab: 'swap' | 'withdraw') => {
  activeTab.value = tab
  
  // 切换到 withdraw tab 时，重新获取可用余额
  if (tab === 'withdraw') {
    fetchWithdrawAvailable()
    fetchWithdrawHistory(true)
  }
  // 切换到 swap tab 时，重新获取钱包数据
  else if (tab === 'swap') {
    fetchWalletData()
    fetchSwapHistory(true)
  }
}

// 监听ETH数量变化 (SWAP) - 用户手动输入时不调用 API
const handleEthAmountChange = () => {
  // 只有点击 MAX 按钮时才会调用 API
  // 用户手动输入时使用本地计算
  const calculated = exchangeRate.value
  usdcAmount.value = calculated > 0 ? calculated.toFixed(0) : '0'
}

// 监听withdraw数量变化 (WITHDRAW)
const handleWithdrawAmountChange = () => {
  // 不需要更新，用户输入即可
}

// 页面加载时获取数据
onMounted(() => {
  fetchWalletData()
  fetchSwapHistory()
  fetchWithdrawAvailable()
  fetchWithdrawHistory()
})

// 调试信息
watch([ethAmount, usdcAmount], ([eth, usdc]) => {
  console.log(`SWAP: ${eth} ETH = ${usdc} USDC`)
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

/* 自定义滚动条样式 */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: rgba(115, 84, 255, 0.1);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(115, 84, 255, 0.5);
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(115, 84, 255, 0.7);
}
</style>
