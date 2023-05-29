<!DOCTYPE html>
<html>

<head>
    <title>个人资料修改</title>
    <#include "../../include/head-file.ftl">
</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-main class="ms-container">
        <div class="box">
            <el-form ref="form" :model="userData" :rules="ruleValidate" label-width="120px" label-position="left" size="mini">
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="用户名：" prop="account">
                        <p>{{ userData.account }}</p>
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="真实姓名：" prop="realname">
                        <el-input size="mini" type="text" v-model="userData.realname" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="电子邮件：" prop="email">
                        <el-input size="mini" type="text" v-model="userData.email" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row v-if="userData.city != '' || userData.district != ''">
                    <el-col :span="24">
                    <el-form-item label="归属地市：" prop="city" >
                        <el-select v-model="userData.city" placeholder="市" :disabled="userData.city != ''" style="width: 100%">
                            <el-option
                                    v-for="(item,index) in regionData"
                                    :key="index"
                                    :value="item.name"
                            >{{item.name}}</el-option>
                        </el-select>
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="邮政编码：" prop="zipcode">
                        <el-input size="mini" type="text" v-model="userData.zipcode" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="联系电话：" prop="phone">
                        <el-input size="mini" type="text" v-model="userData.phone" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item>
                    <@shiro.hasPermission name="manage:userinfo">
                    <el-button size="mini" type="primary" class="blue_btn" @click="sub">提交</el-button>
                    </@shiro.hasPermission>
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
                userData: {
                    account: "",
                    address: "",
                    city: "",
                    createTime: "",
                    creditCode: "",
                    district: "",
                    email: "",
                    id: 0,
                    management: "",
                    newPassword: "",
                    password: "",
                    phone: "",
                    principal: "",
                    principalTel: "",
                    province: "",
                    realname: "",
                    roleId: 0,
                    roleName: "",
                    storeName: "",
                    town: "",
                    updateTime: "",
                    usertype: 0,
                    zipcode: ""
                },
                regionData: [],
                ruleValidate: {
                    account: [
                        {
                            required: true,
                            message: "用户名不能为空",
                            trigger: "blur",
                        },
                    ],
                    realname: [
                        {
                            required: true,
                            message: "真实姓名不能为空",
                            trigger: "blur",
                        },
                    ],
                    email: [
                        {
                            required: true,
                            message: "邮箱不能为空",
                            trigger: "blur",
                        },
                        {pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/, message: "请输入正确的邮箱", trigger: "blur"}
                    ],
                    city: [
                        {
                            required: true,
                            message: "所属市不能为空",
                            trigger: "change",
                        },
                    ],
                    zipcode: [

                        {
                            required: true,
                            message: "邮政编码不能为空",
                            trigger: "change",
                        },
                        {pattern: /^[0-9]\d{5}$/, message: "请输入正确的邮政编码", trigger: "blur"}
                    ],
                    phone: [
                        {
                            required: true,
                            message: '手机号不能为空',
                            trigger: "blur",
                        },
                        {pattern: /^1[3|5|7|8|9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur"}
                    ]
                },
            }
        },
        watch: {},
        components: {},
        computed: {},
        methods: {
            // 获取地区信息
            getRegionData() {
                ms.http.get('/xwh/gd-regin.do').then((res) => {
                    this.regionData = res.data
                })
            },
            getUserList(){
                let id = sessionStorage.getItem('userId')
                ms.http.get('/xwh/user/userInfo.do',{id}).then((res)=>{
                    if(res.code == 200){
                        this.userData = {...this.userData,...res.data, id}
                    }
                })
            },
            sub(){
                let params = JSON.stringify(this.userData)
                this.$nextTick(()=>{
                    this.$refs['form'].validate((valid)=>{
                        if(valid){
                            ms.http.post('/xwh/user/updateById.do',params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res)=>{
                                if(res.code == 200){
                                    this.$message({
                                        type: "success",
                                        message:"修改成功"
                                    })
                                }else{
                                    let msg = res.msg ? res.msg : "修改失败"
                                    this.$message.error(msg)
                                }
                            })
                        }
                    })
                })
            },
        },
        mounted(){
            this.getUserList()
            this.getRegionData()
        }
    });

</script>
<style>
    .ms-container{
        height: 100vh !important;
    }
    .box{
        width: 30%;
        /*min-width: 400px;*/
    }
    .blue_btn {
        background: #409eff !important;
        color: #fff !important;
        border: 0;
        position: relative;
        left: 50%;
        transform: translateX(-50%);
    }

    .blue_btn:hover {
        background: #409eff !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }
    .el-form-item p{
        margin: 0 !important;
    }
    .el-button--primary:focus{
        color: #FFF;
        background-color: #409EFF;
        border-color: #409EFF;
        background: #409EFF
    }
</style>
