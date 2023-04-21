<!DOCTYPE html>
<html>
<head>
    <title>消费者</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/ms/2.0/ms-manager.umd.js"></script>
    <script src="${base}/static/plugins/sockjs/1.4.0/sockjs.min.js"></script>
    <script src="${base}/static/plugins/stomp/2.3.3/stomp.min.js"></script>
    <!-- 此部分是铭飞平台MStroe的客户端（MStore不在铭飞开源产品范围），如果不需要使用MStore可以删除掉 -->
    <script src="https://cdn.mingsoft.net/platform/ms-store.umd.min.js"></script>
</head>
<body>
<div id="index" class="ms-index">
    <ms-search ref="search" @search="search" :condition-data="conditionList" :conditions="conditions"></ms-search>
    <el-header class="ms-header" height="120px">
        <el-row class="tools" ref="tools" type="flex" justify="center" align="middle">
            <!-- 工具栏 -->
            <el-col span="24">
                <el-input placeholder="请输入关键字" v-model="search" :clearable="true"></el-input>
            </el-col>
            <el-col span="20">
                <el-select ref="city" v-model="city" placeholder="市" :clearable="true" filterable
                           @change="cityChange(city)">
                    <el-option v-for="item in regionData" :value="item.name" :key="item.code" :label="item.name">
                    </el-option>
                </el-select>
            </el-col>
            <el-col span="20">
                <el-select ref="district" v-model="district" placeholder="市/县/区/镇" :clearable="true" filterable
                           @change="districtChange(district)"
                           :disabled="!city ||districtData.length == 0">
                    <el-option v-for="item in districtData" :value="item.code" :key="item.code"
                               :label="item.name">
                    </el-option>
                </el-select>
            </el-col>

            <el-col span="20">
                <el-select placeholder="经营类别" :clearable="true" v-model="management">
                    <el-option :value="'商品类'" :key="'商品类'">商品类</el-option>
                    <el-option :value="'服务类'" :key="'服务类'">服务类</el-option>
                    <el-option :value="'商品及服务类'" :key="'商品及服务类'">商品及服务类</el-option>
                </el-select>
            </el-col>
            <el-col span="20">
                <el-select v-model="status" :data="statusData" :clearable="true" placeholder="状态">
                    <el-option v-for="item in statusInfo" :label="item.value" :key="item.id" :value="item.id">
                        {{item.value}}
                    </el-option>
                </el-select>
            </el-col>
            <el-col span="6">
                <el-date-picker format="yyyy-MM-dd" v-model="startTime" placeholder="开始时间" @on-change="
        (value) => {
          startTime = value;
        }
      "></el-date-picker>
            </el-col>
            <el-col span="6">
                <el-date-picker format="yyyy-MM-dd" v-model="endTime" placeholder="结束时间" @on-change="
        (value) => {
          endTime = value;
        }
      "></el-date-picker>
            </el-col>
            <el-col>
                <el-button size="medium" size="medium" class="blue_btn" icon="el-icon-search" @click="getUnitList">
                    查询
                </el-button>
            </el-col>
        </el-row>
        <div class="list-header-btns">
            <div class="item">
                <el-button size="medium" class="blue_btn2" @click="isShowEnteringModal = true" icon="el-icon-edit"
                >
                    <!-- <img class="left" src="@/assets/images/1_17.png" alt /> -->
                    录入
                </el-button>
            </div>
            <#-- 录入弹出框 -->
            <el-dialog
                    ref="formData"
                    :visible.sync="isShowEnteringModal"
                    title="放心消费承诺经营者 - 录入"
                    width="800"
            >
                <el-row>
                    <el-col offset="1" span="22">
                        <el-form
                                ref="formApply"
                                label-width="150px"
                                :model="formData"
                                :rules="formrules"
                                :label-position="'right'">
                            <div class="form-part">基本信息</div>
                            <el-row>
                                <el-col span="24">
                                    <el-form-item label="经营者注册名称" prop="regName">
                                        <el-input
                                                v-model="formData.regName"
                                                placeholder="请输入经营者注册名称"
                                        ></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row>
                                <el-col span="24"
                                >
                                    <el-form-item label="统一社会信用代码" prop="creditCode">
                                        <el-input
                                                v-model="formData.creditCode"
                                                placeholder="请输入统一社会信用代码"
                                        ></el-input>
                                    </el-form-item
                                    >
                                </el-col>
                            </el-row>

                            <el-row v-if="modalType === 1">
                                <el-col span="11">
                                    <el-form-item label="网店名称" prop="onlineName">
                                        <el-input
                                                v-model="formData.onlineName"
                                                placeholder="请输入网店名称"
                                        ></el-input>
                                    </el-form-item
                                    >
                                </el-col>
                                <el-col span="11">
                                    <el-form-item label="所属平台" prop="platform">
                                        <el-input
                                                v-model="formData.platform"
                                                placeholder="请输入所属平台"
                                        ></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-row>
                                <el-col span="11">
                                    <el-form-item label="门店名称" prop="storeName">
                                        <el-input
                                                v-model="formData.storeName"
                                                placeholder="请输入门店名称"
                                        ></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-form-item label="经营场所" prop="addrs">
                                <el-row :key="index" v-for="(addr, index) in formData.addrs">
                                    <el-col span="5">
                                        <el-select
                                                :ref="'city'+index"
                                                v-model="addr.city"
                                                placeholder="市"
                                                :clearable="true"
                                                filterable
                                                @change="cityChange(addr.city)">
                                            <el-option
                                                    v-for="item in regionData"
                                                    :value="item.name"
                                                    :key="item.code"
                                                    :label="item.name"
                                            >{{ item.name }}
                                            </el-option>
                                        </el-select>
                                    </el-col>
                                    <el-col span="5">
                                        <el-select
                                                :ref="`district${index}`"
                                                v-model="addr.district"
                                                @change="districtChange(district)"
                                                :disabled="!addr.city ||districtData.length == 0">
                                            <el-option
                                                    v-for="item in districtData"
                                                    :value="item.name"
                                                    :key="item.code"
                                                    :label="item.name">{{ item.name }}
                                            </el-option>
                                        </el-select>
                                    </el-col>
                                    <el-col span="8">
                                        <el-input v-model="addr.address" placeholder="请输入经营场所地址"></el-input>
                                    </el-col>
                                    <el-col span="1">
                                        <el-button
                                                v-if="index === 0"
                                                icon="el-icon-plus"
                                                @click="addAddress"
                                                circle></el-button>
                                        <el-button
                                                v-else
                                                icon="el-icon-minus"
                                                circle
                                                @click="
            () => {
              formData.addrs.splice(index, 1);
              districtDataArr.splice(index, 1);
              $forceUpdate();
            }
          "
                                        ></el-button>
                                    </el-col>
                                </el-row>
                            </el-form-item>

                            <el-row>
                                <el-col span="11">
                                    <el-form-item label="经营类别" prop="management">
                                        <el-select
                                                ref="management"
                                                placeholder="类别"
                                                :clearable="true"
                                                v-model="formData.management"
                                                @change="managementChange">
                                            <el-option :value="'商品类'" :key="'商品类'">商品类</el-option>
                                            <el-option :value="'服务类'" :key="'服务类'">服务类</el-option>
                                            <el-option :value="'商品及服务类'" :key="'商品及服务类'">商品及服务类
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col span="11">
                                    <el-form-item label="详细类别" prop="details">
                                        <el-select
                                                placeholder="详细类别"
                                                v-model="formData.details"
                                                multiple="true">

                                            <el-option
                                                    v-for="item in activeManageType"
                                                    :value="item"
                                                    :key="item"

                                                    :label="item"
                                            >{{ item }}
                                            </el-option
                                            >
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-row>
                                <el-col span="11">
                                    <el-form-item label="负责人姓名" prop="principal">
                                        <el-input
                                                v-model="formData.principal"
                                                placeholder="请输入负责人姓名"
                                        ></el-input>
                                    </el-form-item
                                    >
                                </el-col>
                                <el-col span="11">
                                    <el-form-item label="负责人电话" prop="principalTel">
                                        <el-input
                                                type="tel"
                                                v-model="formData.principalTel"
                                                placeholder="请输入负责人电话"
                                        ></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-row>
                                <el-col :span="11">
                                    <el-form-item label="企业申请日期：">
                                        <el-row>
                                            <el-col :span="24">
                                                <el-form-item prop="applicationDate">
                                                    <el-date-picker
                                                            v-model="formData.applicationDate"
                                                            type="date"
                                                            placeholder="请选择时间"
                                                            style="width: 100%">
                                                    </el-date-picker>
                                                </el-form-item>
                                            </el-col>
                                        </el-row>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <div class="form-part">承诺事项及具体内容</div>

                            <el-row v-if="modalType === 1">
                                <el-col span="24">
                                    <el-form-item label="品质保证" prop="contents1">
                                        <el-input v-model="formData.contents1" type="textarea"
                                                  placeholder="请输入文字"></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-row v-if="modalType === 1">
                                <el-col span="24">
                                    <el-form-item label="诚信保证" prop="contents2">
                                        <el-input v-model="formData.contents2" type="textarea"
                                                  placeholder="请输入文字"></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-row v-if="modalType === 1">
                                <el-col span="24">
                                    <el-form-item label="维权保证" prop="contents3">
                                        <el-input v-model="formData.contents3" type="textarea"
                                                  placeholder="请输入文字"></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <el-row v-if="modalType === 2">
                                <el-col span="24">
                                    <el-form-item label="适用商品" prop="contents1">
                                        <el-input v-model="formData.contents1" type="textarea"
                                                  placeholder="请输入适用商品"></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row v-if="modalType === 2">
                                <el-col span="24">
                                    <el-form-item label="退货期限（天）" prop="contents2">
                                        <el-input v-model="formData.contents2" type="textarea"
                                                  placeholder="请输入退货期限"></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row v-if="modalType === 2">
                                <el-col span="24">
                                    <el-form-item label="退货约定" prop="contents3">
                                        <el-input v-model="formData.contents3" type="textarea"
                                                  placeholder="请输入退货约定具体信息"></el-input>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <div class="form-part" v-if="modalType === 1">
                                其他承诺事项及具体内容
                            </div>
                            <el-form-item label="内容" v-if="modalType === 1" prop="contents4">
                                <el-input v-model="formData.contents4" type="textarea"
                                          placeholder="请输入内容"></el-input>
                            </el-form-item>

                            <div class="submit-btns">
                                <el-button
                                        size="medium"
                                        type="primary"
                                        @click="handleSubmit('formApply', modalType)">
                                    提交
                                </el-button>
                                <!-- <el-button @click="handleReset('formApply')">重置</el-button> -->
                            </div>
                        </el-form>
                    </el-col>
                </el-row>
            </el-dialog>
            <div class="item">
                <el-upload class="upload" :show-file-list="false"
                           accept="xlsm"
                           :before-upload="beforeUploadAction"
                           :on-success="uploadSucAction"
                           :on-error="uploadErrAction"
                           :action="'/applicants/preImport.do?type='+type">
                    <el-button size="medium" class="green_btn" type="primary" :disabled="!canImport"
                               icon="el-icon-bottom"
                               :title="!canImport ? '没有权限导入' : ''">
                        导入
                    </el-button>
                </el-upload>
            </div>
            <div class="item">
                <el-dropdown @command="exportData">
                    <el-button size="medium" size="medium" class="dgreen_btn" icon="el-icon-top">
                        <!-- <img class="left" src="@/assets/images/1_20.png" alt /> -->
                        导出
                        <!-- <img class="right" src="@/assets/images/1_15.png" alt /> -->
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item :command="''">导出全部</el-dropdown-item>
                        <el-dropdown-item :command="1" divided>导出在期</el-dropdown-item>
                        <el-dropdown-item :command="0" divided>导出摘牌</el-dropdown-item>
                        <el-dropdown-item :command="2" divided>导出过期</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
            <div class="item">
                <el-button size="medium" class="red_btn" @click="downLoadTemplate" icon="el-icon-download">
                    <!-- <img class="left" src="@/assets/images/1_17.png" alt /> -->
                    模板下载
                </el-button>
            </div>

            <div class="item" style="margin-left: 30px;">
                <el-button size="medium" :class="{ red_btn : ids.ids.length!=0 }" icon="el-icon-close"
                           :disabled="ids.ids.length == 0"
                           @click="deleteMoreConsumer">
                    删除
                </el-button>
            </div>
        </div>
    </el-header>
    <el-main class="ms-container">
        <el-table
                v-loading="loading"
                :data="unitDataList"
                border
                height="250"
                style="width:100%"
                @select="addIDs"
                @select-all="addIDs">

            <el-table-column
                    fixed="left"
                    width="60"
                    type="selection"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="regName"
                    label="经营者注册名称"
                    fixed="left"
                    width="200"
                    align="left">
                <template slot-scope="{row}">
                    <img class="new" v-show="row.isNew" src="${base}/static/images/PendingTrial.png" alt="" /><span>{{ row.regName }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="createType"
                    label="申请来源"
                    width="200"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="account"
                    label="创建人"
                    width="200"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="storeName"
                    label="门店名称"
                    width="200"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="address"
                    label="经营场所地址"
                    width="200,"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="状态"
                    width="80"
                    align="left">
                <template slot-scope="scope">
                    <span v-show="scope.row.status == 1">在期</span>
                    <span v-show="scope.row.status == 0">摘牌</span>
                    <span v-show="scope.row.status == 2">过期</span>
                    <span v-if="scope.row.status == '4'">待审核</span>
                    <span v-if="scope.row.status == '5'">县级审核通过</span>
                    <span v-if="scope.row.status == '6'">行业协会审核通过</span>
                    <span v-if="scope.row.status == '7'">审核不通过</span>
                    <span v-if="scope.row.status == '8'">行业协会审核不通过</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="validity"
                    label="有效期"
                    width="170"
                    align="left">
                <template slot-scope="scope">
                    <span>{{ scope.row.startTime }}</span>
                    <span v-if="scope.row.startTime">~</span>
                    <span>{{ scope.row.endTime }}</span>
                </template>
            </el-table-column>
            <el-table-column
                    prop="createTime"
                    label="创建时间"
                    width="200"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="creditCode"
                    label="通过时间"
                    width="200"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="management"
                    label="经营类别"
                    width="130"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="details"
                    label="详细类别"
                    width="210"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="principal"
                    label="负责人"
                    width="120"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="principalTel"
                    label="手机号码"
                    width="140"
                    align="left">
            </el-table-column>
            <el-table-column
                    prop="action"
                    label="操作"
                    fixed="right"
                    width="350"
                    align="left">
                <template slot-scope="{row}">
                    <div class="actions" :id="row.id">
                        <el-button class="action_btn blue_text" icon="el-icon-search" @click="openNew(1,row)">
                            查看
                        </el-button>
                        <el-button class="action_btn orange_text" icon="el-icon-edit" @click="openNew(0,row)">
                            编辑
                        </el-button>
                        <el-button class="action_btn blue_text" icon="el-icon-circle-check" @click="openNew(3,row)">
                            审核
                        </el-button>
                        <el-button class="action_btn green_text" v-if="row.status == 1" icon="el-icon-s-promotion"
                                   @click="openNew(2,row)">
                            摘牌
                        </el-button>
                        <el-button type="text" class="action_btn red_text" icon="el-icon-close"
                                   @click="deleteConsumer(row.id)">
                            删除
                        </el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                background
                @size-change="sizeChange"
                @current-change="currentChange"
                :current-page="current"
                :page-sizes="[10, 20, 30, 40]"
                :page-size="size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
        </el-pagination>
    </el-main>
    <iframe :src="action" class="ms-iframe-style" v-show="action" id="check" ref="check"></iframe>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: '#index',
        data: {
            loading: true,
            // 用户信息临时
            userInfo: {
                // 页面跳转
                action: "",
                account: "测试",
                address: null,
                city: "",
                createTime: "2023-01-10 03:59:30",
                creditCode: null,
                district: "",
                email: "",
                id: 177,
                management: null,
                newPassword: null,
                password: "52f24ccfeef7e6a0f4a17fbc45647361ebb06a839ec6172064a2167299e33d1d",
                phone: "13922108999",
                principal: null,
                principalTel: null,
                province: null,
                realname: "魏",
                roleId: 1,
                roleName: null,
                storeName: null,
                town: null,
                updateTime: "2023-01-10 03:59:30",
                usertype: 1,
                zipcode: ""
            },
            action: "",
            modalType: 1,
            isShowEnteringModal: false,
            // 弹出框数据
            formData: {
                regName: "",
                storeName: "",
                platform: "",
                onlineName: "",
                city: "",
                district: "",
                address: "",
                addrs: [
                    {
                        city: "",
                        district: "",
                        address: "",
                    },
                ],
                creditCode: "",
                management: "",
                details: "",
                principal: "",
                principalTel: "",
                contents1: "",
                contents2: "",
                contents3: "",
                applicationDate: "",
            },
            formrules: {
                regName: [{required: true, message: '经营者注册名称不能为空', trigger: 'blur'}],
                creditCode: [{required: true, message: '统一社会信用代码不能为空，且为18位', trigger: 'blur'},
                    {min: 18, trigger: 'blur'}],
                onlineName: [{required: true, message: '网店名称不能为空', trigger: 'blur'}],
                platform: [{required: true, message: '所属平台不能为空', trigger: 'blur'}],
                storeName: [{required: true, message: '门店名称不能为空', trigger: 'blur'}],
                addrs: [{required: true, message: '经营场所不能为空', trigger: 'blur'}],
                management: [{required: true, message: '经营类别不能为空', trigger: 'blur'}],
                details: [{required: true, message: '详细类别不能为空', trigger: 'blur'}],
                principal: [{required: true, message: '负责人姓名不能为空', trigger: 'blur'}],
                principalTel: [{required: true, message: '手机号码不能为空', trigger: 'blur'},
                    {pattern: /^1[3|5|7|8|9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur"}],
            },
            districtDataArr: [],
            textList: {
                edit: "编辑",
                check: "查看",
                delist: "摘牌",
                audit: "审核",
            },
            activeManageType: [
                // 当前经营类别数据
            ],
            // 经营类别
            manageType: {
                commodities: [],
                services: [],
            },
            auditColumns: [
                {
                    title: "审核单位",
                    key: "name",
                },
                {
                    title: "审核意见",
                    key: "contents",
                },
                {
                    title: "审核状态",
                    key: "status",
                },
                {
                    title: "审核时间",
                    key: "createTime",
                },
            ],
            statusInfo: [
                {id: "1", value: "在期"},
                {id: "0", value: "摘牌"},
                {id: "2", value: "过期"},
                {id: "4", value: "待审核"},
                {id: "5", value: "县级审核通过"},
                {id: "6", value: "和业协会审核通过"},
                {id: "7", value: "审核不通过"},
                {id: "8", value: "行业审核不通过"}
            ],
            management: "",
            isShowComfirm: false, //显示弹窗
            comfirmContent: "", //弹窗内容
            uploadId: null,
            // ----
            // 搜索关键字
            search: "",
            startTime: "", // 开始时间
            endTime: "", // 结束时间
            // 城市级联工具
            city: "", //市
            district: "", //县
            town: "", //镇
            regionSelect: [], //地区选中
            regionData: [], //地区数据 一级市数据
            // cityData: [], //市数据
            districtData: [], //某市县数据
            townData: [], //某县镇数据
            status: "",
            statusData: [],
            // 表格
            tableHeight: null, //表格高度
            current: 1, //当前页
            size: 10, //每页页数
            total: 0, //总数
            columns: [
                {
                    type: "selection",
                    width: 60,
                    align: "left",
                    fixed: "left",
                },
                {
                    title: "经营者注册名称",
                    key: "regName",
                    slot: "regName",
                    fixed: "left",
                    minWidth: 200,
                    align: "left",
                },
                {
                    title: "申请来源",
                    key: "createType",
                    minWidth: 200,
                    align: "left",
                },
                {
                    title: "创建人",
                    key: "account",
                    minWidth: 200,
                    align: "left",
                },
                {
                    title: "门店名称",
                    key: "storeName",
                    minWidth: 200,
                    align: "left",
                },
                {
                    title: "经营场所地址",
                    key: "address",
                    minWidth: 200,
                    align: "left",
                    <#--render: (h, obj) => {-->
                    <#--    // console.log(h, obj);-->
                    <#--    let cityArr = obj.row.city.split(",");-->
                    <#--    let districtArr = obj.row.district.split(",");-->
                    <#--    let addressArr = obj.row.address.split(",");-->
                    <#--    let addrs = cityArr.map((item, index) => {-->
                    <#--        let city = item;-->
                    <#--        let district = districtArr[index];-->
                    <#--        let address = addressArr[index];-->
                    <#--        return `${city}-${district}-${address}`;-->
                    <#--    });-->

                    <#--    return (-->
                    <#--        <div>-->
                    <#--            <Poptip word-wrap width="300" transfer={true}>-->
                    <#--                <p style="cursor:pointer">-->
                    <#--                    {obj.row.address-->
                    <#--                        ? obj.row.address.length > 10-->
                    <#--                            ? `${obj.row.address.slice(0, 10)}...`-->
                    <#--                            : obj.row.address-->
                    <#--                        : ""}-->
                    <#--                </p>-->

                    <#--                <div slot="content">-->
                    <#--                    {addrs.map((item, index) => {-->
                    <#--                        return (-->
                    <#--                            <p>-->
                    <#--                                地址{index + 1}：{item}-->
                    <#--                            </p>-->
                    <#--                        );-->
                    <#--                    })}-->
                    <#--                </div>-->
                    <#--            </Poptip>-->
                    <#--        </div>-->
                    <#--    );-->
                    <#--},-->
                },
                {
                    title: "状态",
                    key: "status",
                    slot: "status",
                    minWidth: 80,
                    align: "left",
                },
                {
                    title: "有效期",
                    slot: "validity",
                    minWidth: 170,
                    align: "left",
                },
                {
                    title: "创建时间",
                    key: "createTime",
                    minWidth: 200,
                    align: "left",
                },
                {
                    title: "通过时间",
                    key: "startTime",
                    minWidth: 100,
                    align: "left",
                },
                {
                    title: "统一社会信用代码",
                    key: "creditCode",
                    minWidth: 200,
                    align: "left",
                },
                {
                    title: "经营类别",
                    key: "management",
                    minWidth: 130,
                    align: "left",
                },
                {
                    title: "详细类别",
                    key: "details",
                    minWidth: 210,
                    align: "left",
                    <#--render: (h, obj) => {-->
                    <#--    // console.log(h, obj)-->
                    <#--    return (-->
                    <#--        <div>-->
                    <#--            <Poptip-->
                    <#--                word-wrap-->
                    <#--                width="300"-->
                    <#--                transfer={true}-->
                    <#--                content={obj.row.details}-->
                    <#--            >-->
                    <#--                <p style="cursor:pointer">-->
                    <#--                    {obj.row.details-->
                    <#--                        ? obj.row.details.length > 10-->
                    <#--                            ? `${obj.row.details.slice(0, 10)}...`-->
                    <#--                            : obj.row.details-->
                    <#--                        : ""}-->
                    <#--                </p>-->
                    <#--            </Poptip>-->
                    <#--        </div>-->
                    <#--    );-->
                    <#--},-->
                },
                {
                    title: "负责人",
                    key: "principal",
                    minWidth: 120,
                    align: "left",
                },
                {
                    title: "手机号码",
                    key: "principalTel",
                    minWidth: 140,
                    align: "left",
                },

                {
                    title: "操作",
                    key: "action",
                    slot: "action",
                    fixed: "right",
                    minWidth: 350,
                    align: "left",
                },
            ],
            unitDataList: [],
            // --------
            isShowRemove: false,
            removeType: "one", //删除方式 all one
            idActive: null, //当前id
            selectDataList: [], //删除列表
            isShowEnteringModal: false,
            allowFiles: [".xlsm"],
            conditions: [],
            conditionList: [],
            dataList: [], //消费者列表
            selectionList: [],//消费者列表选中
            total: 0, //总记录数量
            pageSize: 10, //页面数量
            currentPage: 1, //初始页
            manager: ms.manager,
            // 详细类别
            manageType: {},
            // 被选中的类别的详细类别
            activeManageType: [],
            loading: true,//加载状态
            emptyText: '',//提示文字
            // 级联选
            canImport: true,
            // 消费者 1 无承诺 2
            type: "1",
            details: "",
            // 被删除的id
            ids: {ids: []}
        },
        watch: {},
        methods: {
            save: function () {
            },
            del: function () {

            }
            ,
            search: function () {

            }
            ,
            currentChange: function (v) {
                this.current = v;
                this.debounce(this.getUnitList(), 1000)
            },
            // 防抖
            debounce(fun, wait = 1500) {
                let timeout = null
                return function () {
                    if (timeout) {//如果存在定时器就清空
                        clearTimeout(timeout)
                    }
                    timeout = setTimeout(function () {
                        fun()
                    }, wait)
                }

            },
            sizeChange: function (v) {
                this.size = v;
                this.debounce(this.getUnitList(), 1000)
            }
            ,
            handleSelectionChange: function () {

            }
            ,
            isChecked: function () {

            }
            ,
            cityChange: function (name) {
                // 一级市发生改变
                if (name) {
                    let cityData_active = this.regionData.find((value) => value.name == name);
                    this.districtData = cityData_active.children;
                    this.district = "";
                    // this.town = "";
                }
            },
            districtChange: function (name) {
                // 二级地 县等发生改变
                if (name) {
                    let districtData_active = this.districtData.find((value => value.name == name));
                    // this.townData = districtData_active.children;
                    // this.town = "";
                }
            },
            selectChange: function () {
            }
            ,
            isShowEnteringModal: function () {

            }
            ,
            beforeUploadAction(file) {
                if (this.allowFiles.indexOf(file.name.substring(file.name.lastIndexOf("."))) === -1) {
                    this.$message.error({
                            message: '该后缀格式的文件不支持上传，仅支持' + this.allowFiles.join(" / ") + '文件'
                        }
                    )
                }
                // 上传前
            },
            uploadConfirm() {
                // 确认上传
                ms.http
                    .post('/applicants/import/'+this.uploadId+'.do')
                    .then((res) => {
                        if (res.code == 200) {
                            this.$message({
                                message: "导入成功",
                                type: 'success'
                            });
                            this.getUnitList();
                        }
                        this.isShowComfirm = false;
                    });
            },
            uploadSucAction(even) {
                // console.log("导入成功", even);
                if (even.code == 200 && even.data.length > 0 && !even.data[0].errorMsg) {
                    this.uploadId = even.data[0].fileId;
                    this.uploadConfirm();
                }else if(even.code == 500){
                    this.$message.error(even.data[0].errorMsg)
                }
            },
            uploadErrAction() {
                this.$message.error("导入失败");
            },
            downLoadTemplate: function () {
                // 下载数据 //状态(1:在期； 0:摘牌 ；2过期)
                window.open('/applicants/downTemplateFile/' + this.type + '.do');
            },
            exportData(command) {
                // 导出数据
                ms.http.get('/applicants/export.do', {status: command, type: this.type}).then((res) => {

                    window.open('/applicants/export.do?status=' + command + '&type=' + this.type)
                    this.$message({
                        message: '导出成功',
                        type: "success"
                    })
                })
            },
            regionReset() {
                // 地区重设
                if (this.formData.city) {
                    this.districtData =
                        this.regionData.find((value) =>
                            value.value == this.formData.city
                        ).children || [];
                    if (this.formData.district) {
                        this.townData =
                            this.districtData.find((value) =>
                                value.value == this.formData.district,
                            ).children || [];
                    }
                }
            },
            // 获取表格数据
            getUnitList: function () {
                // 获取放心消费承诺单位列表
                this.loading = true;
                ms.http.post("/applicants/listPage.do", JSON.stringify({
                    search: this.search,
                    city: this.city,
                    district: this.district,
                    status: this.status,
                    management: this.management,
                    startTime: this.startTime,
                    current: this.current,
                    size: this.size,
                    details: this.details,
                    endTime: this.endTime,
                    town: this.town,
                    type: this.type
                }), {headers: {'Content-type': 'application/json;charset=UTF-8'},})
                    .then((res) => {
                        let data = res.data;
                        this.total = Number(data.total);
                        this.unitDataList = data.records;
                        this.loading = false;
                    });
            },
            validityChange(date) {
                // 有效期发生改变
                // console.log("有效期",date)
                this.formData.startTime = date[0];
                this.formData.endTime = date[1];
            },
            // 获取地区信息
            getRegionData() {
                ms.http.get('/gd-regin.do').then((res) => {
                    this.regionData = res.data
                })
            },
            getManagerType() {
                ms.http.get('/type/listGoodsAndServiceType.do').then((res) => {
                    this.manageType = res.data
                })
            },
            managementChange(data) {
                // 主经营类别改变
                if (data == "商品类") {
                    // 商品类
                    this.activeManageType = this.manageType.commodities || [];
                } else if (data == "服务类") {
                    // 经营类
                    this.activeManageType = this.manageType.services || [];
                } else if (data == "商品及服务类") {
                    this.activeManageType = [
                        ...this.manageType.commodities,
                        ...this.manageType.services,
                    ];
                }
            },
            handleSubmit(name, type = 1) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        this.setApply(type);
                    } else {
                        this.$message.error("请按要求重新填写");
                    }
                });
                // this.isShow = true;
                // this.$emit("update:isShow", true);
            },
            // 录入功能
            setApply(type) {
                // let addrs = JSON.stringify(this.formData.addrs)
                let params = JSON.stringify(this.formData)
                ms.http.post('/applicants/apply/input.do', {params},
                    {headers: {'Content-type': 'application/json;charset=UTF-8'}}).then((res) => {

                })

            },
            // 经营地址添加按钮
            addAddress() {
                let userInfo = this.userInfo
                if (this.formData.addrs === undefined) {
                    this.formData.addrs = []
                }
                this.formData.addrs.push({
                    city: userInfo.city || "",
                    district: userInfo.district || "",
                    address: "",
                });
                let data = this.resetRegion(userInfo.city);
                this.districtDataArr[this.formData.addrs.length - 1] = data;
                this.$forceUpdate();
            },
            resetRegion(cityName) {
                if (cityName) {
                    let data = this.regionData.find((value) => value.name == cityName,).children || [];
                    this.districtDataArr.push(data);
                    return data;
                    // if (this.formData.district) {
                    //   this.townData =
                    //     Lodash.find(this.districtData, {
                    //       name: this.formData.district,
                    //     }).children || [];
                    // }
                }
            },
            // 打开编辑等页面
            openNew(num, row) {
                this.$refs.check.style.display = "block"
                this.action = ms.manager + "/route/check.do?type=" + num + "&id=" + row.id;
            },
            // 删除单个单位
            deleteConsumer(id) {
                this.$confirm('确认删除该项数据?', '删除提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error',
                    center: true
                }).then(() => {
                    ms.http.post('/applicants/remove/' + id + '.do').then(()=>{
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getUnitList()
                    })
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            // 为删除添加id
            addIDs(selection) {
                this.ids.ids = []
                selection.forEach((item) => {
                    this.ids.ids.push(item.id)
                })
            },
            // 删除多个单位
            deleteMoreConsumer() {
                let ids = JSON.stringify(this.ids)
                this.$confirm('确认删除该项数据?', '删除提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error',
                    center: true
                }).then(() => {
                    ms.http.post('/applicants/remove.do', ids)
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                    this.getUnitList()
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            // 从上一级页面返回新页面时的成功的提示
            currentTopic(msg) {
                this.$message({
                    message: msg,
                    type: "success"
                })
            },

        },
        mounted: function () {
            this.getRegionData();
            this.getManagerType();
            this.getUnitList();
            // this.action = ms.manager + "/xwh/consumer/check.do";
            let that = this
            window.returnBack = function () {
                that.action = ""
            }
            window.currentTopic = this.currentTopic
        }
    })
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
    }

    #index .ms-iframe-style {
        position: absolute;
        top: 0;
        width: 100%;
        height: 100%;
        border: 0;
        z-index: 5;
    }

    .blue_btn {
        background: #5d7cc9 !important;
        color: #fff !important;
        border: 0;
    }

    .blue_btn:hover {
        background: #899ed1 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }

    .el-col {
        margin-right: 30px;
    }

    .list-header-btns {
        display: flex;
        margin-top: 15px;
    }

    .list-header-btns .item {
        margin-right: 6px;
    }

    .ivu-dropdown {
        margin-left: 0 !important;
    }

    .blue_text {
        color: #7b93d2;
    }

    .green_text {
        color: #54c698;
    }

    .orange_text {
        color: #e4a041;
    }

    .red_text {
        color: #f05858;
    }

    .blue_btn {
        background: #5d7cc9 !important;
        color: #fff !important;
        border: 0;
    }

    .blue_btn2 {
        background: #00c4ff !important;
        color: #fff !important;
        border: 0;
    }

    .blue_btn:hover {
        background: #899ed1 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }

    .green_btn {
        background: #54c698 !important;
        color: #fff !important;
        border: 0;
    }

    .green_btn:hover {
        background: #8ee9c4 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }

    .dgreen_btn {
        background: #55c6ba !important;
        color: #fff !important;
        border: 0;
    }

    .dgreen_btn:hover {
        background: #95e9e0 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }

    .red_btn {
        background: #e55644 !important;
        color: #fff !important;
        border: 0;
    }

    .red_btn:hover {
        background: #f08476 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }

    .item {
        width: 98px;
        height: 45px;
    }

    .actions {
        display: flex;
        justify-content: center;
        align-items: center;

    }

    .actions .action_btn {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 10px 5px;
        cursor: pointer;
        background: transparent;
        border: none;
    }

    .form-part {
        margin: 15px 0;
        font-size: 16px;
        font-weight: bold;
    }

    .submit-btns {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .submit-btns button:not(:last-child) {
        margin-right: 10px;
    }

    .el-select__tags {
        overflow-x: auto !important;
        flex-wrap: inherit !important;
    }
    .new{
        width: 30px;
        height: 20px;
        position: absolute;
        top: 0;
        left: 0;
    }
</style>
