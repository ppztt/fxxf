<!-- 主页 -->
<!DOCTYPE html>
<html>
<head>
    <title>开发</title>
    <script src="https://cdn.mingsoft.net/platform/ms-store.umd.min.js"></script>
    <#include "../../include/head-file.ftl">
    <style>

    </style>
</head>
<body>
<#include 'reset-password.ftl'/>
<div id="app" class="edit-check-delist">
    <div class="form-list">
        <el-form
                ref="formValidate"
                :model="formValidate"
                :rules="ruleValidate"
                :label-width="160"
        >
            <#--            反馈内容-->
            <div class="frame">
                <div class="title">反馈内容</div>
                <div class="content">
                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">反馈类型：&nbsp&nbsp</div>
                                <div>放心消费承诺单位</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">经营者注册名称：&nbsp&nbsp</div>
                                <div>惠州市惠阳区淡水衣生缘百货商行</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">问题类型：&nbsp&nbsp</div>
                                <div>不履行承诺内容</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">归属地市：&nbsp&nbsp</div>
                                <div>惠州市</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">反馈时间：&nbsp&nbsp</div>
                                <div>2022-01-08 10:34:17</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">姓名：&nbsp&nbsp</div>
                                <div>李爽</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">联系电话：&nbsp&nbsp</div>
                                <div>15004007325</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="12">
                            <#--                            <el-form-item label="问题内容：" class="form-item">-->
                            <#--                                    <span>销售商品商家拒绝退款 以特价商品为理由 实际商店里所有商品都打折 有吊牌 小票也拒绝退款</span>-->
                            <#--                            </el-form-item>-->
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">问题内容：&nbsp&nbsp</div>
                                <div style="width: 50%">销售商品商家拒绝退款 以特价商品为理由 实际商店里所有商品都打折
                                    有吊牌 小票也拒绝退款
                                </div>
                            </div>

                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">附件下载：&nbsp&nbsp</div>
                                <div>无</div>
                            </div>
                        </el-col>
                    </el-row>
                </div>
            </div>
            <#--            处理-->
            <div class="frame">
                <div class="title">处理</div>
                <div class="content">
                    <el-row>
                        <el-col :span="14" style="margin-bottom: 5px">
                            <el-form-item label="处理结果" prop="result" class="form-survey">
                                <el-select v-model="formValidate.result" placeholder="请选择" style="width:80%">
                                    <el-option value="督促告诫">督促告诫</el-option>
                                    <el-option value="摘牌">摘牌</el-option>
                                    <el-option value="投诉问题不存在">投诉问题不存在</el-option>
                                    <el-option value="其他">其他</el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row type="flex" align="bottom">
                        <el-col :span="14" style="margin-bottom: 5px">
                            <el-form-item label="调查处理情况：" prop="processingSituation" class="form-survey">
                                <el-input
                                        v-model="formValidate.processingSituation"
                                        type="textarea"
                                        :rows="5"
                                        placeholder="请填写调查处理情况"
                                        maxlength="500"
                                        :show-word-limit="true"
                                />
                            </el-form-item>
                        </el-col>
                        <el-col :offset="1" :span="7" >
                            <el-formItem>
                                <el-button class="blue_btn">提交
                                </el-button>
                            </el-formItem>
                        </el-col>

                    </el-row>
                </div>
            </div>

        </el-form>
    </div>
</div>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            formValidate: {
                result: "", //处理结果
                processingSituation: "", //调查处理情况
            },
            //校验规则
            ruleValidate: {
                result: [
                    {
                        required: true,
                        message: "处理结果不能为空",
                        trigger: "change",
                    },
                ],
                processingSituation: [
                    {
                        required: true,
                        message: "调查处理情况不能为空",
                        trigger: "blur",
                    },
                ],
            },
        },
        computed: {},
        watch: {},
        methods: {},
        created: function () {

        },
        mounted: function () {

        },
    })
</script>
<style>
    <#--1-->
    .edit-check-delist {
        padding: 10px;
    }

    .frame {
        margin-bottom: 15px;
    }

    .title {
        text-align: left;
        margin-bottom: 10px;
        font-size: 16px;
        font-weight: bold;
    }

    .content {
        padding: 15px 15px 15px 15px;
        background-color: #fff;
        border-radius: 3px;
    }

    .form-item {
        margin: 15px 0;
        /*margin-left: 80px!important;*/
    }

    .form-name {
        width: 150px;
        text-align: right;
    }


    .el-select-dropdown {
        max-width: 80%;
    }

    .form-survey {
        margin-left: 80px !important;
    }
    .blue_btn{
        margin-bottom: 26px;
    }
</style>
