<!-- 主页 -->
<!DOCTYPE html>
<html>
<head>
    <title>开发</title>
    <script src="https://cdn.mingsoft.net/platform/ms-store.umd.min.js"></script>
    <#include "../../include/head-file.ftl">
    <style>
    </style>
</head>
<body>
<#include 'reset-password.ftl'/>
<div id="app" class="mes_list">
    <el-row>
        <#--        工具栏-->
        <el-col :span="10">
            <el-row class="datetime">
                <el-col :span="4">时间范围 :</el-col>
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
    </el-row>
<#--    表格-->
    <el-table
            class="table"
            :data="dataList"
            stripe
            style="width: 100%"
            :max-height="tableHeight"
            :header-cell-style="{background:'#d3dcf4',color:'#515a6e'}">
        <el-table-column
                prop="reason"
                label="问题类型"
                align=left">
            <template slot="hh">
                查看处理
            </template>
        </el-table-column>
        <el-table-column
                prop="createTime"
                label="反馈时间"
                align=left">
        </el-table-column>
        <el-table-column
                prop=""
                label="处理结果"
                align=left">
        </el-table-column>
        <el-table-column
                prop="待处理"
                label="状态"
                align=left">
        </el-table-column>
        <el-table-column
                label="操作"
                width="150"
                align=left">
            <template #default="{ row }">
                <i class="el-icon-s-tools"></i>查看处理
            </template>
        </el-table-column>
    </el-table>
<#--    分页-->
    <div class="pagination-box">
        <span>共180条信息 共18页</span>
        <el-pagination
                :current-page.sync="currentPage1"
                background="false"
                :page-size="100"
                layout="prev, pager, next, jumper"
                :total="1000">
        </el-pagination>
    </div>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            value1: '',//日期
            value2: '',
            tableHeight: 530, //表格高度,
            currentPage1:'',
            dataList: [
                {
                    "id": 141,
                    "reason": "不履行承诺内容",
                    "createTime": "2022-01-08 10:34:17",
                    "result": null,
                    "status": "0",
                    "isNew": 1
                }
            ]
        },
        computed: {},
        watch: {},
        methods: {},
        created: function () {

        },
        mounted: function () {

        },
    })
</script>
<style>
    /*头部工具条*/
    .mes_list {
        padding: 10px;
        width: 100%;
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
/*    表格*/
    .table{
        margin-top: 15px;
        border: 1px solid #e2e7e9;
    }
/*    分页*/
    .pagination-box {
        display: flex;
        align-items: center;
        margin-top: 8px;
    }

    .el-pagination {
        margin: 0 auto;
    }
</style>
