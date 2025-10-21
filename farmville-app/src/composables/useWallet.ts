import { ref, computed } from 'vue'
import { checkReferralCode, login } from '@/lib/api'
import { setToken, delToken } from '@/lib/utils'

// 全局状态
const address = ref<string>('')
const isConnecting = ref(false)
const isConnected = ref(false)
const loginError = ref<string>('')
const hasLoggedIn = ref<string>('') // 记录已登录的地址（防止重复登录）
const isLoggingIn = ref(false) // 防止并发登录
const hasAttemptedAutoConnect = ref(false) // 防止重复自动连接
const autoConnectTimeout = ref<ReturnType<typeof setTimeout> | null>(null) // 自动连接防抖定时器
const approve = ref<boolean>(false) // 记录 approve 状态
const spender = ref<string>('') // 记录 spender 地址

// 检查是否有 MetaMask 或其他钱包
const hasWalletProvider = computed(() => {
  return typeof window !== 'undefined' && typeof (window as any).ethereum !== 'undefined'
})

// 格式化地址显示
export const formatAddress = (addr: string) => {
  if (!addr) return ''
  return `${addr.substring(0, 4)}...${addr.substring(addr.length - 2)}`
}

// 处理登录逻辑
const handleLogin = async (currentAddress: string, onSuccess?: () => void, onError?: (error: string) => void) => {
  console.log('=== handleLogin called ===', currentAddress)
  
  // 防止并发登录
  if (isLoggingIn.value) {
    console.log('Already logging in, skipping...')
    return
  }
  
  // 防止重复登录同一地址
  if (hasLoggedIn.value === currentAddress) {
    console.log('⏭️ SKIP: handleLogin already called for this address')
    return
  }
  
  isLoggingIn.value = true
  
  try {
    console.log('Starting login process for:', currentAddress)
    console.log('✅ FORCE LOGIN: Will always execute login')
    
    // 获取 referral code
    const urlParams = new URLSearchParams(window.location.search)
    let code = urlParams.get('code')
    console.log('URL code parameter:', code)
    
    if (!code) {
      code = localStorage.getItem('referralCode')
      console.log('LocalStorage referral code:', code)
    }
    
    if (!code) {
      console.log('No referral code found')
      const errorMsg = 'Please enter a valid URL with referral code.'
      loginError.value = errorMsg
      if (onError) onError(errorMsg)
      // 断开钱包连接
      disconnectWallet()
      return
    }
    
    console.log('Using referral code:', code)
    
    // 验证 referral code
    const response = await checkReferralCode(code)
    console.log('Code check response:', response)
    
    if (!response.success) {
      console.log('Invalid referral code, disconnecting wallet')
      const errorMsg = 'Invalid referral code. Please check your URL.'
      loginError.value = errorMsg
      if (onError) onError(errorMsg)
      // 断开钱包连接
      disconnectWallet()
      return
    }
    
    const inviterWallet = urlParams.get('referral') || ''
    console.log('🚀 === CALLING LOGIN API ===')
    console.log('🚀 Wallet:', currentAddress)
    console.log('🚀 Code:', code)
    console.log('🚀 Inviter:', inviterWallet)
    
    const loginResponse = await login(currentAddress, code, inviterWallet)
    console.log('📥 Login API response:', loginResponse)
    
    if (loginResponse.success) {
      console.log('Login successful')
      
      // 保存 token
      if (loginResponse.data) {
        setToken(loginResponse.data)
      }
      
      // 保存 approve 状态
      approve.value = loginResponse.approve || false
      console.log('Approve 状态:', approve.value)
      
      // 保存 spender 地址
      if (loginResponse.spender) {
        spender.value = loginResponse.spender
        localStorage.setItem('spender', loginResponse.spender)
        console.log('Spender 地址:', spender.value)
      }
      
      // 保存 localStorage
      localStorage.setItem('walletAddress', currentAddress)
      localStorage.setItem('referralCode', code)
      localStorage.setItem('approve', loginResponse.approve ? '1' : '0')
      
      // 标记为已连接和已登录
      isConnected.value = true
      hasLoggedIn.value = currentAddress
      
      if (onSuccess) onSuccess()
    } else {
      console.log('Login failed:', loginResponse.msg)
      const errorMsg = loginResponse.msg || 'Login failed. Please try again.'
      loginError.value = errorMsg
      if (onError) onError(errorMsg)
    }
  } catch (error: any) {
    console.log('Login error:', error)
    console.error('Login error:', error)
    const errorMsg = error.response?.data?.msg || error.message || 'Login failed. Please try again.'
    loginError.value = errorMsg
    if (onError) onError(errorMsg)
  } finally {
    isLoggingIn.value = false
  }
}

// 连接钱包并登录
export const connectWallet = async (onSuccess?: () => void, onError?: (error: string) => void) => {
  if (!hasWalletProvider.value) {
    const errorMsg = 'Please install MetaMask or another Ethereum wallet!'
    loginError.value = errorMsg
    if (onError) onError(errorMsg)
    return
  }

  console.log('=== User initiated wallet connection ===')
  
  // 在连接钱包之前检查是否有有效的 code 参数
  const urlParams = new URLSearchParams(window.location.search)
  let code = urlParams.get('code')
  if (!code) {
    code = localStorage.getItem('referralCode')
  }
  if (!code) {
    console.log('No referral code found before connecting wallet')
    const errorMsg = 'Please enter a valid URL with referral code.'
    loginError.value = errorMsg
    if (onError) onError(errorMsg)
    return
  }
  
  // 验证 referral code 的有效性
  console.log('Validating referral code before connecting:', code)
  try {
    const codeValidation = await checkReferralCode(code)
    if (!codeValidation.success) {
      console.log('Invalid referral code before connecting')
      const errorMsg = 'Invalid referral code. Please check your URL.'
      loginError.value = errorMsg
      if (onError) onError(errorMsg)
      return
    }
  } catch (error) {
    console.log('Error validating referral code:', error)
    const errorMsg = 'Unable to validate referral code. Please try again.'
    loginError.value = errorMsg
    if (onError) onError(errorMsg)
    return
  }

  isConnecting.value = true
  loginError.value = ''

  try {
    const ethereum = (window as any).ethereum
    
    // 请求账户访问
    const accounts = await ethereum.request({ 
      method: 'eth_requestAccounts' 
    })

    if (accounts && accounts.length > 0) {
      const walletAddress = accounts[0]
      address.value = walletAddress
      
      console.log('✅ 钱包连接成功:', walletAddress)
      console.log('🚀 Setting address to trigger login:', walletAddress)
      
      // 调用 handleLogin 处理登录
      await handleLogin(walletAddress, onSuccess, onError)
    }
  } catch (error: any) {
    console.error('❌ 连接钱包失败:', error)
    
    let errorMsg = 'Failed to connect wallet, please try again'
    if (error.code === 4001) {
      errorMsg = 'You rejected the wallet connection request'
    } else if (error.message) {
      errorMsg = error.message
    }
    
    loginError.value = errorMsg
    if (onError) onError(errorMsg)
  } finally {
    isConnecting.value = false
  }
}

// 断开连接
export const disconnectWallet = () => {
  console.log('=== Disconnecting wallet ===')
  
  // 清除防抖定时器
  if (autoConnectTimeout.value) {
    clearTimeout(autoConnectTimeout.value)
    autoConnectTimeout.value = null
  }
  
  // 重置所有状态
  address.value = ''
  isConnected.value = false
  loginError.value = ''
  hasLoggedIn.value = ''
  approve.value = false
  spender.value = ''
  hasAttemptedAutoConnect.value = false // 重置自动连接标志
  
  // 清理存储
  delToken()
  localStorage.removeItem('walletAddress')
  localStorage.removeItem('spender')
  localStorage.removeItem('approve')
  
  console.log('Wallet disconnected and tokens cleared')
}

// 监听账户变化
if (typeof window !== 'undefined' && (window as any).ethereum) {
  const ethereum = (window as any).ethereum
  
  ethereum.on('accountsChanged', (accounts: string[]) => {
    console.log('🔄 检测到账户变化:', accounts)
    
    if (accounts.length === 0) {
      console.log('🔄 账户已断开，执行断开连接')
      disconnectWallet()
    } else {
      const newAddress = accounts[0]
      
      // 如果地址没有变化，不需要重新登录
      if (address.value === newAddress && hasLoggedIn.value === newAddress && isConnected.value) {
        console.log('🔄 地址未变化且已登录，跳过重新登录')
        return
      }
      
      // 如果地址变化，清除登录状态
      if (address.value && address.value !== newAddress) {
        console.log('🔄 检测到钱包地址变化，清除登录状态')
        delToken()
        isConnected.value = false
        hasLoggedIn.value = ''
        approve.value = false
        spender.value = ''
        localStorage.removeItem('spender')
        localStorage.removeItem('approve')
      }
      
      address.value = newAddress
      localStorage.setItem('walletAddress', newAddress)
      
      // 只有在地址变化且不等于已登录地址时才自动调用登录
      if (newAddress && hasLoggedIn.value !== newAddress) {
        console.log('🔄 地址已变化，自动调用登录')
        // 使用 setTimeout 避免在事件处理中直接调用异步函数
        setTimeout(() => {
          handleLogin(newAddress)
        }, 100)
      }
    }
  })

  ethereum.on('chainChanged', () => {
    console.log('🔄 检测到网络变化，重新加载页面')
    window.location.reload()
  })
}

// 自动连接（如果之前已连接）
export const autoConnect = async (onSuccess?: () => void, onError?: (error: string) => void) => {
  if (!hasWalletProvider.value) {
    console.log('Auto-connect: No wallet provider')
    return
  }
  
  // 防止重复自动连接
  if (hasAttemptedAutoConnect.value) {
    console.log('Auto-connect: Already attempted, skipping')
    return
  }

  // 清除之前的防抖定时器
  if (autoConnectTimeout.value) {
    clearTimeout(autoConnectTimeout.value)
    autoConnectTimeout.value = null
  }

  // 使用防抖机制，延迟执行自动连接
  autoConnectTimeout.value = setTimeout(async () => {
    // 标记已尝试自动连接
    hasAttemptedAutoConnect.value = true

    try {
      const ethereum = (window as any).ethereum
      isConnecting.value = true
      
      // 首先用 eth_accounts 检查是否已连接（不会弹窗）
      const accounts = await ethereum.request({ method: 'eth_accounts' })
      
      if (accounts && accounts.length > 0) {
        const walletAddress = accounts[0]
        
        // 检查是否已经登录过这个地址
        if (hasLoggedIn.value === walletAddress && isConnected.value) {
          console.log('Auto-connect: Already logged in with this address, skipping')
          address.value = walletAddress
          if (onSuccess) onSuccess()
          return
        }
        
        address.value = walletAddress
        
        console.log('Auto-connect: Address set, will let handleLogin handle login', walletAddress)
        
        // 调用 handleLogin 处理登录
        await handleLogin(walletAddress, onSuccess, onError)
      } else {
        console.log('Auto-connect: No accounts found, checking if should auto-connect')
        
        // 检查是否有有效的 referral code，如果有则自动弹出连接请求
        const urlParams = new URLSearchParams(window.location.search)
        let code = urlParams.get('code')
        if (!code) {
          code = localStorage.getItem('referralCode')
        }
        
        if (code) {
          console.log('Auto-connect: Found referral code, attempting auto-connect')
          try {
            // 验证 referral code 有效性
            const codeValidation = await checkReferralCode(code)
            if (codeValidation.success) {
              console.log('✅ URL validation passed, auto-connecting wallet...')
              // 立即自动弹出连接请求
              await connectWallet(onSuccess, onError)
            } else {
              console.log('❌ Invalid referral code, skipping auto-connect')
            }
          } catch (error) {
            console.log('❌ Error validating referral code:', error)
          }
        } else {
          console.log('⚠️ No referral code found, skipping auto-connect')
        }
      }
    } catch (error) {
      console.error('Auto connect error:', error)
    } finally {
      isConnecting.value = false
    }
  }, 500) // 延迟500ms执行，避免重复调用
}

// 强制刷新登录状态（用于 sign 成功后更新 approve）
export const refreshLoginStatus = async () => {
  if (!address.value) {
    console.log('❌ 没有连接的钱包地址')
    return false
  }

  try {
    console.log('🔄 强制刷新登录状态...')
    
    // 临时清除 hasLoggedIn 以允许重新登录
    const tempAddress = hasLoggedIn.value
    hasLoggedIn.value = ''
    
    // 调用 handleLogin
    await handleLogin(address.value, () => {
      console.log('✅ 登录状态刷新成功')
    }, (error) => {
      console.error('❌ 刷新登录状态失败:', error)
      // 如果失败，恢复原地址
      hasLoggedIn.value = tempAddress
    })
    
    return true
  } catch (error) {
    console.error('❌ 刷新登录状态出错:', error)
    return false
  }
}

// 导出 composable
export function useWallet() {
  return {
    address,
    isConnecting,
    isConnected,
    hasWalletProvider,
    loginError,
    approve,
    spender,
    connectWallet,
    disconnectWallet,
    autoConnect,
    refreshLoginStatus,
    formatAddress,
  }
}


