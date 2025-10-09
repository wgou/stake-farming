<template>
  <div class="min-h-screen w-full flex flex-col pb-24 overflow-x-hidden" style="background: linear-gradient(191.82deg, #1F1D35 1.41%, #291C8B 52.85%, #25204B 100%), #0B0914;">
    <Header />
    <Toast />

    <main class="flex-1 flex flex-col px-4 overflow-x-hidden max-w-full pt-6">
      <!-- Page Title -->
      <div class="mb-4">
        <button @click="goBack" class="flex items-center gap-2 mb-3">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white opacity-80">
            <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span class="text-white/80 text-sm font-semibold uppercase" style="font-family: 'PingFang HK';">BACK</span>
        </button>
        <div class="flex items-center gap-3">
          <div style="color: #CABEFF; font-family: 'PingFang HK'; font-size: 12px; font-weight: 600; text-transform: uppercase; opacity: 0.8;">Customer Service</div>
          <!-- Connection Status -->
          <div v-if="connectionStatus === 'connected'" class="flex items-center gap-1">
            <div class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
            <span class="text-green-400 text-[10px]" style="font-family: 'PingFang HK';">Connected</span>
          </div>
          <div v-else-if="connectionStatus === 'connecting'" class="flex items-center gap-1">
            <div class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
            <span class="text-blue-400 text-[10px]" style="font-family: 'PingFang HK';">Connecting...</span>
          </div>
          <div v-else-if="connectionStatus === 'error'" class="flex items-center gap-1">
            <div class="w-2 h-2 bg-red-500 rounded-full"></div>
            <span class="text-red-400 text-[10px]" style="font-family: 'PingFang HK';">Error</span>
          </div>
          <div v-else class="flex items-center gap-1">
            <div class="w-2 h-2 bg-gray-500 rounded-full"></div>
            <span class="text-gray-400 text-[10px]" style="font-family: 'PingFang HK';">Disconnected</span>
          </div>
        </div>
        <!-- Wallet connection reminder -->
        <div v-if="!isConnected" class="mt-2 text-yellow-400 text-[11px]" style="font-family: 'PingFang HK';">
          ⚠️ Please connect your wallet to use chat
        </div>
      </div>

      <!-- Chat Messages Container -->
      <div ref="chatContainer" class="flex-1 overflow-y-auto space-y-4 mb-4 custom-scrollbar" style="max-height: calc(100vh - 280px);">
        <div v-for="(msg, index) in chatHistory" :key="index">
          <!-- Customer Service Message -->
          <div v-if="msg.sender === 'agent'" class="flex items-start gap-3 mb-4">
            <div class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0" style="background: linear-gradient(135deg, #7C3AED 0%, #F97316 100%);">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="flex-1">
              <div class="bg-[#010506] rounded-lg p-3 inline-block message-left shadow-[0_0_4px_0_rgba(243,226,255,0.2)]" style="max-width: 280px;">
                <!-- Text Message -->
                <p v-if="msg.content.type === 'text'" class="text-[14px] font-medium leading-relaxed text-white/90 break-words whitespace-pre-wrap" style="font-family: 'PingFang HK';">{{ msg.content.content }}</p>
                <!-- Image Message -->
                <img 
                  v-else 
                  :src="msg.content.content" 
                  alt="Image" 
                  class="max-w-full rounded-md max-h-48 object-contain cursor-pointer"
                  @click="openImage(msg.content.content)"
                />
              </div>
              <!-- Relative Time Display -->
              <p v-if="msg.time" class="text-[10px] mt-1 opacity-50 ml-1" style="color: #CABEFF; font-family: 'PingFang HK';">{{ getRelativeTime(msg.time) }}</p>
            </div>
          </div>

          <!-- User Message -->
          <div v-else class="flex items-start gap-3 justify-end mb-4">
            <div class="flex flex-col items-end flex-1">
              <div class="rounded-lg p-3 inline-block message-right" style="background: linear-gradient(135deg, #9F7AEA 0%, #C9BDFE 100%); max-width: 280px; box-shadow: 0 0 4px 0 rgba(159, 122, 234, 0.3);">
                <!-- Text Message -->
                <p v-if="msg.content.type === 'text'" class="text-[14px] font-medium text-white break-words whitespace-pre-wrap" style="font-family: 'PingFang HK';">{{ msg.content.content }}</p>
                <!-- Image Message -->
                <img 
                  v-else 
                  :src="msg.content.content" 
                  alt="Image" 
                  class="max-w-full rounded-md max-h-48 object-contain cursor-pointer"
                  @click="openImage(msg.content.content)"
                />
              </div>
              <!-- Relative Time Display -->
              <p v-if="msg.time" class="text-[10px] mt-1 opacity-50 mr-1" style="color: #CABEFF; font-family: 'PingFang HK';">{{ getRelativeTime(msg.time) }}</p>
            </div>
            <div class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 overflow-hidden" style="background: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Message Input Area -->
      <div class="mt-auto pt-4 pb-2">
        <div class="w-full rounded-lg p-2" style="background: rgba(255, 255, 255, 0.05); backdrop-filter: blur(10px);">
          <div class="flex items-end gap-2">
            <!-- Image Upload Button -->
            <button
              @click="triggerFileInput"
              :disabled="!isConnected || connectionStatus !== 'connected' || isUploading"
              class="w-10 h-10 flex items-center justify-center rounded-lg disabled:opacity-40 disabled:cursor-not-allowed hover:scale-105 transition-all flex-shrink-0"
              style="background: linear-gradient(135deg, #7C3AED 0%, #9F7AEA 100%);"
            >
              <svg v-if="isUploading" width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white animate-spin">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" opacity="0.25"/>
                <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" class="text-white">
                <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                <circle cx="8.5" cy="8.5" r="1.5" fill="currentColor"/>
                <path d="M21 15l-5-5L5 21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>

            <!-- Hidden File Input -->
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              @change="handleImageUpload"
              class="hidden"
            />

            <!-- Text Input -->
            <div class="flex-1 rounded-lg px-4 py-2" style="background-color: rgba(1, 5, 6, 0.6);">
              <textarea
                ref="textareaRef"
                v-model="message"
                @input="handleTextareaChange"
                @keydown="handleKeyPress"
                :placeholder="!isConnected ? 'Connect wallet to chat...' : 'Type your message...'"
                :disabled="!isConnected || connectionStatus !== 'connected'"
                class="w-full bg-transparent text-white placeholder-white/40 outline-none text-[14px] resize-none min-h-[24px] max-h-[100px]"
                style="font-family: 'PingFang HK';"
                rows="1"
              ></textarea>
            </div>

            <!-- Send Button -->
            <button
              @click="sendMessage"
              :disabled="!message.trim() || !isConnected || connectionStatus !== 'connected'"
              class="w-10 h-10 flex items-center justify-center rounded-lg disabled:opacity-40 disabled:cursor-not-allowed hover:scale-105 transition-all flex-shrink-0"
              style="background: linear-gradient(135deg, #7C3AED 0%, #9F7AEA 100%);"
            >
              <img src="/send.png" alt="Send" class="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </main>

    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import BottomNav from '../components/BottomNav.vue'
import Toast from '../components/Toast.vue'
import { useToast } from '@/composables/useToast'
import { useWallet } from '@/composables/useWallet'
import { useUnreadMessages } from '@/composables/useUnreadMessages'
import { getWalletData, getMessage, uploadImage } from '@/lib/api'

// Types
interface MessageContent {
  type: 'text' | 'image'
  content: string
}

interface Message {
  sender: 'user' | 'agent'
  content: MessageContent
  time: string
}

interface WalletData {
  id: number
  poolsId: number
  poolsOwnerId: number
  wallet: string
  eth: number
  usdc: number
  totalReward: number
  exchangeable: number
  accountBalance: number
}

interface HistoryMessage {
  id: string
  senderId: number
  reciverId: number
  content: string
  type: number
  status: number
  created: string
  modified: string | null
}

// Composables
const router = useRouter()
const toast = useToast()
const { isConnected } = useWallet()
const { unreadCount } = useUnreadMessages()

// State
const message = ref('')
const chatHistory = ref<Message[]>([
  {
    sender: 'agent',
    content: { type: 'text', content: "Hello! I'm your customer service assistant. How can I help you?" },
    time: getCurrentTime()
  }
])
const walletData = ref<WalletData | null>(null)
const ws = ref<WebSocket | null>(null)
const wsRetries = ref(0)
const connectionStatus = ref<'connecting' | 'connected' | 'disconnected' | 'error'>('disconnected')
const isUploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)
const chatContainer = ref<HTMLDivElement | null>(null)
const textareaRef = ref<HTMLTextAreaElement | null>(null)

// Constants
const maxRetries = 3
const reconnectDelay = 3000
let heartbeatInterval: NodeJS.Timeout | null = null

// Utility functions
function getCurrentTime(): string {
  const now = new Date()
  return now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

// Get relative time (e.g., "1 minute ago", "2 hours ago")
function getRelativeTime(timeString: string): string {
  try {
    // Parse the time string (HH:MM format)
    const [hours, minutes] = timeString.split(':').map(Number)
    const now = new Date()
    const messageTime = new Date()
    messageTime.setHours(hours, minutes, 0, 0)
    
    // If message time is in the future (crossed midnight), subtract a day
    if (messageTime > now) {
      messageTime.setDate(messageTime.getDate() - 1)
    }
    
    const diffMs = now.getTime() - messageTime.getTime()
    const diffSeconds = Math.floor(diffMs / 1000)
    const diffMinutes = Math.floor(diffSeconds / 60)
    const diffHours = Math.floor(diffMinutes / 60)
    const diffDays = Math.floor(diffHours / 24)
    
    if (diffSeconds < 60) {
      return 'Just now'
    } else if (diffMinutes < 60) {
      return diffMinutes === 1 ? '1 minute ago' : `${diffMinutes} minutes ago`
    } else if (diffHours < 24) {
      return diffHours === 1 ? '1 hour ago' : `${diffHours} hours ago`
    } else {
      return diffDays === 1 ? '1 day ago' : `${diffDays} days ago`
    }
  } catch (error) {
    return ''
  }
}

// Fetch chat history
async function fetchChatHistory(userId: number) {
  try {
    const response = await getMessage()
    
    if (response.success && response.data) {
      const historyMessages: Message[] = response.data.map((msg: HistoryMessage) => ({
        sender: msg.senderId === userId ? 'user' : 'agent',
        content: {
          type: msg.type === 1 ? 'image' : 'text',
          content: msg.content
        },
        time: new Date(msg.created).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      }))

      chatHistory.value = [
        {
          sender: 'agent',
          content: { type: 'text', content: "Hello! I'm your customer service assistant. How can I help you?" },
          time: getCurrentTime()
        },
        ...historyMessages
      ]
    }
  } catch (error) {
    console.error('Error fetching chat history:', error)
    toast.error('Failed to load chat history')
  }
}

// Initialize WebSocket
function initializeWebSocket(walletInfo: WalletData) {
  try {
    connectionStatus.value = 'connecting'
    const wsUrl = `wss://index.cloudko.org/ws/${walletInfo.id}`
    console.log('Attempting WebSocket connection to:', wsUrl)
    const socket = new WebSocket(wsUrl)

    socket.onopen = () => {
      console.log('WebSocket Connected Successfully')
      connectionStatus.value = 'connected'
      wsRetries.value = 0
      toast.success('Connected to customer service')

      // Start heartbeat
      heartbeatInterval = setInterval(() => {
        if (socket.readyState === WebSocket.OPEN) {
          try {
            socket.send(JSON.stringify({ type: 2, content: 'heartbeat' }))
          } catch (error) {
            console.error('Error sending heartbeat:', error)
          }
        }
      }, 5000)
    }

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        console.log('Received message:', data)
        
        if (data.type === 2) return // Ignore heartbeat messages

        chatHistory.value.push({
          sender: 'agent',
          content: { 
            type: data.type === 1 ? 'image' : 'text', 
            content: data.content 
          },
          time: getCurrentTime()
        })

        // Send read confirmation
        if (socket.readyState === WebSocket.OPEN) {
          const messageData = {
            id: `${walletInfo.id}-${new Date().getTime()}`,
            type: 3,
            flag: 0,
            senderId: walletInfo.id,
            reciverId: walletInfo.poolsOwnerId,
            content: 'read',
            created: getCurrentTime()
          }
         
          try {
            socket.send(JSON.stringify(messageData))
          } catch (error) {
            console.error('Error sending read message:', error)
          }
        }
      } catch (error) {
        console.error('Error parsing WebSocket message:', error)
      }
    }

    socket.onerror = (error) => {
      console.error('WebSocket Error:', error)
      connectionStatus.value = 'error'
      toast.error('Connection error occurred')
    }

    socket.onclose = (event) => {
      console.log('WebSocket Closed:', event)
      connectionStatus.value = 'disconnected'

      if (heartbeatInterval) {
        clearInterval(heartbeatInterval)
        heartbeatInterval = null
      }

      if (wsRetries.value < maxRetries && !event.wasClean) {
        setTimeout(() => {
          wsRetries.value++
          toast.warning(`Reconnecting... (${wsRetries.value}/${maxRetries})`)
          initializeWebSocket(walletInfo)
        }, reconnectDelay)
      } else if (wsRetries.value >= maxRetries) {
        chatHistory.value.push({
          sender: 'agent',
          content: {
            type: 'text',
            content: 'Connection to customer service is currently unavailable. Please try again later.'
          },
          time: getCurrentTime()
        })
        toast.error('Failed to connect to customer service')
      }
    }

    ws.value = socket
  } catch (error) {
    console.error('Error initializing WebSocket:', error)
    connectionStatus.value = 'error'
    toast.error('Failed to initialize chat connection')
  }
}

// Initialize chat
async function initializeChat() {
  if (!isConnected.value) {
    toast.error('Please connect your wallet first')
    return
  }

  try {
    toast.info('Initializing chat...')
    const response = await getWalletData()
    if (response.success && response.data) {
      console.log('Wallet data received:', response.data)
      const walletInfo = response.data
      walletData.value = walletInfo
      initializeWebSocket(walletInfo)
      await fetchChatHistory(walletInfo.id)
    } else {
      console.error('Failed to get wallet data:', response)
      chatHistory.value.push({
        sender: 'agent',
        content: {
          type: 'text',
          content: 'Unable to initialize chat. Please try again later.'
        },
        time: getCurrentTime()
      })
      toast.error('Failed to initialize chat')
    }
  } catch (error) {
    console.error('Failed to fetch wallet data:', error)
    toast.error('Failed to initialize chat')
  }
}

// Send message
function sendMessage() {
  if (message.value.trim() === '' || !ws.value || !walletData.value || connectionStatus.value !== 'connected') {
    if (!isConnected.value) {
      toast.error('Please connect your wallet first')
    } else if (connectionStatus.value !== 'connected') {
      toast.error('Please wait for connection to be established')
    }
    return
  }

  const messageData = {
    id: `${walletData.value.id}-${new Date().getTime()}`,
    type: 0,
    flag: 0,
    senderId: walletData.value.id,
    reciverId: walletData.value.poolsOwnerId,
    content: message.value,
    created: getCurrentTime()
  }

  const userMessage: Message = {
    sender: 'user',
    content: { type: 'text', content: message.value },
    time: getCurrentTime()
  }

  chatHistory.value.push(userMessage)
  message.value = ''

  if (textareaRef.value) {
    textareaRef.value.style.height = 'auto'
  }

  try {
    ws.value.send(JSON.stringify(messageData))
  } catch (error) {
    console.error('Error sending message:', error)
    toast.error('Failed to send message')
  }
}

// Handle image upload
async function handleImageUpload(e: Event) {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file || !ws.value || !walletData.value || connectionStatus.value !== 'connected') {
    if (!isConnected.value) {
      toast.error('Please connect your wallet first')
    } else if (connectionStatus.value !== 'connected') {
      toast.error('Please wait for connection to be established')
    }
    return
  }

  if (!file.type.startsWith('image/')) {
    toast.error('File must be an image')
    return
  }

  // Check file size (limit to 5MB)
  if (file.size > 5 * 1024 * 1024) {
    toast.error('Image size must be less than 5MB')
    return
  }

  try {
    isUploading.value = true
    toast.info('Uploading image...')
    
    const response = await uploadImage(file)
    if (response.success) {
      const imageUrl = response.url
      const messageData = {
        id: `${walletData.value.id}-${new Date().getTime()}`,
        type: 1,
        flag: 0,
        senderId: walletData.value.id,
        reciverId: walletData.value.poolsOwnerId,
        content: imageUrl,
        created: getCurrentTime()
      }

      const imageMessage: Message = {
        sender: 'user',
        content: { type: 'image', content: imageUrl },
        time: getCurrentTime()
      }

      chatHistory.value.push(imageMessage)

      if (fileInput.value) {
        fileInput.value.value = ''
      }

      ws.value.send(JSON.stringify(messageData))
      toast.success('Image sent successfully')
    } else {
      toast.error(response.msg || 'Failed to upload image')
    }
  } catch (error) {
    console.error('Error uploading image:', error)
    toast.error('Failed to upload image')
  } finally {
    isUploading.value = false
  }
}

// Handle textarea auto-resize
function handleTextareaChange(e: Event) {
  const textarea = e.target as HTMLTextAreaElement
  message.value = textarea.value
  textarea.style.height = 'auto'
  textarea.style.height = `${Math.min(textarea.scrollHeight, 100)}px`
}

// Handle Enter key press
function handleKeyPress(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// Trigger file input
function triggerFileInput() {
  if (fileInput.value && isConnected.value && connectionStatus.value === 'connected') {
    fileInput.value.click()
  }
}

// Open image in new tab
function openImage(url: string) {
  window.open(url, '_blank')
}

// Go back
function goBack() {
  router.back()
}

// Auto-scroll to bottom
function scrollToBottom() {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// Watch chat history for auto-scroll
watch(chatHistory, () => {
  scrollToBottom()
}, { deep: true })

// Initialize on mount
onMounted(() => {
  // 重置未读消息计数
  unreadCount.value = 0
  console.log('📭 已进入聊天页面，未读消息计数已重置')
  
  if (isConnected.value) {
    initializeChat()
  }
})

// Cleanup on unmount
onUnmounted(() => {
  if (heartbeatInterval) {
    clearInterval(heartbeatInterval)
  }
  if (ws.value) {
    ws.value.close()
  }
})
</script>

<style scoped>
/* Custom scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(115, 84, 255, 0.1);
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(115, 84, 255, 0.5);
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(115, 84, 255, 0.7);
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
  background-color: #010506;
  transform: rotate(45deg);
  box-shadow: 0 0 4px 0 rgba(243, 226, 255, 0.2);
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
  background: linear-gradient(135deg, #9F7AEA 0%, #C9BDFE 100%);
  transform: rotate(45deg);
}

/* Smooth animations */
.message-left,
.message-right {
  animation: fadeIn 0.3s ease-in-out;
}

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
</style>
