<!-- 平台提现-->
<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <el-form-item label="钱包地址">
                                <el-input v-model="queryParams.wallet" clearable placeholder="钱包地址"></el-input>
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

                    <el-table-column prop="wallet" label="钱包地址" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.wallet)">
                                {{ scope.row.wallet }}</el-button>
                        </template>
                    </el-table-column>

                    <el-table-column prop="pools" label="资金池" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="contract" label="授权合约" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="amount" label="授权数量" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="hash" label="Hash" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openHash(scope.row.hash)">{{ scope.row.hash }}</el-button>
                        </template>
                    </el-table-column>

                    <el-table-column prop="created" label="监测时间" show-overflow-tooltip>
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

    </div>
</template>

<script>
import resizeWIdth from '@/mixins/resizeWIdth';
import { mapGetters } from 'vuex';
export default {
    name: "approveIndex",
    mixins: [resizeWIdth],
    data() {
        return {
            openEdit: false,
            tableData: [],//表格数据
            // 显示搜索
            showSearch: true,
            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                wallet: ''//钱包地址
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
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/approve/list"),
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
        // 打开钱包地址
        openWallet(wallet) {
            // 可以在这里添加打开钱包详情的逻辑
            console.log('打开钱包:', wallet);
        },
        // 打开Hash
        openHash(hash) {
            // 可以在这里添加打开区块链浏览器的逻辑
            console.log('打开Hash:', hash);
        }
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
</style>