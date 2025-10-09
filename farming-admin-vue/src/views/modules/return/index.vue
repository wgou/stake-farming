<!-- 平台退回-->
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
                            <el-form-item label="用户钱包">
                                <el-input v-model="queryParams.wallet" clearable placeholder="用户钱包"></el-input>
                            </el-form-item>
                            
                            <el-form-item label="状态">
                                <el-select v-model="queryParams.status" clearable placeholder="状态">
                                    <el-option v-for="(item, index) in statusOptions" :key="item.id || item.value || index" :label="item.label" :value="item.value"></el-option>
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

                    <el-table-column prop="wallet"  label="用户钱包" >
                    </el-table-column>
               
                    <el-table-column prop="usdc" label="退款数量" >
                    </el-table-column>
                    <el-table-column prop="toWallet"  label="收款钱包" >
                    </el-table-column>
                    <el-table-column prop="status" label="状态" >
                        <template slot-scope="scope">
                            <el-tag type='success' v-if="scope.row.status == 0">成功</el-tag>
                            <el-tag type='danger' v-if="scope.row.status == -1">失败</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="created" label="日期" >
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="hash" label="Hash" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" @click="openUsdcHash(scope.row.hash)">{{ scope.row.hash }}</el-button>
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
            tableData: [],//表格数据
            // 显示搜索
            showSearch: true,
            total: 0,
            dateValue: [],
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                wallet: '',//钱包地址,
                status: '',
                start: '',//开始时间
                end: ''//结束时间
            },
            loading: false,
            statusOptions: [
                { id: 1, value: 0, label: "成功" },
                { id: 2, value: -1, label: "失败" }
            ],
        };
    },
    computed: {
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
                url: this.$http.adornUrl("/admin/return/list"),
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
         openUsdcHash(hash){
            window.open("https://dashboard.tenderly.co/layerx-protocol/project/testnet/ecbd5b0d-8b13-4b76-bb09-fe48de435eb5/tx/" + hash)
        }
 
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
</style>