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
<el-main id="app" class="mes_num_list">
    <el-row>
        <#--        工具栏-->
        <el-col :span="20">
            <el-row class="date-time">
                <el-col :span="2" style="white-space:nowrap">时间范围 :</el-col>
                <el-col :span="15">
                    <div class="date-range">
                        <el-date-picker
                                v-model="startTime"
                                type="date"
                                size="mini"
                                placeholder="开始日期"
                                :picker-options="pickerBeginDate"
                                style="width: 320px"
                        ></el-date-picker>
                        <span class="date-separator">-</span>
                        <el-date-picker
                                v-model="endTime"
                                type="date"
                                size="mini"
                                placeholder="结束日期"
                                :picker-options="pickerEndDate"
                                style="width: 320px"
                        ></el-date-picker>
                        <el-button style="margin-left: 10px" type="primary" icon="el-icon-search" @click="inquire" size="mini">
                            查询
                        </el-button>
                    </div>
                </el-col>
            </el-row>
        </el-col>

    </el-row>
    <#--    表格-->
    <el-table
            class="table"
            :data="dataList"
            element-loading-text="加载中，请稍后..."
            v-loading="loadingShow"
            :height="tableHeight"
            border
            style="width: 100%">
        <el-table-column
                label="问题类型"
                align=left">
            <template #default="{ row }">
                <img
                        class="new"
                        v-show="row.isNew"
                        src="${base}/static/images/NEW.png"
                        alt=""
                /><span>{{ row.reason }}</span>
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

                <div style="cursor: pointer;" @click="goComplaint(row)"><i style="color:#409EFF;margin-right: 5px" class="el-icon-search"></i><span
                            style="color:#409EFF">处理</span></div>
            </template>
        </el-table-column>
    </el-table>
    <#--    分页-->
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
    <#--    返回按钮-->
    <el-button id="backSupervise" type="primary" size="mini" @click="backSupervise">返回</el-button>
    <#--    投诉页面-->
    <iframe v-if="action" :src="action" id="complaint"></iframe>
</el-main>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data() {
            return {
                dataList: [],//数据
                action: '',//跳转页面
                startTime: '',//开始日期
                endTime: '',//结束日期
                tableHeight: 'calc(100vh - 135px)', //表格高度,
                total: 0,
                size: 10,
                current: 1,
                pages: 4,
                id: 0,
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
            //返回按钮
            backSupervise() {
                window.parent.document.getElementById('mes_num_list').style.display = "none"
                //清除跳转页面
                window.parent.returnBack()
                window.parent.getList()
            },
            //跳转页面
            goComplaint(row) {
                this.id = row.id
                this.action = ms.manager + "/route/feedbackHandle.do?id=" + this.id
            },
            getList(startTime, endTime) {
                //切分上个页面传过来的id
                const url = window.location.href;
                const regex = /applicantsId=(\d+)/;
                const match = url.match(regex);
                const applicantsId = match ? match[1] : null;
                //请求数据
                if (startTime === undefined && endTime === undefined) {
                    startTime = ''
                    endTime = ''
                }
                ms.http.get(ms.manager + '/feedback/companyDetails.do?applicantsId=' + applicantsId + '&current=' + this.current + '&startTime=' + startTime + '&endTime=' + endTime+'&size='+this.size).then((res) => {
                    if (res.code != 200) return
                    const {records, total} = res.data
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
            inquire() {
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
            },
            sizeChange(v) {
                this.size = v
                this.getList()
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
            window.message = function (){
                that.getList()
                that.$message.success('反馈成功');
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
        height: calc(100vh);
        background-color: white;
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

    .button_groud {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    /*    表格*/
    .table {
        margin-top: 15px;
    }

    /*    分页*/
    .pagination-box {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin-top: 10px;
    }

    .new {
        position: absolute;
        top: 0;
        left: 0;
        display: block;
        width: 25px;
        margin-right: 5px;
    }

    /*返回按钮*/
    #backSupervise {
        position: fixed;
        top: 20px;
        right: 20px;
    }
</style>
