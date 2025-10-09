<!-- 代理业绩 -->
<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <el-form-item label="日期">
                                <el-date-picker v-model="dateValue" type="daterange" range-separator="-"
                                    :start-placeholder="$t('message.512')" @change="changeDate"
                                    value-format="yyyy-MM-dd"
                                    :end-placeholder="$t('message.513')">
                                </el-date-picker>
                            </el-form-item>
                            <!-- <el-form-item :label="$t('message.15')">
                                <el-select v-model="queryParams.poolsId" clearable :placeholder="$t('message.15')">
                                    <el-option v-for="item in pools" :key="item.value" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item> -->
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
                        row-key="poolsId"
                        lazy
                        :load="load"
                        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
                        style="width: 100%;">
                        <el-table-column label="#" type="index" :formatter="formatterColumn" show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="poolsName" width="200px" label="资金池" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="parentPoolsName" label="所属上级" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="usdc" label="收入USDC" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="withdraw" label="提现USDC" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="parentRebate" label="上级返利(支出)" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column prop="childRebate" label="下级返利(收入)" :formatter="formatterColumn"
                            show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column label="净收益" :formatter="formatterColumn"
                            show-overflow-tooltip>
                            <template #default="scope">
                                {{(scope.row.usdc - scope.row.withdraw - scope.row.parentRebate + scope.row.childRebate).toFixed(2)}}
                            </template>
                        </el-table-column>
                    </el-table>



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

            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                start: "",
                end: "",
                poolsId: '',//资金池
                parentPoolsId:''
            },
            time: '', //支付时间
            times: [], //订单时间范围

            loading: false,
        };
    },
    computed: {
        ...mapGetters(['pools'])

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
            this.queryParams.parentPoolsId = '';
            this.$http({
                url: this.$http.adornUrl("/admin/proxyWithdraw/report"),
                method: "post",
                data: this.$http.adornParams(
                    this.queryParams
                ),
            }).then(({ data }) => {
                if (data.success) {
                    data.data.forEach(item  => {
                        if (item.hasChildren)  {
                        item.children  = [];
                        }
                    });
                    this.tableData = data.data;
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

        load(tree, treeNode, resolve) {
            console.log('Loading  children for:', tree);
                this.queryParams.parentPoolsId = tree.poolsId;
                // 发送请求获取子级数据
                this.$http({
                    url: this.$http.adornUrl("/admin/proxyWithdraw/report"),
                    method: "post",
                    data: this.$http.adornParams(  this.queryParams)
                }).then(({ data }) => {
                    if (data.success) {
                         // 确保返回的数据包含 children 字段
                        const children = data.data.map(item  => ({
                            ...item,
                            children: [] // 添加 children 字段
                        }));
                        resolve(children);
                    } else {
                        this.$message.error(data.msg);
                        resolve([]);
                    }
                }).catch(() => {
                    resolve([]);
                });
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

/* 展开图标样式 */
::v-deep .el-table__expand-icon {
    position: relative;
    cursor: pointer;
    color: #666;
    font-size: 12px;
    -webkit-transition: -webkit-transform 0.2s ease-in-out;
    transition: -webkit-transform 0.2s ease-in-out;
    transition: transform 0.2s ease-in-out;
    transition: transform 0.2s ease-in-out, -webkit-transform 0.2s ease-in-out;
    height: 20px;
    display: inline-block !important;
    margin-right: 5px !important;
}

::v-deep .el-table__cell {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

::v-deep .el-table_1_column_2 .el-table__cell {
    min-width: 200px;
    display: flex;
    align-items: center;
}
</style>
