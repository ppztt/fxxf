<html>
<head>
    <meta charset="utf-8"/>
    <title>${app.appName}</title>
    <#include "/include/head-file.ftl"/>
    <link rel="stylesheet" type="text/css" href="${base}/static/plugins/TextInputEffects/css/normalize.css"/>
    <link rel="stylesheet" type="text/css" href="${base}/static/plugins/TextInputEffects/css/set1.css"/>
    <link rel="icon" href="${base}/static/images/fxxf.png">
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body class="custom-body">
<div id="reset-password" v-cloak>
    <!--大容器开始-->
    <div class="class-1" @keydown.13='login'>
        <!--大容器开始-->
        <div class="class-2">
            <!--大容器开始-->
            <div class="class-3">
                <!--图片开始-->
                <img
                        src="${base}/static/images/login-banner.png"
                        class="class-4"/>
                <!--图片结束-->
            </div>
            <!--大容器结束-->
            <!--大容器开始-->
            <div class="class-5">
                <!--小容器开始-->
                <div class="class-6">
                    <!--文本开始-->
                    <div class="class-7">
                        密码修改
                        <div style="font-size: 16px;color: #F56C6C;display: inline;">
                            超过{{passChangeMaxDay}}天未修改密码
                        </div>
                    </div>
                    <!--文本结束-->
                    <!--小容器开始-->
                    <el-form :model="resetPasswordForm" ref="resetPasswordForm" :rules="resetPasswordFormRule">
                        <div class="class-8">
                            <el-form-item prop="managerName">
                     <span class="input input--hoshi">
                          <input v-model="resetPasswordForm.managerName" class="input__field input__field--hoshi"
                                 type="text" id="input-name" readonly disabled/>
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1 bug" for="input-name">
                              <span class="input__label-content input__label-content--hoshi">账号</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->
                        <!--小容器开始-->
                        <div class="class-13">
                            <!--文本开始-->
                            <el-form-item prop="oldManagerPassword">
                  <span class="input input--hoshi">
                          <input v-model="resetPasswordForm.oldManagerPassword" class="input__field input__field--hoshi"
                                 :type="showPassword1 ? 'password' : 'text'" id="oldManagerPassword"/>
                      <i class="el-icon-view showPass" @click="showPassword1 = !showPassword1"></i>
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1 bug"
                                 for="oldManagerPassword">
                              <span class="input__label-content input__label-content--hoshi">旧密码</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->
                        <!--小容器开始-->
                        <div class="class-13">
                            <!--文本开始-->
                            <el-form-item prop="newManagerPassword">
                  <span class="input input--hoshi">
                          <input v-model="resetPasswordForm.newManagerPassword" class="input__field input__field--hoshi"
                                 :type="showPassword2 ? 'password' : 'text'" id="newManagerPassword"/>
                      <i class="el-icon-view showPass"  @click="showPassword2 = !showPassword2"></i>
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1 bug"
                                 for="newManagerPassword">
                              <span class="input__label-content input__label-content--hoshi">新密码</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->
                        <!--小容器开始-->
                        <div class="class-13">
                            <!--文本开始-->
                            <el-form-item prop="newComfirmManagerPassword">
                  <span class="input input--hoshi">
                          <input v-model="resetPasswordForm.newComfirmManagerPassword"
                                 class="input__field input__field--hoshi" :type="showPassword3 ? 'password' : 'text'"
                                 id="newComfirmManagerPassword"/>
                      <i class="el-icon-view showPass" @click="showPassword3 = !showPassword3"></i>
                          <label class="input__label input__label--hoshi input__label--hoshi-color-1 bug"
                                 for="newComfirmManagerPassword">
                              <span class="input__label-content input__label-content--hoshi">确认新密码</span>
                          </label>
                      </span>
                            </el-form-item>
                        </div>
                        <!--小容器结束-->
                    </el-form>
                    <!--小容器开始-->
                    <div class="class-25" style="height: 20px;">
                    </div>
                    <!--小容器结束-->
                    <!--按钮开始-->
                    <el-button @click="updatePassword" type="primary" :loading="loading"
                               class="class-26">
                        {{loading?'更新密码中':'更新密码'}}
                    </el-button>
                    <!--按钮结束-->
                </div>
                <!--小容器结束-->
            </div>
            <!--大容器结束-->
        </div>
        <!--大容器结束-->
    </div>
    <!--大容器结束-->
</div>
</body>
</html>
<script src="${base}/static/plugins/TextInputEffects/js/classie.js"></script>
<script>
    var resetPasswordVue = new Vue({
        el: '#reset-password',
        data: {
            showPassword1: true,
            showPassword2: true,
            showPassword3: true,
            base: ms.base,
            // 是否直接打开该页面
            isDirect: true,
            loading: false,
            passChangeMaxDay: 90,
            resetPasswordForm: {
                managerName: '',
                oldManagerPassword: '',
                newManagerPassword: '',
                newComfirmManagerPassword: ''//确认新密码
            },
            resetPasswordFormRule: {
                oldManagerPassword: [
                    {required: true, message: '请输入旧密码', trigger: 'blur'},
                    {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'}
                ],
                newManagerPassword: [
                    {required: true, message: '请输入新密码', trigger: 'blur'},
                    {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                    {
                        validator: function (rule, value, callback) {
                            if (resetPasswordVue.resetPasswordForm.oldManagerPassword === value) {
                                callback('新密码必须与旧密码不一致');
                            } else {
                                callback();
                            }
                        }
                    },
                    {
                        pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/,
                        message: '至少包含数字、大写字母、小写字母和特殊字符中的三种'
                    }
                ],
                newComfirmManagerPassword: [
                    {required: true, message: '请再次输入确认密码', trigger: 'blur'},
                    {min: 8, max: 30, message: '长度在 8 到 30 个字符', trigger: 'blur'},
                    {
                        validator: function (rule, value, callback) {
                            if (resetPasswordVue.resetPasswordForm.newManagerPassword === value) {
                                callback();
                            } else {
                                callback('新密码和确认新密码不一致');
                            }
                        }
                    },
                    {
                        pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/,
                        message: '至少包含数字、大写字母、小写字母和特殊字符中的三种'
                    }
                ]
            }
        },
        methods: {
            // 更新密码
            updatePassword: function () {
                var that = this;
                let win = window
                this.$refs['resetPasswordForm'].validate(function (valid) {
                    if (valid) {
                        that.loading = true;
                        ms.http.post(ms.manager + "/updatePasswordForce.do", that.resetPasswordForm).then(function (data) {
                            if (data.result == true) {
                                localStorage.setItem('managerName', that.resetPasswordForm.managerName)
                                that.resetPasswordForm.oldManagerPassword = '';
                                that.resetPasswordForm.newManagerPassword = '';
                                that.$notify({
                                    title: '提示',
                                    message: "修改成功,重新登录系统！",
                                    type: 'success'
                                })
                                setTimeout(function () {
                                    location.href = ms.manager + "/login.do";
                                    location.reload();
                                },500)
                            } else {
                                that.$notify({
                                    title: '错误',
                                    message: data.msg,
                                    type: 'error'
                                });
                                that.loading = false;
                            }
                        }, function (err) {
                            that.$notify({
                                title: '错误',
                                message: err,
                                type: 'error'
                            });
                        }).catch(function () {
                            that.loading = false;
                        });
                    }
                });
            },
            //初始
            initial: function () {
                this.resetPasswordForm.managerName = localStorage.getItem('managerName');
                this.passChangeMaxDay = localStorage.getItem('passChangeMaxDay');
                if (this.passChangeMaxDay > 0) {
                    this.isDirect = false
                }
                localStorage.setItem('passChangeMaxDay', 0);
            }
        },
        created: function () {
            this.initial();
            if (this.isDirect) {
                location.href = ms.manager + "/login.do";
            }
        },

    });
</script>
<script>
    (function () {
        // trim polyfill : https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim
        if (!String.prototype.trim) {
            (function () {
                // Make sure we trim BOM and NBSP
                var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
                String.prototype.trim = function () {
                    return this.replace(rtrim, '');
                };
            })();
        }

        [].slice.call(document.querySelectorAll('input.input__field')).forEach(function (inputEl) {
            // in case the input is already filled..
            if (inputEl.value.trim() !== '') {
                classie.add(inputEl.parentNode, 'input--filled');
            }

            // events:
            inputEl.addEventListener('focus', onInputFocus);
            inputEl.addEventListener('blur', onInputBlur);
        });

        function onInputFocus(ev) {
            classie.add(ev.target.parentNode, 'input--filled');
        }

        function onInputBlur(ev) {
            if (ev.target.value.trim() === '') {
                classie.remove(ev.target.parentNode, 'input--filled');
            }
        }
    })();
</script>

<style>
    .el-form-item {
        margin-bottom: 0px;
    }

    .el-form-item__content {
        line-height: initial;
    }

    .custom-body {
    }

    .class-1 {
        color: #333333;
        background-image: url(${base}/static/images/login-bg.jpg);
        outline: none;
        outline-offset: -1px;
        background-size: cover;
        background-position: center;
        height: 100%;
        max-width: 100%;
        align-items: center;
        flex-direction: row;
        display: flex;
        justify-content: center;
        animation-duration: 1s;
        width: 100%;
        background-repeat: no-repeat;
    }

    .class-2 {
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 540px;
        max-width: 100%;
        background-color: rgba(255, 255, 255, 1);
        flex-direction: row;
        display: flex;
        animation-duration: 1s;
        border-radius: 12px;
        width: 1000px;
        background-repeat: no-repeat;
    }

    .class-3 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 100%;
        max-width: 100%;
        align-items: flex-start;
        flex-direction: row;
        display: flex;
        justify-content: flex-start;
        animation-duration: 1s;
        width: 460px;
        background-repeat: no-repeat;
    }

    .class-4 {
        height: 100%;
        animation-duration: 1s;
        width: 100%;
    }

    .class-5 {
        color: #333333;
        outline: none;
        padding-bottom: 20px;
        outline-offset: -1px;
        flex: 1;
        padding-top: 20px;
        height: 100%;
        max-width: 100%;
        align-items: center;
        flex-direction: column;
        display: flex;
        justify-content: flex-start;
        animation-duration: 1s;
        width: 200px;
        background-repeat: no-repeat;
    }

    .class-6 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        max-width: 100%;
        flex-direction: column;
        display: flex;
        animation-duration: 1s;
        width: 330px;
        background-repeat: no-repeat;
        margin-top: 20px;
    }

    .class-7 {
        color: #333333;
        word-wrap: break-word;
        display: inline-block;
        animation-duration: 1s;
        font-size: 36px;
        line-height: 1.4;
        margin-bottom: 20px;
    }

    .class-8 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 80px;
        max-width: 100%;
        flex-direction: column;
        display: flex;
        justify-content: flex-end;
        animation-duration: 1s;
        width: 100%;
        background-repeat: no-repeat;
    }

    .class-9 {
        color: #BBBBBB;
        word-wrap: break-word;
        display: inline-block;
        animation-duration: 1s;
        font-size: 12px;
        line-height: 1.4;
    }

    .class-10 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 40px;
        max-width: 100%;
        align-items: center;
        flex-direction: row;
        display: flex;
        animation-duration: 1s;
        width: 100%;
        background-repeat: no-repeat;
    }

    .class-11 {
        color: #333333;
        word-wrap: break-word;
        display: inline-block;
        animation-duration: 1s;
        font-size: 14px;
        line-height: 1.4;
    }

    .class-12 {
        margin-right: auto;
        animation-duration: 1s;
        background-color: #eee;
        border-radius: 1px;
        width: 100%;
        height: 1px;
        margin-left: auto;
    }

    .class-13 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 80px;
        max-width: 100%;
        flex-direction: column;
        display: flex;
        justify-content: flex-end;
        animation-duration: 1s;
        width: 100%;
        background-repeat: no-repeat;
    }

    .class-14 {
        color: #BBBBBB;
        word-wrap: break-word;
        padding-bottom: 10px;
        display: inline-block;
        animation-duration: 1s;
        font-size: 14px;
        line-height: 1.4;
    }

    .class-15 {
        margin-right: auto;
        animation-duration: 1s;
        background-color: #eee;
        border-radius: 1px;
        width: 100%;
        height: 1px;
        margin-left: auto;
    }

    .class-16 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 80px;
        max-width: 100%;
        align-items: flex-end;
        flex-direction: row;
        display: flex;
        justify-content: flex-start;
        animation-duration: 1s;
        background-repeat: no-repeat;
    }

    .class-17 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        flex: 1;
        height: 80px;
        max-width: 100%;
        flex-direction: column;
        display: flex;
        justify-content: flex-end;
        animation-duration: 1s;
        width: 200px;
        background-repeat: no-repeat;
    }

    .class-18 {
        color: #BBBBBB;
        word-wrap: break-word;
        display: inline-block;
        animation-duration: 1s;
        font-size: 14px;
        line-height: 1.4;
        margin-bottom: 10px;
    }

    .class-19 {
        margin-right: auto;
        animation-duration: 1s;
        background-color: #eee;
        border-radius: 1px;
        width: 100%;
        height: 1px;
        margin-left: auto;
    }

    .class-20 {
        cursor: pointer;
        color: #333333;
        margin-right: 10px;
        outline-offset: -1px;
        height: 40px;
        max-width: 100%;
        align-items: center;
        flex-direction: row;
        display: flex;
        justify-content: center;
        margin-left: 10px;
        animation-duration: 1s;
        width: 88px;
        background-repeat: no-repeat;
        margin-bottom: 0.85em;
    }

    .class-21 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        max-width: 100%;
        align-items: flex-end;
        flex-direction: column;
        display: flex;
        justify-content: flex-end;
        animation-duration: 1s;
        background-repeat: no-repeat;
        margin-bottom: 0.85em;
    }

    .class-22 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        max-width: 100%;
        flex-direction: column;
        display: flex;
        animation-duration: 1s;
        background-repeat: no-repeat;
    }

    .class-23 {
        color: #BBBBBB;
        word-wrap: break-word;
        display: inline-block;
        animation-duration: 1s;
        font-size: 12px;
        line-height: 1.4;
    }

    .class-24 {
        cursor: pointer;
        color: #0099FF;
        word-wrap: break-word;
        display: inline-block;
        animation-duration: 1s;
        font-size: 12px;
        line-height: 1.4;
    }

    .class-25 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        height: 40px;
        max-width: 100%;
        flex-direction: row;
        display: flex;
        animation-duration: 1s;
        width: 100px;
        background-repeat: no-repeat;
    }

    .class-26 {
        background-color: #0099ff;
    }

    .class-27 {
        color: #333333;
        outline: none;
        outline-offset: -1px;
        max-width: 100%;
        align-items: center;
        flex-direction: row;
        display: flex;
        animation-duration: 1s;
        width: 100px;
        background-repeat: no-repeat;
        margin-top: 20px;
    }

    .class-28 {
        color: #333333;
        outline: 1px dashed hsla(0, 0%, 66.7%, .7);
        outline-offset: -1px;
        height: 14px;
        max-width: 100%;
        flex-direction: row;
        display: flex;
        animation-duration: 1s;
        width: 14px;
        background-repeat: no-repeat;
    }

    .class-29 {
        color: #999999;
        word-wrap: break-word;
        display: inline-block;
        margin-left: 10px;
        animation-duration: 1s;
        font-size: 14px;
        line-height: 1.4;
    }

    @media (max-width: 768px) {
    }

    .input__label--hoshi::before {
        content: '';
        position: absolute;
        top: 1px;
        left: 0;
        width: 100%;
        height: calc(100% - 10px);
        border-bottom: 1px solid #B9C1CA;
    }
    .showPass{
        position: absolute;
        right: 10px;
        top: 50%;
        cursor: pointer;
    }
    .bug{
        line-height: 30px !important;
    }
    .input__label-content{
        height: 60px;
        top: 5px;
    }
    .input--filled .input__label-content{
        top: 5px !important;
    }

</style>
