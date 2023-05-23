<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>文章</title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/mdiy/index.js"></script>
</head>
<body style="overflow: hidden">
<div id="index" class="ms-index" v-cloak>
    <!--左侧-->
    <el-container>
        <el-main class="ms-container">

            <el-form
                    ref="checkForm"
                    label-width="160px"
                    :model="formData"
                    label-position="right"
            >
                <div class="check-item">详情信息- {{ textList[detailType] }}</div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3' || detailType == '2'">
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="经营者注册名称：" prop="regName">
                                <p>{{ formData.regName }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="门店名称：" prop="storeName">
                                <p v-if="formData.storeName">{{ formData.storeName }}</p>
                                <p v-else>无</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="20">
                            <el-form-item label="经营场所地区：" prop="addrs">
                                <div v-for="(arr,index) in formData.addrs" :key="index">
                                    <p style="display: inline-block">{{ arr.city }}</p>-
                                    <p style="display: inline-block">{{ arr.district }}</p>-
                                    <p style="display: inline-block">{{ arr.address }}</p>
                                </div>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="统一社会信用代码：" prop="creditCode">
                                <p>{{ formData.creditCode }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="12">
                            <el-form-item label="有效期：" prop="validity">
                                <p>{{ formData.startTime }} {{ formData.endTime }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="经营类别：">
                                <span>{{ formData.management }}</span>
                                <span v-show="formData.details && formData.details.length > 0">:
                                    <span v-for="item in formData.details" :key="item">
                                        {{item}}
                                    </span>
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="所属平台：" prop="platform">
                                <p v-if="formData.platform">{{ formData.platform }}</p>
                                <p v-else>无</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="网店名称：" prop="onlineName">
                                <p v-if="formData.onlineName">{{ formData.onlineName }}</p>
                                <p v-else>无</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="连续承诺次数：" prop="commNum">
                                <p v-if="formData.commNum">{{ formData.commNum }}</p>
                                <p v-else>0</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="6">
                            <el-form-item label="负责人：" prop="principal">
                                <p>{{ formData.principal }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="3">
                            <el-form-item label="负责人联系电话：" prop="principalTel" style="margin-left: -160px">
                                <p>{{ formData.principalTel }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="是否为连续承诺" prop="contCommitment">
                                <el-radio disabled v-model="formData.contCommitment" label="是">是</el-radio>
                                <el-radio disabled v-model="formData.contCommitment" label="否">否</el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="企业申请日期：" prop="applicationDate">
                                <p v-if="formData.applicationDate">{{ formData.applicationDate }}</p>
                                <p v-else>无</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-else>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="经营者注册名称：" prop="regName">
                                <el-input size="mini" v-model="formData.regName"
                                          placeholder="请输入经营者注册名称："></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="门店名称：" prop="storeName">
                                <el-input size="mini" v-model="formData.storeName"
                                          placeholder="请输入门店名称："></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="20">
                            <el-form-item label="经营场所地区：" prop="formData.addrs">
                                <el-row :key="index" v-for="(addr, index) in formData.addrs">
                                    <el-col span="5">
                                        <el-select size="mini"
                                                   v-model="addr.city"
                                                   placeholder="市"
                                                   :clearable="true"
                                                   filterable
                                                   @change="cityChange(addr.city)"
                                                   :disabled="userInfo.roleId == 2 || userInfo.roleId == 3 "
                                                   @clear="clear">
                                            <el-option
                                                    v-for="item in regionData"
                                                    :value="item.name"
                                                    :key="item.code"
                                                    :label="item.name">
                                            </el-option>
                                        </el-select>
                                    </el-col>
                                    <el-col span="5">
                                        <el-select size="mini"
                                                   v-model="addr.district"
                                                   placeholder="市/县/区/镇"
                                                   :clearable="true"
                                                   filterable
                                                   :disabled="!addr.city ||districtData.length == 0 || userInfo.roleId == 3 ">
                                            <el-option
                                                    v-for="item in districtData"
                                                    :value="item.name"
                                                    :label="item.name"
                                                    :key="item.code">
                                            </el-option>
                                        </el-select>
                                    </el-col>
                                    <el-col span="8">
                                        <el-input size="mini" v-model="addr.address"
                                                  placeholder="请输入经营场所地址"></el-input>
                                    </el-col>
                                    <el-col span="1">
                                        <el-button
                                                size="mini"
                                                v-if="index === 0"
                                                icon="el-icon-plus"
                                                @click="addAddress"
                                                style="margin-left: 20px"
                                                circle></el-button>
                                        <el-button
                                                size="mini"
                                                v-else
                                                style="margin-left: 20px"
                                                icon="el-icon-minus"
                                                circle
                                                @click="deleteAddr(index)"
                                        ></el-button>
                                    </el-col>
                                </el-row>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="统一社会信用代码：" prop="creditCode">
                                <el-input size="mini" v-model="formData.creditCode"
                                          placeholder="请输入统一社会信用代码："></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="12">
                            <el-form-item label="有效期：" prop="validity">
                                <el-date-picker size="mini"
                                                v-model="formData.startTime"
                                                type="date"
                                                value-format="yyyy-MM-dd"
                                                :picker-options="pickerBeginDate"
                                                placeholder="开始有效期">
                                </el-date-picker>
                                ~
                                <el-date-picker size="mini"
                                                v-model="formData.endTime"
                                                type="date"
                                                :picker-options="pickerEndDate"
                                                value-format="yyyy-MM-dd"
                                                placeholder="结束有效期">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="经营类别：">
                                <el-col :span="8">
                                    <el-form-item>
                                        <el-select size="mini"
                                                   ref="management"
                                                   placeholder="类别"
                                                   :clearable="true"
                                                   v-model="formData.management"
                                                   @change="managementChange">
                                            <el-option :value="'商品类'" :key="'商品类'">商品类
                                            </el-option>
                                            <el-option :value="'服务类'" :key="'服务类'">服务类
                                            </el-option>
                                            <el-option :value="'商品及服务类'" :key="'商品及服务类'">
                                                商品及服务类
                                            </el-option>
                                        </el-select>
                                        <!-- <p v-if="detailType == 'check' || detailType == 'delist'">
                                          {{ formData.management }}
                                        </p>-->
                                    </el-form-item>
                                </el-col>
                                <el-col :offset="1" :span="15">
                                    <el-form-item>
                                        <el-select size="mini"
                                                   ref="details"
                                                   class="multiSelect"
                                                   :clearable="true"
                                                   placeholder="详细类别"
                                                   v-model="formData.details"
                                                   multiple>
                                            <el-option
                                                    v-for="item in activeManageType"
                                                    :value="item"
                                                    :key="item"
                                                    :label="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="所属平台：" prop="platform">
                                <el-input size="mini"
                                          placeholder="请填写所属平台"
                                          v-model="formData.platform"
                                          type="text"
                                />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="网店名称：" prop="onlineName">
                                <el-input size="mini" v-model="formData.onlineName"
                                          placeholder="请输入网店名称："></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="连续承诺次数：" prop="commNum">
                                <el-input size="mini" v-model="formData.commNum"
                                          placeholder="请输入连续承诺次数："></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="6">
                            <el-form-item label="负责人：" prop="principal">
                                <el-input size="mini" v-model="formData.principal" placeholder="姓名"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="6">
                            <el-form-item label="负责人联系电话：" prop="principalTel">
                                <el-input size="mini" v-model="formData.principalTel" placeholder="手机号码"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="是否为连续承诺" prop="contCommitment">
                                <el-radio v-model="formData.contCommitment" label="是">是</el-radio>
                                <el-radio v-model="formData.contCommitment" label="否">否</el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="企业申请日期：" prop="applicationDate">
                                <el-date-picker size="mini"
                                                value-format="yyyy-MM-dd HH:mm:ss"
                                                format="yyyy-MM-dd"
                                                v-model="formData.applicationDate"
                                                type="date"
                                                :picker-options="applicant"
                                                placeholder="请选择时间">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item">承诺事项及具体内容</div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3'">
                    <el-row>
                        <el-col span="8">
                            <el-form-item label="品质保证：" prop="contents1">
                                <p>
                                    {{ formData.contents1 }}
                                </p>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="诚信保证：" prop="contents2">
                                <p>
                                    {{ formData.contents2 }}
                                </p>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="维权保证：" prop="contents3">
                                <p>
                                    {{ formData.contents3 }}
                                </p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-if="detailType == '0'">
                    <el-row>
                        <el-col span="8">
                            <el-form-item label="品质保证：" prop="contents1">
                                <el-input size="mini"
                                          type="textarea"
                                          :rows="3"
                                          placeholder="请输入品质保证"
                                          v-model="formData.contents1">
                                </el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="诚信保证：" prop="contents2">
                                <el-input size="mini"
                                          type="textarea"
                                          :rows="3"
                                          placeholder="请输入诚信保证"
                                          v-model="formData.contents2">
                                </el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="维权保证：" prop="contents3">
                                <el-input size="mini"
                                          type="textarea"
                                          :rows="3"
                                          placeholder="请输入维权保证"
                                          v-model="formData.contents3">
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-if="type == 2">
                    <el-row>
                        <el-col span="8">
                            <el-form-item label="适用商品：" prop="contents1">
                                <p>{{ formData.contents1 }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="退货约定：" prop="contents2">
                                <p>{{ formData.contents2 }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="退货期限：" prop="contents3">
                                <p>{{ formData.contents3 }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item" v-if="detailType == '1' || detailType == '3' || detailType == '0'">
                    其他承诺事项及具体内容
                </div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3'">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="" prop="contents4">
                                <p v-if="formData.contents4">{{ formData.contents4 }}</p>
                                <p v-else>无</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-if="detailType == '0'">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="" prop="contents4">
                                <el-input size="mini"
                                          type="textarea"
                                          :rows="2"
                                          placeholder="请输入其他承诺事项及具体内容"
                                          v-model="formData.contents4">
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item">创建日期</div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3' || detailType == '2'">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="日期：" prop="applicationDate">
                                <p>{{ formData.createTime }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-else>
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="日期：" prop="applicationDate">
                                <el-date-picker size="mini"
                                                value-format="yyyy-MM-dd HH:mm:ss"
                                                format="yyyy-MM-dd"
                                                v-model="formData.createTime"
                                                type="date"
                                                placeholder="选择日期"
                                                disabled>
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item" v-if="detailType == '1' || detailType == '3'">消委会意见</div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3'">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="审核状态：" v-if="detailType == '1' || detailType == '2'">
                                <span v-if="formData.status == '0'">摘牌</span>
                                <span v-if="formData.status == '1'">在期</span>
                                <span v-if="formData.status == '2'">过期</span>
                                <!-- <span v-if="formData.status == '3'"></span> -->
                                <span v-if="formData.status == '4'">待审核</span>
                                <span v-if="formData.status == '5'">县级审核通过</span>
                                <span v-if="formData.status == '6'">行业协会审核通过</span>
                                <span v-if="formData.status == '7'">审核不通过</span>
                                <span v-if="formData.status == '8'">行业协会审核不通过</span>
                            </el-form-item>
                            <el-form-item label="意见内容：" prop="ccContent"
                                          v-if="detailType == '0' || detailType == '3'">
                                <el-input size="mini"
                                          placeholder="请填写意见内容"
                                          v-model="formData.ccContent"
                                          v-if="detailType == '0' || detailType == '3'"
                                          type="textarea"
                                />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row v-if="detailType == '1'">
                        <el-col span="22">
                            <el-form-item label="审核意见：">
                                <el-table
                                        border
                                        :data="formData.auditLogs"
                                        align="center"
                                        style="width: 100%">
                                    <el-table-column
                                            prop="name"
                                            label="审核单位"
                                    >
                                    </el-table-column>
                                    <el-table-column
                                            prop="contents"
                                            label="审核意见"
                                    >
                                    </el-table-column>
                                    <el-table-column
                                            prop="status"
                                            label="审核状态">
                                    </el-table-column>
                                    <el-table-column
                                            prop="createTime"
                                            label="审核时间">
                                    </el-table-column>
                                </el-table>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item" v-if="detailType == '2'">摘牌信息</div>
                <div class="check-form-item" v-if="detailType == '2'">
                    <el-row>
                        <el-col span="11">
                            <el-form-item label="具体摘牌信息：" prop="delReason">
                                <el-input size="mini"
                                          placeholder="请输入具体摘牌信息"
                                          v-model="formData.delReason"
                                          type="textarea"
                                />
                            </el-form-item>
                        </el-col>
                        <el-col span="11">
                            <el-form-item label="其他必要信息：" prop="delOther">
                                <el-input size="mini"
                                          placeholder="请输入其他必要信息"
                                          v-model="formData.delOther"
                                          type="textarea"
                                />
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
            </el-form>
            <div class="btn">
                <el-button size="mini" v-if="detailType=='0'" type="primary" @click="perEditUnitTnfo">保存
                </el-button>
                <el-button size="mini" v-if="detailType=='3'" type="primary" @click="auditUnitTnfo('1')">审核通过
                </el-button>
                <el-button size="mini" v-if="detailType=='3'" type="primary" @click="auditUnitTnfo('2')">
                    审核不通过
                </el-button>
                <el-button size="mini" v-if="detailType=='2'" type="primary" @click="delistUnitTnfo">提交</el-button>
                <el-button type="primary" icon="iconfont icon-fanhui" size="mini" @click="returnBack">返回</el-button>
            </div>
        </el-main>

    </el-container>
</div>
</body>
</html>
<script>
    // window.parent.exec_success_callback();
    var indexVue = new Vue({
        el: "#index",
        data() {
            return {
                // 放心消费 1  无理由 2
                type: '1',
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
                //结束日期限制
                pickerEndDate: {
                    disabledDate: (time) => {
                        if (this.startTime) {
                            return (
                                time.getTime() <= new Date(this.startTime).getTime()
                            );
                        }
                    }
                },
                applicant: {
                    disabledDate: (time) => {
                        return (
                            time.getTime() >= new Date().getTime()
                        );

                    }
                },
                // 详情后的名字，根据query参数来切换
                textList: [
                    "编辑",
                    "查看",
                    "摘牌",
                    "审核",
                ],
                // 用户信息
                userInfo: {
                    // 页面跳转
                    action: "",
                    account: "",
                    address: null,
                    city: "",
                    createTime: "",
                    creditCode: null,
                    district: "",
                    email: "",
                    id: 0,
                    management: null,
                    newPassword: null,
                    password: "",
                    phone: "",
                    principal: null,
                    principalTel: null,
                    province: null,
                    realname: "",
                    roleId: 0,
                    roleName: null,
                    storeName: null,
                    town: null,
                    updateTime: "",
                    usertype: 1,
                    zipcode: ""
                },
                // 类型代号：查看，编辑.....
                detailType: '',
                // 对应的信息id
                consumerId: -1,
                // 表单数据
                regionData: [], //地区数据 一级市数据
                districtDataArr: [],
                formData: {
                    regName: "",
                    storeName: "",
                    platform: "",
                    onlineName: "",
                    city: "",
                    district: "",
                    address: "",
                    addrs: [],
                    creditCode: "",
                    management: "",
                    details: "",
                    principal: "",
                    principalTel: "",
                    contents1: "",
                    contents2: "",
                    contents3: "",
                    applicationDate: "",
                    delReason: "",
                    delOther: "",
                },
                // 当前经营类别数据
                activeManageType: [],
                // 经营类别
                manageType: {
                    commodities: [],
                    services: [],
                },
                districtData: [], //某市县数据
            }
        },
        watch: {
            'formData.commNum': function (n, o) {
                // this.$set(this.formData, 'commNum', 0);
                if (isNaN(Number(n))) {
                    this.$set(this.formData, 'commNum', 0);
                    this.$message.error('请输入正整数')
                }
                this.$set(this.formData, 'commNum', Number(n));
            }
        },
        methods: {
            // 返回上一级页面
            returnBack() {
                window.parent.returnBack()
            },
            // 获取该商家数据
            getList() {
                ms.http.get("/xwh/applicants/" + this.consumerId + '.do').then((res) => {
                    this.formData = res.data
                    let cityArr = this.formData.city.split(",");
                    let districtArr = this.formData.district.split(",");
                    let addressArr = this.formData.address.split(",");
                    this.formData.addrs = [];
                    cityArr.forEach((item, index) => {
                        this.formData.addrs.push({
                            city: cityArr[index],
                            district: districtArr[index],
                            address: addressArr[index],
                        });
                    });
                    this.getManagerType();
                })
            },
            // 获取地区信息
            getRegionData() {
                ms.http.get('/xwh/gd-regin.do').then((res) => {
                    if (res.code == 200) {
                        this.regionData = res.data
                        this.getUserInfo()
                    }
                })
            },
            clear() {
                this.district = "";
                this.formData.district = ""
            },
            cityChange: function (name) {
                // 一级市发生改变
                if (name) {
                    let cityData_active = this.regionData.find((value) => value.name == name);
                    this.districtData = cityData_active.children;
                    this.district = "";
                    this.formData.district = ""
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
            deleteAddr(index) {
                this.formData.addrs.splice(index, 1);
                this.districtDataArr.splice(index, 1);
                this.$forceUpdate();
            },
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
                    let data = this.regionData.find((value) => value.name == cityName).children || [];
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
            // 编辑保存按钮
            perEditUnitTnfo() {
                let adds = JSON.stringify(this.formData.addrs)
                ms.http.get('/xwh/applicants/find.do',
                    {
                        creditCode: this.formData.creditCode,
                        id: this.formData.id,
                        type: this.type
                    }).then((res) => {
                    let isReapt = res.data.isRepeatRegName
                    if (!isReapt) {
                        ms.http.post('/xwh/applicants/update.do', {...this.formData, addrs: adds},
                            {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                            if (res.code == 200) {
                                this.returnBack()
                                this.currentTopic("保存成功")
                            }
                        })
                    } else {
                        this.$message.error('经营者注册名称重复,请确认单位名称后提交')
                    }
                })
                // let params = JSON.stringify(this.formData)
            },
            // 获取类别信息
            getManagerType() {
                ms.http.get('/xwh/type/listGoodsAndServiceType.do').then((res) => {
                    this.manageType = res.data
                    this.managementChange(this.formData.management)
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
            // 审核按钮
            auditUnitTnfo(status) {
                let query = {
                    id: this.consumerId,
                    notes: this.formData.ccContent,
                    type: status
                }
                ms.http.get('/xwh/applicants/audit.do', query).then(async (res) => {
                    if (res.code == 200) {
                        this.returnBack()
                        this.currentTopic("审核成功")
                    }
                    if (res.code == 500) {
                        this.$message.error(res.msg)
                    }
                })
            },
            // 返回到上一个界面的提示
            currentTopic(msg) {
                window.parent.currentTopic(msg)
            },
            // 根据id更新状态
            delistUnitTnfo(status) {
                let params = JSON.stringify({
                    applicantsId: this.consumerId,
                    delOther: this.formData.delOther,
                    delReason: this.formData.delOther,
                    status: 0
                })
                ms.http.post('/xwh/applicants/updateApplicantsStatus.do', params,
                    {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                    if (res.code == 200) {
                        this.returnBack()
                        this.currentTopic('申请摘牌成功')
                    } else {
                        this.$message.error('')
                    }
                })
            },
            // 获取用户信息
            getUserInfo() {
                let id = sessionStorage.getItem('userId')
                ms.http.get('/xwh/user/userInfo.do', {id}).then((res) => {
                    if (res.code == 200) {
                        this.userInfo = {...res.data, id}
                        this.cityChange(this.userInfo.city)
                        this.districtChange(this.userInfo.district)
                    }
                })
            }
        },
        mounted
    :

    function () {
        this.detailType = window.location.href.split("?")[1].split("&")[0].split('=')[1]
        this.consumerId = Number(window.location.href.split("?")[1].split("&")[1].split('=')[1])
        this.getRegionData()
        this.getList();
    }
    })
    ;
</script>
<style>
    #index .ms-container {
        height: calc(100vh - 78px);
        background: none !important;
    }

    .check-item {
        font-size: 16px;
        font-weight: 700;
    }

    .check-item:nth-child(n+2) {
        margin: 15px 0 15px 0;
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

    .check-form-item {
        padding: 15px 15px 0 15px;
        background-color: #fff;
        border-radius: 3px;
        margin-top: 5px;
    }

    .btn {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        margin-top: 20px;
        height: 60px;
        line-height: 60px;
    }

    .btn .el-button {
        margin-left: 10px;
    }

    p {
        margin: 0;
        padding: 0;
    }

    .el-select__tags {
        overflow-y: auto !important;
        max-height: 28px;
    }
</style>
