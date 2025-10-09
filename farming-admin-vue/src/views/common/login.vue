<template>

  <div id="bg">
    <div id="hint">
      <!-- 提示框 -->
      <p>登录失败</p>
    </div>
    <div id="login_wrap">
      <div id="login">
        <!-- 登录注册切换动画 -->
        <div id="status">
          <i style="top: 0">Log</i>
          <i style="top: 35px">Sign</i>
          <i style="right: 5px">in</i>
        </div>
        <span>
          <el-form @keyup.enter.native="dataFormSubmit()">

            <form action="post" >
                <div class="form"><input type="text"  v-model="dataForm.userName" id="user" placeholder="username"></div>
                <div class="form"><input type="password" v-model="dataForm.password" id="passwd" placeholder="password"></div>

                <div class="form captcha">
                  <div style="width: 300px">
                    <input type="text" v-model="dataForm.captcha" placeholder="captcha">
                     <img :src="captchaPath" @click="getCaptcha()" alt="">
                  </div>

                </div>
                <div class="form">
                  <input type="text" v-model="dataForm.googleAuthCode" id="googleAuthCode" placeholder="请输入Google令牌（必填）" required>
                </div>
                <input type="button" value="Log in" class="btn" @click="dataFormSubmit()" style="margin-right: 20px;">
            </form>
            </el-form>
          <!-- <a href="#">Forget your password?</a> -->
        </span>
      </div>

      <div id="login_img">
        <!-- 图片绘制框 -->
        <span class="circle">
          <span></span>
          <span></span>
        </span>
        <span class="star">
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
        </span>
        <span class="fly_star">
          <span></span>
          <span></span>
        </span>
        <p id="title">ETH FARMINGN</p>
      </div>
    </div>
 </div>


</template>

<script>
import { getUUID } from "@/utils";
import { mapMutations } from 'vuex';

export default {
  data() {
    return {
      dataForm: {
        userName: "",
        password: "",
        uuid: "",
        captcha: "",
        googleAuthCode:"",
      },
      captchaPath: "",
    };
  },
  created() {
    this.getCaptcha();
  },
  methods: {
    ...mapMutations({
      setSelect:'select/setSelect'
    }),
    //获取下拉数据
    getSelection() {
      this.$http({
        url: this.$http.adornUrl("/admin/common/select"),
        method: "post"
      }).then(({ data }) => {
        this.setSelect(data)
      })
    },


    // handleBlur(){ //检查是否绑定google
    //   const loginName = this.dataForm.userName;
    //   const formData = new FormData();
    //   formData.append('userName', loginName);
    //   this.$http({
    //     url: this.$http.adornUrl("/googleAuth/get"),
    //     data:formData,
    //     method: "post",
    //     headers: {
    //     'Content-Type': 'multipart/form-data'
    //   }
    //   }).then(({ data }) => {
    //     if (data && data.code === 0) {
    //      this.showGoogleBuild = data.data;
    //     }else{
    //       this.$message.error(data.msg);
    //     }
    //   })
    // },
    // 提交表单
    dataFormSubmit() {
      if (!this.dataForm.userName) {
        this.$message.error("请输入username!");
        return;
      }

      if (!this.dataForm.password) {
        this.$message.error("请输入password!");
        return;
      }
      if (!this.dataForm.googleAuthCode) {
        this.$message.error("请输入Google令牌！");
        return;
      }
      if (!this.dataForm.captcha) {
        this.$message.error("请输入验证码!");
        return;
      }
      this.$http({
        url: this.$http.adornUrl("/sys/login"),
        method: "post",
        data: this.$http.adornData({
          username: this.dataForm.userName,
          password: this.dataForm.password,
          uuid: this.dataForm.uuid,
          captcha: this.dataForm.captcha,
          googleAuthCode: this.dataForm.googleAuthCode
        }),
      }).then(({ data }) => {
        if (data && data.code === 0) {
          const expires = new Date();
          expires.setTime(expires.getTime() + 4 * 60 * 60 * 1000) // 4 hours
          this.$cookie.set("token", data.token, { expires: expires });
          // 设置token后，让路由守卫处理导航
          this.$nextTick(() => {
            this.getSelection();
            // 使用 window.location 进行页面刷新，让路由守卫重新处理
            window.location.href = window.location.origin + window.location.pathname + '#/home';
          });
        } else {
          this.getCaptcha();
          this.$message.error(data.msg);
        }
      });
    },
    // 获取验证码
    getCaptcha() {
      this.dataForm.uuid = getUUID();
      this.captchaPath = this.$http.adornUrl(
        `/captcha.jpg?uuid=${this.dataForm.uuid}`
      );
    },
  },
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
}
html,
body {
  height: 100%;
}

@font-face {
  font-family: "neo";
  src: url(~@/assets/font/NEOTERICc.ttf);
}
input:focus {
  outline: none;
}
.form input {
  width: 300px;
  height: 30px;
  font-size: 18px;
  background: none;
  border: none;
  border-bottom: 1px solid #fff;
  color: #fff;
  margin-bottom: 20px;
}
.form input::placeholder {
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
  font-family: "neo";
}
.confirm {
  height: 0;
  overflow: hidden;
  transition: 0.25s;
}
.btn {
  /* width: 140px; */
  width: 100%;
  height: 40px;
  border: 1px solid #fff;
  background: none;
  font-size: 20px;
  color: #fff;
  cursor: pointer;
  margin-top: 25px;
  font-family: "neo";
  transition: 0.25s;
}
.btn:hover {
  background: rgba(255, 255, 255, 0.25);
}
#login_wrap {
  width: 980px;
  min-height: 500px;
  border-radius: 10px;
  font-family: "neo";
  overflow: hidden;
  box-shadow: 0px 0px 120px rgba(0, 0, 0, 0.25);
  position: fixed;
  top: 50%;
  right: 50%;
  margin-top: -250px;
  margin-right: -490px;
}
#login {
  width: 50%;
  height: 100%;
  min-height: 500px;
  background: linear-gradient(45deg, #9a444e, #e06267);
  position: relative;
  float: right;
}
#login #status {
  width: 90px;
  height: 35px;
  margin: 40px auto;
  color: #fff;
  font-size: 30px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
}
#login #status i {
  font-style: normal;
  position: absolute;
  transition: 0.5s;
}
#login span {
  text-align: center;
  position: absolute;
  left: 50%;
  margin-left: -150px;
  top: 52%;
  margin-top: -140px;
}
#login span a {
  text-decoration: none;
  color: #fff;
  display: block;
  margin-top: 80px;
  font-size: 18px;
}
#bg {
  background: linear-gradient(45deg, #211136, #bf5853);
  height: 100%;
}
/*绘图*/
#login_img {
  width: 50%;
  min-height: 500px;
  background: linear-gradient(45deg, #221334, #6c3049);
  float: left;
  position: relative;
}
#login_img span {
  position: absolute;
  display: block;
}
#login_img .circle {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: linear-gradient(45deg, #df5555, #ef907a);
  top: 70px;
  left: 50%;
  margin-left: -100px;
  overflow: hidden;
}
#login_img .circle span {
  width: 150px;
  height: 40px;
  border-radius: 50px;
  position: absolute;
}
#login_img .circle span:nth-child(1) {
  top: 30px;
  left: -38px;
  background: #c55c59;
}
#login_img .circle span:nth-child(2) {
  bottom: 20px;
  right: -35px;
  background: #934555;
}
#login_img .star span {
  background: radial-gradient(#fff 10%, #fff 20%, rgba(72, 34, 64, 0));
  border-radius: 50%;
  box-shadow: 0 0 7px #fff;
}
#login_img .star span:nth-child(1) {
  width: 15px;
  height: 15px;
  top: 50px;
  left: 30px;
}
#login_img .star span:nth-child(2) {
  width: 10px;
  height: 10px;
  left: 360px;
  top: 80px;
}
#login_img .star span:nth-child(3) {
  width: 5px;
  height: 5px;
  top: 400px;
  left: 80px;
}
#login_img .star span:nth-child(4) {
  width: 8px;
  height: 8px;
  top: 240px;
  left: 60px;
}
#login_img .star span:nth-child(5) {
  width: 4px;
  height: 4px;
  top: 20px;
  left: 200px;
}
#login_img .star span:nth-child(6) {
  width: 4px;
  height: 4px;
  top: 460px;
  left: 410px;
}
#login_img .star span:nth-child(7) {
  width: 6px;
  height: 6px;
  top: 250px;
  left: 350px;
}
#login_img .fly_star span {
  width: 90px;
  height: 3px;
  background: -webkit-linear-gradient(
    left,
    rgba(255, 255, 255, 0.67),
    rgba(255, 255, 255, 0)
  );
  background: -o-linear-gradient(
    left,
    rgba(255, 255, 255, 0.67),
    rgba(255, 255, 255, 0)
  );
  background: linear-gradient(
    to right,
    rgba(255, 255, 255, 0.67),
    rgba(255, 255, 255, 0)
  );
  transform: rotate(-45deg);
}
#login_img .fly_star span:nth-child(1) {
  top: 60px;
  left: 80px;
}
#login_img .fly_star span:nth-child(2) {
  top: 210px;
  left: 332px;
  opacity: 0.6;
}
#login_img p {
  text-align: center;
  color: #af4b55;
  font-weight: 600;
  margin-top: 365px;
  font-size: 25px;
}
#login_img p i {
  font-style: normal;
  margin-right: 45px;
}
#login_img p i:nth-last-child(1) {
  margin-right: 0;
}

.captcha input{
  float: left;
  width: 50%;
}
.captcha img{
  float: right;
  width: 45%;
  height: 33px;
  cursor: pointer;
}
/*提示*/
#hint {
  width: 100%;
  line-height: 70px;
  background: linear-gradient(-90deg, #9b494d, #bf5853);
  text-align: center;
  font-size: 25px;
  color: #fff;
  box-shadow: 0 0 20px #733544;
  display: none;
  opacity: 0;
  transition: 0.5s;
  position: absolute;
  top: 0;
  z-index: 999;
}
/* 响应式 */
@media screen and (max-width: 1000px) {
  #login_img {
    display: none;
  }
  #login_wrap {
    width: 490px;
    margin-right: -245px;
  }
  #login {
    width: 100%;
  }
}
@media screen and (max-width: 560px) {
  #login_wrap {
    width: 330px;
    margin-right: -165px;
  }
  #login span {
    margin-left: -125px;
  }
  .form input {
    width: 250px;
  }
  .btn {
    width: 113px;
  }
}
@media screen and (max-width: 345px) {
  #login_wrap {
    width: 290px;
    margin-right: -145px;
  }
}
</style>
