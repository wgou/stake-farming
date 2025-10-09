<template>
  <div class="app-container">
    <appLayout :showtop="showSearch">
      <template #top>
        <cardSearch>
          <div slot="left">
            <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()" size="small">
              <el-form-item label="用户名／用户操作">
                <el-input v-model="dataForm.key" placeholder="用户名／用户操作" clearable></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div slot="right">
            <el-button type="info" plain @click="getDataList()">查询</el-button>
          </div>
        </cardSearch>
      </template>
      <template #dmain="scope">
        <div>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getDataList"></right-toolbar>
        </div>
        <el-table :data="dataList" border v-loading="dataListLoading" style="width: 100%" :height="scope.maxheight">
          <el-table-column prop="id" header-align="center" align="center" width="80" label="ID">
          </el-table-column>
          <el-table-column prop="username" header-align="center" align="center" label="用户名">
          </el-table-column>
          <el-table-column prop="operation" header-align="center" align="center" label="用户操作">
          </el-table-column>
          <el-table-column prop="method" header-align="center" align="center" width="150" :show-overflow-tooltip="true"
            label="请求方法">
          </el-table-column>
          <el-table-column prop="params" header-align="center" align="center" width="150" :show-overflow-tooltip="true"
            label="请求参数">
          </el-table-column>
          <el-table-column prop="time" header-align="center" align="center" label="执行时长(毫秒)">
          </el-table-column>
          <el-table-column prop="ip" header-align="center" align="center" width="150" label="IP地址">
          </el-table-column>
          <el-table-column prop="createDate" header-align="center" align="center" width="180" label="创建时间">
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="page">
          <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex"
            :page-sizes="[20, 30, 40, 50, 100]" :page-size="size" :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper">
          </el-pagination>
        </div>
      </template>
    </appLayout>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataForm: {
        key: ''
      },
      dataList: [],
      pageIndex: 1,
      size: 20,
      totalPage: 0,
      dataListLoading: false,
      selectionDataList: [],
      showSearch:!this.isMobile
    }
  },
  created() {
    this.getDataList()
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/sys/log/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.size,
          'key': this.dataForm.key
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
    }
  }
}
</script>
