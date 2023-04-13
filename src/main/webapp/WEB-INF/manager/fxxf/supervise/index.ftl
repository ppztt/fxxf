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
<div id="app" class="mesnum_list">
    <#--查询-->
    <el-row class="tools">
        <el-col span="6">
            <el-input
                    placeholder="请输入关键字"
                    v-model="search"
                    :clearable="true"
            ></el-input>
        </el-col>
        <el-col offset="1" span="3">
            <el-button type="primary" class="blue_btn" size="medium" icon="el-icon-search">
                查询
            </el-button>
        </el-col>
    </el-row>
    <#--表格-->
    <el-table
            class="table"
            :data="dataList"
            stripe
            style="width: 100%"
            :max-height="tableHeight"
            :header-cell-style="{background:'#d3dcf4',color:'#515a6e'}">
        <el-table-column
                prop="regName"
                label="经营者注册名称"
                align=left">
            <template slot="hh">
                查看处理
            </template>
        </el-table-column>
        <el-table-column
                prop="count"
                label="留言数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop="handleCnt"
                label="处理数量"
                align=left">
        </el-table-column>
        <el-table-column
                label="操作"
                width="150"
                align=left">
            <template #default="{ row }" >
                <div @click="goCheck" class="action_btn blue_text">
                    <i class="el-icon-s-tools"></i>
                    查看
                </div>
                <#--                <img src="ms.base.+/static/images/tip.png" />-->

            </template>
        </el-table-column>
    </el-table>

    <div class="paginationbox">
        <span>共180条信息 共18页</span>
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage1"
                background="false"
                :page-size="100"
                :pager-count="4"
                layout="prev, pager, next, jumper"
                :total="1000">
        </el-pagination>
    </div>
<#--    <img :src="ms.base+'/static/images/'待办-2.png'"/>-->
<#--    <img :src="ms.base+'/static/images/logo.png'"/>-->
</div>

<#--<iframe v-if="action" :src=''-->
<#--         class="ms-loading"></iframe>-->
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            action: "",//跳转页面
            search: "",//搜索关键字
            tableHeight: 530, //表格高度,
            dataList: [//模拟数据
                {
                    applicantsId: 57517,
                    regName: "清远市聚能燃气有限公司",
                    count: 2,
                    handleCnt: 0,
                    isNew: 1
                },
                {
                    applicantsId: 2938,
                    regName: "惠州市天虹商场有限公司惠城区瑶芳大厦店",
                    count: 1,
                    handleCnt: 0,
                    isNew: 1
                },
                {
                    applicantsId: 57517,
                    regName: "清远市聚能燃气有限公司",
                    count: 2,
                    handleCnt: 0,
                    isNew: 1
                },
                {
                    applicantsId: 2938,
                    regName: "惠州市天虹商场有限公司惠城区瑶芳大厦店",
                    count: 1,
                    handleCnt: 0,
                    isNew: 1
                },
                {
                    applicantsId: 57517,
                    regName: "清远市聚能燃气有限公司",
                    count: 2,
                    handleCnt: 0,
                    isNew: 1
                },
                {
                    applicantsId: 2938,
                    regName: "惠州市天虹商场有限公司惠城区瑶芳大厦店",
                    count: 1,
                    handleCnt: 0,
                    isNew: 1
                },
            ],
            currentPage1: 1,//模拟数据
        },
        computed: {},
        watch: {},
        methods: {
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
            },
            //跳转
            goCheck(){
                this.action = ms.manager + "/xwh/checkSupervise.do"
            }

        },
        created: function () {

        },
        mounted: function () {

        },
    })
</script>
<style>
    <#-- 头部工具条  -->
    .mesnum_list {
        padding: 10px;
        width: 100%
    }

    .tools {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        font-size: 14px !important;
        margin-top: 10px;
    }


    <#-- 表格  -->
    .table {
        margin-top: 15px;
        border: 1px solid #e2e7e9;
    }

    .actions {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .action_btn {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 10px 5px;
        cursor: pointer;
        color: #7b93d2;
    }


    .el-button--primary {
        background: #5d7cc9 !important;
        color: #fff !important;
        border: 0!important;
    }


    img {
        display: block;
        width: 15px;
        margin-right: 5px;
    }

    .paginationbox {
        display: flex;
        align-items: center;
        margin-top: 8px;
    }


    .el-pagination {
        margin: 0 auto;
    }
</style>
