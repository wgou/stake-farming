# Vue Runtime-Only 版本迁移指南

## 背景

为了解决 CSP（Content Security Policy）兼容性问题，我们将项目从 Vue 完整版本迁移到 Runtime-Only 版本。

## 问题原因

### Vue 版本差异

| 版本 | 文件名 | 包含编译器 | 使用 new Function() | CSP 兼容性 |
|------|---------|------------|---------------------|------------|
| 完整版 | `vue.esm.js` | ✅ 是 | ✅ 是 | ❌ 需要 unsafe-eval |
| 运行时版 | `vue.runtime.esm.js` | ❌ 否 | ❌ 否 | ✅ CSP 友好 |

### 完整版的问题
完整版 Vue 包含模板编译器，会在运行时使用 `new Function()` 动态编译模板：

```javascript
// ❌ 这种写法会触发 new Function()
new Vue({
  el: '#app',
  template: '<div>{{ message }}</div>',  // 运行时编译
  data: { message: 'Hello' }
})
```

## 迁移步骤

### 1. 修改 Webpack 配置

**文件：`build/webpack.base.conf.js`**

```diff
resolve: {
  alias: {
-   'vue$': 'vue/dist/vue.esm.js',
+   'vue$': 'vue/dist/vue.runtime.esm.js',
  }
}
```

### 2. 修改主入口文件

**文件：`src/main.js`**

```diff
new Vue({
  el: '#app',
  router,
  store,
  i18n,
- template: '<App/>',
- components: { App }
+ render: h => h(App)
})
```

### 3. 检查其他组件

确保所有组件都使用以下方式之一：

#### ✅ 推荐方式

**方式1：使用 .vue 单文件组件**
```vue
<!-- MyComponent.vue -->
<template>
  <div>{{ message }}</div>
</template>

<script>
export default {
  data() {
    return { message: 'Hello' }
  }
}
</script>
```

**方式2：使用 render 函数**
```javascript
export default {
  render(h) {
    return h('div', this.message)
  },
  data() {
    return { message: 'Hello' }
  }
}
```

**方式3：使用 JSX（需要额外配置）**
```javascript
export default {
  render() {
    return <div>{this.message}</div>
  },
  data() {
    return { message: 'Hello' }
  }
}
```

#### ❌ 不兼容的方式

```javascript
// ❌ 会触发 new Function()
new Vue({
  template: '<div>{{ message }}</div>',
  data: { message: 'Hello' }
})

// ❌ 动态模板字符串
Vue.component('my-component', {
  template: '<div>{{ message }}</div>'
})

// ❌ 使用 Vue.compile()
const compiled = Vue.compile('<div>{{ message }}</div>')
```

## 技术优势

### 1. 安全性
- ✅ 完全兼容严格的 CSP 策略
- ✅ 不需要 `unsafe-eval` 权限
- ✅ 减少 XSS 攻击面

### 2. 性能
- ✅ 更小的包体积（减少 ~30% Vue.js 大小）
- ✅ 更快的启动时间（无运行时编译）
- ✅ 更好的 Tree Shaking

### 3. 开发体验
- ✅ .vue 文件在构建时预编译
- ✅ 模板语法检查和优化
- ✅ 更好的 IDE 支持

## 验证步骤

### 1. 构建验证
```bash
npm run build
```

### 2. 检查生成的文件
```bash
# 检查是否还包含 new Function
grep -r "new Function" dist/static/js/
```

应该没有任何输出，说明已成功移除。

### 3. CSP 验证
使用严格的 CSP 策略测试：
```html
<meta http-equiv="Content-Security-Policy"
      content="script-src 'self'; object-src 'none';">
```

页面应该正常工作，无任何 CSP 违规报告。

### 4. 功能验证
- ✅ 页面正常渲染
- ✅ 路由正常工作
- ✅ 组件交互正常
- ✅ 数据绑定正常

## 潜在问题和解决方案

### 1. 第三方库兼容性
某些第三方 Vue 组件可能依赖完整版：

**问题：**
```
[Vue warn]: You are using the runtime-only build of Vue where the template compiler is not available.
```

**解决方案：**
1. 查找该库的 ES modules 版本
2. 使用预编译版本
3. 替换为兼容的库

### 2. 动态组件模板
如果需要动态生成模板：

**问题：**
```javascript
// ❌ 不再支持
Vue.component('dynamic', {
  template: generateTemplate()
})
```

**解决方案：**
```javascript
// ✅ 使用 render 函数
Vue.component('dynamic', {
  render(h) {
    return generateVNode(h)
  }
})
```

### 3. 单元测试
某些测试工具可能需要调整：

**问题：**
```javascript
// ❌ 测试中使用模板字符串
const wrapper = mount(Component, {
  template: '<div>test</div>'
})
```

**解决方案：**
```javascript
// ✅ 使用预编译组件
const TestComponent = {
  render: h => h('div', 'test')
}
const wrapper = mount(TestComponent)
```

## 回退方案

如果遇到严重兼容性问题，可以临时回退：

```diff
// build/webpack.base.conf.js
resolve: {
  alias: {
+   'vue$': 'vue/dist/vue.esm.js',
-   'vue$': 'vue/dist/vue.runtime.esm.js',
  }
}
```

同时需要在 CSP 中重新允许 `unsafe-eval`。

## 总结

迁移到 Vue Runtime-Only 版本：
1. **解决了根本问题**：从源头消除 `new Function()` 使用
2. **提升了安全性**：完全兼容严格 CSP 策略
3. **改善了性能**：更小的包体积和更快的启动
4. **保持了功能**：所有现有功能继续正常工作

这是解决 CSP 兼容性问题的最佳方案。
