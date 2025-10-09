<!-- 管理招聘人员 -->

<template>
    <div class="app-container">
        <appLayout :showtop="showSearch">
            <template #top>
                <cardSearch>
                    <div slot="left">
                        <el-form :model="queryParams" size="small" :inline="true">
                            <el-form-item :label="$t('message.15')">
                                <el-select v-model="queryParams.poolsId" clearable :placeholder="$t('message.15')">
                                    <el-option v-for="item in pools" :key="item.id" :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        
                        </el-form>
                    </div>
                    <div slot="right">
                        <el-button type="primary" icon="el-icon-search" size="mini" @click="search">搜索</el-button>
                        <el-button type="primary" @click="createStaff">{{ $t('message.562') }}</el-button>
                    </div>

                </cardSearch>
            </template>
            <template #dmain="scope">
                <div>
                    <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                </div>
                <el-table v-loading="loading" ref="multipleTable" :data="tableData" style="width: 100 %;"
                    :height="scope.maxheight">
                    <el-table-column label="#" type="index" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="poolId" label="poolId" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="name" :label="$t('message.41')" :formatter="formatterColumn"
                        show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="poolName" :label="$t('message.15')" :formatter="formatterColumn"
                        show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="inviteUrl" :label="$t('message.561')" :formatter="formatterColumn"
                        show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="code" label="Code" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column prop="name" label="招聘员" :formatter="formatterColumn" show-overflow-tooltip>
                    </el-table-column>
                    <el-table-column :label="$t('message.24')" :formatter="formatterColumn" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <el-button type="text" size="small" @click="handleEdit(scope.row)">{{ $t('message.25')
                                }}</el-button>
                            <!-- <el-button type="text" size="small" @click="handleDel(scope.row)">删除</el-button> -->
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

        <!-- 添加/新增招聘管理员 -->
        <el-dialog :title="isEdit ? 'Edit Recruiter' : 'Add Recruiter'" :visible.sync="showEdit" append-to-body
            :close-on-click-modal="false" :width="dialogWidth" @close="canceladd">
            <el-form ref="addForm" :model="addForm" :label-width="labelWidth" :rules="rules" inline size="small">
                <el-form-item :label="$t('message.41')" prop="name">
                    <el-input v-model="addForm.name"></el-input>
                </el-form-item>
                <el-form-item label="Code" prop="code">
                    <el-input v-model="addForm.code"></el-input>
                </el-form-item>

                <!-- <el-form-item v-if="isEdit" label="邀请链接" prop="inviteUrl">
                    <el-input v-model="addForm.inviteUrl"></el-input>
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
import { timeList } from '@/utils/index';
import resizeWIdth from '@/mixins/resizeWIdth';
import { mapGetters } from 'vuex';
export default {
    name: "commodityOrder",
    mixins: [resizeWIdth],
    data() {
        return {
            isEdit: false,//编辑还是添加
            labelSize: 3,
            tableData: [{}],//表格数据
            // 显示搜索
            showSearch: !this.isMobile,
            // 总计
            total: 0,
            // 查询参数
            queryParams: {
                current: 1,
                size: 100,
                poolsId: '',//资金池
            },
            loading: false,
            showEdit: false,//显示编辑弹窗
            // form表单
            addForm: {
                poolId:'',
                name:'',
                code:'',
            },
            // rules
            rules: {
                name: [{ required: true, message: 'Name is required', trigger: 'blur' }],
                code: [
                    { required: true, message: 'Code is required', trigger: 'blur' },
                    { pattern: /^\d{4}$/, message: 'Must be 4 digits', trigger: 'blur' }
                ]
            },
            poolsId: 0,//资金池id
        };
    },
    created() {
        this.getList();
    },
    computed: {
        ...mapGetters(['pools']),
    },
    methods: {

        // 搜索
        search() {
            this.getList()
        },
        getList() {
            this.loading = true;
            this.$http({
                url: this.$http.adornUrl("/admin/invite/list"),
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
            this.loading = false

        },
        // 创建招聘人员
        createStaff() {
            this.isEdit = false;//显示添加标题
            this.showEdit = true;
        },
        // 编辑
        handleEdit(data) {
            this.isEdit = true;//显示编辑标题
            this.addForm.poolId = data.id;
            this.addForm.code = data.code;
            this.addForm.name = data.name;
            this.showEdit = true;
        },
        // 搜索
        search() {
            this.getList()
        },
        // 保存
        saveEdit() {

            if (!this.isEdit) { //添加
                this.$http({
                    url: this.$http.adornUrl("/admin/invite/add"),
                    method: "post",
                    data: this.$http.adornData(
                        {
                            code: this.addForm.code,
                            name: this.addForm.name
                        }
                    ),
                }).then(({ data }) => {
                    if (data.success) {
                        this.$message.success(data.msg);
                        this.getList();
                        this.canceladd();
                    } else {
                        this.$message.error(data.msg);
                    }
                })
                this.addForm = this.$options.data().addForm;
                this.openEdit = false;
               

            } else if (this.isEdit) { //编辑
                this.$http({
                    url: this.$http.adornUrl("/admin/invite/update"),
                    method: "post",
                    data: this.$http.adornData(
                        {
                            id: this.addForm.poolId,
                            code: this.addForm.code,
                            name: this.addForm.name
                        }
                    ),
                }).then(({ data }) => {
                    if (data.success) {
                        this.$message.success(data.msg);
                        this.getList();
                        this.canceladd();
                    } else {
                        this.$message.error(data.msg);
                    }
                })
                this.addForm = this.$options.data().addForm;
                this.showEdit = false;
                this.openEdit = false;
                this.isEdit = false
            }
        },
        // 取消
        canceladd() {
            this.showEdit = false;
            this.addForm = {
                poolId:'',
                name:'',
                code:'',
            };
        },

        handleDel(data){
            this.$http({
                    url: this.$http.adornUrl("/admin/invite/del"),
                    method: "post",
                    data: this.$http.adornData(
                        {
                            id: data.id
                        }
                    ),
                }).then(({ data }) => {
                    if (data.success) {
                        this.$message.success(data.msg);
                        this.getList();
                    } else {
                        this.$message.error(data.msg);
                    }
                })
        },
        // 重置
        restSearchData() {
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
    width: 78PX !important;
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
</style>