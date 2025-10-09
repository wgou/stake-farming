<!-- 客户钱包提现列表 -->
<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <el-form-item label="日期">
                                <el-date-picker v-model="dateValue" type="daterange" range-separator="——"
                                    :start-placeholder="$t('message.512')" @change="changeDate"
                                    value-format="yyyy-MM-dd"
                                    :end-placeholder="$t('message.513')">
                                </el-date-picker>
                            </el-form-item>
                            <el-form-item :label="$t('message.5')">
                                <el-input v-model="queryParams.wallet" clearable
                                    :placeholder="$t('message.5')"></el-input>
                            </el-form-item>
                            <!-- <el-form-item :label="$t('message.6')">
                                <el-select v-model="queryParams.isStaked" clearable :placeholder="$t('message.6')">
                                    <el-option v-for="item in isStakedOptions" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item> -->
                            <el-form-item :label="$t('message.15')">
                                <el-select v-model="queryParams.poolsId" clearable :placeholder="$t('message.15')">
                                    <el-option v-for="item in pools" :key="item.value" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="$t('message.16')">
                                <el-select v-model="queryParams.inviteId" clearable :placeholder="$t('message.16')">
                                    <el-option v-for="item in invitesList" :key="item.value" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="$t('message.520')">
                                <el-select v-model="queryParams.real" clearable :placeholder="$t('message.520')">
                                    <el-option v-for="item in reals" :key="item.value" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="$t('message.523')">
                                <el-select v-model="queryParams.status" clearable :placeholder="$t('message.523')">
                                    <el-option v-for="(item, index) in withdrawList" :key="index" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="Has Hash">
                                <el-select v-model="queryParams.hasHash" clearable placeholder="Has Hash">
                                    <el-option v-for="item in hasHashOptions" :key="item.value" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
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
                <div>
                    <div>
                        <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                    </div>
                    <el-table v-loading="loading" ref="multipleTable" :height="scope.maxheight - 60"
                        @cell-click="copyToClipboard" :data="tableData" style="width: 100%;">
                        <el-table-column prop="created" :label="$t('message.23')"   width="150px">
                        </el-table-column>
                        <el-table-column prop="wallet" :label="$t('message.5')"   width="200px">
                            <template slot-scope="scope">
                                <el-button type="text" @click="openWallet(scope.row.wallet)">{{ scope.row.wallet
                                    }}</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column prop="balance" label="钱包余额" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>

                        <el-table-column prop="withdrawUsdc" label="USDC金额" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <!-- 状态 0：待处理  1：处理中 2：成功  -1： 失败 -->
                        <el-table-column prop="status" :label="$t('message.523')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                <el-tag type='info' v-if="scope.row.status == 0">待审批 </el-tag>
                                <el-tag type='warning' v-if="scope.row.status == 1">审批通过 </el-tag>
                                <el-tag type='success' v-if="scope.row.status == 2">执行中 </el-tag>
                                <el-tag type='success' v-if="scope.row.status == 3">成功 </el-tag>
                                <el-tag type='danger' v-if="scope.row.status == -1">失败 </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="remark" label="客户备注" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="redesc" label="系统备注" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="hash" :label="$t('message.534')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                <el-button type="text" @click="openHash(scope.row.hash)">{{ scope.row.hash
                                    }}</el-button>
                            </template>
                        </el-table-column>

                    <el-table-column prop="inviteName" label="资金池/招聘员" width="120px">
                        <template slot-scope="scope">
                            {{ (scope.row.pools ? scope.row.pools : '-') + '/' + (scope.row.inviteName ?
                                scope.row.inviteName : '-') }}
                        </template>
                    </el-table-column>

                        <el-table-column label="操作" width="160px" >
                            <template slot-scope="scope">
                                <el-button type="text" v-if="scope.row.status == 0" size="small"
                                    @click="passHandler(scope.row)">通过</el-button>
                                <el-button type="text" v-if="scope.row.status == 0" size="small"
                                    @click="rejectHandler(scope.row)">拒绝</el-button>
                                <el-button type="text" v-if="scope.row.status == -1" size="small"
                                    @click="recoverHandler(scope.row)">恢复</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <!-- 表格底部 -->
                    <div class="tableSum">
                        <!-- 总体现 -->
                        <div class="totalWithdrawal">实际总提现:{{ totalUsdc }}</div>
                    </div>
                </div>
                <!-- 分页 -->
                <div class="page">
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                        :current-page="queryParams.current" :page-sizes="[100,20, 30, 40, 50,200]"
                        :page-size="queryParams.size" layout="total, sizes, prev, pager, next" :total="total">
                    </el-pagination>
                </div>
                <el-dialog title="拒绝" :visible.sync="showEdit" append-to-body :close-on-click-modal="false"
                    width="583px" @close="canceladd">
                    <el-form ref="optParams" :model="optParams" :label-width="labelWidth" :rules="rules" inline
                        size="small">
                        <el-form-item label="拒绝理由" prop="remark">
                            <el-input v-model="optParams.remark" type="textarea" placeholder="拒绝理由"></el-input>
                        </el-form-item>
                        <el-form-item label="Google令牌" prop="googleAuthCode">
                            <el-input v-model="optParams.googleAuthCode" placeholder="请输入Google令牌" required></el-input>
                        </el-form-item>
                    </el-form>
                    <div slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="rejectHandlerFun" size="small">确认拒绝</el-button>
                        <el-button @click="canceladd" type="info" plain size="small">{{ $t('message.44') }}</el-button>
                    </div>
                </el-dialog>
            </template>
        </appLayout>
    </div>
</template>

<script>
// import { postRequest } from '@/api/requestApi';
import { formValidate } from '@/utils/decorator';
import { timeList } from '@/utils/index';
import { mapGetters } from 'vuex';
import resizeWIdth from '@/mixins/resizeWIdth';
export default {
    name: "commodityOrder",
    mixins: [resizeWIdth],
    data() {
        return {
            labelSize: 6,
            showEdit: false,
            dateValue: '',
            tableData: [],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            // 总计
            total: 0,
            totalUsdc: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                start: '',
                end: '',
                poolsId: '',//资金池
                inviteId: '',//招聘员
                wallet: '',//钱包地址,
                status: '',//状态
                real: '',//真实/虚拟
                hash: '',//hasHash
            },
            optParams: {
                id: undefined,
                remark: '',
                googleAuthCode: ''
            },
            hasHashOptions:
                [
                    { value: 0, label: "否" },
                    { value: 1, label: "是" },

                ],
            time: '', //支付时间
            times: [], //订单时间范围
            tableData: [],
            loading: false,
            rules: {
                remark: [
                    { required: true, message: '请输入拒绝理由', trigger: 'blur' }
                ],
                googleAuthCode: [
                    { required: true, message: '请输入Google令牌', trigger: 'blur' }
                ]
            },
        };
    },
    computed: {
        ...mapGetters(['pools', 'invitesList', 'reals', 'withdrawList'])
    },
    created() {
        this.getList();
    },
    methods: {
        changeDate(data) {
            if (data) {
                this.queryParams.start = data[0] + ' 00:00:00';
                this.queryParams.end = data[1] + ' 23:59:59';
            } else {
                this.queryParams.start = '';
                this.queryParams.end = '';
            }
        },
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/withdraw/list"),
                method: "post",
                data: this.$http.adornParams(
                    this.queryParams
                ),
            }).then(({ data }) => {
                this.loading = false
                if (data.success) {
                    this.tableData = data.data.records;
                    this.total = data.data.total;
                    this.totalUsdc = data.totalUsdc;
                } else {
                    this.$message.error(data.msg);
                }
            })
        },
        //通过
        passHandler(row) {
            this.$prompt('请输入Google令牌', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                if (!value) {
                    this.$message.error('请输入Google令牌');
                    return;
                }
                this.loading = true;
                const requestData = {
                    id: row.id,
                    googleAuthCode: value,
                };
                this.$http({
                    url: this.$http.adornUrl("/admin/withdraw/pass"),
                    method: "post",
                    data: this.$http.adornData(requestData)
                }).then(({ data }) => {
                    this.loading = false;
                    if (data && data.success) {
                        this.$message({
                            message: "操作成功",
                            type: "success",
                            duration: 1500,
                            onClose: () => {
                                this.getList();
                            }
                        });
                    } else {
                        console.error('Error response:', data);
                        this.$message.error(data.msg || "操作失败");
                    }
                }).catch((error) => {
                    console.error('Request error:', error);
                    this.loading = false;
                    this.$message.error("操作失败，请稍后重试");
                });
            }).catch(() => {});
        },
        //拒绝
        rejectHandler(row) {
            this.optParams = {
                id: row.id,
                remark: '',
                googleAuthCode: '',
                poolsIds: []
            };
            this.showEdit = true;
        },
        rejectHandlerFun() {
            this.$refs["optParams"].validate(valid => {
                if (valid) {
                    this.loading = true;
                    this.$http({
                        url: this.$http.adornUrl("/admin/withdraw/reject"),
                        method: "post",
                        data: this.$http.adornData({
                            id: this.optParams.id,
                            remark: this.optParams.remark,
                            googleAuthCode: this.optParams.googleAuthCode,
                        })
                    }).then(({ data }) => {
                        this.loading = false;
                        if (data && data.success) {
                            this.$message({
                                message: "操作成功",
                                type: "success",
                                duration: 1500,
                                onClose: () => {
                                    this.showEdit = false;
                                    this.getList();
                                }
                            });
                        } else {
                            this.$message.error(data.msg || "操作失败");
                        }
                    }).catch(() => {
                        this.loading = false;
                    });
                }
            });
        },
        //恢复
        recoverHandler(row) {
            this.$prompt('请输入Google令牌', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                if (!value) {
                    this.$message.error('请输入Google令牌');
                    return;
                }
                this.loading = true;
                this.$http({
                    url: this.$http.adornUrl("/admin/withdraw/recover"),
                    method: "post",
                    data: this.$http.adornData({
                        id: row.id,
                        googleAuthCode: value,
                    }),
                }).then(({ data }) => {
                    this.loading = false;
                    if (data && data.success) {
                        this.$message({
                            message: "操作成功",
                            type: "success",
                            duration: 1500,
                            onClose: () => {
                                this.getList();
                            }
                        });
                    } else {
                        this.$message.error(data.msg || "操作失败");
                    }
                }).catch(() => {
                    this.loading = false;
                });
            }).catch(() => {});
        },
        // 搜索
        search() {
            this.getList()
        },
        // 重置
        restSearchData() {
            this.time = '';
            this.times = []
            this.queryParams = this.$$options.data().queryParams;
            this.getList();
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
        canceladd() {
            this.showEdit = false;
            this.addForm = this.$options.data().addForm;
        },

        copyToClipboard(row, column, cell, event) {
            if (column.property !== 'wallet') return;
            var text = row.wallet;
            navigator.clipboard.writeText(text).then(() => {
                this.$message.success(text + ' Copied to clipboard!');
            }).catch(err => {
                this.$message.error('Failed to copy: ' + err);
            });
        },
    },
};
</script>

<style lang="scss" scoped>
::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
}
::v-deep .el-table__body tr.hover-row > td,
::v-deep .el-table__row.hover-row,
::v-deep .el-table__row.hover-row > td,
::v-deep .el-table__body tr:hover > td,
::v-deep .el-table__row:hover,
::v-deep .el-table__row:hover > td {
  background: #1d1d1d !important;
  background-color: #1d1d1d !important;
  color: #fff !important;
}
::v-deep .el-table__footer-wrapper tbody td {
    background-color: #1d1d1d !important;
}

.tableSum {
    width: 100%;
    min-height: 57px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 14px;
    font-weight: 400;
    color: #FFFFFF;
    border-bottom: #434343 1px solid;

    .totalWithdrawal {
        margin-right: 30px;
    }
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
.el-table__footer-wrapper tbody td {}
</style>