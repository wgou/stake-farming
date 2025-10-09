<template>
  <div class="component-upload-image">
    <el-upload multiple :action="uploadImgUrl" list-type="picture-card" :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload" :limit="limit" :on-error="handleUploadError" :on-exceed="handleExceed"
      ref="imageUpload" :on-remove="handleDelete" :show-file-list="true" :headers="headers" :file-list="fileList"
      :on-preview="handlePictureCardPreview" :class="{ hide: this.fileList.length >= this.limit }">
      <i class="el-icon-plus"></i>
    </el-upload>

    <!-- 上传提示 -->
    <div class="el-upload__tip" slot="tip" v-if="showTip">
      请上传
      <template v-if="fileSize"> 大小不超过 {{ fileSize }}MB</template>
      <template v-if="fileType"> 格式为 {{ fileType.join("/") }}</template>
      的文件
    </div>

    <el-dialog :visible.sync="dialogVisible" title="预览" width="800" append-to-body>
      <img :src="`${dialogImageUrl}`" style="display: block; max-width: 100%; margin: 0 auto" />
    </el-dialog>
  </div>
</template>

<script>
import Vue from 'vue'

export default {
  props: {
    value: [String, Object, Array],
    // 图片数量限制
    limit: {
      type: Number,
      default: 5,
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 5,
    },
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: {
      type: Array,
      default: () => ["png", "jpg", "jpeg"],
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true
    },
    // 回显图片
    showimgurl: {
      type: Array,
      default: () => []
    },
    uploadType: {
      type: String,
      default: () => {
        return 'cover'
      }
    }
  },
  data() {
    return {
      number: 0,
      uploadList: [],
      dialogImageUrl: "",
      dialogVisible: false,
      hideUpload: false,
      uploadImgUrl: '', // 先设置为空字符串
      headers: {
        token: ''
      },
      fileList: [],
      loadings:null
    };
  },
  created() {
    this.fileList = this.showimgurl;
    // 在created中设置uploadImgUrl
    this.uploadImgUrl = this.$http.adornUrl('/upload/image');
    // 更新headers中的token
    this.headers.token = this.getToken();
  },
  watch: {
    showimgurl(newName, oldName) {
      this.fileList = newName
    },
  },
  computed: {
    // 是否显示提示
    showTip() {
      return this.isShowTip && (this.fileType || this.fileSize);
    },
  },
  methods: {
    getToken() {
      const token = Vue.cookie.get('token');
      return token ? token : '';
    },
    // 上传前loading加载
    handleBeforeUpload(file) {
      // 更新token
      this.headers.token = this.getToken();
      
      let isImg = false;
      if (this.fileType.length) {
        let fileExtension = "";
        if (file.name.lastIndexOf(".") > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1);
        }
        isImg = this.fileType.some(type => {
          if (file.type.indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.indexOf(type) > -1) return true;
          return false;
        });
      } else {
        isImg = file.type.indexOf("image") > -1;
      }

      if (!isImg) {
        this.$message.error(`文件格式不正确, 请上传${this.fileType.join("/")}图片格式文件!`);
        return false;
      }
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$message.error(`上传头像图片大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      this.loadings=this.$loading({
          lock: true,
          text: '正在上传图片，请稍候...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
      this.number++;
    },
    // 文件个数超出
    handleExceed() {
      this.$message.error(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 获取文件名称
    getFileName(name) {
      console.log(name, "fileName");
      if (name.lastIndexOf("/") > -1) {
        return name.slice(name.lastIndexOf("/") + 1);
      } else {
        return "";
      }
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      console.log(res);
      if (res.code === 0) {
        this.uploadList.push({ name: this.getFileName(res.url), url: res.url });
        this.uploadedSuccessfully();
      } else {
        this.number--;
        this.loadings.close();
        this.$message.error(res.msg);
        this.$refs.imageUpload.handleRemove(file);
        this.uploadedSuccessfully();
      }
    },

    // 删除图片
    handleDelete(file) {
      const findex = this.fileList.map(f => f.name).indexOf(file.name);
      if (findex > -1) {
        this.fileList.splice(findex, 1);
        this.$emit("input", this.listToString(this.fileList));
        this.$emit("getimg", this.fileList)
      }
    },
    // 上传失败
    handleUploadError() {
      this.$message.error("上传图片失败，请重试");
      this.loadings.close();
    },
    // 上传结束处理
    uploadedSuccessfully() {
      if (this.number > 0 && this.uploadList.length === this.number) {
        this.fileList = this.fileList.concat(this.uploadList);
        this.uploadList = [];
        this.number = 0;
        this.$emit("input", this.listToString(this.fileList));
        this.$emit("getimg", this.fileList)
        this.loadings.close();
      }
    },
    // 预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    // 对象转成指定字符串分隔
    listToString(list, separator) {
      let strs = "";
      separator = separator || ",";
      for (let i in list) {
        if (list[i].url) {
          strs += list[i].url.replace(location.origin, "") + separator;
        }
      }
      return strs != '' ? strs.substr(0, strs.length - 1) : '';
    }
  }
};
</script>
<style scoped lang="scss">
.el-upload__tip {
  font-size: 13px;
  color: #9196AB;
  margin-top: 12px;
  line-height: 13px;
  height: 13px;
  font-family: "SourceHanSans";
}

::v-deep .el-upload--picture-card {
  background-color: #1d1d1d !important;
  border: 1px solid #434343 !important;
}

// .el-upload--picture-card 控制加号部分
::v-deep.hide .el-upload--picture-card {
  display: none !important;
}

// 去掉动画效果
::v-deep .el-list-enter-active,
::v-deep .el-list-leave-active {
  transition: all 0s;
}

::v-deep .el-list-enter,
.el-list-leave-active {
  opacity: 0;
  transform: translateY(0);
}

::v-deep .el-upload--picture-card {
  width: 80px;
  height: 80px;
  border: 1px solid #c0ccda;
  line-height: 85px;
}

::v-deep .el-upload-list--picture-card .el-upload-list__item {
  width: 80px !important;
  height: 80px !important;
}

::v-deep .el-upload--picture-card i {
  font-size: 18px;
}

::v-deep .el-upload-list__item.is-success .el-upload-list__item-status-label {
  display: none !important;
}
</style>
