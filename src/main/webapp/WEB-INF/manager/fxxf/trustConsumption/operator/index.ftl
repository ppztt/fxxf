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
<#--    <el-row>-->
<#--        &lt;#&ndash;        工具栏&ndash;&gt;-->
<#--        <el-col :span="7">-->
<#--            <el-row class="datetime">-->
<#--                <el-col :span="9">-->
<#--                    <div class="date-range">-->
<#--                        <el-date-picker-->
<#--                                v-model="startTime"-->
<#--                                type="date"-->
<#--                                placeholder="开始日期"-->
<#--                                style="width: 220px"-->
<#--                        ></el-date-picker>-->
<#--                        <span class="date-separator">-</span>-->
<#--                        <el-date-picker-->
<#--                                v-model="endTime"-->
<#--                                type="date"-->
<#--                                placeholder="结束日期"-->
<#--                                style="width: 220px"-->
<#--                        ></el-date-picker>-->
<#--                    </div>-->
<#--                </el-col>-->
<#--            </el-row>-->
<#--        </el-col>-->

<#--        <el-col :span="2">-->
<#--            <el-row class="button_groud" type="flex">-->
<#--                <el-col span="24">-->
<#--                    <el-button type="primary" icon="el-icon-search" @click="getOperatorStatisticList">查询</el-button>-->
<#--                </el-col>-->
<#--            </el-row>-->
<#--        </el-col>-->

<#--        <el-col :span="4">-->
<#--            <el-row class="button_groud" type="flex">-->
<#--                <el-col span="24">-->
<#--                    <el-button type="success" icon="el-icon-top" @click="derive">导出</el-button>-->
<#--                </el-col>-->
<#--            </el-row>-->
<#--        </el-col>-->
<#--    </el-row>-->

<#--    <el-table-->
<#--            class="table"-->
<#--            :data="dataList"-->
<#--            stripe-->
<#--            show-summary-->
<#--            border-->
<#--            style="width: 100%"-->
<#--            :max-height="tableHeight">-->
<#--        <el-table-column-->
<#--                prop="city"-->
<#--                label="地区"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--        <el-table-column-->
<#--                prop="companyTotal"-->
<#--                label="承诺单位总数"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--        <el-table-column-->
<#--                prop="complaintCompanyNum"-->
<#--                label="增加其他承诺的单位数量"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--        <el-table-column-->
<#--                prop="takeOff"-->
<#--                label="连续承诺单位数量"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--        <el-table-column-->
<#--                prop="complaintTotal"-->
<#--                label="零有效投诉单位数量"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--        <el-table-column-->
<#--                prop="unprocessed"-->
<#--                label="被监督告诫单位数量"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--        <el-table-column-->
<#--                prop="warning"-->
<#--                label="被摘牌单位数量"-->
<#--                align=left">-->
<#--        </el-table-column>-->
<#--    </el-table>-->
    99
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
            dataList: []//数据
        },
        computed: {},
        watch: {},
        methods: {
            //获取数据
            getList(startTime, endTime) {
                ms.http.post('/applicants/unit/operatorStatistics/list.do?type=1',JSON.stringify({}),{headers:{'Content-Type': 'application/json'}}).then(res=>{
                    console.log(res)
                })
                //请求数据
                //判断是否有日期
                // if (startTime && endTime) {
                //     ms.http.get(ms.manager + '/feedback/statistic.do?type=1&startTime=' + startTime + '&endTime=' + endTime).then((res) => {
                //         this.dataList = res.data.records
                //     })
                // } else {
                //     ms.http.get(ms.manager + '/feedback/statistic.do?type=1').then((res) => {
                //         this.dataList = res.data.records
                //     })
                // }
            },
            //导出接口
            derive() {
                ms.http.get(ms.manager + '/feedback/exportStatistic.do?type=1')
            },
            //查询
            getOperatorStatisticList() {
                if (this.startTime && this.endTime) {
                    //处理日期格式
                    const startTime = this.startTime.getFullYear() + '-' + ('0' + (this.startTime.getMonth() + 1)).slice(-2) + '-' + ('0' + this.startTime.getDate()).slice(-2);
                    const endTime = this.endTime.getFullYear() + '-' + ('0' + (this.endTime.getMonth() + 1)).slice(-2) + '-' + ('0' + this.endTime.getDate()).slice(-2);
                    this.getList(startTime, endTime)
                } else {
                    this.getList()
                }
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
