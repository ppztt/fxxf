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
<el-main id="app" class="statistics">
    <el-row>
        <#--        工具栏-->
        <el-col :span="24">
            <el-row class="date-time">
                <el-col :span="24">
                    <div class="date-range">
                        <el-date-picker
                                v-model="startTime"
                                type="date"
                                placeholder="开始日期"
                                :picker-options="pickerBeginDate"
                                size="mini"
                                style="width: 320px"
                        ></el-date-picker>
                        <span class="date-separator">-</span>
                        <el-date-picker
                                v-model="endTime"
                                type="date"
                                placeholder="结束日期"
                                :picker-options="pickerEndDate"
                                size="mini"
                                style="width: 320px"
                        ></el-date-picker>
                        <el-button style="margin-left: 50px" type="primary" icon="el-icon-search" @click="getOperatorStatisticList" size="mini">查询</el-button>
                        <@shiro.hasPermission name="wlythcn:jdtsj">
                            <el-button style="margin-left: 100px" type="success" icon="el-icon-top" @click="derive" size="mini">导出</el-button>
                        </@shiro.hasPermission>
                    </div>
                </el-col>
            </el-row>
        </el-col>

    </el-row>

    <el-table
            element-loading-text = "加载中，请稍后..."
            v-loading="loadingShow"
            class="table"
            :data="dataList"
            show-summary
            border
            style="width: 100%">
        <el-table-column
                prop="area"
                label="地区"
                align=left">
        </el-table-column>
        <el-table-column
                prop="applicantsCnt"
                label="承诺单位总数"
                align=left">
        </el-table-column>
        <el-table-column
                prop="addContents1Cnt"
                label="增加其他承诺的单位数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop="commitmentCnt"
                label="连续承诺单位数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop="nullCnt"
                label="零有效投诉单位数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop="beSupervisedCnt"
                label="被监督告诫单位数量"
                align=left">
        </el-table-column>
        <el-table-column
                prop="delCnt"
                label="被摘牌单位数量"
                align=left">
        </el-table-column>
    </el-table>
</el-main>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data(){
            return{
                startTime: '',//开始日期
                endTime: '',//结束日期
                tableHeight: 'calc(100vh - 100px)',//表格高度
                dataList: [],//数据
                loadingShow: true,
                // 开始结束日期限制
                pickerBeginDate: {
                    disabledDate: (time) => {
                        if (this.endTime) {
                            return (
                                time.getTime() >= new Date(this.endTime).getTime()
                            );
                        }
                    }
                },
                // 结束日期限制
                pickerEndDate: {
                    disabledDate: (time) => {
                        if (this.startTime) {
                            return (
                                time.getTime() <= new Date(this.startTime).getTime()
                            );
                        }
                    }
                }
            }
        },
        computed: {

        },
        watch: {},
        methods: {
            //获取数据
            getList(startTime, endTime) {
                if (startTime === undefined && endTime === undefined) {
                    startTime = ''
                    endTime = ''
                }
                ms.http.post('/applicants/operatorStatistics/list.do',JSON.stringify({"type": 1,"startTime":startTime,"endTime":endTime}),{headers:{'Content-Type': 'application/json'}}).then(res=>{
                    if(res.code!=200)return
                    this.dataList = res.data
                    this.loadingShow = false
                })
            },
            //导出接口
            derive() {
                window.open('/applicants/operatorStatistics/export.do?type=1')
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
        height: calc(100vh);
        background-color: white;
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


</style>
