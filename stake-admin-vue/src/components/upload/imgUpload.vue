/**
* 图片上传 公共组件
*/
<template>
  <div class="uploadWrapper" v-if="upfileShow" >
    <vuedraggable class="vue-draggable" :class="{ single: isSingle, maxHidden: isMaxHidden }" v-model="imgList" tag="ul"
      draggable=".draggable-item" @start="onDragStart" @end="onDragEnd">
       <!--拖拽元素-->
      <li v-for="(item, index) in imgList" :key="index" class="draggable-item" :style="{ width: width + 'px', height: height + 'px' }">
        <img  :src="item.url"    ></img>
        <div class="shadow" @click="onRemoveHandler(index)">
          <i class="el-icon-delete"></i>
        </div>
      </li>
      <!-- 上传按钮 -->
      <el-upload slot="footer" ref="uploadRef" class="uploadBox" :style="{ width: width+'px', height: height+  'px' }"
        :action="url" accept=".jpg,.jpeg,.png,.gif" :show-file-list="false" :multiple="!isSingle" :limit="limit"
        :before-upload="beforeUpload" :on-success="onSuccessUpload" :on-exceed="onExceed">
        <i class="el-icon-plus uploadIcon">
          <span class="uploading" v-show="isUploading">正在上传...</span>
          <span v-if="!isUploading && limit && limit!==99 && !isSingle" class="limitTxt">最多{{ limit }}张</span>
        </i>
      </el-upload>
    </vuedraggable>
  </div>
</template>

<script>
  import vuedraggable from 'vuedraggable'
  import tools from '@/utils/tools'
  export default {
    name: 'ImgUpload',
    props:['value','limit'],
      // 图片数据(图片url组成的数组) 通过v-model传递
    data() {
      return {
        isUploading: false, // 正在上传状态
        isFirstMount: true, // 控制防止重复回显
        url: "",
        width:100,
        height:100,
        isSingle:false,
        imgList:[],
        upfileShow:true
      }
    },
    created() {
       console.log(this.value+"==========================");
      this.url = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`);
      if (this.value.length >= 0 ) {
         let imgList=JSON.parse(this.value);
         if(imgList instanceof Array){
            this.imgList=imgList;
         }else{
           this.imgList=[imgList]
         }
        this.syncElUpload();
      }
    },
    computed: {
      isMaxHidden() {
        return this.imgList.length >= this.limit
      }
    },

    watch: {
      value: {
        handler(val) {
           
          if (this.value.length >= 0 ) {
             let imgList=JSON.parse(this.value);
             if(imgList instanceof Array){
                this.imgList=imgList;
             }else{
               this.imgList=[imgList]
             }
            this.syncElUpload();
          }
        },
        deep: true
      }
    },

   /* mounted() {
      if (this.value.length > 0) {
        this.syncElUpload()
      }
    }, */

    methods: {
      srcList(){
        let urlList=[];
        for (var i = 0; i < this.imgList.length; i++) {
          urlList.push(this.imgList[i].url);
        }
        return urlList;
      },
      // 同步el-upload数据
      syncElUpload(val) {
        const imgList = val || this.imgList || []
        this.$refs.uploadRef.uploadFiles = imgList.map((v, i) => {
          return {
            name: 'pic' + i,
            url: v.url,
            status: 'success',
            uid: tools.createUniqueString()
          }
        })
        this.isFirstMount = false
      },
      // 上传图片之前
      beforeUpload(file) {
        const isJPEG = file.type === 'image/jpeg';
        const isJPG = file.type === 'image/jpg';
        const isGIF = file.type === 'image/gif';
        const isPNG = file.type === 'image/png';
        const isBMP = file.type === 'image/bmp';
        const isLt2M = file.size / 1024 / 1024 < 80;

        if (!isJPEG && !isJPG && !isGIF && !isPNG && !isBMP) {
          this.$message('上传图片必须是JPG/GIF/PNG/BMP 格式!');
        }
        if (!isLt2M) {
          this.$message('上传图片大小不能超过 2MB!');
        }
        return (isJPEG || isJPG || isBMP || isGIF || isPNG) && isLt2M;

      },
      // 上传完单张图片
      onSuccessUpload(res, file, fileList) {
        if (res.url) {
          if (this.imgList.length < this.limit) {
            this.imgList.push({url:res.url})
          }
        } else {
          this.syncElUpload()
          this.$message({
            type: 'error',
            message: res.msg
          })
        }
         this.$emit("update:value",JSON.stringify(this.imgList))
        this.isUploading = false
      },
      // 移除单张图片
      onRemoveHandler(index) {
        this.$confirm('确定删除该图片?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
          .then(() => {
            this.imgList = this.imgList.filter((v, i) => {
              return i !== index
            })
            this.$emit("update:value",JSON.stringify(this.imgList))
          })
          .catch(() => {})
      },
      // 超限
      onExceed() {
        this.$refs.uploadRef.abort() // 取消剩余接口请求
        this.syncElUpload()
        this.$message({
          type: 'warning',
          message: `图片超限，最多可上传${this.limit}张图片`
        })
      },
      onDragStart(e) {
        e.target.classList.add('hideShadow')
      },
      onDragEnd(e) {
        e.target.classList.remove('hideShadow');
           this.$emit("update:value",JSON.stringify(this.imgList))
      }
    },

    components: {
      vuedraggable
    }
  }
</script>

<style lang="less" scoped>
  /deep/ .el-upload {
    width: 100%;
    height: 100%;
  }

  // 上传按钮
  .uploadIcon {
    width: 100px;
    height: 100px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px dashed #c0ccda;
    background-color: #fbfdff;
    border-radius: 6px;
    font-size: 20px;
    color: #999;

    .limitTxt,
    .uploading {
      position: absolute;
      bottom: 10%;
      left: 0;
      width: 100%;
      font-size: 14px;
      text-align: center;
    }
  }

  // 拖拽
  .vue-draggable {
    display: flex;
    flex-wrap: wrap;

    .draggable-item {
      margin-right: 5px;
      margin-bottom: 5px;
      border: 1px solid #ddd;
      border-radius: 6px;
      position: relative;
      overflow: hidden;

      .el-image {
        width: 100%;
        height: 100%;
      }

      .shadow {
        position: absolute;
        top: 0;
        right: 0;
        background-color: rgba(0, 0, 0, .5);
        opacity: 0;
        transition: opacity .3s;
        color: #fff;
        font-size: 20px;
        line-height: 20px;
        padding: 2px;
        cursor: pointer;
      }

      &:hover {
        .shadow {
          opacity: 1;
        }
      }
    }

    &.hideShadow {
      .shadow {
        display: none;
      }
    }

    &.single {
      overflow: hidden;
      position: relative;

      .draggable-item {
        position: absolute;
        left: 0;
        top: 0;
        z-index: 1;
      }
    }

    &.maxHidden {
      .uploadBox {
        display: none;
      }
    }
  }

  // el-image
  .el-image-viewer__wrapper {
    .el-image-viewer__mask {
      opacity: .8;
    }

    .el-icon-circle-close {
      color: #fff;
    }
  }
</style>
