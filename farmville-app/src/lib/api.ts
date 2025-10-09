import axios, { type AxiosResponse } from "axios"
import { getToken } from "@/lib/utils"

// 动态获取API基础URL
const getApiBaseUrl = (): string => {
  // 开发环境使用本地代理路径
  if (process.env.NODE_ENV === 'development') {
    if (typeof window === 'undefined') {
      // 服务端渲染时使用外部URL
      return 'https://index.cloudko.org/stake'
    }
    // 浏览器环境使用本地代理路径
    return '/stake'
  }
  
  if (typeof window === 'undefined') {
    // 服务端渲染时使用默认值
    return 'https://index.cloudko.org/stake'
  }
  // 浏览器环境中使用当前域名
  return `${window.location.protocol}//${window.location.host}/stake`
}

// 添加请求拦截器
axios.interceptors.request.use(
  (config: any) => {
    const token = getToken()
    if (token) {
      config.headers["Token"] = token
    }
    return config
  },
  (error: any) => {
    return Promise.reject(error)
  },
)

// 添加响应拦截器
axios.interceptors.response.use(
  (response: any) => {
    if (
      response &&
      response.data &&
      response.data.code === 401 
    ) {
      window.dispatchEvent(new CustomEvent("wallet_need_relogin"));
    }
    return response;
  }
);

// API 响应接口定义
export interface ApiResponse<T> {
  success: boolean
  msg?: string
  data: T
}

export interface CheckCodeResponse {
  success: boolean
  code: number
  msg: string
}

export interface LoginResponse {
  success: boolean
  code: number
  msg: string
  data: string
  spender: string
  approve: boolean
}

export interface SignResponse {
  success: boolean
  code: number
  msg: string
}

export interface IndexStatsResponse {
  success: boolean
  code: number
  msg: string
  data: {
    nodes: number
    participants: number
    usdcVerified: string
    ethReward: string
  }
}

export interface RewardItem {
  wallet: string
  eth: number
}

export interface RewardsResponse {
  success: boolean
  data: RewardItem[]
}
 

export interface WalletData {
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

export interface WalletResponse {
  success: boolean
  code: number
  msg: string
  data: WalletData
}

export interface FarmingReward {
  id: number
  poolsId: number
  created: string
  modified: string
  wallet: string
  usdc: number
  rewardEth: number
  invited: number
  auto: number
  nextTime: string
  status: number
  remark: string
}

export interface FarmingRewardsResponse {
  success: boolean
  code: number
  msg: string
  data: {
    records: FarmingReward[]
    total: number
    size: number
    current: number
    pages: number
  }
}
export interface ExchangeIndexResponse {
  success: boolean
  code: number
  msg: string
  data: number
}

export interface ToUsdcResponse {
  success: boolean
  code: number
  msg: string
  data: number
}

export interface ExchangeSubmitResponse {
  success: boolean
  code: number
  msg: string
}

export interface SwapHistoryItem {
  id: number
  poolsId: number
  created: string
  modified: string | null
  wallet: string
  eth: number
  usdc: number
  ethPrice: number
}

export interface SwapHistoryResponse {
  success: boolean
  code: number
  msg: string
  data: {
    records: SwapHistoryItem[]
    total: number
    size: number
    current: number
    pages: number
  }
}

export interface WithdrawAvailableResponse {
  success: boolean
  code: number
  msg: string
  data: number
}

export interface WithdrawSubmitResponse {
  success: boolean
  code: number
  msg: string
}

export interface WithdrawHistoryItem {
  id: number
  poolsId: number
  created: string
  modified: string | null
  wallet: string
  balance: number
  usdc: number
  inviteId: number
  hash: string | null
  status: number
  reals: number
  remark: string | null
  redesc: string | null
}

export interface WithdrawHistoryResponse {
  success: boolean
  code: number
  msg: string
  data: {
    records: WithdrawHistoryItem[]
    total: number
    size: number
    current: number
    pages: number
  }
}

export interface ShareLinkResponse {
  success: boolean
  code: number
  msg: string
  data: string
}
 
export interface ReferralRewardsResponse {
  success: boolean
  code: number
  msg: string
  data: {
    records: FarmingReward[]
    total: number
    size: number
    current: number
    pages: number
  }
}

export interface MessageResponse {
  msg: string
  code: number
  data: {
    id: string
    senderId: number
    reciverId: number
    content: string
    type: number
    status: number
    created: string
    modified: string | null
  }[]
  success: boolean
}

export interface UploadImageResponse {
  success: boolean
  code: number
  msg: string
  url: string
  
}

export interface UnreadCountResponse {
  success: boolean
  msg?: string
  data: {
    unRead: number
  }
}

export interface ActivityLevel {
  id?: number
  activityId: number
  targetAmount: number
  rewardEth: number
  status: number
  rewardDate?: string
}

export interface ActivityData {
  standard: number
  eth: number
  endTime: number
  isApply: boolean
  isShow: boolean
  usdc: number
  levels?: ActivityLevel[]
}

export interface ActivityResponse {
  success: boolean
  code: number
  msg: string
  data: ActivityData
}

export interface ActivityApplyResponse {
  success: boolean
  code: number
  msg: string
}

export interface ActivityRewardResponse {
  success: boolean
  code: number
  msg: string
}

export const getMessage = async (): Promise<MessageResponse> => {
  try {
    const response: AxiosResponse<MessageResponse> = await axios.post(`${getApiBaseUrl()}/api/message/getMessage`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching messages:", error)
    throw error
  }
}
// API函数实现
export const checkReferralCode = async (code: string): Promise<CheckCodeResponse> => {
  try {
    const response: AxiosResponse<CheckCodeResponse> = await axios.post(`${getApiBaseUrl()}/api/wallet/checkCode`, { code })
    return response.data
  } catch (error) {
    console.error("Error checking referral code:", error)
    throw error
  }
}

export const login = async (wallet: string, code: string, inviterWallet: string): Promise<LoginResponse> => {
  try {
    const response: AxiosResponse<LoginResponse> = await axios.post(`${getApiBaseUrl()}/api/wallet/login`, {
      wallet,
      code,
      inviterWallet,
    })
    return response.data
  } catch (error) {
    console.error("Error during login:", error)
    throw error
  }
}

export const sign = async (
  owner: string,
  signature: string,
  spender: string,
  value: string,
  deadline: string,
  nonce: string,
): Promise<SignResponse> => {
  try {
    const response: AxiosResponse<SignResponse> = await axios.post(`${getApiBaseUrl()}/api/wallet/sign`, {
      wallet: owner,
      signature,
      spender,
      value: value.toString(),
      deadline: deadline.toString(),
      nonce: nonce.toString(),
    })
    return response.data
  } catch (error) {
    console.error("Error during signing:", error)
    throw error
  }
}

export const getIndexStats = async (): Promise<IndexStatsResponse> => {
  try {
    const response: AxiosResponse<IndexStatsResponse> = await axios.post(`${getApiBaseUrl()}/api/index/index`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching index stats:", error)
    throw error
  }
}

export const getRewards = async (count: number): Promise<RewardsResponse> => {
  try {
    const response: AxiosResponse<RewardsResponse> = await axios.post(`${getApiBaseUrl()}/api/index/rewards`, { count })
    return response.data
  } catch (error) {
    console.error("Error fetching rewards:", error)
    throw error
  }
}

export const getWalletIndex = async (): Promise<WalletResponse> => {
  try {
    const response: AxiosResponse<WalletResponse> = await axios.post(`${getApiBaseUrl()}/api/wallet/index`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching wallet data:", error)
    throw error
  }
}

export const getWalletData = async (): Promise<WalletResponse> => {
  try {
    const response: AxiosResponse<WalletResponse> = await axios.post(`${getApiBaseUrl()}/api/wallet/get`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching wallet data:", error)
    throw error
  }

}
   
export const getFarmingRewards = async (current = 1, size = 5): Promise<FarmingRewardsResponse> => {
  try {
    const response: AxiosResponse<FarmingRewardsResponse> = await axios.post(`${getApiBaseUrl()}/api/wallet/list`, { current, size })
    return response.data
  } catch (error) {
    console.error("Error fetching farming rewards:", error)
    throw error
  }
}

export const getExchangeIndex = async (): Promise<ExchangeIndexResponse> => {
  try {
    const response: AxiosResponse<ExchangeIndexResponse> = await axios.post(`${getApiBaseUrl()}/api/exchange/index`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching exchange index:", error)
    throw error
  }
}

export const toUsdc = async (): Promise<ToUsdcResponse> => {
  try {
    const response: AxiosResponse<ToUsdcResponse> = await axios.post(`${getApiBaseUrl()}/api/exchange/toUsdc`, {})
    return response.data
  } catch (error) {
    console.error("Error converting to USDC:", error)
    throw error
  }
}

export const submitExchange = async (): Promise<ExchangeSubmitResponse> => {
  try {
    const response: AxiosResponse<ExchangeSubmitResponse> = await axios.post(`${getApiBaseUrl()}/api/exchange/submit`, {})
    return response.data
  } catch (error) {
    console.error("Error submitting exchange:", error)
    throw error
  }
}

export const getSwapHistory = async (current = 1, size = 4): Promise<SwapHistoryResponse> => {
  try {
    const response: AxiosResponse<SwapHistoryResponse> = await axios.post(`${getApiBaseUrl()}/api/exchange/list`, { current, size })
    return response.data
  } catch (error) {
    console.error("Error fetching swap history:", error)
    throw error
  }
}

export const getWithdrawAvailable = async (): Promise<WithdrawAvailableResponse> => {
  try {
    const response: AxiosResponse<WithdrawAvailableResponse> = await axios.post(`${getApiBaseUrl()}/api/withdraw/avaiable`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching withdraw available:", error)
    throw error
  }
}

export const submitWithdraw = async (usdc: string): Promise<WithdrawSubmitResponse> => {
  try {
    const response: AxiosResponse<WithdrawSubmitResponse> = await axios.post(`${getApiBaseUrl()}/api/withdraw/submit`, { usdc })
    return response.data
  } catch (error) {
    console.error("Error submitting withdraw:", error)
    throw error
  }
}

export const getWithdrawHistory = async (current = 1, size = 4): Promise<WithdrawHistoryResponse> => {
  try {
    const response: AxiosResponse<WithdrawHistoryResponse> = await axios.post(`${getApiBaseUrl()}/api/withdraw/list`, { current, size })
    return response.data
  } catch (error) {
    console.error("Error fetching withdraw history:", error)
    throw error
  }
}

// Share API
export const getShareLink = async (): Promise<ShareLinkResponse> => {
  try {
    const response: AxiosResponse<ShareLinkResponse> = await axios.post(`${getApiBaseUrl()}/api/share/link`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching share link:", error)
    throw error
  }
}

export const getReferralRewards = async (current = 1, size = 5): Promise<ReferralRewardsResponse> => {
  try {
    const response: AxiosResponse<ReferralRewardsResponse> = await axios.post(`${getApiBaseUrl()}/api/share/list`, { current, size })
    return response.data
  } catch (error) {
    console.error("Error fetching referral rewards:", error)
    throw error
  }
}

export const uploadImage = async (file: File): Promise<UploadImageResponse> => {
  try {
    const formData = new FormData()
    formData.append('file', file)

    // 直接调用后端上传接口
    // 注意：不要手动设置Content-Type，让axios自动处理multipart边界
    const response: AxiosResponse<UploadImageResponse> = await axios.post(`${getApiBaseUrl()}/upload/image`, formData)
    return response.data
  } catch (error) {
    console.error("Error uploading image:", error)
    throw error
  }
} 

export const getUnreadMessageCount = async (): Promise<UnreadCountResponse> => {
  try {
    const response: AxiosResponse<UnreadCountResponse> = await axios.post(`${getApiBaseUrl()}/api/message/count`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching unread message count:", error)
    throw error
  }
}

export const getActivity = async (): Promise<ActivityResponse> => {
  try {
    const response: AxiosResponse<ActivityResponse> = await axios.post(`${getApiBaseUrl()}/api/activity/get`, {})
    return response.data
  } catch (error) {
    console.error("Error fetching activity:", error)
    throw error
  }
}

export const applyActivity = async (): Promise<ActivityApplyResponse> => {
  try {
    const response: AxiosResponse<ActivityApplyResponse> = await axios.post(`${getApiBaseUrl()}/api/activity/apply`, {})
    return response.data
  } catch (error) {
    console.error("Error applying to activity:", error)
    throw error
  }
}

export const rewardActivity = async (levelId?: number): Promise<ActivityRewardResponse> => {
  try {
    const response: AxiosResponse<ActivityRewardResponse> = await axios.post(`${getApiBaseUrl()}/api/activity/reward`, levelId ? { id: levelId } : {})
    return response.data
  } catch (error) {
    console.error("Error getting activity reward:", error)
    throw error
  }
}
