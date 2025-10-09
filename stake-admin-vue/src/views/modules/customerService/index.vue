<!--
* @Date: 2024/07/22 16:36:22
* @LastEditTime: 2024/07/22 16:36:22
* @Description:客服
-->
<template>
    <div class="app-container">
        <!-- 新增：连接状态提示 -->
        <div v-if="connectionStatus !== 'open'" style="background:#f56c6c;color:#fff;padding:8px 16px;text-align:center;">
            <span v-if="connectionStatus==='reconnecting'">WebSocket 连接断开，正在自动重连...</span>
            <span v-else-if="connectionStatus==='fail'">WebSocket 重连失败，请刷新页面</span>
            <span v-else-if="connectionStatus==='closed'">WebSocket 已断开</span>
            <span v-else-if="connectionStatus==='error'">WebSocket 发生错误</span>
        </div>
        <appLayout :showtop="false" :showleft="true" style="display: flex;">
            <template #leftmain>
                <div class="user-box">

                    <div style="margin-bottom:10px">客户列表</div>
                    <!-- 列表 -->
                    <div class="list-user" ref="listUserRef">
                        <div class="user-tip" v-for="user in users" :key="user.id"
                            @click="selectUser(user.id, user.wallet,user.poolsId)" :class="{ 'selected': selectedUser === user.id }">
                            <div class="iaccount">{{ formatWallet(user.wallet) }}
                                <span v-if="user.online" :class="['status', 'online']">(在线)</span>
                                <span v-else :class="['status', 'offline']">(离线)</span>
                                <br>
                                <span class="last-date"> {{ user.lastDate }}</span>
                                <span class="owner"> ({{user.ownerName}}/{{user.inviter}})</span>
                                <span class="unread-badge" v-if="user.messageCount > 0">{{ user.messageCount }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
            <template #dmain>
                <div class="main" :style="'height:calc(100vh - 160px)'">
                    <!-- 头部 -->
                    <div class="message-top">
                        <div class="text">与{{ selectedUserName }}的消息</div>
                        <el-button v-if="selectedMessages.length == 0" type="danger" icon="el-icon-select" size="mini"
                            @click="selectMsg">选择消息</el-button>
                        <el-button v-if="selectedMessages.length > 0" type="danger" icon="el-icon-delete" size="mini"
                            @click="deleteMessages">删除选中的消息</el-button>
                        <el-button v-if="cancelShow" type="danger" icon="el-icon-select" size="mini"
                            @click="cancelMessages">取消</el-button>
                        <!-- <el-button type="primary" icon="el-icon-takeaway-box" size="mini">{{ $t('message.34') }}</el-button> -->
                        <!-- <el-button type="info" plain icon="el-icon-refresh" size="mini">{{ $t('message.38')
                        }}</el-button> -->
                    </div>
                    <!-- 消息 -->
                    <div class="message-main" id="scrollBox">
                        <div class="messages" v-for="msg in messages" :key="msg.id">

                            <!-- 发 -->
                            <!-- 图片 -->
                            <div class="box-message" v-if="msg.type === 1 && msg.senderId != selectedUser">
                                <img class="messageImg" :src="msg.content" alt=""
                                    @click="openImageModal(msg.content)" />
                                <div class="time-btn">{{ msg.created }}
                                    <input v-if="isCheckboxDisabled" type="checkbox" v-model="selectedMessages"
                                        :value="msg.id" class="checkbox-small" />
                                </div>
                            </div>
                            <!-- 文字 -->
                            <div class="box-message" v-if="msg.type === 0 && msg.senderId != selectedUser">
                                <div>{{ msg.content }} </div>
                                <div class="time-btn">{{ msg.created }}
                                    <input v-if="isCheckboxDisabled" type="checkbox" v-model="selectedMessages"
                                        :value="msg.id" class="checkbox-small" />
                                </div>

                            </div>
                            <!--收 -->
                            <div class="left-box-message" v-if="msg.type === 1 && msg.senderId == selectedUser">
                                <img class="messageImg" :src="msg.content" alt=""
                                    @click="openImageModal(msg.content)" />
                                <div class="time-btn">{{ msg.created }}
                                </div>
                            </div>
                            <!-- 文字 -->
                            <div class="left-box-message" v-if="msg.type === 0 && msg.senderId == selectedUser">
                                <div>{{ msg.content }} </div>
                                <div class="time-btn">{{ msg.created }}
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 底部 -->
                    <div class="message-bottom">
                        <div @click="sendMoreHandle">
                            <input ref="selectImgInp" class="selectInp" type="file" accept="image/*"
                                @change="handleFileChange" />
                            <img class="sendImg" src="../../../assets/images/img.png" alt="" />
                        </div>
                        <el-input type="textarea" :rows="1" v-model="msg" @keyup.enter.native="sendText"></el-input>
                        <el-button type="primary" size="mini" @click="sendText">{{ $t('message.37') }}</el-button>
                    </div>
                </div>
            </template>
        </appLayout>

        <!-- Image Modal -->
        <el-dialog class="currentImgDialog" :visible.sync="imageModalVisible" width="50%" append-to-body center
            :close-on-click-modal="false" @close="closeImageModal">
            <img class="currentImg" :src="currentImage" style="width: 100%;" />
        </el-dialog>

    </div>




</template>

<script>
import { connectWebSocket, sendMessage, buildMessage } from './websocket';
import { mapGetters } from 'vuex';
export default {
    name: "commodityOrder",
    data() {
        return {
            // 查询参数
            queryParams: {},
            msg: "",
            users: [],
            messages: [],
            selectedMessages: [], // 用于存储选中的消息ID
            isCheckboxDisabled: false,
            selectedUser: null, // 当前选中的用户
            selectPoolsId:'',
            cancelShow: false,
            selectedUserName: '',
            imageUrl: '', // 上传后图片的 URL
            timer: null,
            heartbeatInterval: null,
            imageModalVisible: false,
            currentImage: '',
            connectionStatus: 'open', // 新增：WebSocket连接状态
            reconnectingNotified: false, // 新增：避免重复弹窗
        };
    },
    created() {
        this.fetchUsers();

    },
    mounted() {
        this.$refs.selectImgInp.value = null;
        this.fetchUsers();
        this.initWebSocket(); // 用新方法初始化 WebSocket
        this.timer = setInterval(() => {
            this.refreshUsers();
        }, 3000);



    },

    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer); // 组件销毁前清除定时器
            this.timer = null;
        }

    },

    computed: {
        ...mapGetters(['pools', "userId"]),
    },
    methods: {
        // 刷新用户列表并保持页面稳定
        refreshUsers() {
            const scrollBox = document.querySelector(".list-user") ? document.querySelector(".list-user") : this.$refs.listUserRef;

        //    console.log('scrollBox:', scrollBox)
            if (!scrollBox) return; // 关键检查
            const scrollPosition = scrollBox.scrollTop; // 保存当前滚动位置
            this.fetchUsers();
            this.$nextTick(() => {
                if (scrollBox) { // 再次检查防止异步后元素被销毁
                    scrollBox.scrollTop = scrollPosition;
                }
            });

        },
        selectMsg() {
            this.isCheckboxDisabled = true;
            this.cancelShow = true;
        },
        cancelMessages() {
            this.selectedMessages = [];
            this.isCheckboxDisabled = false;
            this.cancelShow = false;
        },
        deleteMessages() {
            // 删除选中的消息
            if (this.selectedMessages.length == 0) return;

            if(this.userId != this.selectPoolsId){
                this.$message.error('您不是当前用户的管理员,无法删除消息.');
                return ;
            }
            var messageIds = this.selectedMessages;
            this.$http({
                url: this.$http.adornUrl("/admin/message/del"),
                method: "post",
                data: this.$http.adornData(
                    {
                        messageIds: messageIds
                    })
            }).then(({ data }) => {
                if (data.success) {
                    this.messages = this.messages.filter(msg => !this.selectedMessages.includes(msg.id));
                    this.scrollToBottom()
                    this.selectedMessages = [];
                    this.isCheckboxDisabled = false;
                    this.cancelShow = false;
                } else {
                    this.$message.error(data.msg);
                    this.selectedMessages = [];
                    this.isCheckboxDisabled = false;
                    this.cancelShow = false;
                }
            })


        },

        openImageModal(imageUrl) {
            console.log(imageUrl)
            this.currentImage = imageUrl;
            this.imageModalVisible = true;
        },
        closeImageModal() {
            this.currentImage = '';
            this.imageModalVisible = false;
        },
        formatWallet(wallet) {
            if (wallet.length <= 20) {
                return wallet; // 如果钱包地址小于等于20个字符，不进行截取
            }
            return wallet.substring(0, 12) + "..." + wallet.substring(wallet.length - 12);
        },
        selectUser(userId, wallet,poolsId) {
            this.selectedUser = userId;
            this.selectedUserName = wallet;
            this.selectPoolsId = poolsId;
            this.getMessageList();
        },
        onMessageReceived(message) {
            if (message.senderId == this.selectedUser) { //当前选中用户的消息才显示
                this.messages.push(message);
                sendMessage(buildMessage(this.userId, this.selectedUser, 3, 'ready'));
            }
            this.scrollToBottom();
        },
        // 获取用户列表
        fetchUsers() {
            this.$http({
                url: this.$http.adornUrl("/admin/message/listUser"),
                method: "post",
            }).then(({ data }) => {
                if (data.success) {
                    this.users = data.data;
                } else {
                    this.$message.error(data.msg);
                }
            })
        },
        scrollToBottom() {
            const scrollBox = document.getElementById("scrollBox")
            this.$nextTick(() => {
                scrollBox.scrollTop = scrollBox.scrollHeight;
            });
        },
        sendMoreHandle() {
            this.$refs.selectImgInp.click();
        },
        handleFileChange(event) {
            const file = event.target.files[0];
            if (!file) return;

            // 限制文件大小为 5MB
            if (file.size > 5 * 1024 * 1024) {
                alert('请选择小于 1MB 的图片');
                return;
            }

            const formData = new FormData();
            formData.append('file', file);

            // 上传图片到服务器
            this.uploadImage(formData);

            this.$refs.selectImgInp.value = null;
        },
        uploadImage(formData) {
            this.$http({
                url: this.$http.adornUrl("/upload/image"),
                method: "post",
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data', // 设置内容类型为 multipart/form-data
                },
            }).then(({ data }) => {
                this.loading = false
                if (data.success) {
                    this.imageUrl = data.url;
                    this.sendImage();
                } else {
                    this.$message.error(data.msg);
                }
            })
        },
        // 删除
        delMsg() {
        },
        initWebSocket() {
            // 新增：统一初始化和重连 WebSocket
            this.reconnectingNotified = false;
            connectWebSocket(this.userId, this, this.onMessageReceived, this.onConnectionStatusChange);
        },
        onConnectionStatusChange(status) {
            this.connectionStatus = status;
            if (status === 'reconnecting' && !this.reconnectingNotified) {
                this.$message.error('WebSocket 连接断开，正在自动重连...');
                this.reconnectingNotified = true;
            }
            if (status === 'open') {
                this.$message.success('WebSocket 已连接');
                this.reconnectingNotified = false;
            }
            if (status === 'fail') {
                this.$message.error('WebSocket 重连失败，请刷新页面');
            }
        },
        sendText() {
            if (!this.selectedUser) {
                this.$message.error('请选择需要发送消息的用户.');
                return;
            }
            if (this.msg.replace(/\s+/g, '') == '') {
                return;
            }
            if(this.userId != this.selectPoolsId){
                this.$message.error('您不是当前用户的管理员,无法发送消息.');
                return ;
            }
            let _message_ = buildMessage(this.userId, this.selectedUser, 0, this.msg);
            let send = sendMessage(_message_);
            if (!send) {
                if (!this.reconnectingNotified) {
                    this.$message.error('WebSocket 断开，正在自动重连...');
                    this.reconnectingNotified = true;
                }
                this.initWebSocket();
                return;
            }
            this.messages.push(_message_);
            this.scrollToBottom();
            this.msg = '';
        },
        sendImage() {
            if (!this.selectedUser) {
                this.$message.error('请选择需要发送消息的用户.');
                return;
            }
            if(this.userId != this.selectPoolsId){
                this.$message.error('您不是当前用户的管理员,无法发送消息.');
                return ;
            }
            if (this.imageUrl.replace(/\s+/g, '') == '') {
                return;
            }
            let _message_ = buildMessage(this.userId, this.selectedUser, 1, this.imageUrl);
            let send = sendMessage(_message_);
            if (!send) {
                if (!this.reconnectingNotified) {
                    this.$message.error('WebSocket 断开，正在自动重连...');
                    this.reconnectingNotified = true;
                }
                this.initWebSocket();
                return;
            }
            this.messages.push(_message_);
            this.scrollToBottom();
            this.imageUrl = '';
        },
        // 查询历史消息
        getMessageList() {
            this.messages = [];
            this.$http({
                url: this.$http.adornUrl("/admin/message/getMessage"),
                method: "post",
                data: this.$http.adornData(
                    {
                        id: this.selectedUser
                    }
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.messages = data.data; //显示当前用户和 这个钱包的所有消息 
                    // this.messages = data.data.filter(item=>{
                    //     return item.senderId == this.userId &&item.reciverId == this.selectedUser
                    // });
                    console.log(this.messages)
                    this.scrollToBottom()
                } else {
                    this.messages = [];
                    this.$message.error(data.msg);
                }
            })
        },

    },
};
</script>

<style lang="scss">
.currentImg {
    object-fit: contain;
    max-width: 400px !important;
}

.currentImgDialog .el-dialog__body {
    text-align: center !important;
    max-height: 400px;
    overflow: hidden;
    overflow-y: auto;
}

.user-tip.selected {
    background-color: #1d232b;
    /* 设置选中后的背景色 */
    border: 2px solid #4caf50;
    /* 添加边框颜色 */
}


.layout-app .right {
    flex: 1 !important;
}

.user-box .el-card__body .el-input--small {
    height: 36PX !important;
    max-width: 130PX !important;
}
</style>
<style lang="scss" scoped>
/* 自定义整个滚动条 */
::-webkit-scrollbar {
    width: 0px;
    /* 设置滚动条的宽度 */
    background-color: #f9f9f9;
    /* 滚动条的背景色 */
}

/* 自定义滚动条轨道 */
::-webkit-scrollbar-track {
    background: #e1e1e1;
    /* 轨道的背景色 */
    border-radius: 3px;
    /* 轨道的圆角 */
}

/* 自定义滚动条的滑块（thumb） */
::-webkit-scrollbar-thumb {
    background-color: #c1c1c1;
    /* 滑块的背景色 */
    border-radius: 3px;
    /* 滑块的圆角 */
    border: 2px solid #ffffff;
    /* 滑块边框 */
}

/* 滑块hover效果 */
::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
    /* 滑块hover时的背景色 */
}

.el-card .el-form-item__label {
    min-width: 52PX;
}

.el-card__body .el-input--small {
    height: 36PX !important;
    max-width: 130PX !important;
}

.el-form-item--medium .el-form-item__content {
    max-width: 130PX !important;
}

.el-textarea {
    width: 80% !important;
    height: 36px !important;
    color: #000 !important;
    border-radius: 36px;

}

.el-textarea__inner {
    color: #000 !important;
    border-radius: 36px !important;
}

.layout-app .left .left-crad {
    height: 100%;
    background: #1d1d1d;
    color: #fff; 
}

.el-button--primary {
    margin-bottom: 0PX !important;
    border-radius: 25px !important;
    padding: 0 25px !important;

}

.el-button--info {
    margin-bottom: 0 !important;
    border-radius: 25px !important;
    padding: 0 25px !important;
}

.list-user {
    height: calc(100vh - 180px);
    overflow: hidden;
    overflow-y: auto;

    .iaccount {
        color: #fff;
        word-wrap: break-word;
        /* 允许在单词内换行 */
        overflow-wrap: break-word;
        /* 同上，现代浏览器推荐使用这个属性别名 */
        white-space: normal;
        /* 允许自动换行 */
        cursor: pointer;
        /* 鼠标移上去时显示手形 */
    }

    .time {
        text-align: right;
        color: #909399;
    }

    margin-right: -10px;

    .user-tip {
        height: 60px;
        border-radius: 10px;
        padding: 10px;
        margin-bottom: 10px;
        position: relative;

        &:hover {
            background: #1d232b;
        }

        .unread-badge {
            position: absolute;
            right: 10px;
            display: inline-block;
            width: 20px;
            height: 20px;
            text-align: center;
            background-color: red;
            color: white;
            border-radius: 50%;
        }
    }
}

// 消息主体
.main {
    display: flex;
    flex-direction: column;

    .message-main {
        width: 100%;
        flex: 1;
        overflow: hidden;
        padding: 10px 0;
        overflow-y: auto;
        display: flex;
        flex-direction: column;

        .messageImg {
            width: 200px;
            height: 200px;
            cursor: pointer;
        }

        .messages {
            display: flex;
            flex-direction: column;
            // align-items: flex-end;
        }


    }
}

// 发
.box-message {
    align-self: flex-end;
    background-color: #434343;
    padding: 10px;
    border-radius: 15px;
    color: #fff;
    margin-bottom: 20px;
    max-width: 500px;
    width: 400px;

    .time-btn {
        color: #909399;
        text-align: right;
        font-size: 12px;

        .el-icon-delete {
            color: red;
            cursor: pointer;
        }

        .el-icon-select {
            color: rgb(0, 255, 166);
            cursor: pointer;
        }


    }

}

//收
.left-box-message {
    align-self: flex-start;
    background-color: #434343;
    padding: 10px;
    border-radius: 15px;
    color: #fff;
    margin-bottom: 20px;
    max-width: 220px;
    width: 400px;

    .time-btn {
        color: #909399;
        text-align: right;
        font-size: 12px;

        .el-icon-delete {
            color: red;
            cursor: pointer;
        }
    }

}

.message-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    color: #fff;
}

.checkbox-small {
    width: 12px;
    height: 12px;
    margin-left: 10px;
    opacity: 0.5;
    color: #909399;
    transition: opacity 0.2s ease-in-out;
}


.checkbox-small:hover {
    cursor: pointer;
}



.el-button--danger {
    background-color: #f56c6c;
    color: #fff;
}

.message-bottom {
    height: 56px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;

    .selectInp {
        position: absolute;
        width: 100%;
        height: 40px;
        left: 0;
        top: 0;
        visibility: hidden;
    }

    .sendImg {
        width: 23px;
        height: 20px;
    }
}

.last-date {
    color: gray;
    /* 设置字体颜色为灰色 */
    font-size: 12px;
    /* 设置字体大小变小 */
}


.status {
    font-size: 12px;
    /* 字体变小 */
}

.online {
    color: green;
    /* 在线状态绿色 */
}

.owner {
    color: green;
    /* 在线状态绿色 */
    font-size:12px;
}


.offline {
    color: gray;
    /* 离线状态灰色 */
}
</style>