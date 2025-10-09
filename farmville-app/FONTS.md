# 字体配置说明

## 项目使用的字体

本项目使用以下字体系列：

### 1. PingFang HK / PingFang SC (苹方)
- **用途**: 主要文字、UI 文本
- **来源**: 系统字体（macOS/iOS 自带）
- **备用方案**: Microsoft YaHei（微软雅黑）, Noto Sans SC
- **特点**: 中文优化，清晰易读

### 2. Tilt Warp
- **用途**: 特殊标题、强调文本
- **来源**: Google Fonts
- **在线加载**: https://fonts.google.com/specimen/Tilt+Warp
- **特点**: 现代、动态的标题字体

### 3. DIN Alternate
- **用途**: 数字显示、数据展示
- **来源**: 商业字体（项目中使用 Orbitron 作为替代）
- **备用方案**: Orbitron, Arial Narrow, Impact
- **特点**: 清晰的数字显示

### 4. Akshar
- **用途**: 表格数据、统计信息
- **来源**: Google Fonts
- **在线加载**: https://fonts.google.com/specimen/Akshar
- **特点**: 几何形状，现代感强

## 字体加载方式

### 在线加载（Google Fonts）
```css
@import url('https://fonts.googleapis.com/css2?family=Tilt+Warp:wght@400;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Akshar:wght@400;600;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700&display=swap');
```

### 系统字体（本地优先）
- PingFang HK/SC: macOS/iOS 系统自带
- 在 Windows 上会自动降级到微软雅黑

## 使用方法

### 在 Vue 组件中使用
```vue
<style>
/* 方式 1: 使用 inline style */
<div style="font-family: 'PingFang HK';">文本内容</div>

/* 方式 2: 使用 CSS 类 */
<div class="font-sans">普通文本</div>
<div class="font-display">标题文本</div>
<div class="font-numbers">123456</div>
</style>
```

### CSS 工具类
- `.font-sans` - PingFang HK/SC
- `.font-display` - Tilt Warp
- `.font-numbers` - DIN Alternate (Orbitron)
- `.font-pingfang-hk` - PingFang HK
- `.font-pingfang-sc` - PingFang SC
- `.font-tilt-warp` - Tilt Warp
- `.font-din-alternate` - DIN Alternate
- `.font-akshar` - Akshar

## 字体文件位置
- `/src/assets/fonts.css` - 字体定义和引用
- `/public/fonts/` - 本地字体文件目录（如需）

## 性能优化
1. 使用 `font-display: swap` 确保文本快速显示
2. 优先使用系统字体减少加载时间
3. Google Fonts 自动优化字重和字符集

## 浏览器兼容性
- ✅ Chrome/Edge (最新版本)
- ✅ Safari (最新版本)
- ✅ Firefox (最新版本)
- ✅ iOS Safari
- ✅ Android Chrome

## 注意事项
1. PingFang 字体在非 Apple 设备上会自动降级
2. DIN Alternate 是商业字体，项目中使用 Orbitron 作为开源替代
3. 所有 Google Fonts 需要网络连接才能加载

