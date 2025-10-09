<!-- 平台提现-->
<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <el-form-item label="日期">
                                <el-date-picker v-model="dateValue" type="daterange" range-separator="—"
                                    start-placeholder="开始时间" @change="changeDate" value-format='yyyy-MM-dd'
                                    end-placeholder="结束时间">
                                </el-date-picker>
                            </el-form-item>
                            <el-form-item label="发送钱包">
                                <el-input v-model="queryParams.fromWallet" clearable placeholder="发送钱包"></el-input>
                            </el-form-item>
                            <el-form-item label="收款钱包">
                                <el-input v-model="queryParams.toWallet" clearable placeholder="收款钱包"></el-input>
                            </el-form-item>
                            <el-form-item label="代理账号">
                                <el-input v-model="queryParams.proxyAccount" clearable placeholder="代理账号"></el-input>
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
                    <el-button type="primary" size="mini" @click="openEdit = true">代理提现</el-button>
                    <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                </div>
                <el-table v-loading="loading" ref="multipleTable" :data="tableData" style="width: 100%"
                    :height="scope.maxheight">
                    <el-table-column prop="created" label="日期" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="fromWallet" label="发送钱包" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.fromWallet)">{{ scope.row.fromWallet
                                }}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="toWallet" label="收款钱包" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.toWallet)">{{ scope.row.toWallet
                                }}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="proxyAccount" label="代理账号" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="beforeUsdc" label="提现前USDC" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="afterUsdc" label="提现后USDC" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-tag type='warning' v-if="scope.row.status == 0">等待执行 </el-tag>
                            <el-tag type='success' v-if="scope.row.status == 1">提现完成</el-tag>
                            <el-tag type='danger' v-if="scope.row.status == -1">提现失败</el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="usdc" label="提现USDC" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="hash" label="提现Hash" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openHash(scope.row.hash)">{{ scope.row.hash }}</el-button>
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
        <el-dialog title="代理提现" :visible.sync="openEdit" append-to-body :width="dialogWidth"
            :close-on-click-modal="false" @close="canceladd">
            <el-form ref="addForm" :model="addForm" :rules="rules" inline :label-width="labelWidth" size="small">
                <el-form-item label="提现钱包" prop="formWallet">
                    <el-input v-model="addForm.fromWallet" placeholder="提现钱包"></el-input>
                </el-form-item>
                <el-form-item label="收款钱包" prop="toWallet">
                    <el-input v-model="addForm.toWallet" placeholder="提现钱包"></el-input>
                </el-form-item>
                <el-form-item label="代理账号" prop="proxyAccount">
                    <el-input v-model="addForm.proxyAccount" placeholder="代理账号"></el-input>
                </el-form-item>
                <el-form-item label="提现USDC" prop="usdc">
                    <el-input v-model="addForm.usdc" placeholder="提现USDC"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveEdit" size="small">保存</el-button>
                <el-button @click="canceladd" type="info" plain size="small">取消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { timeList } from '@/utils/index';
import { formValidate } from '@/utils/decorator';
import resizeWIdth from '@/mixins/resizeWIdth';
export default {
    name: "commodityOrder",
    mixins: [resizeWIdth],
    data() {
        return {
            openEdit: false,
            tableData: [],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            addForm: {
                fromWallet: "",
                toWallet: "",
                proxyAccount: "",
                usdc: ""
            },
            rules: {
                fromWallet: [{ required: true, message: '请输入提现钱包', trigger: 'blur' }],
                toWallet: [{ required: true, message: '请输入收款', trigger: 'blur' }],
                proxyAccount: [{ required: true, message: '请输入代理账号', trigger: 'blur' }],
                usdc: [{ required: true, message: '请输入提现金额', trigger: 'blur' }],
            },
            // 总计
            total: 0,
            dateValue: [],
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                proxyAccount: '',
                fromWallet: '',//钱包地址,
                toWallet: '',//收款钱包
                start: '',//开始时间
                end: ''//结束时间
            },
            loading: false,
        };
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
                this.dateValue = ""
                this.queryParams.start = '';
                this.queryParams.end = '';
            }
        },
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/proxyWithdraw/list"),
                method: "post",
                data: this.$http.adornParams(
                    this.queryParams
                ),
            }).then(({ data }) => {
                this.loading = false
                if (data.success) {
                    this.tableData = data.data.records;
                    this.total = data.data.total;
                } else {
                    this.$message.error(data.msg);
                }
            })
            this.loading = false;
        },
        // 搜索
        search() {
            this.getList()
        },
        // 重置
        restSearchData() {
            this.queryParams = this.$options.data().queryParams;
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
        // 取消
        canceladd() {
            this.openEdit = false;
            this.addForm = this.$options.data().addForm;
            this.$refs['addForm'].resetFields();
        },

        async saveEdit() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/proxyWithdraw/submit"),
                method: "post",
                data: this.$http.adornParams(
                    this.addForm
                ),
            }).then(({ data }) => {
                this.loading = false
                if (data.success) {
                    this.$message.success('提现发起成功');
                    this.canceladd();
                    this.getList();
                } else {
                    this.$message.error(data.msg);
                }
            })
            this.loading = false;
        },
    },
};
</script>

<style lang="scss" scoped>
::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
}
</style>