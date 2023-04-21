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
            <el-form ref="form" :model="userData" :rules="ruleValidate" label-width="120px" label-position="left" size="medium">
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
                        <el-input type="text" v-model="userData.realname" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="电子邮件：" prop="email">
                        <el-input type="text" v-model="userData.email" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="归属地市：" prop="city">
                        <el-select v-model="userData.city" placeholder="市">
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
                        <el-input type="text" v-model="userData.zipcode" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="联系电话：" prop="phone">
                        <el-input type="text" v-model="userData.phone" />
                    </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item>
                    <el-button type="primary" class="blue_btn" @click="sub">提交</el-button>
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
                ms.http.get('/gd-regin.do').then((res) => {
                    this.regionData = res.data
                })
            },
            getUserList(){
                let id = sessionStorage.getItem('userId')
                ms.http.get('/user/userInfo.do',{id}).then((res)=>{
                    if(res.code == 200){
                        this.userData = {...this.userData,...res.data, id}
                        console.log(this.userData)
                    }
                })
            },
            sub(){
                let params = JSON.stringify(this.userData)
                this.$nextTick(()=>{
                    this.$refs['form'].validate((valid)=>{
                        if(valid){
                            ms.http.post('/user/updateById.do',params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res)=>{
                                console.log(res)
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
    .el-form-item p{
        margin: 0 !important;
    }
</style>
