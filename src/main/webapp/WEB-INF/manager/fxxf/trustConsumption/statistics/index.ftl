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
                                :picker-options="pickerEndDate"
                                placeholder="结束日期"
                                size="mini"
                                style="width: 320px"
                        ></el-date-picker>
                        <el-row>
                            <el-col span="10" offset="1">
                                <el-button type="primary" icon="el-icon-search" @click="getOperatorStatisticList"
                                           size="mini">查询
                                </el-button>
                            </el-col>
                            <el-col span="10" offset="3">
                                <@shiro.hasPermission name="wlythcn:jdtstj">
                                    <el-button type="primary" icon="el-icon-top" @click="derive" size="mini">导出
                                    </el-button>
                                </@shiro.hasPermission>
                            </el-col>
                        </el-row>

                    </div>
                </el-col>
            </el-row>
        </el-col>
    </el-row>

    <el-table
            element-loading-text="加载中，请稍后..."
            ref="table"
            v-loading="loadingShow"
            class="table"
            :data="dataList"
            height="80%"
            :summary-method="getSummaries"
            show-summary
            border
            style="width: 100%; height: 30%">
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
</el-main>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data() {
            return {
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
        computed: {},
        watch: {},
        methods: {
            getSummaries(param) {
                const {columns, data} = param;
                const sums = [];
                columns.forEach((column, index) => {
                    if (index === 0) {
                        sums[index] = '合计';
                        return;
                    }
                    const values = data.map(item => Number(item[column.property]));
                    if (!values.every(value => isNaN(value))) {
                        sums[index] = values.reduce((prev, curr) => {
                            const value = Number(curr);
                            if (!isNaN(value)) {
                                return prev + curr;
                            } else {
                                return prev;
                            }
                        }, 0);
                        sums[index] += '';
                    } else {
                        sums[index] = 'N/A';
                    }
                });
                return sums;
            },
            //获取数据
            getList(startTime, endTime) {
                if (startTime === undefined && endTime === undefined) {
                    startTime = ''
                    endTime = ''
                }
                ms.http.get(ms.manager + '/feedback/statistic.do?type=1&startTime=' + startTime + '&endTime=' + endTime).then((res) => {
                    if (res.code != 200) return
                    this.dataList = res.data.records
                    this.loadingShow = false
                })
            },
            //导出接口
            downFile(url) {
                let that = this
                let iframe = document.createElement('iframe');
                iframe.style.display = 'none';
                iframe.style.zIndex = "-999"
                iframe.addEventListener('load', function () {
                    // 文件下载完成
                    iframe.src = ""
                });

                document.body.appendChild(iframe);
                iframe.src = url;
            },
            derive() {
                let url = ms.manager + '/feedback/exportStatistic.do?type=1'
                // this.$message({
                //     showClose: true,
                //     message: "正在导出"
                // })
                this.fullscreenLoading = true
                axios({
                    url: ms.manager + '/feedback/exportStatistic.do?type=1',
                    responseType: 'blob',
                    noHandleResponse: true,
                    timeout: 60000
                }).then(res => {
                    console.log(res)
                    if (res.code && res.code == 500) {
                        this.$message.error(res.msg || "导出失败")
                    } else {
                        let filename = decodeURIComponent(res.headers['content-disposition'].match(/filename=(.*)$/)[1]);
                        let blob = new Blob([res.data], {type: "application/vnd.ms-excel"});
                        let url = window.URL.createObjectURL(blob);
                        let a = document.createElement('a');
                        a.href = url;
                        a.setAttribute('download', filename);
                        document.body.appendChild(a);
                        a.click();
                        a.remove();
                        this.fullscreenLoading = false
                    }
                })
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

    .el-table thead.is-group th.el-table__cell {
        background: none;
    }
    .el-table__footer-wrapper{
        position: absolute;
        bottom: 0;
    }
    .el-button--primary:focus{
        color: #FFF;
        background-color: #409EFF;
        border-color: #409EFF;
        background: #409EFF
    }
</style>
