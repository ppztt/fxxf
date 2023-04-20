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
        <el-col :span="12">
            <el-row class="date-time">
                <el-col :span="24">
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
                        <el-button style="margin-left: 50px" type="primary" icon="el-icon-search" @click="getOperatorStatisticList">查询</el-button>
                        <el-button style="margin-left: 200px" type="success" icon="el-icon-top" @click="derive">导出</el-button>
                    </div>
                </el-col>
            </el-row>
        </el-col>

    </el-row>

    <el-table
            class="table"
            element-loading-text = "加载中，请稍后..."
            v-loading="loadingShow"
            :data="dataList"
            show-summary
            border
            style="width: 100%"
            :max-height="tableHeight">
        <el-table-column
                prop="city"
                label="地区"
                align="center">
        </el-table-column>
        <el-table-column
                prop="companyTotal"
                label="承诺单位数量"
                align="center">
        </el-table-column>
        <el-table-column
                prop="complaintCompanyNum"
                label="被反馈单位数量"
                align="center">
        </el-table-column>
        <el-table-column
                prop="takeOff"
                label="摘牌数量"
                align="center">
        </el-table-column>
        <el-table-column
                prop="complaintTotal"
                label="监督投诉的总条数"
                align="center">
        </el-table-column>

        <el-table-column
                label="处理结果"
                align="center">
            <el-table-column
                    prop="unprocessed"
                    label="待处理"
                    align="center">
            </el-table-column>
            <el-table-column
                    prop="warning"
                    label="督促告诫"
                    align="center">
            </el-table-column>
            <el-table-column
                    prop="disqualification"
                    label="摘牌"
                    align="center">
            </el-table-column>
            <el-table-column
                    prop="nonExistentComplaints"
                    label="投诉问题不存在"
                    align="center">
            </el-table-column>
            <el-table-column
                    prop="other"
                    label="其他"
                    align="center">
            </el-table-column>
        </el-table-column>

    </el-table>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            startTime: '',//开始日期
            endTime: '',//结束日期
            tableHeight: 500,//表格高度
            dataList: [],//数据
            loadingShow: true,

        },
        computed: {},
        watch: {},
        methods: {
            //获取数据
            getList(startTime, endTime) {
                if (startTime === undefined && endTime === undefined) {
                    startTime = ''
                    endTime = ''
                }
                ms.http.get(ms.manager + '/feedback/statistic.do?type=2&startTime=' + startTime + '&endTime=' + endTime).then((res) => {
                    if(res.code!=200)return
                    this.dataList = res.data.records
                    this.loadingShow = false
                })
            },
            //导出接口
            derive() {
                window.open(ms.manager + '/feedback/exportStatistic.do?type=2')
            },
            //查询
            getOperatorStatisticList() {
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
                this.getList(startTime, endTime)
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
    .statistics {
        padding: 10px;
    }

    .table {
        margin-top: 10px;
    }

    .date-time {
        display: flex;
        justify-content: flex-start;
        align-items: center;
    }

    .date-range {
        display: flex;
        align-items: center;
    }

    .date-separator {
        margin: 0 10px;
        font-size: 16px;
        font-weight: bold;
    }

    .el-table thead.is-group th.el-table__cell{
        background: none;
    }
</style>
