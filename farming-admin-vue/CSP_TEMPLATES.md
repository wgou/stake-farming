# CSP 双模板解决方案

## 概述

为了解决开发和生产环境的不同CSP需求，我们采用了双模板解决方案：

- **开发环境** (`index.html`) - 允许eval用于webpack-dev-server
- **生产环境** (`index.prod.html`) - 严格安全策略，禁用eval

## 文件结构

```
项目根目录/
├── index.html              # 开发环境模板（允许eval）
├── index.prod.html         # 生产环境模板（严格模式）
├── dist/
│   └── index.html          # 构建后的生产环境文件
└── build/
    └── webpack.prod.conf.js # 生产构建配置
```

## 模板对比

### 开发环境模板 (`index.html`)

```html
<!-- Content Security Policy - Development Mode (Allows eval for webpack-dev-server) -->
<meta http-equiv="Content-Security-Policy"
      content="default-src 'self';
               script-src 'self' 'unsafe-eval' 'unsafe-inline';
               style-src 'self' 'unsafe-inline';
               ...">
```

**特点**：
- ✅ 允许 `unsafe-eval` - 支持webpack-dev-server热重载
- ✅ 允许 `unsafe-inline` - 支持内联样式和脚本
- ⚠️ 安全性较低，仅用于开发

### 生产环境模板 (`index.prod.html`)

```html
<!-- Content Security Policy - Production Mode (Compatible with qs library) -->
<meta http-equiv="Content-Security-Policy"
      content="default-src 'self';
               script-src 'self' 'unsafe-eval';
               style-src 'self' 'unsafe-inline';
               ...
               upgrade-insecure-requests;">
```

**特点**：
- ⚠️ 允许 `unsafe-eval` - 兼容qs库等第三方依赖
- ✅ 允许 `unsafe-inline` - 必要的样式支持
- 🔒 启用 `upgrade-insecure-requests` - 强制HTTPS
- 🛡️ 其他安全限制仍然有效

## 配置说明

### Webpack生产配置

```javascript
// build/webpack.prod.conf.js
new HtmlWebpackPlugin({
  template: 'index.prod.html', // 使用生产环境模板
  // ... 其他配置
})
```

### 开发环境配置

开发环境继续使用 `index.html` 模板，webpack-dev-server会自动处理。

## 安全策略详解

### 开发环境策略

| 指令 | 值 | 说明 |
|------|-----|------|
| `script-src` | `'self' 'unsafe-eval' 'unsafe-inline'` | 允许eval用于热重载 |
| `style-src` | `'self' 'unsafe-inline'` | 允许内联样式 |
| `frame-src` | `'none'` | 禁止iframe嵌入 |

### 生产环境策略

| 指令 | 值 | 说明 |
|------|-----|------|
| `script-src` | `'self' 'unsafe-eval'` | 允许同源脚本和eval（兼容qs库） |
| `style-src` | `'self' 'unsafe-inline'` | 允许内联样式 |
| `frame-src` | `'none'` | 禁止iframe嵌入 |
| `upgrade-insecure-requests` | `[]` | 强制HTTPS |

## 构建流程

### 开发环境
```bash
npm run dev
# 使用 index.html 模板
# 允许eval，支持热重载
```

### 生产环境
```bash
npm run build
# 使用 index.prod.html 模板
# 严格CSP，禁用eval
```

## 优势

### 1. 环境隔离
- 开发环境：功能优先，支持热重载
- 生产环境：安全优先，禁用危险功能

### 2. 自动化切换
- 无需手动修改配置
- 构建时自动选择正确模板

### 3. 安全性保证
- 生产环境兼容第三方库的eval需求
- 保持其他安全限制
- 符合现代浏览器安全标准

### 4. 开发体验
- 开发环境保持热重载功能
- 不影响开发效率

## 测试建议

### 开发环境测试
1. 启动开发服务器：`npm run dev`
2. 检查热重载是否正常工作
3. 确认没有CSP错误

### 生产环境测试
1. 构建项目：`npm run build`
2. 检查生成的HTML文件
3. 确认CSP策略正确应用
4. 测试所有功能是否正常

## 注意事项

⚠️ **重要提醒**：
- 开发环境模板仅用于本地开发
- 生产环境模板用于实际部署
- 不要在生产环境使用开发模板
- 定期检查CSP策略的有效性

## 故障排除

### 开发环境问题
- **热重载不工作**：检查CSP是否包含 `unsafe-eval`
- **样式问题**：检查CSP是否包含 `unsafe-inline`

### 生产环境问题
- **功能异常**：检查是否误用了开发模板
- **CSP错误**：检查浏览器控制台错误信息

## 总结

这个双模板解决方案完美解决了开发和生产环境的不同需求：

- ✅ 开发环境：功能完整，支持热重载
- ✅ 生产环境：兼容第三方库，保持安全
- ✅ 自动化：无需手动切换
- ✅ 可维护：配置清晰，易于理解

## 问题解决

### 第三方库兼容性
- **问题**：`qs` 库在生产环境中使用 `eval()`
- **解决方案**：在生产环境CSP中允许 `unsafe-eval`
- **影响**：保持功能完整性，同时维持其他安全限制
