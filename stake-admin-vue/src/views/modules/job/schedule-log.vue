<template>
  <el-dialog title="日志列表" :close-on-click-modal="false" :visible.sync="visible" width="75%">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()" size="small">
      <el-form-item size="small">
        <el-input v-model="dataForm.id" placeholder="任务ID" clearable></el-input>
      </el-form-item>
      <el-form-item size="small">
        <el-button @click="getDataList()" type="info" plain>查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="dataList" border v-loading="dataListLoading" height="460" style="width: 100%;">
      <el-table-column prop="logId" header-align="center" align="center" width="80" label="日志ID">
      </el-table-column>
      <el-table-column prop="jobId" header-align="center" align="center" width="80" label="任务ID">
      </el-table-column>
      <el-table-column prop="beanName" header-align="center" align="center" label="bean名称">
      </el-table-column>
      <!-- <el-table-column
        prop="methodName"
        header-align="center"
        align="center"
        label="方法名称">
      </el-table-column> -->
      <el-table-column prop="params" header-align="center" align="center" label="参数">
      </el-table-column>
      <el-table-column prop="status" header-align="center" align="center" label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small">成功</el-tag>
          <el-tag v-else @click.native="showErrorInfo(scope.row.logId)" size="small" type="danger"
            style="cursor: pointer;">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="times" header-align="center" align="center" label="耗时(单位: 毫秒)">
      </el-table-column>
      <el-table-column prop="createTime" header-align="center" align="center" width="180" label="执行时间">
      </el-table-column>
    </el-table>
    <div class="page">
      <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex"
        :page-sizes="[20, 30, 40, 50, 100]" :page-size="size" :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      dataForm: {
        id: ''
      },
      dataList: [],
      pageIndex: 1,
      size: 20,
      totalPage: 0,
      dataListLoading: false
    }
  },
  methods: {
    init() {
      this.visible = true
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/sys/scheduleLog/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.size,
          'jobId': this.dataForm.id
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list
          this.totalPage = data.page.totalCount
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    // 每页数
    sizeChangeHandle(val) {
      this.size = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    // 失败信息
    showErrorInfo(id) {
      this.$http({
        url: this.$http.adornUrl(`/sys/scheduleLog/info/${id}`),
        method: 'get',
        params: this.$http.adornParams()
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$alert(data.log.error)
        } else {
          this.$message.error(data.msg)
        }
      })
    }
  }
}
</script>
<style scoped>
.el-dialog .el-input--small {
  width: 240PX !important;
}

.el-form-item__content {
  width: 240px !important;
}

.el-dialog .el-form-item {
  margin-right: 0PX !important;
  width: 240px !important;
}

.el-dialog.el-pagination .el-select .el-input .el-input__inner {
  padding-right: 25px;
  border-radius: 3px;
  width: 100% !important;
}

.page .el-select--medium .el-input__inner {
  width: 80px !important;
}
</style>
