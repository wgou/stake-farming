<template>
  <div class="upload-file">
    <el-upload
      v-show="showbtn"
      multiple
      :action="uploadFileUrl"
      :before-upload="handleBeforeUpload"
      :file-list="fileList"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-success="handleUploadSuccess"
      :show-file-list="false"
      :headers="headers"
      class="upload-file-uploader"
      ref="fileUpload"
    >
      <!-- 上传按钮 -->
      <div class="el-upload__btn" >
        <div style="width:69px;height:36px;line-height:36px">
            <i class="el-icon-link" style="color:#016EEB"></i>
          <span style="color:#016EEB;font-size:13px">上传附件</span>
        </div>
                <!-- 上传提示 -->
          <div class="el-upload__tip" slot="tip" v-if="showTip">(≤{{limit}}个,{{fileSize?'大小不超过'+fileSize+'MB':''}}{{fileType?'格式为'+fileType.join("/"):''}})
          </div>
      </div>
    </el-upload>
    <!-- 文件列表 -->
    <transition-group class="upload-file-list el-upload-list el-upload-list--text" name="el-fade-in-linear" tag="ul" v-if="!istable">
      <li :key="index+'list'" class="el-upload-list__item ele-upload-list__item-content " v-for="(file, index) in fileList" >
        <el-link :href="`${file.url}`" :underline="false" target="_blank" >
           <el-tooltip class="item" effect="dark" :content="file.name" placement="top-start">
              <span class="el-icon-document myontherdocument"> {{file.name}} </span>
            </el-tooltip>
        </el-link>
        <div class="ele-upload-list__item-content-action">
          <span @click="handleDelete(index)"  ><i class="el-icon-close"></i></span>
        </div>
      </li>
    </transition-group>
    <transition-group class="upload-file-list el-upload-list el-upload-list--text " name="el-fade-in-linear" tag="ul" v-if="istable">
      <li :key="index+'item'" class="el-upload-list__item ele-upload-list__item-content tablelist" v-for="(file, index) in fileList" >
        <el-link :href="`${file.url}`" :underline="false" target="_blank" >
          <el-tooltip class="item" effect="dark" :content="file.name" placement="top-start">
              <span class="el-icon-document mydocument"> {{file.name}} </span>
            </el-tooltip>
        </el-link>
        <div class="ele-upload-list__item-content-action">
          <span @click="handleDelete(index)"  class="myclose"><i class="el-icon-close"></i></span>
        </div>
      </li>
    </transition-group>
  </div>
</template>

<script>

export default {
  name: "FileUpload",
  props: {
    // 值
    value: [String, Object, Array],
    // 数量限制
    limit: {
      type: Number,
      default: 1,
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 5,
    },
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: {
      type: Array,
      default: () => ["doc","docx", "xls" ,"xlsx","ppt", "txt", "pdf",'png', 'jpg', 'jpeg'],
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true
    },
    // 回显图片
    showimgurl:{
      type:Array,
      default:[]
    },
    // 是否在表格里
    istable: {
      type: Boolean,
      default: false
    },
    isshowbtn: {
      type: Number,
      default: 0
    },

  },
  data() {
    return {
      showbtn:true,
      number: 0,
      uploadList: [],
      uploadFileUrl: "/product/upload/file/video", // 上传文件服务器地址
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      fileList: [],
      type:"",
    };
  },
  created(){
      this.fileList=this.showimgurl
      if(this.istable&&this.showimgurl.length==0){
        this.showbtn=true
      }

  },
  watch: {
    isshowbtn: {
      handler(val) {
        if(val==0){
          this.showbtn=true
        }else{
           this.showbtn=false
        }
      },
      deep: true,
      immediate: true
    }, 
    fileList:{
      handler(newName,oldName){
          if(this.istable&&newName.length==0){
            this.showbtn=true
          }
        },
      deep: true,
      immediate: true
    },
    showimgurl(newName,oldName){
      this.fileList=newName
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
    // 上传前校检格式和大小
    handleBeforeUpload(file) {
      // 校检文件类型
      if (this.fileType) {
        const fileName = file.name.split('.');
        const fileExt = fileName[fileName.length - 1];
        this.type=fileExt
        console.log(this.type);
        const isTypeOk = this.fileType.indexOf(fileExt) >= 0;
        if (!isTypeOk) {
         this.$message.error(`文件格式不正确, 请上传${this.fileType.join("/")}格式文件!`);
          return false;
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
         this.$message.error(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      this.$modal.loading("正在上传文件，请稍候...");
      this.number++;
      return true;
    },
    // 文件个数超出
    handleExceed() {
     this.$message.error(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 上传失败
    handleUploadError(err) {
     this.$message.error("上传文件失败，请重试");
      this.$modal.closeLoading()
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      console.log(file);
      if (res.code === 200) {
        this.uploadList.push({ name: file.name, url: res.data.fileUrl,type:this.type });
        this.uploadedSuccessfully();
        
      } else {
        this.number--;
        this.$modal.closeLoading();
       this.$message.error(res.msg);
        this.$refs.fileUpload.handleRemove(file);
        this.uploadedSuccessfully();
      }
    },
    // 删除文件
    handleDelete(index) {
      this.fileList.splice(index, 1);
      if(this.istable){
          this.showbtn=true
        }
      this.$emit("getfile",this.fileList)
      this.$emit("input", this.listToString(this.fileList));
    },
    // 上传结束处理
    uploadedSuccessfully() {
      if (this.number > 0 && this.uploadList.length === this.number) {
        this.fileList = this.fileList.concat(this.uploadList);
        console.log(this.fileList);
        this.uploadList = [];
        this.number = 0;
        if(this.istable){
          this.showbtn=false
        }
        this.$emit("input", this.listToString(this.fileList));
        this.$emit("getfile",this.fileList)
        this.$modal.closeLoading();
      }
    },
    // 获取文件名称
    getFileName(name) {
      if (name.lastIndexOf("/") > -1) {
        return name.slice(name.lastIndexOf("/") + 1);
      } else {
        return "";
      }
    },
    // 对象转成指定字符串分隔
    listToString(list, separator) {
      let strs = "";
      separator = separator || ",";
      for (let i in list) {
        strs += list[i].url + separator;
      }
      return strs != '' ? strs.substr(0, strs.length - 1) : '';
    }
  }
};
</script>

<style scoped lang="scss">
.upload-file-uploader {
  margin-bottom: 5px;
}
 .el-upload-list__item:hover {
    background-color: transparent;
}
.el-upload__btn{
  // display: flex;
  // align-items: center;
}
.el-upload__tip {
    font-size: 13px;
    color: #9196AB;
    margin-top: 0;
    line-height: 13px;
    height: 13px;
    font-family: "SourceHanSans";
}
.el-link.el-link--default {
    color: #0D132A;
}
.el-link.el-link--default:hover {
    color: #0D132A;
}
.el-upload-list__item:hover .el-icon-close {
    display: inline-block;
}
.upload-file-list .el-upload-list__item {
  border-bottom: 1px solid rgba(13, 19, 42, 0.1);
  line-height: 2;
  height: 40px;
  position: relative;
  width: 442PX;

}
.tablelist{
    border-bottom: none !important;
}
.el-upload-list__item:first-child{
  margin-top: 0px !important;
}
.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}
.ele-upload-list__item-content-action .el-link {
  height: 100%;
  width: 50px;
  margin-right: 10px;
  .el-icon-close{
    top: -3px;
    right: -20px;
    position: relative;
    z-index: 6;
  }
}
.el-icon-link{
  font-size: 17px;
}
.el-upload-list__item .el-icon-close {
  display: inline-block;
  top: 15px !important;
}
.mydocument{
  width: 140px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.myontherdocument{
  width: 240px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.myclose{
  .el-icon-close {
    display: inline-block;
    top: 10px !important;
  }
}
</style>
