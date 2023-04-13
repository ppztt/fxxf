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
        <el-header>
            <el-button type="primary" icon="iconfont icon-fanhui" size="mini" @click="returnBack">返回</el-button>
        </el-header>
        <el-main class="ms-container">

            <el-form
                    ref="formRef"
                    :label-width="160"
                    :model="formData"
                    label-position="right"
            >
                <div class="check-item">详情信息- {{ textList.edit }}</div>
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
                            <el-form-item  prop="principalTel">
                                <p>{{ formData.principalTel }}</p>
                            </el-form-item>
                        </el-col>
                        <el-col span="9">
                            <el-form-item label="是否为连续承诺"  prop="contCommitment">
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

                </div>

                <div class="check-item">其他承诺事项及具体内容</div>

                <div class="check-item">创建日期</div>
                <div class="check-item">消委会意见</div>
            </el-form>
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
            textList: {
                edit: "编辑",
                check: "查看",
                delist: "摘牌",
                audit: "审核",
            },
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

        }
    });
</script>
<style>
    .el-header {
        border-bottom: 1px solid #ccc;
        background: #fff;
        line-height: 60px;
    }

    #index .ms-container {
        height: calc(100vh - 78px);
        background: none !important;
    }

    .check-item {
        font-size: 16px;
        font-weight: 700;
    }
    .check-form-item{
        padding: 15px 15px 0 15px;
        background-color: #fff;
        border-radius: 3px;
        margin-top: 5px;
    }
</style>
