<!-- 管理资金池 -->

<template>
    <div class="app-container">
        <appLayout :showtop="false">
            <template #dmain="scope">
                <div>
                    <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                    <el-button v-if="isAuth('admin:pools:add')" type="primary" @click="createPool">创建资金池</el-button>
                </div>
                <el-table v-loading="loading" ref="multipleTable"  :data="tableData"
                    style="width: 100%;" :height="scope.maxheight">
                    <el-table-column prop="nickName" label="资金池名称" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="wallet" label="Pool Wallet" :formatter="formatterColumn">
                        <!-- <template #default="scope">
                            <span>{{ scope.row.wallet }}</span>
                            <el-button 
                                type="text" 
                                size="mini" 
                                @click="showPrivateKey(scope.row.id,0)">
                                查看私钥
                            </el-button> 
                         </template> -->
                    </el-table-column> 
                    <el-table-column prop="ownerName" :label="$t('message.553')" :formatter="formatterColumn"
                        show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="usdc" label="资金池USDC" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="eth" label="资金池ETH" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    
                    <el-table-column prop="newApproveWallet" label="新授权钱包" :formatter="formatterColumn">
                    </el-table-column>
                    <el-table-column prop="newApproveEth" label="新授权钱包ETH" :formatter="formatterColumn"
                        show-overflow-tooltip>
                    </el-table-column>

                    <el-table-column prop="approveWallet" label="授权钱包" :formatter="formatterColumn">
                        <!-- <template #default="scope">
                            <span>{{ scope.row.approveWallet }}</span>
                            <el-button 
                                type="text" 
                                size="mini" 
                                @click="showPrivateKey(scope.row.id,1)">
                                查看私钥
                            </el-button>
                         </template> -->
                </el-table-column>
                    <el-table-column prop="approveEth" label="授权钱包ETH" :formatter="formatterColumn"
                        show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="createdUser" label="创建者" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="domain" label="域名" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="pnickName" label="所属上级" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="rebate" label="上级返利(%)" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column  label="操作" :formatter="formatterColumn"
                        show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                              <el-button type="text"  v-if="isAuth('admin:pools:delete')" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
        <!-- 新增编辑弹窗 -->
        <el-dialog title="Add Pool" :visible.sync="showEdit" append-to-body :close-on-click-modal="false" width="583px"
            @close="canceladd">
            <el-form ref="addForm" :model="addForm" :label-width="labelWidth" :rules="rules" inline size="small">
                <el-form-item label="资金池名称" prop="name">
                    <el-input v-model="addForm.name" placeholder="资金池名称"></el-input>
                </el-form-item>
                <el-form-item label="归属用户" prop="ownerName">
                    <el-input v-model="addForm.ownerName" placeholder="归属用户">
                    </el-input>
                </el-form-item>
                <el-form-item label="域名" prop="domain">
                    <el-input v-model="addForm.domain" placeholder="域名">
                    </el-input>
                </el-form-item>
                <el-form-item label="上级返利(%)" prop="rebate">
                    <el-input v-model="addForm.rebate" placeholder="上级返利">
                    </el-input>
                </el-form-item>
                
                <!-- <el-form-item :label="$t('message.556')" prop="minStakeAmount">
                    <div class="row"> <el-input-number class="input180" v-model="addForm.minStakeAmount"
                            controls-position="right" :min="1" :max="999999999">
                        </el-input-number>
                        <div class="input-tail">USDC</div>
                    </div>
                </el-form-item>
                <el-form-item :label="$t('message.557')" prop="stakedAddAPY">
                    <div class="row"><el-input-number class="input180" v-model="addForm.stakedAddAPY"
                            controls-position="right" :min="1" :max="999999999"></el-input-number>
                        <div class="input-tail">%</div>
                    </div>
                </el-form-item> -->
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveEdit" size="small">{{ $t('message.45') }}</el-button>
                <el-button @click="canceladd" type="info" plain size="small">{{ $t('message.44') }}</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
// import { postRequest } from '@/api/requestApi';
//import { copyToClipboard } from '@/utils/index';
import resizeWIdth from '@/mixins/resizeWIdth';
import { mapGetters } from 'vuex';
export default {
    name: "commodityOrder",
    mixins: [resizeWIdth],
    data() {
        return {
            labelSize: 6,
            showEdit: false,//显示编辑弹窗
            tableData: [{}],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
            },
            loading: false,
            // form表单
            addForm: {
                name: '',
                ownerName: '',
                domain: '',
                rebate: ''
            },
            // rules
            rules: {
                name: [{ required: true, message: '请输入名字', trigger: 'blur' }],
                domain: [{ required: true, message: '请输入域名', trigger: 'blur' }],
                ownerName: [{ required: true, message: '请输入归属用户Id', trigger: 'blur' }],
                rebate: [
                            { 
                                required: true, 
                                message: '请输入上级返利', 
                                trigger: 'blur' 
                            },
                            {
                                pattern: /^[+]?\d+(\.\d+)?$/,  // Matches positive integers or floating point numbers
                                message: '上级返利必须为正数',
                                trigger: 'blur'
                            }
                        ],
            },
        };
    },
    created() {
        this.getList();
    },
    methods: {
        // showPrivateKey(id,type){
        //     this.loading = true;
        //     this.$http({
        //         url: this.$http.adornUrl("/admin/pools/decrypt"),
        //         method: "post",
        //         data: this.$http.adornData(
        //            {
        //             id:id,
        //             type:type
        //            }
        //         ),
        //     }).then(({ data }) => {
        //         this.loading = false
        //         if (data.success) {
        //             navigator.clipboard.writeText(data.msg).then(() => {
        //                 this.$message.success(data.msg + ' Copied to clipboard!');
        //             }).catch(err => {
        //                 this.$message.error('Failed to copy: ' + err);
        //             });
        //         } else {
        //             this.$message.error(data.msg);
        //         }
        //     })
        //     this.loading = false;
        
        // },
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/pools/list"),
                method: "post",
                data: this.$http.adornData(
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
        copyToClipboard(row, column, cell, event) {
            if (column.property !== 'wallet') return;
            var text = row.wallet;
            navigator.clipboard.writeText(text).then(() => {
                this.$message.success(text + ' Copied to clipboard!');
            }).catch(err => {
                this.$message.error('Failed to copy: ' + err);
            });
        },
        // 新增资金池
        createPool() {
            this.showEdit = true;
        },

        handleDelete(data) {
            this.$confirm('确定要删除该数据吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 用户点击了“确定”
                this.$http({
                    url: this.$http.adornUrl("/admin/pools/delete"),
                    method: "post",
                    data: this.$http.adornData({
                        id: data.id
                    }),
                }).then(({ data }) => {
                    this.loading = false
                    this.getList()
                    this.$message({
                        type: 'success',
                        message: '删除成功！'
                    });
                });
            }).catch(() => {
                // 用户点击了“取消”
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },

        // 编辑
        handleEdit(data) {
            this.addForm.id = data.id;
            this.addForm.name = data.nickName;
            this.addForm.ownerName = data.ownerName;
            this.addForm.domain = data.domain;
            this.addForm.rebate = data.rebate;
            this.showEdit = true;
        },
        // 保存
        saveEdit() {
            this.showEdit = false;
            this.$http({
                url: this.$http.adornUrl("/admin/pools/add"),
                method: "post",
                data: this.$http.adornData(
                    {
                        id:this.addForm.id,
                        name: this.addForm.name,
                        ownerName: this.addForm.ownerName,
                        domain:this.addForm.domain,
                        rebate:this.addForm.rebate
                        // ownerId: Number(this.addForm.ownerId)
                    }
                ),
            }).then(({ data }) => {
                this.loading = false
                this.addForm = this.$options.data().addForm;
                this.showEdit = false
                this.getList()
            })
        },
        // 取消
        canceladd() {
            this.showEdit = false;
            this.addForm = this.$options.data().addForm;
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
.row {
    display: flex;
    align-items: center;
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

// .input180 {
//     // width: 180px !important;

//      .el-input--small {
//         width: 300px !important;
//         border-radius: 5px 0 0 5px !important;
//     }

//     .el-input__inner {
//         border-radius: 4px 0 0 4px;
//         text-align: left !important;
//     }

//     // ::v-deep .el-input-number--small,
//     // ::v-deep .el-dialog .el-input--small {
//     //     width: 180px !important;
//     // }
// } 
.input-tail {
    display: flex;
    align-items: center;
    height: 36px;
    padding: 0 11px;
    color: hsla(0, 0%, 100%, .85);
    font-weight: 400;
    text-align: center;
    background-color: hsla(0, 0%, 100%, .04);
    border: 1px solid #434343;
    border-radius: 0 4px 4px 0 !important;
    border-left: 0;
}

::v-deep .el-card .el-form-item__label {
    width: 78PX !important;
}

::v-deep .el-table__footer-wrapper tbody td {
    background-color: #1d1d1d !important;
}
</style>