<!DOCTYPE html>
<html>

<head>
    <title>个人资料修改</title>
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
            <el-form ref="form" :model="userData" label-width="90px" label-position="left" size="medium">
                <el-row>
                    <el-col :span="24">
                    <el-form-item label="用户名：">
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
                regionData: []
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
            test(){
                ms.http.get('/index/attachmentList.do',{current: 1, keyword: "汕尾", size: 10}).then((res)=>{
                    console.log(res)
                })
            }
        },
        mounted(){
            this.test()
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
