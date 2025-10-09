import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

// 全局保存的 URL 参数
const savedParams = ref<Record<string, string>>({})

export const useUrlParams = () => {
  const router = useRouter()
  const route = useRoute()

  // 初始化时从 URL 获取参数并保存
  const initializeParams = () => {
    const urlParams = new URLSearchParams(window.location.search)
    const params: Record<string, string> = {}
    
    urlParams.forEach((value, key) => {
      params[key] = value
    })
    
    if (Object.keys(params).length > 0) {
      savedParams.value = { ...params }
      console.log('🔗 保存 URL 参数:', savedParams.value)
    }
  }

  // 获取带参数的路径
  const getPathWithParams = (path: string): string => {
    if (Object.keys(savedParams.value).length === 0) {
      return path
    }
    
    const params = new URLSearchParams(savedParams.value)
    return `${path}?${params.toString()}`
  }

  // 导航到指定路径（带参数）
  const navigateWithParams = (path: string) => {
    if (Object.keys(savedParams.value).length === 0) {
      router.push(path)
    } else {
      router.push({
        path: path,
        query: savedParams.value
      })
    }
  }

  // 获取保存的参数
  const getParams = () => {
    return savedParams.value
  }

  // 获取特定参数值
  const getParam = (key: string): string | null => {
    return savedParams.value[key] || null
  }

  return {
    initializeParams,
    getPathWithParams,
    navigateWithParams,
    getParams,
    getParam,
    savedParams
  }
}

