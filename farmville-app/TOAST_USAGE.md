# Toast 提示组件使用说明

## 概述

全局 Toast 提示组件用于显示成功、错误、警告和信息提示。

## 文件结构

```
src/
├── composables/
│   └── useToast.ts          # Toast 状态管理
├── components/
│   └── Toast.vue            # Toast UI 组件
└── App.vue                  # 已集成 Toast 组件
```

## 基本使用

### 1. 在任何 Vue 组件中使用

```vue
<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const toast = useToast()

// 成功提示
const handleSuccess = () => {
  toast.success('Operation completed successfully')
}

// 错误提示
const handleError = () => {
  toast.error('Something went wrong')
}

// 警告提示
const handleWarning = () => {
  toast.warning('Please check your input')
}

// 信息提示
const handleInfo = () => {
  toast.info('This is an information message')
}
</script>
```

### 2. 自定义持续时间

```typescript
// 默认 3 秒
toast.success('Success message')

// 自定义 5 秒
toast.success('Success message', 5000)

// 不自动关闭
toast.error('Critical error', 0)
```

### 3. 完整示例

```vue
<template>
  <div>
    <button @click="handleSubmit">Submit</button>
  </div>
</template>

<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const toast = useToast()

const handleSubmit = async () => {
  try {
    // 执行某些操作
    await someApiCall()
    toast.success('Data submitted successfully')
  } catch (error) {
    toast.error('Failed to submit data')
  }
}
</script>
```

## API 参考

### useToast()

返回以下方法：

| 方法 | 参数 | 说明 |
|------|------|------|
| `success(message, duration?)` | message: string, duration?: number | 显示成功提示 |
| `error(message, duration?)` | message: string, duration?: number | 显示错误提示 |
| `warning(message, duration?)` | message: string, duration?: number | 显示警告提示 |
| `info(message, duration?)` | message: string, duration?: number | 显示信息提示 |
| `showToast(message, type, duration?)` | message: string, type: ToastType, duration?: number | 通用方法 |
| `removeToast(id)` | id: number | 手动移除指定提示 |

### ToastType

```typescript
type ToastType = 'success' | 'error' | 'warning' | 'info'
```

## 样式说明

Toast 组件使用以下颜色方案：

- **Success**: 绿色 (`green-500`)
- **Error**: 红色 (`red-500`)
- **Warning**: 黄色 (`yellow-500`)
- **Info**: 蓝色 (`blue-500`)

## 现有集成

Toast 组件已在 `App.vue` 中集成，用于以下场景：

1. ✅ **验证成功**: `Referral code verified successfully`
2. ❌ **验证失败**: `Invalid referral code`
3. ⚠️ **缺少参数**: `Invalid URL: Missing referral code parameter`
4. ❌ **网络错误**: `Failed to verify referral code`

## 注意事项

1. Toast 组件已在 `App.vue` 中全局集成，无需在每个页面重复添加
2. 所有 toast 都会自动在指定时间后消失（默认 3 秒）
3. 用户可以点击 toast 或点击关闭按钮手动关闭
4. Toast 显示在页面顶部居中位置，使用 `z-index: 9999` 确保在最上层

