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
        <#--        工具栏-->
        <el-col :span="22">
            <el-row>
                <el-col :span="24">
                    <div class="date-range">
                        <el-select style="width: 320px" @change="selectCity(city)" @clear="clearCity" clearable v-model="city"
                                   placeholder="市">
                            <el-option
                                    v-for="item in area"
                                    :key="item.code"
                                    :label="item.name"
                                    :value="item.name">
                            </el-option>
                        </el-select>
                        <el-select style="width: 320px;margin: 0 10px" clearable :disabled="district.length>0?false:true"
                                   @change="selectCity(city)" v-model="region" placeholder="市/县/区/镇">
                            <el-option
                                    v-for="item in district"
                                    :key="item.code"
                                    :label="item.name"
                                    :value="item.name">
                            </el-option>
                        </el-select>
                        <el-date-picker
                                v-model="startTime"
                                type="date"
                                placeholder="开始日期"
                                style="width: 320px"
                        ></el-date-picker>
                        <el-date-picker
                                style="margin: 0 10px;width: 320px"
                                v-model="endTime"
                                type="date"
                                placeholder="结束日期"
                        ></el-date-picker>
                    </div>
                </el-col>
            </el-row>
        </el-col>

        <el-col :span="2">
            <el-row class="button_groud" type="flex">
                <el-col span="24">
                    <el-button type="primary" icon="el-icon-search" @click="getOperatorStatisticList">查询</el-button>
                </el-col>
            </el-row>
        </el-col>
    </el-row>

    <el-table
            element-loading-text="加载中，请稍后..."
            v-loading="loadingShow"
            class="table"
            :data="dataList"
            border
            show-summary
            style="width: 100%">
        border
        <el-table-column
                label="类别"
                align="center"
                width="880px">
            <template #default="{ row }">
                <div>
                    <span class="category">{{row.servicetype}}</span>
                    <span class="category">{{row.typename}}</span>
                </div>
            </template>
        </el-table-column>
        <el-table-column
                prop="fxxf"
                label="放心消费承诺单位"
                align="center"
        >
        </el-table-column>
        <el-table-column
                prop="wlyxf"
                label="线下无理由退货承诺店"
                align="center"
        >
        </el-table-column>
    </el-table>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            dataList: [],//数据
            area: [],//广东省地区
            city: '',//选择的市
            district: [],//区/县
            region: '',//选择的区/县
            startTime: '',//开始日期
            endTime: '',//结束日期
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
                ms.http.get("/typestat/list?city=" + this.city + "&district=" + this.region + "&startTime=" + startTime + "&endTime=" + endTime).then(res => {
                    if (res.code != 200) return
                    this.dataList = res.data
                    this.loadingShow = false
                })
            },
            //获取地区数据
            getArea(){
                ms.http.get("/gd-regin.do").then(res => {
                    this.area = res.data
                })
            },
            //选择市
            selectCity(items) {
                //区
                const districts = this.area.filter(item => {
                    return item.name === items
                })
                if (districts.length > 0) {
                    this.district = districts[0].children
                }
            },
            //清空市
            clearCity() {
                this.district = []
                //判断市区是否有选
                if (!this.city) {
                    this.region = ''
                }
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
                //判断市区是否有选
                if (!this.city) {
                    this.region = ''
                }
                this.getList(startTime, endTime)
            }
        },
        created: function () {
            this.getList()
            this.getArea()
        },
        mounted: function () {

        },
    })
</script>
<style>
    html {
        overflow: scroll;
    }

    .statistics {
        padding: 10px;
    }

    .table {
        margin-top: 10px;
    }

    .category {
        width: 400px;
        display: inline-block;
    }

    .datetime {
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
</style>
