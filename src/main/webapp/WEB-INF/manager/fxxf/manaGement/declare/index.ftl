<!-- 主页 -->
<!DOCTYPE html>
<html>
<head>
    <title>开发</title>
    <#include "../../include/head-file.ftl">
    <script src="https://cdn.mingsoft.net/platform/ms-store.umd.min.js"></script>
</head>
<body>
<#include 'reset-password.ftl'/>
<div id="app" class="statistics">
    <el-row>
        <el-col span="5">
            <el-input v-model="keyword" clearable placeholder="请输入关键字"></el-input>

        </el-col>
        <el-col span="19">
            <div class="button-Box">
                <el-button class="item-Box"  type="primary" icon="el-icon-search" @click="getOperatorStatisticList">查询</el-button>
                    <el-upload
                            ref="upload"
                            action
                            :http-request="uploadFile"
                            :show-file-list="false"
                    >
                        <el-button class="upDateItemBox" type="primary"  icon="el-icon-top">上传</el-button>
                    </el-upload>
                <el-button class="item-Box" :type="judgmentButton" :disabled="judgmentDisable"
                           :plain="judgmentDisable" icon="el-icon-close" @click="deleteOperatorStatisticList">
                    删除
                </el-button>
            </div>
        </el-col>
    </el-row>

    <el-table
            element-loading-text="加载中，请稍后..."
            v-loading="loadingShow"
            class="table"
            ref="multipleTable"
            :data="dataList"
            tooltip-effect="dark"
            style="width: 100%"
            @selection-change="handleSelectionChange">
        <el-table-column
                type="selection"
                width="50"
                align="center">
        </el-table-column>
        <el-table-column type="index" label="序号" width="130" align="center"></el-table-column>
        <el-table-column
                prop="filename"
                label="资料名称"
                width="800"
                align="left">
        </el-table-column>
        <el-table-column
                prop="updateTime"
                label="上传时间"
                width="250"
                show-overflow-tooltip>
        </el-table-column>
        <el-table-column
                prop="uploader"
                label="上传者"
                width="250"
                show-overflow-tooltip>
        </el-table-column>
        <el-table-column
                prop="downloads"
                label="下载次数"
                width="200"
                show-overflow-tooltip>
        </el-table-column>
        <el-table-column
                label="操作"
                show-overflow-tooltip>
            <template #default="{ row }">
                <span @click="deleteSingleOperatorStatisticList(row)" style="color: red; cursor: pointer">X删除</span>
            </template>
        </el-table-column>
    </el-table>

    <div class="pagination-box">
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
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            dataList: [],//数据
            chooseList: [],//选择数据
            keyword: '',//关键字
            total: 0,//总数据
            current: 1,//当前页数
            pages: 4,//页码按钮的数量，当总页数超过该值时会折叠
            size: 10,//一页展示多少条数据
            headers: {
                // 添加需要的请求头信息
                "Content-Type": "multipart/form-data"
            },
            loadingShow: true,
        },
        computed: {
            //计算总共有多少页return Math.ceil(total / pageSize);
            Totalpage() {
                return Math.ceil(this.total / this.size)
            },
            //判断是否可以删除
            judgmentButton() {
                return !this.chooseList.length > 0 ? 'info' : 'danger'
            },
            //判断是否禁用
            judgmentDisable() {
                return !this.chooseList.length > 0
            }
        },
        watch: {

        },
        methods: {
            //获取数据
            getList() {
                ms.http.get('/attachment/info.do?current=' + this.current + '&keyword=' + this.keyword + '&size=10').then(res => {
                    if (res.code != 200) return
                    this.dataList = res.data.records
                    this.total = Number(res.data.total)
                    this.loadingShow = false
                })
            },
            //查询
            getOperatorStatisticList() {
                this.loadingShow = true
                this.getList()
            },
            //批量删除
            deleteOperatorStatisticList() {
                let id = this.chooseList.map(item => {
                    return item.id
                })
                let idArr = {"idArr": id}
                this.$confirm('确认删除该项数据？', '删除提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    ms.http.post('/attachment/del.do', JSON.stringify(idArr), {headers: {'Content-Type': 'application/json'}}).then(res => {
                        if (res.code == 200) {
                            this.loadingShow = true
                            this.getList()
                        }
                    })
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                }).catch(() => {
                });
            },
            //单个删除
            deleteSingleOperatorStatisticList(row) {
                let idArr = {"idArr": [row.id]}
                this.$confirm('确认删除该项数据？', '删除提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    ms.http.post('/attachment/del.do', JSON.stringify(idArr), {headers: {'Content-Type': 'application/json'}}).then(res => {
                        if (res.code == 200) {
                            this.getList()
                        }
                    })
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                }).catch(() => {
                });
            },
            //选中按钮
            handleSelectionChange(val) {
                this.chooseList = val
            },
            //下一页
            handleCurrentChange(val) {
                this.current = val
                this.getList()
            },


            //上传文件
            uploadFile(item) {
                let formData = new FormData()
                formData.append('files', item.file);
                ms.http.post('/attachment/uploadFile.do', formData, {headers: {"Content-Type": "multipart/form-data"}}).then(res => {
                    if (res.code == 200) {
                        this.loadingShow = true
                        this.getList()
                        this.$message({
                            type: 'success',
                            message: res.msg
                        });
                    } else {
                        this.$message({
                            type: 'error',
                            message: '上传失败'
                        });
                    }
                })
            },
        },
        created: function () {
            this.getList()
            // ms.http.post('/attachment/countById/85.do').then(res => {
            //     console.log(res)
            // })
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

    .button-Box {
        display: inline-flex;
    }
    .item-Box{
        display: inline-block;
        width: 150px;
        margin-left: 10px;
    }

    .pagination-box {
        display: flex;
        align-items: center;
        margin-top: 8px;
    }

    .upDateItemBox {
        background-color: #2db7f5;
        width: 150px;
        margin-left: 10px;
    }

    .upDateItemBox:hover {
        background-color: #55c2f5;
    }
    .el-pagination {
        margin: 0 auto;
    }

</style>
