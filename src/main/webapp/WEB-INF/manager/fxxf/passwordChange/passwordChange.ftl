<!DOCTYPE html>
<html>

<head>
    <title>个人密码修改</title>
    <#include "../../include/head-file.ftl">
</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-button type="primary" icon="iconfont icon-baocun" size="mini" @click="save()" :loading="saveDisabled">保存
        </el-button>
        <el-button size="mini" icon="iconfont icon-fanhui" plain onclick="javascript:history.go(-1)">返回</el-button>
    </el-header>
    <el-main class="ms-container">
        <div class="box">
            <el-form ref="form" :model="form" :rules="rules" label-width="90px" label-position="left" size="medium">

                <!--旧密码-->

                <el-form-item label="旧密码" prop="passwordone">
                    <el-input type="password"
                              :show-password="true"
                              :clearable="true" autocomplete="off"
                              v-model="form.passwordone"
                              :disabled="false"
                              placeholder="请输入内容"></el-input>
                </el-form-item>

                <!--新密码-->

                <el-form-item label="新密码" prop="passwordtwo">
                    <el-input type="password"
                              :show-password="true"
                              :clearable="true" autocomplete="off"
                              v-model="form.passwordtwo"
                              :disabled="false"
                              placeholder="请输入内容"></el-input>
                </el-form-item>

                <!--确认密码-->

                <el-form-item label="确认密码" prop="realpassword">
                    <el-input type="password"
                              :show-password="true"
                              :clearable="true" autocomplete="off"
                              v-model="form.realpassword"
                              :disabled="false"
                              placeholder="请输入内容"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="blue_btn">提交</el-button>
                </el-form-item>

            </el-form>
        </div>

    </el-main>
</div>
</body>

</html>

<script>
    var formVue = new Vue({
        el: '#form',
        data: function () {
            return {
                loading: false,
                saveDisabled: false,
                //表单数据
                form: {
                    // 旧密码
                    passwordone: '',
                    // 新密码
                    passwordtwo: '',
                    // 确认密码
                    realpassword: '',

                },
                rules: {
                    // 旧密码
                    passwordone: [{"required": true, "message": "旧密码不能为空"}],
                    // 新密码
                    passwordtwo: [{"required": true, "message": "新密码不能为空"}],
                    // 确认密码
                    realpassword: [{"required": true, "message": "确认密码不能为空"}],

                },

            }
        },
        watch: {},
        components: {},
        computed: {},
        methods: {

            save: function () {
                var that = this;
                var url = ms.manager + "/fxxf/personalPassword/save.do"
                if (that.form.id > 0) {
                    url = ms.manager + "/fxxf/personalPassword/update.do";
                }
                this.$refs.form.validate(function (valid) {
                    if (valid) {
                        that.saveDisabled = true;
                        var form = JSON.parse(JSON.stringify(that.form));
                        ms.http.post(url, form).then(function (res) {
                            if (res.result) {
                                that.$notify({
                                    title: "成功",
                                    message: "保存成功",
                                    type: 'success'
                                });
                                location.href = ms.manager + "/fxxf/personalPassword/index.do";
                            } else {
                                that.$notify({
                                    title: "错误",
                                    message: res.msg,
                                    type: 'warning'
                                });
                            }

                            that.saveDisabled = false;
                        }).catch(function (err) {
                            console.err(err);
                            that.saveDisabled = false;
                        });
                    } else {
                        return false;
                    }
                })
            },

            //获取当前个人密码修改
            get: function (id) {
                var that = this;
                this.loading = true
                ms.http.get(ms.manager + "/fxxf/personalPassword/get.do", {"id": id}).then(function (res) {
                    that.loading = false
                    if (res.result && res.data) {

                        that.form = res.data;
                    }
                });
            },
        },
        created: function () {
            var that = this;

            this.form.id = ms.util.getParameter("id");
            if (this.form.id) {
                this.get(this.form.id);
            }
        }
    });

</script>
<style>
    .box{
        width: 30%;
        /*min-width: 400px;*/
    }
    .blue_btn {
        background: #5d7cc9 !important;
        color: #fff !important;
        border: 0;
        position: relative;
        left: 50%;
        transform: translateX(-50%);
    }

    .blue_btn:hover {
        background: #899ed1 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }
</style>
