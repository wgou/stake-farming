<template>
    <div class="app-container">
        <editLayout >
            <div slot="main">
                <el-form :model="form" ref="form" :rules="rules" label-width="80px" style="margin-top:20px;" :inline="false" size="small" class="myform">
                    <el-form-item label="用户名">
                        <!-- <el-input v-model="form.nickname"></el-input> -->
                        <span>{{ form.nickname }}</span>
                    </el-form-item>
                    <el-form-item label="新密码" prop="password">
                        <el-input v-model="form.password" placeholder="请输入新密码"></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="repeatPassword">
                        <el-input v-model="form.repeatPassword" placeholder="请再次输入密码"></el-input>
                    </el-form-item>
                </el-form>
                
            </div>
            <div slot="btn">
                <el-button  @click="cancle">取消</el-button>
                <el-button type="primary" @click="saveData">确定</el-button>
            </div>
        </editLayout>
    </div>
</template>

<script>
import {formValidate } from '@/utils/decorator';
// import {postRequest} from "@/api/requestApi";
export default {
  name: 'updatePassword',
  data() {
    var validatePass = (rule, value, callback) => {
        if (value === '') {
            callback(new Error('请输入新密码'));
        } else if(value.length<8 || value.length>16){
            callback(new Error('请输入8-16位新密码'));
        }else{
            if (this.form.password !== '') {
                this.$refs.form.validateField('password');
            }
            callback();
        }
    };
    var validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        }else if(value.length<8 || value.length>16){
            callback(new Error('请输入8-16位新密码'));
        }else if (value !== this.form.password) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
    };
    return {
      form:{
        nickname:''|| localStorage.getItem('user_Name'),
        password:'',
        repeatPassword:'',
      },
      rules:{
        password: [
            { validator: validatePass, trigger: 'blur' }
        ],
        repeatPassword: [
            { validator: validatePass2, trigger: 'blur' }
        ],
      }
    };
  },
  methods: {
    // @formValidate('form')
    async saveData(){
        // const data=await postRequest('/customer/password/update',this,form);
        // if(data.success){
        //     this.$message.success('密码修改成功!');
        //     this.cancle();
        // }else{
        //     this.$message.error(data.message);
        // }
    },
    cancle(){
        this.$refs.form.resetFields();
        this.$router.push({path:'/dashboard'})
    },
  },
};
</script>