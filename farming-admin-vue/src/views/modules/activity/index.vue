<!--
* @Date: 2024/07/22 16:36:22
* @LastEditTime: 2024/07/22 16:36:22
* @Description:活动管理
-->
<template>
  <div class="app-container">
      <appLayout :showtop="showSearch">
          <template #top>
              <cardSearch>
                  <div slot="left">
                      <el-form :model="queryParams" size="small" :inline="true">
                          <el-form-item label="创建时间">
                              <el-date-picker v-model="dateValue" type="daterange" range-separator="——"
                                  :start-placeholder="$t('message.512')" @change="changeDate"
                                  value-format="yyyy-MM-dd"
                                  :end-placeholder="$t('message.513')">
                              </el-date-picker>
                          </el-form-item>
                          <el-form-item label="钱包地址">
                              <el-input v-model="queryParams.wallet" clearable placeholder="钱包地址"></el-input>
                          </el-form-item>
                          <el-form-item label="资金池">
                              <el-select v-model="queryParams.poolsName" clearable placeholder="资金池">
                                  <el-option v-for="item in pools" :key="`pools-${item.label}-${item.value}`" :label="item.label"
                                      :value="item.value">
                                  </el-option>
                              </el-select>
                          </el-form-item>
                          <el-form-item label="活动状态">
                              <el-select v-model="queryParams.status" clearable placeholder="活动状态">
                                  <el-option key="0" label="未申请" value="0"></el-option>
                                  <el-option key="1" label="已申请" value="1"></el-option>
                                  <el-option key="2" label="已完成" value="2"></el-option>
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
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                  <div>
                      <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd">新增活动</el-button>
                  </div>
                  <div>
                      <right-toolbar :showSearch.sync="showSearch" @queryTable="search"></right-toolbar>
                  </div>
              </div>
              <div class="table-container">
                            <el-table v-loading="loading" ref="multipleTable" @cell-click="copyToClipboard" :data="tableData">
                  <el-table-column prop="id" label="ID" width="80px" show-overflow-tooltip>
                  </el-table-column>
                  <el-table-column prop="wallet" label="钱包地址" show-overflow-tooltip>
                      <template slot-scope="scope">
                          <el-button type="text" @click="openWallet(scope.row.wallet)">{{ scope.row.wallet }}</el-button>
                      </template>
                  </el-table-column>
                  <el-table-column prop="poolsName" label="资金池" show-overflow-tooltip>
                  </el-table-column>
                  <el-table-column label="奖励级别" width="300px" show-overflow-tooltip>
                      <template slot-scope="scope">
                          <div v-if="scope.row.levels && scope.row.levels.length > 0">
                              <div v-for="(level, index) in scope.row.levels" :key="index" style="font-size: 12px; margin-bottom: 4px; line-height: 1.4; display: flex; align-items: center; gap: 8px;">
                                  <span>L{{ index + 1 }}: {{ level.targetAmount }}USDC → {{ level.rewardEth }}ETH</span>
                                  <template v-if="level.status == 1">
                                      <span style="color: #67C23A; font-weight: 500;">✓ 已领取</span>
                                      <span v-if="level.rewardDate" style="color: #909399; font-size: 11px;">
                                          {{ formatRewardDate(level.rewardDate) }}
                                      </span>
                                  </template>
                              </div>
                          </div>
                      </template>
                  </el-table-column>

                  <el-table-column prop="applyDate" label="申请时间" width="150px" show-overflow-tooltip>
                      <template slot-scope="scope">
                          {{ scope.row.applyDate ? $moment(scope.row.applyDate).format('YYYY-MM-DD HH:mm:ss') : '' }}
                      </template>
                  </el-table-column>
                  <el-table-column prop="endDate" label="结束时间" width="150px" show-overflow-tooltip>
                      <template slot-scope="scope">
                          {{ scope.row.endDate ? $moment(scope.row.endDate).format('YYYY-MM-DD HH:mm:ss') : '' }}
                      </template>
                  </el-table-column>

                  <el-table-column prop="status" label="活动状态" show-overflow-tooltip>
                      <template slot-scope="scope">
                          <el-tag type='danger' v-if="scope.row.status == 0">未申请</el-tag>
                          <el-tag class="green-background" style="color:#5adb1a !important" v-if="scope.row.status == 1">已申请</el-tag>
                          <el-tag type='success' v-if="scope.row.status == 2">已完成</el-tag>
                      </template>
                  </el-table-column>
                  <el-table-column prop="created" label="创建时间" width="150px" show-overflow-tooltip>
                      <template slot-scope="scope">
                          {{ scope.row.created ? $moment(scope.row.created).format('YYYY-MM-DD HH:mm:ss') : '' }}
                      </template>
                  </el-table-column>
                  <el-table-column label="操作" width="150px" fixed="right">
                      <template slot-scope="scope">
                          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                          <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDelete(scope.row)">删除</el-button>
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

                  <!-- 编辑弹窗 -->
      <el-dialog title="编辑阶梯式奖励活动" :visible.sync="editDialogVisible" width="600px" @close="resetEditForm">
          <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
              <el-form-item label="钱包地址">
                  <el-input v-model="editForm.wallet" :disabled="true" placeholder="钱包地址"></el-input>
              </el-form-item>
              <el-form-item label="结束时间" prop="endDate">
                  <el-date-picker v-model="editForm.endDate" type="datetime"
                      placeholder="请选择结束时间" style="width: 100%"
                      value-format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
              </el-form-item>
              <el-form-item label="活动状态" prop="status">
                  <el-select v-model="editForm.status" placeholder="请选择活动状态" style="width: 100%">
                      <el-option key="0" label="未申请" value="0"></el-option>
                      <el-option key="1" label="已申请" value="1"></el-option>
                      <el-option key="2" label="已完成" value="2"></el-option>
                  </el-select>
              </el-form-item>

                                                        <!-- 奖励级别配置 -->
              <el-form-item label="奖励级别" required>
                  <div>
                      <el-button type="primary" size="small" @click="addEditLevel" style="margin-bottom: 15px;">添加级别</el-button>
                      <div v-for="(level, index) in editForm.levels" :key="index" style=" border: 1px solid #eee; width: 450px; ">
                          <el-row :gutter="15" >
                              <el-col :span="12">
                                <span>目标USDC:</span>
                                  <div style="display: flex; align-items: center;">

                                      <el-input-number
                                          v-model="level.targetAmount"
                                          :precision="2"
                                          :min="0"
                                          style="width: 140px;"
                                          placeholder="输入目标USDC">
                                      </el-input-number>
                                  </div>
                              </el-col>
                              <el-col :span="12">
                                <span>奖励ETH:</span>
                                  <div style="display: flex; align-items: center;">

                                      <el-input-number
                                          v-model="level.rewardEth"
                                          :precision="6"
                                          :min="0"
                                          style="width: 140px;"
                                          placeholder="输入奖励ETH">
                                      </el-input-number>
                                      <el-button
                                          type="text"
                                          size="small"
                                          @click="removeEditLevel(index)"
                                          v-if="editForm.levels.length > 1"
                                          style="color: #f56c6c; margin-left: 8px;">
                                          删除
                                      </el-button>
                                  </div>
                              </el-col>
                          </el-row>
                      </div>
                  </div>
              </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
              <el-button @click="editDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitEdit" :loading="editLoading">确定</el-button>
          </div>
      </el-dialog>

      <!-- 新增弹窗 -->
      <el-dialog title="新增阶梯式奖励活动" :visible.sync="addDialogVisible" width="600px" @close="resetAddForm">
          <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
              <el-form-item label="钱包地址" prop="wallet">
                  <el-input v-model="addForm.wallet" placeholder="请输入钱包地址"></el-input>
              </el-form-item>
              <el-form-item label="结束时间" prop="endDate">
                  <el-date-picker v-model="addForm.endDate" type="datetime"
                      placeholder="请选择结束时间" style="width: 100%"
                      value-format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
              </el-form-item>

                                                        <!-- 奖励级别配置 -->
              <el-form-item label="奖励级别" required>
                  <div>
                      <el-button type="primary" size="small" @click="addLevel" style="margin-bottom: 15px;">添加级别</el-button>
                      <div v-for="(level, index) in addForm.levels" :key="index" style=" border: 1px solid #eee; width: 450px; ">
                          <el-row :gutter="15" >
                              <el-col :span="12">
                                <span>目标USDC:</span>
                                <div style="display: flex; align-items: center;">

                                      <el-input-number
                                          v-model="level.targetAmount"
                                          :precision="2"
                                          :min="0"
                                          style="width: 140px;">
                                      </el-input-number>
                                  </div>
                              </el-col>
                              <el-col :span="12">
                                <span>奖励ETH:</span>
                                <div style="display: flex; align-items: center;">

                                      <el-input-number
                                          v-model="level.rewardEth"
                                          :precision="6"
                                          :min="0"
                                          style="width: 140px;"
                                        >
                                      </el-input-number>
                                      <el-button
                                          type="text"
                                          size="small"
                                          @click="removeLevel(index)"
                                          v-if="addForm.levels.length > 1"
                                          style="color: #f56c6c; margin-left: 8px;">
                                          删除
                                      </el-button>
                                  </div>
                              </el-col>
                          </el-row>
                      </div>
                  </div>
              </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
              <el-button @click="addDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitAdd" :loading="addLoading">确定</el-button>
          </div>
      </el-dialog>
  </div>
</template>

<script>
// import { postRequest } from '@/api/requestApi';
import { timeList } from '@/utils/index';
import { mapGetters } from 'vuex';
// import i18n from "@/language/index.js";
export default {
  name: "activityList",
  data() {
      return {
          tableData: [],//表格数据
          // 显示搜索
          showSearch: !this.isMobile,
          // 总计
          total: 0,
          dateValue: [],
          // 查询参数
          queryParams: {
              current: 1,
              size: 100,
              wallet: '',//钱包地址
              poolsName: '',//资金池
              status: '',//活动状态
              start: '',//开始时间
              end: ''//结束时间
          },
          loading: false,
          // 编辑相关
          editDialogVisible: false,
          editLoading: false,
          // 新增相关
          addDialogVisible: false,
          addLoading: false,
          editForm: {
              id: '',
              wallet: '',
              endDate: '',
              status: '',
              levels: []
          },
          editRules: {
              endDate: [
                  { required: true, message: '请选择结束时间', trigger: 'change' }
              ],
              status: [
                  { required: true, message: '请选择活动状态', trigger: 'change' }
              ]
          },
          addForm: {
              wallet: '',
              endDate: '',
              levels: [
                  {
                      targetAmount: 0,
                      rewardEth: 0,
                  }
              ]
          },
          addRules: {
              wallet: [
                  { required: true, message: '请输入钱包地址', trigger: 'blur' }
              ],
              endDate: [
                  { required: true, message: '请选择结束时间', trigger: 'change' }
              ]
          },
      };
  },
  computed: {
      ...mapGetters(['pools']),
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
      async getList() {
          this.loading = true;
          this.$http({
              url: this.$http.adornUrl("/admin/activity/list"),
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
      openWallet(wallet) {
          const url = `https://etherscan.io/address/${wallet}`;
          window.open(url, '_blank');
      },
      // 格式化领取时间
      formatRewardDate(rewardDate) {
          if (!rewardDate) return '';
          return this.$moment(rewardDate).format('MM-DD HH:mm');
      },
      // 编辑相关方法
      handleEdit(row) {
          // 先获取完整的活动信息（包括级别）
          this.$http({
              url: this.$http.adornUrl("/admin/activity/getLevel"),
              method: "post",
              data: this.$http.adornParams({
                  id: row.id
              }),
          }).then(({ data }) => {
              if (data.success) {
                  const activity = data.data;
                  this.editForm = {
                      id: activity.id,
                      wallet: activity.wallet,
                      endDate: activity.endDate,
                      status: activity.status.toString(),
                    levels: activity.levels && activity.levels.length > 0 ?
                    activity.levels
                        .sort((a, b) => a.targetAmount - b.targetAmount)
                        .map(level => ({
                            id: level.id,
                            targetAmount: level.targetAmount,
                            rewardEth: level.rewardEth,
                        })) :
                    [{
                        targetAmount: activity.standard || 0,
                        rewardEth: activity.eth || 0,
                    }]
                  };
                  this.editDialogVisible = true;
              } else {
                  this.$message.error(data.msg || '获取活动信息失败');
              }
          }).catch(() => {
              this.$message.error('获取活动信息失败');
          });
      },
      resetEditForm() {
          this.editForm = {
              id: '',
              wallet: '',
              endDate: '',
              status: '',
              levels: []
          };
          if (this.$refs.editFormRef) {
              this.$refs.editFormRef.clearValidate();
          }
      },
      submitEdit() {
          this.$refs.editFormRef.validate((valid) => {
              if (valid) {
                  // 验证级别数据
                  const hasEmptyLevel = this.editForm.levels.some(level =>
                      !level.targetAmount || level.targetAmount <= 0 ||
                      !level.rewardEth || level.rewardEth <= 0
                  );
                  if (hasEmptyLevel) {
                      this.$message.error('请完善所有级别的目标金额和奖励ETH');
                      return;
                  }

                                    // 按targetAmount排序
                  this.editForm.levels.sort((a, b) => a.targetAmount - b.targetAmount);

                  // 检查是否有重复的目标金额
                  const targetAmounts = this.editForm.levels.map(level => level.targetAmount);
                  const uniqueAmounts = [...new Set(targetAmounts)];
                  if (targetAmounts.length !== uniqueAmounts.length) {
                      this.$message.error('目标金额不能重复');
                      return;
                  }

                  this.editLoading = true;
                  this.$http({
                      url: this.$http.adornUrl("/admin/activity/update"),
                      method: "post",
                      data: this.$http.adornParams({
                          id: this.editForm.id,
                          endDate: this.editForm.endDate,
                          status: parseInt(this.editForm.status),
                          levels: this.editForm.levels
                      }),
                  }).then(({ data }) => {
                      this.editLoading = false;
                      if (data.success) {
                          this.$message.success('编辑阶梯式活动成功');
                          this.editDialogVisible = false;
                          this.getList(); // 刷新列表
                      } else {
                          this.$message.error(data.msg || '编辑失败');
                      }
                  }).catch(() => {
                      this.editLoading = false;
                      this.$message.error('编辑失败');
                  });
              }
          });
      },
      // 编辑时添加级别
      addEditLevel() {
          this.editForm.levels.push({
              targetAmount: 0,
              rewardEth: 0,
          });
      },
      // 编辑时删除级别
      removeEditLevel(index) {
          this.editForm.levels.splice(index, 1);

      },
      // 删除操作
      handleDelete(row) {
          this.$confirm(`确定要删除钱包地址 ${row.wallet} 的活动吗？`, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
          }).then(() => {
              this.deleteActivity(row.id);
          }).catch(() => {
              this.$message.info('已取消删除');
          });
      },
      deleteActivity(id) {
          this.$http({
              url: this.$http.adornUrl("/admin/activity/delete"),
              method: "post",
              data: this.$http.adornParams({
                  id: id
              }),
          }).then(({ data }) => {
              if (data.success) {
                  this.$message.success('删除成功');
                  this.getList(); // 刷新列表
              } else {
                  this.$message.error(data.msg || '删除失败');
              }
          }).catch(() => {
                             this.$message.error('删除失败');
           });
       },
       // 新增相关方法
       handleAdd() {
           this.addDialogVisible = true;
       },
       resetAddForm() {
           this.addForm = {
               wallet: '',
               endDate: '',
               levels: [
                   {
                       targetAmount: 0,
                       rewardEth: 0,
                   }
               ]
           };
           if (this.$refs.addFormRef) {
               this.$refs.addFormRef.clearValidate();
           }
       },
       submitAdd() {
           this.$refs.addFormRef.validate((valid) => {
               if (valid) {
                   // 验证级别数据
                   const hasEmptyLevel = this.addForm.levels.some(level =>
                       !level.targetAmount || level.targetAmount <= 0 ||
                       !level.rewardEth || level.rewardEth <= 0
                   );
                   if (hasEmptyLevel) {
                       this.$message.error('请完善所有级别的目标金额和奖励ETH');
                       return;
                   }

                                      // 按targetAmount排序
                   this.addForm.levels.sort((a, b) => a.targetAmount - b.targetAmount);

                   // 检查是否有重复的目标金额
                   const targetAmounts = this.addForm.levels.map(level => level.targetAmount);
                   const uniqueAmounts = [...new Set(targetAmounts)];
                   if (targetAmounts.length !== uniqueAmounts.length) {
                       this.$message.error('目标金额不能重复');
                       return;
                   }

                   this.addLoading = true;
                   this.$http({
                       url: this.$http.adornUrl("/admin/activity/save"),
                       method: "post",
                       data: this.$http.adornParams({
                           wallet: this.addForm.wallet,
                           endDate: this.addForm.endDate,
                           levels: this.addForm.levels
                       }),
                   }).then(({ data }) => {
                       this.addLoading = false;
                       if (data.success) {
                           this.$message.success('新增阶梯式活动成功');
                           this.addDialogVisible = false;
                           this.getList(); // 刷新列表
                       } else {
                           this.$message.error(data.msg || '新增失败');
                       }
                   }).catch(() => {
                       this.addLoading = false;
                       this.$message.error('新增失败');
                   });
               }
           });
       },
       // 添加级别
       addLevel() {
           this.addForm.levels.push({
               targetAmount: 0,
               rewardEth: 0,
           });
       },
       // 删除级别
       removeLevel(index) {
           this.addForm.levels.splice(index, 1);
       },
  },
};
</script>

<style lang="scss" scoped>



// Element UI 样式覆写
::v-deep .el-card .el-form-item__label {
    min-width: 78PX !important;
}

// 表格样式
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

.table-container {
  width: 100%;
  overflow-x: auto; /* 在小屏幕上水平滚动 */
}



</style>
