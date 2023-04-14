<!-- 主页 -->
<!DOCTYPE html>
<html>
<head>
    <title>开发</title>
    <#include "../../include/head-file.ftl">
    <script src="https://cdn.mingsoft.net/platform/ms-store.umd.min.js"></script>
</head>
<body>
<#--<#include 'reset-password.ftl'/>-->
<div id="app" class="statistics">
    <el-row>
        <#--        工具栏-->
        <el-col :span="7">
            <el-row class="datetime">
                <el-col :span="9">
                    <div class="date-range">
                        <el-date-picker
                                v-model="value1"
                                type="date"
                                placeholder="开始日期"
                                style="width: 220px"
                        ></el-date-picker>
                        <span class="date-separator">-</span>
                        <el-date-picker
                                v-model="value2"
                                type="date"
                                placeholder="结束日期"
                                style="width: 220px"
                        ></el-date-picker>
                    </div>
                </el-col>
            </el-row>
        </el-col>

        <el-col :span="2">
            <el-row class="button_groud" type="flex">
                <el-col span="24">
                    <el-button type="primary" icon="el-icon-search">查询</el-button>
                </el-col>
            </el-row>
        </el-col>

        <el-col :span="4">
            <el-row class="button_groud" type="flex">
                <el-col span="24">
                    <el-button type="success" icon="el-icon-top">导出</el-button>
                </el-col>
            </el-row>
        </el-col>
    </el-row>

    <el-table
            class="table"
            :data="dataList"
            stripe
            style="width: 100%"
            :max-height="tableHeight">
        <el-table-column
                prop=""
                label="地区"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="承诺单位数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="被反馈单位数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="摘牌数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="监督投诉的总条数"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="待处理"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="督促告诫"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="摘牌"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="处理结果"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="投诉问题不存在"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="其他"
                align=left">
        </el-table-column>
    </el-table>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            value1: '',//开始日期
            value2: '',//结束日期
            tableHeight:500,
            dataList:[]
        },
        computed: {
        },
        watch: {},
        methods: {
            getList(){
                ms.http.get( '/applicants/store/statListByUserRole.do').then((res) => {
                    console.log(res)
                })
            }
        },
        created: function () {
            this.getList()
        },
        mounted: function () {

        },
    })
</script>
<style>
    .statistics{
        padding: 10px;
    }
    .datetime {
        display: flex;
        justify-content: flex-start;
        align-items: center;
    }

    .date-range {
        display: flex;
        align-items: center;
        width: 500px;
    }

    .date-separator {
        margin: 0 10px;
        font-size: 16px;
        font-weight: bold;
    }

    .button_groud {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>
