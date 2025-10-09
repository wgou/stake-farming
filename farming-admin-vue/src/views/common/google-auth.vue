<template>
  
  <el-dialog 
    title="绑定Google令牌"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :append-to-body="true"
    width="400px"
  >
    <el-form label-position="right" ref="dataForm">
      <!-- <img
        style="width: 300px; margin: 30px"
        src="https://qr.api.cli.im/newqr/create?data=2323&level=H&transparent=false&bgcolor=%23FFFFFF&forecolor=%23000000&blockpixel=12&marginblock=1&logourl=&logoshape=no&size=260&kid=cliim&key=f917862eec6a0d0980743e325cde6db9"
      />  -->
      <canvas ref="qr" style="width: 300px; margin: 30px"></canvas>

      <el-alert
        title="1.请在各大应用商店下载[Google Authenticator]并安装"
        type="info"
        :closable="false"
      >
      </el-alert>

      <el-alert
        style="margin-top: 5px"
        title="2.使用[Google Authenticator]扫上面二维码,后点击绑定完成"
        type="info"
        :closable="false"
      >
      </el-alert>

      <!-- <div>请在各大应用商店下载[Google Authenticator]并安装后扫码绑定</div> -->
      <!-- 
      <el-alert
    title="请在各大应用商店下载[Google Authenticator]并安装后扫码绑定"
    type="info"
    :closable="false"
    >
  </el-alert> -->
    </el-form>
    <span  slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭窗口</el-button>
      <!-- <el-button v-if="!butShow" type="primary"  @click="removeBound">解除绑定</el-button> -->
      <el-button v-if="butShow" type="primary" @click="bindFinish">确定绑定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Loading } from "element-ui";
import QRCode from 'qrcode';
export default {
  data() {
    return {
      secretKey: "",
      authCode: "",
      userName:"",
      butShow:true,
      visible: false,
    };
  },

 
  created() {
    this.check();
  },

  methods: {
    init() {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
      });
    },

    check() {
      this.$http({
        url: this.$http.adornUrl("/googleAuth/check"),
        method: "get",
      }).then(({ data }) => {
        if(data.success){
          this.butShow = false;
        }
      });
    },
    // removeBound(){
    //   this.$http({
    //     url: this.$http.adornUrl("/googleAuth/remove"),
    //     method: "post",
    //   }).then(({ data }) => {
    //     if (data.success) {
    //         this.$message({
    //             message: "Google 解除绑定成功!",
    //             type: "success",
    //             duration: 2500,
    //             onClose: () => {
    //           },
    //         });
    //         this.visible = false;
    //     } else {
    //       this.$message.error(data.msg);
    //     }
    //   });
    // },
    firstAuth() {
      this.$http({
        url: this.$http.adornUrl("/googleAuth/firstAuth"),
        method: "post",
        data: this.$http.adornParams({
          secretKey: this.secretKey,
          authCode: this.authCode
        }),
      }).then(({ data }) => {
        if (data.success) {
            this.$message({
                message: "Google 令牌绑定成功!",
                type: "success",
                duration: 2500,
                onClose: () => {
              },
            });
            this.visible = false;
        } else {
          this.$message.error(data.msg);
          // this.bind();
        }
      });
    },
    bindFinish() {
      this.visible = false;
      this.$prompt("请输入Google 令牌", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      }) .then(({ value }) => {
        if(!value) {
          this.$message.error("请输入Google 令牌");
          return;
        }
          this.authCode = value;
          this.firstAuth();
        })
        .catch(() => {
          // this.$message({
          //   type: 'info',
          //   message: '取消输入'
          // });
        });
    },
    bind() {
      this.visible = true;
      this.$http({
        url: this.$http.adornUrl("/googleAuth/gen"),
        method: "post",
      }).then(({ data }) => {
        if (data.success) {
          this.secretKey = data.data.secretKey;
          console.log("QR Code Text:", data.data.qr); // Log to check if `data.qr` is valid
          QRCode.toCanvas(this.$refs.qr, data.data.qr, {
              width: 300,
              height: 300,
              colorDark: "#000000",
              colorLight: "#ffffff",
            }, function (error) {
              if (error)  this.$message.error(error);
            });
        
        } else {
          this.$message.error(data.msg);
        }
      });
    }, 
  },
};
</script>
