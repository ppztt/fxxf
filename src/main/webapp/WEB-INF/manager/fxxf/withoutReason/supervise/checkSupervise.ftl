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
<div id="app" class="mes_num_list">
    <el-row>
        <#--        工具栏-->
        <el-col :span="10">
            <el-row class="datetime">
                <el-col :span="4">时间范围 :</el-col>
                <el-col :span="9">
                    <div class="date-range">
                        <el-date-picker
                                v-model="startTime"
                                type="date"
                                placeholder="开始日期"
                                style="width: 220px"
                        ></el-date-picker>
                        <span class="date-separator">-</span>
                        <el-date-picker
                                v-model="endTime"
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
                    <el-button type="primary" icon="el-icon-search" @click="inquire">查询</el-button>
                </el-col>
            </el-row>
        </el-col>
    </el-row>
    <#--    表格-->
    <el-table
            class="table"
            :data="dataList"
            element-loading-text = "加载中，请稍后..."
            v-loading="loadingShow"
            border
            style="width: 100%"
            :max-height="tableHeight">
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
                prop="result"
                label="处理结果"
                align=left">

        </el-table-column>
        <el-table-column
                prop="待处理"
                label="状态"
                align=left">
            <template #default="{ row }">
                <span v-if="!row.status || row.status == 0">待处理</span>
                <span v-if="row.status == 1">已处理</span>
            </template>
        </el-table-column>
        <el-table-column
                label="操作"
                width="150"
                align=left">
            <template #default="{ row }">

                <div style="cursor: pointer;" @click="goComplaint(row)"><i class="el-icon-s-tools"></i><span
                            style="color:#409EFF">&nbsp处理</span></div>
            </template>
        </el-table-column>
    </el-table>
    <#--    分页-->
    <div class="pagination-box">
        <span>共{{total}}条信息 共{{Totalpage}}页</span>
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
    <#--    返回按钮-->
    <el-button id="backSupervise" type="primary" size="medium" @click="backSupervise">返回</el-button>
    <#--    投诉页面-->
    <iframe v-if="action" :src="action" id="complaint"></iframe>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            dataList: [],//数据
            action: '',//跳转页面
            startTime: '',//开始日期
            endTime: '',//结束日期
            tableHeight: 530, //表格高度,
            loadingShow: true,
            total:0,
            size:10,
            current:0,
            pages:4,
            id:0,
        },
        computed: {
            //计算总共有多少页return Math.ceil(total / pageSize);
            Totalpage(){
                return Math.ceil(this.total/this.size)
            }
        },
        watch: {},
        methods: {
            //返回按钮
            backSupervise() {
                window.parent.document.getElementById('mes_num_list').style.display = "none"
                //清除跳转页面
                window.parent.returnBack()
            },
            //跳转页面
            goComplaint(row) {
                this.id = row.id
                this.action = ms.manager + "/route/reasonHandle.do?id="+this.id
            },
            getList(startTime,endTime){
                //切分上个页面传过来的id
                const url = window.location.href;
                const regex = /applicantsId=(\d+)/;
                const match = url.match(regex);
                const applicantsId = match ? match[1] : null;
                //请求数据
                if(startTime===undefined&&endTime===undefined){
                    startTime=''
                    endTime=''
                }
                    ms.http.get(ms.manager + '/feedback/companyDetails.do?applicantsId='+applicantsId+'&current='+this.current+'&size=10&startTime='+startTime+'&endTime='+endTime).then((res) => {
                        if(res.code!=200)return
                        const {records,total} = res.data
                        this.dataList = records
                        this.total = Number(total)
                        this.loadingShow = false
                    })
            },
            //下一页
            handleCurrentChange(val) {
                this.loadingShow = true
                this.current = val
                this.getList()
            },
            //查询
            inquire(){
                this.loadingShow = true
                //处理日期格式
                let startTime = '';
                let endTime = '';
                if (this.startTime) {
                    startTime = this.startTime.getFullYear() + '-' + ('0' + (this.startTime.getMonth() + 1)).slice(-2) + '-' + ('0' + this.startTime.getDate()).slice(-2);
                }
                if (this.endTime) {
                    endTime = this.endTime.getFullYear() + '-' + ('0' + (this.endTime.getMonth() + 1)).slice(-2) + '-' + ('0' + this.endTime.getDate()).slice(-2)
                }
                this.getList(startTime,endTime)
            }
        },
        created: function () {
            this.getList()
        },
        mounted: function () {
            ////清除跳转页面
            let that = this
            window.returnBack = function () {
                that.action = ""
            }
        },
    })
</script>
<style>
    #complaint {
        border: 0;
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 5;
    }

    /*头部工具条*/
    .mes_num_list {
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
    .table {
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


    /*返回按钮*/
    #backSupervise {
        position: absolute;
        top: 0;
        right: 10px;
        margin: 10px;
    }
</style>
