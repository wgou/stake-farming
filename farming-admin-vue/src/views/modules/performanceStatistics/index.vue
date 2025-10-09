<!-- 业绩统计 -->
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
                    <el-table v-loading="loading" ref="multipleTable" :hight="scope.maxheight" :data="tableData"
                        style="width: 100%;">
                        <el-table-column label="#" type="index" :formatter="formatterColumn" show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="wallet" :label="$t('message.5')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template slot-scope="scope">
                                <el-button type="text" @click="openWallet(scope.row.wallet)">{{ scope.row.wallet
                                    }}</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column prop="created" label="Date" :formatter="formatterColumn" show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="pools" :label="$t('message.15')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="inviteName" :label="$t('message.16')" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="usdc" label="USDC金额" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <!-- <el-table-column prop="withdraw" label="提现金额" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column> -->
                        <!-- <el-table-column label="净收益" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template #default="scope">
                                {{(scope.row.usdc - scope.row.withdraw).toFixed(2)}}
                            </template>
                        </el-table-column> -->
                    </el-table>
                    <!-- 表格底部 -->
                    <div class="tableFooter">
                        <div class="sumItem">{{ $t('message.564') }} <span>{{ totalUsdc }}</span></div>
                        <div class="sumItem">{{ $t('message.565') }} <span>{{ totalWithDraw }}</span></div>
                        <div class="sumItem"> {{ $t('message.566') }}<span>{{ totalProfit }}</span></div>
                    </div>
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
import { mapGetters } from 'vuex'
export default {
    name: "commodityOrder",
    data() {
        return {

            dateValue: [],
            tableData: [],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            totalUsdc: 0,
            totalWithDraw: 0,
            totalProfit: 0,

            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                start: "",
                end: "",
                status:1,
                poolsId: '',//资金池
                inviteId: '',//招聘员
                wallet: '',//钱包地址,
            },
            time: '', //支付时间
            times: [], //订单时间范围

            loading: false,
        };
    },
    computed: {
        ...mapGetters(['pools', 'invitesList'])
        
    },



    created() {
        // 初始化为当月日期范围
        const now = new Date();
        const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
        const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

        // 设置日期范围值
        this.dateValue = [
            this.formatDate(startOfMonth),
            this.formatDate(endOfMonth)
        ];

        // 初始化 queryParams
        this.changeDate(this.dateValue);

        this.getList();
    },
    methods: {

            // 格式化日期为 yyyy-MM-dd
        formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        changeDate(data) {
            if (data) {
                this.queryParams.start = data[0] + ' 00:00:00';
                this.queryParams.end = data[1] + ' 23:59:59';
            } else {
                this.dateValue = []
                this.queryParams.start = '';
                this.queryParams.end = '';
            }
        },
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/Performance/index"),
                method: "post",
                data: this.$http.adornParams(
                    this.queryParams
                ),
            }).then(({ data }) => {
                if (data.success) {
                    this.tableData = data.data.records;
                    this.total = data.data.total;
                    this.totalUsdc = data.totalUsdc;
                    this.totalWithDraw = data.totalWithDraw;
                    this.totalProfit = data.totalProfit;
                    this.loading = false
                } else {
                    this.$message.error(data.msg);
                    this.loading = false
                }
            })
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

.tableFooter {
    color: #fff;
    width: 100%;

    .sumItem {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 48px;
        border-bottom: #434343 1px solid;

        span {
            margin-left: 15px;
        }
    }
}
</style>