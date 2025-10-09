/**
 * Content Security Policy 配置
 * 为开发和生产环境提供不同的CSP策略
 */

// 开发模式 - 允许eval用于webpack-dev-server
const developmentCSP = {
  'default-src': ["'self'"],
  'script-src': ["'self'", "'unsafe-eval'", "'unsafe-inline'"],
  'style-src': ["'self'", "'unsafe-inline'"],
  'img-src': ["'self'", 'data:', 'blob:'],
  'font-src': ["'self'", 'data:'],
  'connect-src': ["'self'", 'https:', 'wss:', 'ws:'],
  'frame-src': ["'none'"],
  'object-src': ["'none'"],
  'base-uri': ["'self'"],
  'form-action': ["'self'"],
  'media-src': ["'self'", 'data:', 'blob:'],
  'worker-src': ["'self'", 'blob:'],
  'child-src': ["'self'", 'blob:'],
  'manifest-src': ["'self'"],
  'script-src-elem': ["'self'", "'unsafe-eval'"],
  'script-src-attr': ["'none'"],
  'style-src-elem': ["'self'", "'unsafe-inline'"],
  'style-src-attr': ["'self'", "'unsafe-inline'"]
};

// 生产模式 - 严格安全策略
const productionCSP = {
  'default-src': ["'self'"],
  'script-src': ["'self'"],
  'style-src': ["'self'", "'unsafe-inline'"],
  'img-src': ["'self'", 'data:', 'blob:'],
  'font-src': ["'self'", 'data:'],
  'connect-src': ["'self'", 'https:', 'wss:', 'ws:'],
  'frame-src': ["'none'"],
  'object-src': ["'none'"],
  'base-uri': ["'self'"],
  'form-action': ["'self'"],
  'media-src': ["'self'", 'data:', 'blob:'],
  'worker-src': ["'self'", 'blob:'],
  'child-src': ["'self'", 'blob:'],
  'manifest-src': ["'self'"],
  'script-src-elem': ["'self'"],
  'script-src-attr': ["'none'"],
  'style-src-elem': ["'self'", "'unsafe-inline'"],
  'style-src-attr': ["'self'", "'unsafe-inline'"],
  'upgrade-insecure-requests': []
};

/**
 * 生成CSP字符串
 * @param {Object} cspConfig - CSP配置对象
 * @returns {string} CSP字符串
 */
function generateCSPString(cspConfig) {
  return Object.entries(cspConfig)
    .map(([key, values]) => `${key} ${values.join(' ')}`)
    .join('; ');
}

/**
 * 根据环境获取CSP配置
 * @param {string} env - 环境名称 ('development', 'production')
 * @returns {string} CSP字符串
 */
function getCSPForEnvironment(env = 'development') {
  const configs = {
    development: developmentCSP,
    production: productionCSP
  };

  return generateCSPString(configs[env] || configs.development);
}

/**
 * 获取当前环境的CSP配置
 * @returns {string} CSP字符串
 */
function getCurrentCSP() {
  const isProduction = process.env.NODE_ENV === 'production';
  return getCSPForEnvironment(isProduction ? 'production' : 'development');
}

module.exports = {
  developmentCSP,
  productionCSP,
  generateCSPString,
  getCSPForEnvironment,
  getCurrentCSP
};
