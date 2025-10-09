# Webpack DevTool 配置与 CSP 兼容性解决方案

## 问题分析

### 根本原因
Webpack 的 `devtool` 配置决定了如何生成 source map，某些配置会使用 `eval()` 函数，这与严格的 CSP 策略冲突。

### DevTool 选项对 eval() 的影响

| devtool 值 | 是否使用 eval | 适用环境 | CSP 兼容性 |
|------------|--------------|----------|------------|
| `eval` | ✅ 是 | 开发 | ❌ 需要 `unsafe-eval` |
| `eval-source-map` | ✅ 是 | 开发 | ❌ 需要 `unsafe-eval` |
| `cheap-eval-source-map` | ✅ 是 | 开发 | ❌ 需要 `unsafe-eval` |
| `source-map` | ❌ 否 | 生产 | ✅ CSP 友好 |
| `hidden-source-map` | ❌ 否 | 生产 | ✅ CSP 友好 |
| `nosources-source-map` | ❌ 否 | 生产 | ✅ CSP 友好 |
| `false` | ❌ 否 | 生产 | ✅ CSP 友好 |

## 解决方案

### 1. 配置修改

**config/index.js:**
```javascript
module.exports = {
  dev: {
    // 开发环境：允许 eval 用于快速构建和调试
    devtool: 'eval-source-map',
  },
  build: {
    // 生产环境：禁用 source map 避免 eval
    productionSourceMap: false,
    devtool: false, // 关键：设置为 false 而不是 'source-map'
  }
}
```

### 2. CSP 策略

**开发环境 (index.html):**
```html
<!-- 允许 unsafe-eval 用于 webpack-dev-server -->
<meta http-equiv="Content-Security-Policy"
      content="script-src 'self' 'unsafe-eval' 'unsafe-inline'; ...">
```

**生产环境 (index.prod.html):**
```html
<!-- 严格 CSP，不允许 eval -->
<meta http-equiv="Content-Security-Policy"
      content="script-src 'self'; ...">
```

### 3. 构建流程

**开发环境:**
```bash
npm run dev
# 使用 eval-source-map，需要 unsafe-eval
# 快速构建，便于调试
```

**生产环境:**
```bash
npm run build
# devtool: false，不使用 eval
# 体积更小，CSP 友好
```

## 技术细节

### Webpack 5 的模块加载
- 开发环境：使用 `eval()` 快速生成模块
- 生产环境：预编译模块，无需 `eval()`

### Source Map 权衡
| 环境 | 配置 | 优势 | 劣势 |
|------|------|------|------|
| 开发 | `eval-source-map` | 快速构建，完整调试信息 | 需要 `unsafe-eval` |
| 生产 | `false` | 体积小，CSP 友好，安全 | 无调试信息 |

### 替代调试方案
如果生产环境需要调试：
1. **独立构建**：单独生成 source map 文件
2. **条件加载**：根据需要动态启用
3. **外部工具**：使用外部错误监控服务

## 验证步骤

### 1. 检查构建输出
```bash
npm run build
# 检查生成的 JS 文件中是否包含 eval
grep -r "eval\|new Function" dist/static/js/
```

### 2. CSP 测试
```html
<!-- 测试页面 -->
<script>
try {
  eval('console.log("eval test")');
  console.log("❌ eval() 工作 - CSP 配置有问题");
} catch (e) {
  console.log("✅ eval() 被阻止 - CSP 配置正确");
}
</script>
```

### 3. 功能验证
- 页面正常加载
- JavaScript 功能正常
- 无 CSP 违规报告

## 安全评估

### 开发环境
- ⚠️ 允许 `unsafe-eval`：仅用于本地调试
- ✅ 其他限制正常：保持基本安全

### 生产环境
- ✅ 禁用 `unsafe-eval`：最高安全等级
- ✅ 完整 CSP 策略：防止 XSS 攻击
- ✅ 无调试信息泄露：减少攻击面

## 总结

通过合理配置 webpack 的 `devtool` 选项：
1. **解决了根本问题**：从源头消除 `eval()` 的使用
2. **保持开发体验**：开发环境仍可正常调试
3. **提升生产安全**：生产环境实现严格 CSP
4. **性能优化**：生产构建体积更小

这是比单纯放宽 CSP 策略更好的解决方案。
