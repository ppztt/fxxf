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
<div  id="app" class="mesnum_list">
    <div>    <#--查询-->
        <el-row class="tools">
            <el-col span="6">
                <el-input
                        placeholder="请输入关键字"
                        v-model="search"
                        :clearable="true"
                ></el-input>
            </el-col>
            <el-col offset="1" span="3">
                <el-button @click="inquire" type="primary" class="blue_btn" size="medium" icon="el-icon-search">
                    查询
                </el-button>
            </el-col>
        </el-row>
        <#--表格-->
        <el-table
                element-loading-text = "加载中，请稍后..."
                v-loading="loadingShow"
                border
                class="table"
                :data="mesNumDataList"
                border
                style="width: 100%"
                :max-height="tableHeight">
            <el-table-column
                    prop="regName"
                    label="经营者注册名称"
                    align=left">

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
                <template #default="{ row }">
                    <div class="action_btn blue_text" @click="goCheck(row)">
                        <i class="el-icon-s-tools"></i>
                        <span style="color:#409EFF">查看处理</span>
                    </div>

                </template>
            </el-table-column>
        </el-table>

        <div class="paginationbox">
            <span style="white-space:nowrap">共{{total}}条信息 共{{Totalpage}}页</span>
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="current"
                    background="false"
                    :page-size="size"
                    :pager-count="pages"
                    layout="prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
    <iframe v-if="action" :src="action" id="mes_num_list"></iframe>

</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            action: "",//跳转页面
            search: "",//搜索关键字
            tableHeight: 530, //表格高度,
            mesNumDataList: [],//表格数据
            total: 0,//总数据
            current: 0,//当前页数
            pages: 4,//页码按钮的数量，当总页数超过该值时会折叠
            size: 10,//一页展示多少条数据
            loadingShow: true,
        },
        computed: {
            //计算总共有多少页return Math.ceil(total / pageSize);
            Totalpage(){
                return Math.ceil(this.total/this.size)
            }
        },
        watch: {},
        methods: {
            //下一页
            handleCurrentChange(val) {
                this.loadingShow = true
                this.current = val
                this.getMesNumList()
            },
            //获取数据
            getMesNumList() {
                ms.http.get(ms.manager + '/feedback/countByApplicantList.do?current='+this.current+'&search='+this.search+'&type=1').then((res) => {
                    if(res.code!=200)return
                    this.mesNumDataList = res.data.records
                    this.total = Number(res.data.total)
                    this.loadingShow = false
                })
            },
            //搜索查询
            inquire(){
                this.loadingShow = true
                this.current = 1
                this.getMesNumList()
            },
            //跳转页面
            goCheck(row) {
                this.action = ms.manager + `/route/feedbackDetail.do?applicantsId=`+row.applicantsId
            }

        },
        created: function () {
            this.getMesNumList()

        },
        mounted: function () {
            //清除跳转页面
            let that = this
            window.returnBack = function (){
                that.action = ""
            }
        },
    })
</script>
<style>
    #mes_num_list {
        border: 0;
        width: 100%;
        height: 100%;
        position: absolute;
        top:0;
        left:0;
        z-index: 5;
    }

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
        /*justify-content: center;*/
        align-items: center;
        /*padding: 10px 5px;*/
        cursor: pointer;
        color: #7b93d2;
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
