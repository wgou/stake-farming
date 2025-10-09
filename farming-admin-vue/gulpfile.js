const gulp = require('gulp');
const $    = require('gulp-load-plugins')();

// 只保留 webpack 打包
function build() {
  return $.shell.task([ 'node build/build.js' ])();
}

exports.build = build;
exports.default = build;
