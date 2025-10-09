# 钱包连接与登录流程

## 完整流程说明

### 第一步：URL 验证（可选）
**位置**: `App.vue` - `onMounted` 钩子

1. 检查 URL 中是否有 `code` 参数（例如：`https://xxx.com?code=5687`）
2. 如果有 `code` 参数：
   - 调用 `checkReferralCode(code)` 接口验证邀请码
   - 验证成功：保存到 `localStorage` 并显示成功提示
   - 验证失败：显示错误提示
3. 如果没有 `code` 参数：
   - 用户可以直接访问网站（不显示警告）

### 第二步：用户点击"CONNECT FARMVILLE"按钮
**位置**: `HomePage.vue` - 按钮点击事件

1. 用户点击按钮触发 `handleConnectWallet()` 函数
2. 按钮状态变化：
   - 未连接：显示 "CONNECT FARMVILLE"（紫色渐变）
   - 连接中：显示 "CONNECTING..."（半透明）
   - 已连接：显示 "CONNECTED: 0x12...34"（绿色渐变）

### 第三步：钱包连接
**位置**: `useWallet.ts` - `connectWallet()` 函数

1. **检查钱包插件**
   - 检查是否安装 MetaMask 或其他以太坊钱包
   - 如果没有：显示错误提示 "Please install MetaMask or another Ethereum wallet!"

2. **请求账户访问**
   - 调用 `ethereum.request({ method: 'eth_requestAccounts' })`
   - 弹出 MetaMask 授权窗口
   - 用户确认或拒绝连接

3. **处理用户响应**
   - 用户拒绝：显示错误提示 "You rejected the wallet connection request"
   - 用户同意：获取钱包地址，继续下一步

### 第四步：自动登录
**位置**: `useWallet.ts` - `connectWallet()` 函数内部

1. **获取邀请码**
   - 优先从 URL 获取 `code` 参数
   - 如果 URL 没有，从 `localStorage` 获取已保存的邀请码
   - 如果都没有，使用空字符串

2. **调用登录接口**
   ```javascript
   login(walletAddress, referralCode, '')
   ```

3. **处理登录响应**
   - **成功**：
     - 保存 token 到 `localStorage`
     - 标记为已连接
     - 保存钱包地址和邀请码
     - 显示成功提示："Wallet connected and logged in successfully!"
   
   - **失败**：
     - 清除钱包连接状态
     - 显示错误提示（后端返回的错误信息）

## 数据流示意图

```
用户访问 URL
    ↓
[App.vue] 检查并验证 code 参数 ← checkReferralCode API
    ↓ (保存到 localStorage)
用户点击 CONNECT FARMVILLE
    ↓
[HomePage.vue] 触发 handleConnectWallet()
    ↓
[useWallet.ts] connectWallet() 开始
    ↓
检查钱包插件 → 没有? → 错误提示
    ↓ 有
请求账户访问 (MetaMask 弹窗)
    ↓
用户确认? → 拒绝? → 错误提示
    ↓ 同意
获取钱包地址 (0x123...456)
    ↓
获取邀请码 (URL 或 localStorage)
    ↓
调用 login API ← login(wallet, code, '')
    ↓
登录成功?
    ↓ 是
保存 token、地址、邀请码
    ↓
显示成功提示 ✅
    ↓
按钮变绿色显示 "CONNECTED: 0x12...34"
```

## API 接口说明

### 1. checkReferralCode
```typescript
// 请求
POST /stake/api/wallet/checkCode
Body: { code: "5687" }

// 响应
{
  success: boolean
  code: number
  msg: string
}
```

### 2. login
```typescript
// 请求
POST /stake/api/wallet/login
Body: {
  wallet: "0x123...",      // 钱包地址
  code: "5687",            // 邀请码（可选）
  inviterWallet: ""        // 邀请人钱包（暂未使用）
}

// 响应
{
  success: boolean
  code: number
  msg: string
  data: string             // token
  spender: string
  approve: boolean
}
```

## 状态管理

### localStorage 存储的数据
- `referralCode`: 邀请码
- `walletAddress`: 钱包地址
- `token`: 登录凭证

### useWallet 全局状态
- `address`: 当前连接的钱包地址
- `isConnecting`: 是否正在连接
- `isConnected`: 是否已连接
- `loginError`: 登录错误信息

## Toast 提示消息

### 成功提示（绿色）
- ✅ "Referral code verified successfully" - 邀请码验证成功
- ✅ "Wallet connected and logged in successfully!" - 钱包连接并登录成功

### 错误提示（红色）
- ❌ "Invalid referral code" - 邀请码无效
- ❌ "Failed to verify referral code" - 验证邀请码失败
- ❌ "Please install MetaMask or another Ethereum wallet!" - 未安装钱包
- ❌ "You rejected the wallet connection request" - 用户拒绝连接
- ❌ "Failed to connect wallet, please try again" - 连接失败
- ❌ "Login failed, please try again" - 登录失败

## 使用示例

### 带邀请码访问
```
用户访问: https://xxx.com?code=5687
1. 自动验证邀请码 5687
2. 用户点击 CONNECT FARMVILLE
3. 连接钱包并使用邀请码 5687 登录
```

### 不带邀请码访问
```
用户访问: https://xxx.com
1. 无邀请码验证
2. 用户点击 CONNECT FARMVILLE
3. 连接钱包并使用空邀请码登录
```

### 刷新页面（已连接钱包）
```
场景 A: 有 token 且地址未变化
1. autoConnect 检测到之前的连接
2. 检查到 token 存在 ⚡
3. 跳过 login 接口，直接使用现有 token
4. 恢复连接状态

场景 B: 无 token 或地址变化
1. autoConnect 检测到之前的连接
2. 没有 token 或地址已变化 🔄
3. 调用 login 接口重新登录
4. 登录成功：静默处理，保存新 token
5. 登录失败：显示错误提示
```

## 自动重连与登录功能

应用启动时（`HomePage.vue` 的 `onMounted`）：
1. 调用 `autoConnect()` 检查是否之前已连接
2. 如果 localStorage 中有保存的钱包地址且钱包仍授权访问
3. **自动恢复连接状态并重新调用 login 接口**
4. 登录成功：静默处理，不显示提示（用户体验更流畅）
5. 登录失败：显示错误提示

### 刷新页面的完整流程

```
用户刷新页面 (F5)
    ↓
[HomePage.vue] onMounted 触发
    ↓
调用 autoConnect()
    ↓
检查 localStorage 是否有保存的钱包地址
    ↓ 有
检查钱包是否仍然授权访问
    ↓ 是
🔄 检测到钱包已连接
    ↓
检查 localStorage 中的 token
    ↓
Token 存在?
    ↓ 是           ↓ 否
检查地址是否变化？  获取邀请码
    ↓ 否           ↓
⚡ 跳过 login     调用 login API
直接使用现有 token  ↓
                   ✅ 登录成功 → 保存 token
                   ❌ 登录失败 → 显示错误 Toast
```

## Token 管理逻辑

### 避免重复登录

系统会智能检测是否需要调用 login 接口：

1. **首次连接钱包**
   - 没有 token → 调用 login 接口
   - 保存 token 和钱包地址

2. **刷新页面**
   - 有 token 且地址未变化 → **跳过 login 接口** ⚡
   - 没有 token → 调用 login 接口
   - 地址变化 → 清除旧 token，调用 login 接口

3. **切换钱包账户**
   - 监听 `accountsChanged` 事件
   - 检测到地址变化 → 清除旧 token，标记为未登录
   - 需要用户重新点击连接按钮

### Token 验证流程

```
检查 localStorage 中的 token
    ↓
Token 存在?
    ↓ 是
检查钱包地址是否变化?
    ↓ 否
⚡ 跳过 login，直接使用现有 token
    ↓ 是
🔄 清除旧 token，调用 login 接口
```

## 注意事项

1. **Token 管理**：登录成功后的 token 会自动添加到所有后续 API 请求的 Header 中
2. **避免重复登录**：有 token 且地址未变化时，不会重复调用 login 接口
3. **账户切换**：监听 MetaMask 账户切换事件，自动清除旧 token 并要求重新登录
4. **网络切换**：监听链切换事件，自动刷新页面
5. **断开连接**：会同时清除 token、地址和连接状态
6. **邀请码优先级**：URL 中的 code > localStorage 中的 code > 空字符串
7. **lastLoginAddress**：内部记录上次登录的地址，用于检测地址变化

## 调试技巧

在浏览器控制台查看详细日志：

### 首次连接
- `检测到邀请码，准备调用 checkReferralCode`
- `✅ 钱包连接成功`
- `准备调用登录接口...`
- `✅ 登录成功`

### 刷新页面（有 token）
- `🔄 检测到钱包已连接...`
- `⚡ Token 已存在且地址未变化，跳过登录`

### 刷新页面（无 token）
- `🔄 检测到钱包已连接...`
- `📝 未找到 Token，准备自动登录...`
- `✅ 自动登录成功`

### 切换钱包账户
- `🔄 检测到钱包地址变化，需要重新登录`
- `🔄 钱包地址已变化，需要重新登录`
- `准备调用登录接口...`

查看 Network 标签确认 API 调用：
- `/stake/api/wallet/checkCode` - 验证邀请码
- `/stake/api/wallet/login` - 登录接口（只在必要时调用）

