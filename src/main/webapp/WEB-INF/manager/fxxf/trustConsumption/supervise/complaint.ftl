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
<el-main id="app" class="edit-check-delist">
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
                                <div>{{data.type}}</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">经营者注册名称：&nbsp&nbsp</div>
                                <div>{{data.regName}}</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">问题类型：&nbsp&nbsp</div>
                                <div>{{data.reason}}</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">归属地市：&nbsp&nbsp</div>
                                <div>{{data.city}}</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">反馈时间：&nbsp&nbsp</div>
                                <div>{{data.procTime || data.createTime}}</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">姓名：&nbsp&nbsp</div>
                                <div>{{data.name}}</div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="7">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">联系电话：&nbsp&nbsp</div>
                                <div>{{data.phone}}</div>
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
                                <div style="width: 50%">{{data.content}}
                                </div>
                            </div>

                        </el-col>
                    </el-row>

                    <el-row>
                        <el-col :span="24">
                            <div style="display: flex" class="form-item">
                                <div style="white-space: nowrap" class="form-name">附件下载：&nbsp&nbsp</div>
                                <div class="files-lis" v-if="data.filesInfo && data.filesInfo.length > 0">
                                    <div class="file-item" v-for="(item, index) in data.filesInfo" :key="index">
                                        <p class="files-p">
                                            <a class="files-a"><i class="el-icon-link">{{item.fileName}}</i></a>
                                            <span class="files-span" v-if="allowImage.indexOf(item.fileName.substring(item.fileName.lastIndexOf('.'))) !== -1" @click="showImage(item)">预览</span>
                                            <span class="files-span" @click="downloadData(item)">下载</span>
                                        </p>
                                    </div>
                                </div>
                                <div v-else>无</div>
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
                            <el-form-item label-width="130px" label="处理结果：" prop="result" class="form-survey">
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
                            <el-form-item label-width="130px" label="调查处理情况：" prop="processingSituation" class="form-survey">
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
                        <el-col :offset="1" :span="7">
                            <el-formItem>
                                <el-button class="blue_btn" @click="handleSubmit('formValidate')">提交
                                </el-button>
                            </el-formItem>
                        </el-col>

                    </el-row>
                </div>
            </div>

            <div class="frame" v-if="history.length">
                <div class="title">操作记录</div>
                <div class="content">
                    <el-table
                            border
                            class="table"
                            :data="history"
                            style="width: 100%">
                        <el-table-column
                                prop="context"
                                label="处理结果"
                                align=left">
                        </el-table-column>
                        <el-table-column
                                label="调查情况"
                                align=left">
                            <template #default="{ row }">
                                <el-popover
                                        placement="top"
                                        width="300"
                                        trigger="click"
                                        :content="row.processingSituation">
                                    <span slot="reference" class="situation">
                                    {{row.processingSituation}}
                                </span>
                                </el-popover>
                            </template>
                        </el-table-column>
                        <el-table-column
                                prop="userName"
                                label="处理人"
                                align=left">
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                label="处理时间"
                                align=left">
                        </el-table-column>
                    </el-table>
                </div>
            </div>
        </el-form>
        <#--    返回按钮-->
        <el-button id="backSupervise" type="primary" size="mini" @click="checkSupervise">返回</el-button>
        <#--        图片预览框-->
        <el-dialog width="70%"  :visible.sync="dialogImageVisible">
            <img style="object-fit: cover;width: 100%" v-show="imageURL" :src="imageURL" alt="image" />
        </el-dialog>
    </div>
</el-main>
</body>

</html>
<script>
    var indexVue = new Vue({
        el: "#app",
        data: {
            //数据
            data: {},
            history: [],
            allowImage: [".png", ".jpg", ".jpeg"],
            imagesPath: [
                "https://raw.githubusercontent.com/FxPixels/SavePicGoImg/master/20200303234329.jpg",
            ],
            dialogImageVisible: false,
            formValidate: {
                result: "", //处理结果
                processingSituation: "", //调查处理情况
            },
            imageURL:'',
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
        methods: {
            //返回按钮
            checkSupervise() {
                window.parent.document.getElementById('complaint').style.display = "none"
                //清除跳转页面
                window.parent.returnBack()
            },
            //获取数据
            getList() {
                //切分上个页面传过来的id
                const url = window.location.href;
                const regex = /id=(\d+)/;
                const match = url.match(regex);
                const id = match ? match[1] : null;
                ms.http.get(ms.manager + '/feedback/getFeedbackById/' + id + '.do').then((res) => {
                    this.data = res.data.feedback
                    this.data.filesInfo = JSON.parse(this.data.filesInfo);
                    this.history = res.data.history
                })
            },
            //提交
            resultMesInfo(){
                //切分上个页面传过来的id
                const url = window.location.href;
                const regex = /id=(\d+)/;
                const match = url.match(regex);
                const id = match ? match[1] : null;
                const {processingSituation,result} = this.formValidate
                ms.http.post(ms.manager + '/feedback/handleFeedback/' + id + '.do?id='+id+'&processingSituation='+processingSituation+'&result='+result).then((res) => {
                    if(res.code==200) {
                        this.getList()
                    }

                })
            },
            //预览
            showImage(item) {
                const path = encodeURIComponent(item.path)
                this.imageURL = ms.manager + '/feedback/downloadAttachment.do?fileName='+item.fileName+'&filePath='+path
                this.dialogImageVisible = true
            },
            //下载
            downloadData(item) {
                const path = encodeURIComponent(item.path)
                window.open(ms.manager + '/feedback/downloadAttachment.do?fileName='+item.fileName+'&filePath='+path);
            },
            //提交
            handleSubmit(name){
                this.$refs[name].validate(async (valid) => {
                    if (valid) {
                        this.resultMesInfo()
                        this.$message.success('反馈成功');
                    } else {
                        this.$message.error('填写有误');
                    }
                });
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
    html {
        overflow: scroll;
    }

    <#--1-->
    .edit-check-delist {
        background: white;
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
        margin-left: 40px !important;
    }

    .blue_btn {
        margin-bottom: 26px;
    }

    .situation {
        display: inline-block;
        white-space: nowrap;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        cursor: pointer;
    }
    .files-lis{

    }
        .files-p {
            display: flex;
            align-items: center;
        }
        .files-a {
            margin-right: 12px;
            /*display: inline-block;*/
            /*overflow: hidden;*/
            /*text-overflow: ellipsis;*/
            /*white-space: nowrap;*/
            /*max-width: 640px;*/
            color: #2e8cf1;
        }
        .files-span {
            display: inline-block;
            width: 40px;
            color: #2e8cf1;
            margin-left: 6px;
            cursor: pointer;
            font-size: 14px;
        }
    .el-form-item__content{
        display: flex;
    }

    /*返回按钮*/
    #backSupervise {
        position: absolute;
        top: 10px;
        right: 10px;
    }
</style>
