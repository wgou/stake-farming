<!--
* @Date: 2023/09/08 18:06:07
* @LastEditTime: 2023/09/08 18:06:07
* @Description:card-search
-->
<template>
  <div class="card-padding">
    <el-card class="card-search">
      <div class="card-top">
        <slot name="top"></slot>
      </div>
      <div class="content">
        <div class="card-left">
          <slot name="left"></slot>
        </div>
        <div class="card-right" :style="{ width: width + 'px' }">
          <slot name="right"></slot>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
export default {
  name: "card-search",
  data() {
    return {};
  },
  props: {
    width: Number,
  },
};
</script>

<style lang="scss" scoped>
.card-padding {
  .card-search {
    background-color: #1d232b;
    padding: 0px !important;
    color: #fff !important;

    .el-card__body {
      padding: 16px;
    }

    .card-top {
      padding-bottom: 20px;
    }

    .content {
      width: 100%;
      height: auto;
      display: flex;

      .card-left {
        flex: 1;
      }

      .card-right {
        // width: 158PX;
      }
    }
  }
}
</style>