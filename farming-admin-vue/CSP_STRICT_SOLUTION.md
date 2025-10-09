# 严格 CSP 策略实施方案

## 目标

实现完全禁用 `unsafe-eval` 的严格 CSP 策略，同时保证 Vue 项目正常运行。

## 已实施的解决方案

### 1. Vue Runtime-Only 配置

**文件：`build/webpack.base.conf.js`**
```javascript
resolve: {
  alias: {
    'vue$': 'vue/dist/vue.runtime.esm.js',  // 使用运行时版本
  }
}
```

**文件：`src/main.js`**
```javascript
new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)  // 使用 render 函数而不是 template
})
```

### 2. Webpack 严格配置

**Source Map 完全禁用：**
```javascript
// config/index.js
build: {
  productionSourceMap: false,
  devtool: false,
}

// build/webpack.prod.conf.js
devtool: false,  // 强制禁用所有 source map
```

**输出环境配置：**
```javascript
output: {
  globalObject: 'window',  // 明确指定全局对象
  environment: {
    arrowFunction: false,
    bigIntLiteral: false,
    const: false,
    destructuring: false,
    dynamicImport: false,
    forOf: false,
    module: false
  }
}
```

**目标环境：**
```javascript
target: ['web', 'es5'],  // 明确指定目标环境
```

### 3. 代码拆分优化

```javascript
optimization: {
  splitChunks: {
    chunks: 'all',
    cacheGroups: {
      default: false,
      vendors: false,
      vendor: {
        test: /[\\/]node_modules[\\/]/,
        name: 'vendors',
        chunks: 'all'
      }
    }
  }
}
```

### 4. 严格 CSP 策略

**生产环境 (`index.prod.html`)：**
```html
<meta http-equiv="Content-Security-Policy"
      content="default-src 'self';
               script-src 'self';
               style-src 'self' 'unsafe-inline';
               img-src 'self' data: blob:;
               font-src 'self' data:;
               connect-src 'self' https: wss: ws:;
               frame-src 'none';
               object-src 'none';
               base-uri 'self';
               form-action 'self';
               upgrade-insecure-requests;">
```

## 验证步骤

### 1. 构建验证
```bash
npm run build
```

### 2. 检查生成代码
```bash
# 检查是否包含 eval 或 new Function
grep -r "eval\|new Function" dist/static/js/

# 应该没有任何输出
```

### 3. CSP 违规检查
- 打开浏览器开发者工具
- 查看 Console 面板
- 确认没有 CSP 违规报告

### 4. 功能测试
- 页面正常加载
- 路由导航正常
- 组件交互正常
- API 请求正常

## 可能的问题和解决方案

### 问题1：第三方库不兼容
**症状：**
```
[Vue warn]: You are using the runtime-only build of Vue...
```

**解决方案：**
1. 查找库的预编译版本
2. 使用 render 函数重写组件
3. 替换为兼容的库

### 问题2：动态导入失败
**症状：**
```
ChunkLoadError: Loading chunk X failed
```

**解决方案：**
1. 禁用动态导入：`dynamicImport: false`
2. 使用静态导入
3. 预加载所有必要的 chunks

### 问题3：polyfill 问题
**症状：**
```
ReferenceError: globalThis is not defined
```

**解决方案：**
1. 使用 `globalObject: 'window'`
2. 添加必要的 polyfill
3. 降级 webpack 目标环境

### 问题4：样式处理问题
**症状：**
CSS 无法正常加载或应用

**解决方案：**
1. 确保 `style-src 'unsafe-inline'` 已配置
2. 检查 CSS 文件路径
3. 验证 MiniCssExtractPlugin 配置

## 性能影响

### 正面影响
- ✅ 更小的包体积（移除 Vue 编译器）
- ✅ 更快的启动时间（无运行时编译）
- ✅ 更好的安全性（严格 CSP）

### 负面影响
- ⚠️ 无法使用动态模板
- ⚠️ 某些第三方库可能不兼容
- ⚠️ 调试可能更困难（无 source map）

## 回退策略

如果遇到无法解决的兼容性问题：

### 1. 临时允许 unsafe-eval
```html
<meta http-equiv="Content-Security-Policy"
      content="script-src 'self' 'unsafe-eval'; ...">
```

### 2. 恢复 Vue 完整版
```javascript
'vue$': 'vue/dist/vue.esm.js',
```

### 3. 启用有限的 source map
```javascript
devtool: 'source-map',  // 不使用 eval 的 source map
```

## 最佳实践

### 1. 开发环境策略
- 开发环境可以使用相对宽松的 CSP
- 保持 `unsafe-eval` 以支持热重载
- 定期测试生产环境构建

### 2. 代码编写规范
- 优先使用 .vue 单文件组件
- 避免动态模板字符串
- 使用 render 函数处理复杂逻辑

### 3. 第三方库选择
- 优先选择支持 CSP 的库
- 检查库的 webpack 兼容性
- 避免使用过时的库

### 4. 持续监控
- 定期检查 CSP 违规报告
- 监控第三方库更新
- 测试新功能的 CSP 兼容性

## 总结

通过综合配置 Vue、webpack 和 CSP 策略，我们实现了：
1. **完全禁用 `unsafe-eval`**
2. **保持应用功能完整**
3. **最大化安全防护**
4. **优化性能表现**

这是一个平衡安全性和功能性的最佳实践方案。
