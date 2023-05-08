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
<el-main  id="app" class="mesnum_list">
    <div>    <#--查询-->
        <el-row class="tools">
            <el-col span="6">
                <el-input
                        size="mini"
                        placeholder="请输入关键字"
                        v-model="search"
                        :clearable="true"
                        @clear="clearQuery"
                ></el-input>
            </el-col>
            <el-col span="3">
                <el-button @click="inquire" type="primary" class="blue_btn" size="mini" icon="el-icon-search" style="margin-left: 10px">
                    查询
                </el-button>
            </el-col>
        </el-row>
        <#--表格-->
        <el-table
                element-loading-text = "加载中，请稍后..."
                v-loading="loadingShow"
                border
                :height="tableHeight"
                class="table"
                :data="mesNumDataList"
                style="width: 100%">
            <el-table-column
                    label="经营者注册名称"
                    align=left">
                <template #default="{ row }">
                        <img
                                class="new"
                                v-show="row.isNew || row.count !== row.handleCnt"
                                src="${base}/static/images/be-done.png"
                                alt=""
                        /><span>{{ row.regName }}</span>
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
                <template #default="{ row }">
                    <@shiro.hasPermission name="fxxfcn:jdts">
                      <div class="action_btn blue_text" @click="goCheck(row)">
                          <i style="color:#409EFF;margin-right: 5px" class="el-icon-search"></i>
                          <span style="color:#409EFF">查看处理</span>
                      </div>
                    </@shiro.hasPermission>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination-box">
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="current"
                    @size-change="sizeChange"
                    background="false"
                    :page-size="size"
                    :page-sizes="[10, 20, 30, 40]"
                    :pager-count="pages"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
    <iframe v-if="action" :src="action" id="mes_num_list"></iframe>

</el-main>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            action: "",//跳转页面
            search: "",//搜索关键字
            tableHeight: 'calc(100vh - 135px)', //表格高度,
            mesNumDataList: [],//表格数据
            total: 0,//总数据
            current: 1,//当前页数
            pages: 4,//页码按钮的数量，当总页数超过该值时会折叠
            size: 10,//一页展示多少条数据
            loadingShow: true,
        },
        computed: {
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
                ms.http.get(ms.manager + '/feedback/countByApplicantList.do?current='+this.current+'&search='+this.search+'&type=2&size=' + this.size).then((res) => {
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
                this.action = ms.manager + `/route/reasonDetail.do?applicantsId=`+row.applicantsId
            },
            //清空重新获取数据
            clearQuery(){
                this.getMesNumList()
            },
            sizeChange(v) {
                this.size = v
                this.getMesNumList()
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
            window.getList = function (){
                that.getMesNumList()
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
        height: calc(100vh);
        background-color: white;
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


    .new {
        position: absolute;
        top: 0;
        left: 0;
        display: block;
        width: 25px;
        margin-right: 5px;
    }

    .pagination-box {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin-top: 10px;
    }
</style>
