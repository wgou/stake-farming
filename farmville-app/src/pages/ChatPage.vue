<template>
  <div class="h-screen w-full flex flex-col" style="background: linear-gradient(188deg, #201D41 -2.21%, #291C88 153.11%);">
    <!-- Chat Header -->
    <div class="px-4 py-3 flex items-center gap-3" style="background-color: #201D41;">
      <button @click="goBack" class="flex items-center gap-2">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span class="text-white text-sm font-medium">BACK</span>
      </button>
    </div>

    <!-- Chat Messages -->
    <div class="flex-1 px-4 py-2 space-y-4 overflow-y-auto" style="scrollbar-width: none; -ms-overflow-style: none;">
      <div v-for="msg in chatHistory" :key="msg.id">
        <!-- Customer Service Message -->
        <div v-if="msg.type === 'service'" class="flex items-start gap-3">
          <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-orange-400 flex items-center justify-center flex-shrink-0">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="flex-1">
            <div class="bg-white rounded-lg p-3 shadow-sm inline-block message-left" style="max-width: 246px;">
              <p class="text-[15px] font-semibold leading-relaxed" style="color: #000;">{{ msg.text }}</p>
              <p v-if="msg.time" class="text-gray-500 text-xs mt-1">{{ msg.time }}</p>
            </div>
          </div>
        </div>

        <!-- User Message -->
        <div v-else class="flex items-start gap-3 justify-end">
          <div class="flex justify-end">
            <div class="rounded-lg p-3 shadow-sm inline-block message-right" style="background-color: #C9BDFE; max-width: 246px;">
              <p class="text-[15px] font-semibold" style="color: #000;">{{ msg.text }}</p>
            </div>
          </div>
          <div class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center flex-shrink-0 overflow-hidden">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-gray-600">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Message Input -->
    <div class="px-4 py-2 bg-gray-800 border-t border-gray-700">
      <div class="flex items-center gap-3">
        <div class="flex-1 rounded-lg px-4 py-3" style="background-color: #444350;">
          <input
            v-model="message"
            type="text"
            placeholder=""
            class="w-full bg-transparent text-white placeholder-gray-400 outline-none font-sans text-sm"
            @keypress.enter="sendMessage"
          />
        </div>
        <button
          @click="sendMessage"
          :disabled="!message.trim()"
          class="w-12 h-12 flex items-center justify-center disabled:cursor-not-allowed hover:scale-105 transition-transform"
        >
          <img src="/send.png" alt="Send" class="w-6 h-6" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const message = ref('')

// Mock chat history
const chatHistory = ref([
  { id: 1, type: 'service', text: 'Hey,Evgeniy', time: '' },
  { id: 2, type: 'service', text: "Hello! I'm Your Customer Service Assistant. How Can I Help You Today?", time: 'Less Than A Minute Ago' },
  { id: 3, type: 'user', text: 'Hi! I have a question about my farming rewards.' },
  { id: 4, type: 'service', text: 'Of course! I\'d be happy to help you with your farming rewards. What would you like to know?' },
  { id: 5, type: 'user', text: 'How do I check my current ETH balance?' },
  { id: 6, type: 'service', text: 'You can check your ETH balance by going to the Account page. There you\'ll see your ETH and USDC balances displayed at the top.' },
  { id: 7, type: 'user', text: 'Great! And how often are rewards distributed?' },
  { id: 8, type: 'service', text: 'Rewards are distributed daily based on your farming activities. You can view your reward history in the transaction page.' },
  { id: 9, type: 'user', text: 'Can I withdraw my rewards anytime?' },
  { id: 10, type: 'service', text: 'Yes! You can withdraw your rewards at any time from the Transaction page. Just go to the Withdraw tab and follow the instructions.' },
  { id: 11, type: 'user', text: 'OK,Thank You' },
])

const goBack = () => {
  router.back()
}

const sendMessage = () => {
  if (message.value.trim()) {
    console.log('Sending message:', message.value)
    // Here you would typically send the message to a chat service
    message.value = ''
  }
}
</script>

<style scoped>
/* Hide scrollbar for WebKit browsers */
.overflow-y-auto::-webkit-scrollbar {
  display: none;
}

/* Left message triangle - pointing left to avatar */
.message-left {
  position: relative;
}

.message-left::before {
  content: '';
  position: absolute;
  left: -6px;
  top: 12px;
  width: 12px;
  height: 12px;
  background-color: #ffffff;
  transform: rotate(45deg);
}

/* Right message triangle - pointing right to avatar */
.message-right {
  position: relative;
}

.message-right::before {
  content: '';
  position: absolute;
  right: -6px;
  top: 12px;
  width: 12px;
  height: 12px;
  background-color: #C9BDFE;
  transform: rotate(45deg);
}
</style>
