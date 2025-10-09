<!--
* @Date: 2024/07/22 16:36:22
* @LastEditTime: 2024/07/22 16:36:22
* @Description:钱包收益列表
-->
<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <!-- 推荐收益 -->
                            <!-- <el-form-item :label="$t('message.538')">
                                <el-select v-model="queryParams.referralReward" clearable :placeholder="$t('message.538')">
                                    <el-option v-for="item in recommandOptions" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item> -->
                            <el-form-item label="推荐奖励">
                                <el-select clearable v-model="queryParams.invited" placeholder="请选择">
                                    <el-option v-for="item in invite" :key="`invite-${item.id}-${item.value}`" :label="item.label"
                                        :value="item.value"> </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="资金池">
                                <el-select v-model="queryParams.poolsId" clearable placeholder="资金池">
                                    <el-option v-for="item in pools" :key="`pools-${item.label}-${item.value}`" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="$t('message.5')">
                                <el-input v-model="queryParams.wallet" clearable
                                    :placeholder="$t('message.514')"></el-input>
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
                <div class="table-container">
                <el-table v-loading="loading" ref="multipleTable" @cell-click="copyToClipboard" :data="tableData">
                    <el-table-column prop="created " label="奖励时间" fixed="left" width="150px"  :formatter="formatterColumn" >
                        <template slot-scope="scope">
                            {{ scope.row.created ? $moment(scope.row.created).format('YYYY-MM-DD HH:mm:ss') : '' }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="rewardEth " label="收益ETH" :formatter="formatterColumn" show-overflow-tooltip>
                        <template slot-scope="scope">
                            {{ scope.row.rewardEth }}
                        </template>
                    </el-table-column>
                    <el-table-column label="推荐 " :formatter="formatterColumn" show-overflow-tooltip>
                        <template slot-scope="scope">
                            {{ scope.row.invited == 0 ? 'No' : 'Yes' }}
                        </template>
                    </el-table-column> 
                    <el-table-column prop="usdc  " label="USDC " :formatter="formatterColumn" show-overflow-tooltip>
                        <template slot-scope="scope">
                            {{ scope.row.usdc }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="wallet" label="钱包地址 "  >
                        <template slot-scope="scope">
                            <el-button type="text" @click="openWallet(scope.row.wallet)">{{ scope.row.wallet
                                }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
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
// import { postRequest } from '@/api/requestApi';
import { timeList } from '@/utils/index';
import { mapGetters } from 'vuex';
// import i18n from "@/language/index.js";
export default {
    name: "walletIncomeList",
    data() {
        return {
            // 是否奖励
            rewardOptions: [{
                value: '1',
                label: 'Yes'
            }, {
                value: '0',
                label: 'No'
            }],

            tableData: [{}],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                invited: '',//是否奖励
                wallet: '',//钱包地址,
                poolsId: "",//资金池
            },
            loading: false,
        };
    },
    computed: {
        ...mapGetters(["invite", 'pools',]),
        recommandOptions() {
            return [
                {
                    value: true,
                    label: this.$t('message.539'),
                },
                {
                    value: false,
                    label: this.$t('message.540'),
                },
            ];
        },

    },
    created() {
        this.getList();
    },
    methods: {
        async getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/reward/list"),
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
            this.queryParams.pageSize = val;
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
.table-container {
    width: 100%;
    overflow-x: auto; /* 在小屏幕上水平滚动 */
}

::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
}
</style>