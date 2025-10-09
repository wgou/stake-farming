<!-- 划账记录 -->
<!--
* @Date: 2024/07/22 16:36:22
* @LastEditTime: 2024/07/22 16:36:22
* @Description:划账记录
-->
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
                            <el-form-item label="资金池">
                                <el-select v-model="queryParams.poolsId" clearable placeholder="资金池">
                                    <el-option v-for="item in pools" :key="`pools-${item.label}-${item.value}`" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="招聘人员">
                                <el-select v-model="queryParams.inviteId" clearable placeholder="招聘人员">
                                    <el-option v-for="item in invitesList" :key="`invite-${item.value}-${item.label}`" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="状态">
                                <el-select v-model="queryParams.status" clearable placeholder="状态">
                                    <el-option v-for="item in collect" :key="`status-${item.label}-${item.value}`" :label="item.label"
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
                        <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                    </div>
                    <el-table v-loading="loading" ref="multipleTable" @cell-click="copyToClipboard" :data="tableData"
                    :height="scope.maxheight">
                        <el-table-column label="#" type="index" :formatter="formatterColumn" show-overflow-tooltip
                            width="60px">
                        </el-table-column>
                        <el-table-column prop="created" :label="$t('message.23')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="wallet" :label="$t('message.5')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                <el-button type="text" @click="openWallet(scope.row.wallet)">{{ scope.row.wallet
                                    }}</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column prop="reciverWallet" :label="$t('message.20')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                <el-button type="text" @click="openWallet(scope.row.reciverWallet)">{{
                                    scope.row.reciverWallet }}</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column prop="usdc" label="USDC金额" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="pools" :label="$t('message.548')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                {{ (scope.row.pools ? scope.row.pools : '-') + '/' + (scope.row.inviteName ?
                                    scope.row.inviteName : '-') }}
                            </template>
                        </el-table-column>

                    <el-table-column prop="status" label="状态" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-tag type='warning' v-if="scope.row.status == 0">等待 </el-tag>
                            <el-tag type='success' v-if="scope.row.status == 1">成功</el-tag>
                            <el-tag type='danger' v-if="scope.row.status == -1">失败</el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="auto" label="类型" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-tag type='warning' v-if="scope.row.auto == 0">手动 </el-tag>
                            <el-tag type='danger' v-if="scope.row.auto == 1">自动</el-tag>
                        </template>
                    </el-table-column>


                        <el-table-column prop="hash" label="Hash" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                <el-button type="text" @click="openHash(scope.row.hash)">{{ scope.row.hash }}</el-button>
                            </template>

                        </el-table-column>
                    </el-table>
                    <!-- 表格底部 -->
                    <div class="tableSum">
                        <div class="totalWithdrawal">总计:{{ totalUsdc }}</div>
                    </div>
             
                <!-- 分页 -->
                <div class="page">
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="queryParams.current" :page-sizes="[100,20, 30, 40, 50,200]"
                        :page-size="queryParams.size" layout="total, sizes, prev, pager, next" :total="total">
                    </el-pagination>
                </div>
           
            </template>
        </appLayout>
    </div>
</template>

<script>
import { timeList } from '@/utils/index';
import { mapGetters } from 'vuex';
export default {
    name: "debitRecord",
    data() {
        return {
            dateValue: [],
            tableData: [{}],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            totalUsdc: 0,
            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                wallet: '',//钱包地址,
                poolsId: '',//资金池id
                inviteId: "",//招聘人员id
                status:'',
                start: '',//开始时间
                end: '',//结束时间

            },
            loading: false,
        };
    },
    created() {
        this.getList();
    },
    computed: {
        ...mapGetters(['pools', "invitesList",'collect']),
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
                url: this.$http.adornUrl("/admin/transfer/list"),
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
            this.loading = false;
        },
        // 搜索
        search() {
            this.getList()
        },
        // 重置
        restSearchData() {
            this.time = '';
            this.times = []
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


::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
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
}
.tableSum .totalWithdrawal {
    margin-right: 30px;
}
</style>