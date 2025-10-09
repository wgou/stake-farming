<!-- 平台提现-->
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
                            <el-form-item label="收款钱包">
                                <el-input v-model="queryParams.fromWallet" clearable placeholder="收款钱包"></el-input>
                            </el-form-item>
                            <el-form-item label="归集钱包">
                                <el-input v-model="queryParams.toWallet" clearable placeholder="归集钱包"></el-input>
                            </el-form-item>
                            <el-form-item label="币种">
                                <el-select v-model="queryParams.coin" clearable placeholder="状态">
                                    <el-option key="USDC" label="USDC" value="USDC"></el-option>
                                    <el-option key="ETH" label="ETH" value="ETH"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="状态">
                                <el-select v-model="queryParams.status" clearable placeholder="状态">
                                    <el-option v-for="item in collect" :key="item.label" :label="item.label"
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
                <el-table v-loading="loading" ref="multipleTable" :data="tableData" style="width: 100%"
                    :height="scope.maxheight">

                    <el-table-column prop="fromWallet" label="收款钱包" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.fromWallet)">
                                {{ scope.row.fromWallet }}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="toWallet" label="归集钱包" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.toWallet)">
                                {{ scope.row.toWallet }}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="coin" label="币种" >
                        <template slot-scope="scope">
                            <el-tag type='warning' v-if="scope.row.coin == 'USDC'">USDC </el-tag>
                            <el-tag type='success' v-if="scope.row.coin == 'ETH'">ETH</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="amount" label="归集数量" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-tag type='warning' v-if="scope.row.status == 0">等待归集 </el-tag>
                            <el-tag type='success' v-if="scope.row.status == 1">归集完成</el-tag>
                            <el-tag type='danger' v-if="scope.row.status == -1">归集失败</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="created" label="日期" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="hash" label="归集Hash" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openHash(scope.row.hash)">{{ scope.row.hash }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                  <!-- 表格底部 -->
                  <div class="tableSum">
                        <!-- 总体现 -->
                        <div class="totalWithdrawal">合计USDC: {{ totalUsdc }} ,  ETH: {{ totalEth }}</div>
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
import resizeWIdth from '@/mixins/resizeWIdth';
import { mapGetters } from 'vuex';
export default {
    name: "commodityOrder",
    mixins: [resizeWIdth],
    data() {
        return {
            openEdit: false,
            tableData: [],//表格数据
            // 显示搜索
            showSearch: true,
            // 总计
            total: 0,
            totalEth:0,
            totalUsdc:0,
            dateValue: [],
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                fromWallet: '',//钱包地址,
                toWallet: '',//收款钱包
                coin:'',
                status: '',
                start: '',//开始时间
                end: ''//结束时间
            },
            loading: false,
        };
    },
    computed: {
        ...mapGetters(['collect'])
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
                url: this.$http.adornUrl("/admin/collect/list"),
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
                    this.totalEth = data.totalEth;
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
    },
};
</script>

<style lang="scss" scoped>
::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
}
::v-deep .el-table__footer-wrapper tbody td {
    background-color: #1d1d1d !important;
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
</style>