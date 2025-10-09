<!--
* @Date: 2023/10/13 13:40:10
* @LastEditTime: 2023/10/13 13:40:10
* @Description:editLayout
-->
<template>
    <div class='edit-layout'>
        <el-card class="default" id="edit">
            <slot name="main"></slot>
        </el-card>
        <div class="btn" v-if="showend">
            <slot name="btn"></slot>
        </div>
    </div>
</template>

<script>
export default {
    name: "editLayout",
    props: {
        showend: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
        }
    },
    mounted() {
        document.getElementById('edit').scrollTop = 0;
    }
}
</script>

<style lang="scss">
.staffname {
    .el-input--small .el-input__inner {
        width: 146px !important;
    }

    .el-input-group__append {
        padding: 0 10px;
        cursor: pointer;
    }

    .el-input-group__prepend {
        padding: 0 10px;
        background-color: #016EEB;
        color: #fff;
        cursor: pointer;
    }
}

.selectstaff {
    .el-input-group__append {
        cursor: pointer;
    }

    .el-input-group__prepend {
        background-color: #016EEB;
        color: #fff;
        cursor: pointer;
    }
}
</style>
<style lang="scss" scoped>
.edit-layout {
    height: 100%;
    width: 100%;
    border-radius: 4px;
    background: #000;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .default {
        flex: 1;
        overflow: hidden;
        overflow-y: auto;
        background: #000;
    }

    ::v-deep.el-card__body {
        padding: 0 20PX !important;

    }

    .btn {
        height: 56PX;
        margin-top: 20PX;
        margin-right: 20PX;
        align-self: flex-end;
    }
}

::v-deep .el-form-item__label {
    font-size: 13PX !important;
    font-weight: 400;
    font-family: "SourceHanSans";
    padding: 0 0PX 0 0 !important;
    margin-right: 12PX;
    width: 63PX !important;
}
</style>