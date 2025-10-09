<template>
    <div class="layout-app" id="layoutapp">
        <div class="left" v-if="showleft">
            <el-card class="left-crad">
                <slot name="leftmain"></slot>
            </el-card>
        </div>
        <div class="right">
            <div class="top-crad" id="topcrad" :style="(!showmain) ? 'margin-bottom: 16PX;' : 'margin-bottom: 8PX;'"
                v-if="showtop">
                <slot name="top"></slot>
            </div>
            <el-card class="default" v-show="!showmain">
                <slot name="dmain" :maxheight="tableheight"></slot>
            </el-card>
            <el-card class="special" v-show="showmain">
                <slot name="smain" :maxheight="stableheight"></slot>
            </el-card>
        </div>
    </div>
</template>
<script>
export default {
    name: "app-layout",
    props: {
        showleft: {
            type: Boolean,
            default: false
        },
        showmain: {
            type: Boolean,
            default: false
        },
        showtop: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            tableheight: 0,
            stableheight: 0,
            rtopheight: "",
        }
    },
    watch: {
        showtop: {
            handler: function (newBool, oldBool) {
                if (newBool) {
                    this.$nextTick(() => {
                        this.rtopheight = document.getElementById("topcrad").offsetHeight;
                        let tableheight=document.getElementById("layoutapp").offsetHeight - this.rtopheight - 160;
                        let stableheight=document.getElementById("layoutapp").offsetHeight - this.rtopheight - 153;
                        this.tableheight =tableheight<500?500:tableheight;
                        this.stableheight = stableheight<500?500:stableheight;
                    })
                } else {
                    this.$nextTick(() => {
                        this.rtopheight = 0
                        let tableheight=document.getElementById("layoutapp").offsetHeight - this.rtopheight - 144;
                        let stableheight=document.getElementById("layoutapp").offsetHeight - this.rtopheight - 145;
                        this.tableheight =tableheight<500?500:tableheight;
                        this.stableheight = stableheight<500?500:stableheight;
                    })
                }
            },
            deep: true,
            immediate: true
        }
    },
    created() {
        this.$nextTick(() => {
            if (this.showtop) {
                this.rtopheight = document.getElementById("topcrad").offsetHeight;
                let tableheight=document.getElementById("layoutapp").offsetHeight - this.rtopheight - 160;
                let stableheight=document.getElementById("layoutapp").offsetHeight - this.rtopheight - 153;
                this.tableheight =tableheight<500?500:tableheight;
                this.stableheight = stableheight<500?500:stableheight;
            }

        })
    }
}
</script>

<style lang="scss" scoped>
.layout-app {
    height: 100%;
    width: 100%;
    font-family: 'SourceHanSans' !important;
    background-color: #000;
    overflow-y: auto;

    ::v-deep .el-card {
        overflow: visible !important;
    }

    .left {
        height: 100%;
        display: flex;
        flex-direction: column;
        width: 305px;
        margin-right: 16PX;

        .left-crad {
            height: 100%;
        }

    }

    .right {
        overflow-y: auto;
        background-color: transparent;
        border: none;
        height: 100%;
        .top-crad {
            margin-bottom: 16PX;
            background-color: transparent;
        }

        .default {
            background-color: #1d232b;
            overflow: hidden;
        }

        .special {
            flex: 1;
            background-color: #fff;
        }
    }

}
</style>