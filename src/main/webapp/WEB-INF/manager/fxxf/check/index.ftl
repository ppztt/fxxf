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
                <div class="check-item">详情信息- {{ textList[misTypeNum] }}</div>
                <div class="check-form-item">
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
                <div class="check-item">承诺事项及具体内容</div>
                <div class="check-form-item">
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

                <div class="check-item">其他承诺事项及具体内容</div>
                <div class="check-form-item">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="" prop="contents4">
                                无
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item">创建日期</div>
                <div class="check-form-item">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="日期：" prop="applicationDate">
                                <p>{{ formData.applicationDate }}</p>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
                <div class="check-item">消委会意见</div>
                <div class="check-form-item">
                    <el-row>
                        <el-col span="24">
                            <el-form-item label="审核状态：">
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
                        </el-col>
                    </el-row>
                    <el-row>
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
            </el-form>
            <div class="btn">
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
            misTypeNum: '',
            consumerId: -1,
            // 表单数据
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
        },
        methods: {
            // 返回上一级页面
            returnBack() {
                window.parent.document.getElementById("check").style.display = "none"
            }
        },
        mounted: function () {

            this.misTypeNum = window.location.href.split("?")[1].split("&")[0].split('=')[1]
            this.consumerId = Number(window.location.href.split("?")[1].split("&")[1].split('=')[1])
            console.log(this.misTypeNum,this.consumerId)
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

    .check-form-item {
        padding: 15px 15px 0 15px;
        background-color: #fff;
        border-radius: 3px;
        margin-top: 5px;
    }
    .btn{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        margin-top: 20px;
        height: 60px;
        line-height: 60px;
    }
</style>
