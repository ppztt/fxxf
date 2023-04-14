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
                    ref="formRef"
                    label-width="160px"
                    :model="formData"
                    label-position="right"
            >
                <div class="check-item">详情信息- {{ textList[detailType] }}</div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3' || detailType == '2'" >
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="经营者注册名称：" prop="regName">
                                <p></p>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="门店名称：" prop="storeName">
                                <p></p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="20">
                            <el-form-item label="经营场所地区：" prop="addrs">
                                <p></p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="统一社会信用代码：" prop="creditCode">
                                <p></p>
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
                                <p>{{ formData.platform }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="网店名称：" prop="onlineName">
                                <p></p>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="连续承诺次数：" prop="commNum">
                                <p></p>
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
                            <el-form-item prop="principalTel">
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
                                <p></p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-else>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="经营者注册名称：" prop="regName">
                                <el-input v-model="formData.regName" placeholder="请输入经营者注册名称："></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="门店名称：" prop="storeName">
                                <el-input v-model="formData.storeName" placeholder="请输入门店名称："></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="20">
                            <el-form-item label="经营场所地区：" prop="formData.addrs">
                                <el-row class="addr-row" :key="index" v-for="(addr, index) in formData.addrs">
                                    <el-col span="5">
                                        <el-select
                                                :ref="`city${index}`"
                                                v-model="addr.city"
                                                placeholder="市"
                                                :clearable="true"
                                                filterable
                                                @on-change="
            () => {
              let data = cityChange(addr.city);
              districtDataArr[index] = data;
              $forceUpdate();
            }
          "
                                                @on-open-change="
            () => {
              let data = cityChange(addr.city);
              districtDataArr[index] = data;
              $forceUpdate();
            }
          "
                                                @on-clear="
            () => {
              $refs[`district${index}`].clearSingleSelect();
              $refs.town.clearSingleSelect();
            }
          "
                                        >
                                            <el-option
                                                    v-for="item in regionData"
                                                    :value="item.name"
                                                    :key="item.name"
                                            >{{ item.name }}
                                            </el-option
                                            >
                                        </el-select>
                                    </el-col>
                                    <el-col span="5">
                                        <el-select
                                                :ref="`district${index}`"
                                                v-model="addr.district"
                                                :disabled="
            !addr.city ||
            !(
              $store.state.login_module.userInfo.roleId === 1 ||
              $store.state.login_module.userInfo.roleId === 2 ||
              $store.state.login_module.userInfo.roleId === 4
            )
          "
                                                placeholder="市/县/区/镇"
                                                :clearable="true"
                                                filterable
                                                @on-change="districtChange(addr.district)"
                                                @on-open-change="districtChange(addr.district)"
                                                @on-clear="
            () => {
              $refs.town.clearSingleSelect();
            }
          "
                                        >
                                            <el-option
                                                    v-for="item in districtDataArr[index]"
                                                    :value="item.name"
                                                    :key="item.name"
                                            >{{ item.name }}
                                            </el-option
                                            >
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
                                                style="margin-left: 20px"
                                                circle></el-button>
                                        <el-button
                                                v-else
                                                style="margin-left: 20px"
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
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="9">
                            <el-form-item label="统一社会信用代码：" prop="creditCode">
                                <el-input v-model="formData.creditCode"
                                          placeholder="请输入统一社会信用代码："></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="12">
                            <el-form-item label="有效期：" prop="validity">
                                <el-date-picker
                                        v-model="formData.startTime"
                                        type="date"
                                        placeholder="开始有效期">
                                </el-date-picker>
                                ~
                                <el-date-picker
                                        v-model="formData.endTime"
                                        type="date"
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
                                    <el-select
                                            ref="management"
                                            placeholder="类别"
                                            :clearable="true"
                                            v-model="formData.management">
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
                                    <el-select
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
                                        >{{ item }}
                                        </el-option
                                        >
                                    </el-select>
                                </el-form-item>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="所属平台：" prop="platform">
                                <el-input
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
                                <el-input v-model="formData.onlineName" placeholder="请输入网店名称："></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="连续承诺次数：" prop="commNum">
                                <el-input v-model="formData.commNum" placeholder="请输入连续承诺次数："></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col span="6">
                            <el-form-item label="负责人：" prop="principal">
                                <el-input v-model="formData.principal" placeholder="姓名"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="6">
                            <el-form-item prop="principalTel">
                                <el-input v-model="formData.principalTel" placeholder="手机号码"></el-input>
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
                                <el-date-picker
                                        v-model="formData.applicationDate"
                                        type="date"
                                        placeholder="请选择时间">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item">承诺事项及具体内容</div>
                <div class="check-form-item"  v-if="detailType == '1' || detailType == '3'" >
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
                                <el-input
                                        type="textarea"
                                        :rows="3"
                                        placeholder="请输入品质保证"
                                        v-model="formData.contents1">
                                </el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="诚信保证：" prop="contents2">
                                <el-input
                                        type="textarea"
                                        :rows="3"
                                        placeholder="请输入诚信保证"
                                        v-model="formData.contents2">
                                </el-input>
                            </el-form-item>
                        </el-col>
                        <el-col span="8">
                            <el-form-item label="维权保证：" prop="contents3">
                                <el-input
                                        type="textarea"
                                        :rows="3"
                                        placeholder="请输入维权保证"
                                        v-model="formData.contents3">
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-else>
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
                <div class="check-item" v-if="detailType == '1' || detailType == '3' || detailType == '0'">其他承诺事项及具体内容</div>
                <div class="check-form-item" v-if="detailType == '1' || detailType == '3'">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="" prop="contents4">
                                <p>{{ formData.contents4 }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-if="detailType == '0'">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="" prop="contents4">
                                <el-input
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
                                <p>{{ formData.applicationDate }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-form-item" v-else>
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="日期：" prop="applicationDate">
                                <el-date-picker
                                        v-model="formData.applicationDate"
                                        type="date"
                                        placeholder="选择日期"
                                        disabled>
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item"v-if="detailType == '1' || detailType == '3'" >消委会意见</div>
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
                                <el-input
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
                <div class="check-item"v-if="detailType == '2'" >摘牌信息</div>
                <div class="check-form-item" v-if="detailType == '2'">
                    <el-row>
                        <el-col span="11">
                            <el-form-item label="具体摘牌信息：" prop="delReason">
                                <el-input
                                        placeholder="请输入具体摘牌信息"
                                        v-model="formData.delReason"
                                        type="textarea"
                                />
                            </el-form-item>
                        </el-col>
                        <el-col span="11">
                            <el-form-item label="其他必要信息：" prop="delOther">
                                <el-input
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
                <el-button size="small" v-if="detailType=='0'" class="blue_btn ">保存</el-button>
                <el-button size="small" v-if="detailType=='3'" class="blue_btn ">审核通过</el-button>
                <el-button size="small" v-if="detailType=='3'" class="blue_btn ">审核不通过</el-button>
                <el-button size="small" v-if="detailType=='2'" class="blue_btn ">提交</el-button>
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
        data: {
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
            // 类型代号：查看，编辑.....
            detailType: '',
            // 对应的信息id
            consumerId: -1,
            // 表单数据
            regionData: [
                //地区数据
                {
                    value: "beijing",
                    label: "北京",
                    children: [
                        {
                            value: "gugong",
                            label: "故宫"
                        },
                        {
                            value: "tiantan",
                            label: "天坛"
                        },
                        {
                            value: "wangfujing",
                            label: "王府井"
                        }
                    ]
                },
                {
                    value: "jiangsu",
                    label: "江苏",
                    children: [
                        {
                            value: "nanjing",
                            label: "南京",
                            children: [
                                {
                                    value: "fuzimiao",
                                    label: "夫子庙"
                                }
                            ]
                        },
                        {
                            value: "suzhou",
                            label: "苏州",
                            children: [
                                {
                                    value: "zhuozhengyuan",
                                    label: "拙政园"
                                },
                                {
                                    value: "shizilin",
                                    label: "狮子林"
                                }
                            ]
                        }
                    ]
                }
            ], //地区数据 一级市数据
            districtDataArr: [],
            formData: {
                regName: "",
                storeName: "",
                platform: "",
                onlineName: "",
                city: "",
                district: "",
                address: "",
                addrs: [{},],
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
            activeManageType: [
                // 当前经营类别数据
            ],
            // 经营类别
            manageType: {
                commodities: [],
                services: [],
            },
        },
        methods: {
            // 返回上一级页面
            returnBack() {
                window.parent.returnBack()
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
                    let data = this.regionData.find((value) => value.value == cityName,).children || [];
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
        },
        mounted: function () {
            this.detailType = window.location.href.split("?")[1].split("&")[0].split('=')[1]
            this.consumerId = Number(window.location.href.split("?")[1].split("&")[1].split('=')[1])
            console.log(window.parent)
        }
    });
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
</style>
