# 构建过程说明

## 文件生成说明

### 1. static/js/ 目录的来源

**重要说明**：`static/js/` 目录和其中的文件（如 `72.85b466ce74b6c2caa6cb.js`）是 **webpack构建过程中自动生成的**，不是您手动创建的。

### 2. 构建流程

```
源代码 (src/)
    ↓
webpack 打包
    ↓
生成 dist/static/js/ 目录
    ↓
复制到 static/js/ 目录
```

### 3. 文件命名规则

生成的文件名格式：`[chunkId].[contenthash].js`

- `chunkId`: webpack分块ID
- `contenthash`: 基于文件内容的哈希值，用于缓存优化

例如：
- `72.85b466ce74b6c2caa6cb.js` - 第72个代码块
- `app.a68b07df65e087608e3b.js` - 主应用文件
- `vendors.335246c3423916fd61ab.js` - 第三方库文件

### 4. eval() 问题的真正原因

虽然您的代码没有直接使用 `eval()`，但以下第三方库内部使用了 `eval()`：

#### 已确认使用eval的库：
- **mock.js** (`static/plugins/mock-1.0.0-beta3/mock-min.js`)
- **ueditor** (`static/plugins/ueditor-1.4.3.3/ueditor.all.min.js`)
- **jquery** (`static/plugins/ueditor-1.4.3.3/third-party/jquery-1.10.2.js`)

#### 解决方案：
1. ✅ 已在 `index.html` 中添加CSP策略，允许 `unsafe-eval`
2. ✅ 修复了webpack配置中的Terser错误
3. ✅ 创建了CSP配置文件供不同环境使用

### 5. 构建配置说明

#### webpack输出配置：
```javascript
// build/webpack.prod.conf.js
output: {
  path: config.build.assetsRoot,        // dist/
  filename: utils.assetsPath('js/[name].[contenthash].js'),
  chunkFilename: utils.assetsPath('js/[id].[contenthash].js'),
  clean: true
}
```

#### 文件复制配置：
```javascript
// 复制静态资源
new CopyWebpackPlugin({
  patterns: [
    {
      from: path.resolve(__dirname, '../static'),
      to: config.build.assetsSubDirectory,  // static/
      globOptions: {
        ignore: ['.*']
      }
    }
  ]
})
```

### 6. 目录结构说明

```
项目根目录/
├── src/                    # 源代码
├── static/                 # 静态资源
│   ├── plugins/           # 第三方插件
│   ├── img/              # 图片资源
│   └── config/           # 配置文件
├── dist/                  # 构建输出目录
│   └── static/
│       ├── js/           # 自动生成的JS文件
│       ├── css/          # 自动生成的CSS文件
│       └── ...           # 其他静态资源
└── build/                # 构建配置
```

### 7. 安全配置

#### CSP策略（已在index.html中配置）：
```html
<meta http-equiv="Content-Security-Policy"
      content="default-src 'self'; script-src 'self' 'unsafe-eval' 'unsafe-inline' data: blob:; ...">
```

#### 为什么需要 'unsafe-eval'：
- 第三方库（mock.js、ueditor、jquery）内部使用eval()
- 这些库是项目功能必需的
- CSP策略允许这些库正常工作

### 8. 构建命令

```bash
# 开发环境
npm run dev

# 生产构建
npm run build

# 使用gulp构建
gulp build
```

### 9. 故障排除

如果构建失败：
1. 检查 `build/webpack.prod.conf.js` 配置
2. 确认第三方库版本兼容性
3. 查看控制台错误信息
4. 检查CSP配置是否正确

### 10. 注意事项

⚠️ **重要提醒**：
- 不要手动修改 `static/js/` 目录中的文件
- 这些文件会在每次构建时重新生成
- 如果需要修改，请修改源代码而不是生成的文件
- `unsafe-eval` 会降低安全性，但为了兼容第三方库是必要的
