<!DOCTYPE html>
<html>

<head>
    <title></title>
    <#include "../../include/head-file.ftl">
    <script src="${base}/static/plugins/ms/2.0/ms-manager.umd.js"></script>
    <script src="${base}/static/plugins/sockjs/1.4.0/sockjs.min.js"></script>
    <script src="${base}/static/plugins/stomp/2.3.3/stomp.min.js"></script>
    <!-- 此部分是铭飞平台MStroe的客户端（MStore不在铭飞开源产品范围），如果不需要使用MStore可以删除掉 -->
    <script src="https://cdn.mingsoft.net/platform/ms-store.umd.min.js"></script>
</head>

<body>
<div id="form" v-loading="loading" v-cloak>
    <el-header class="ms-header ms-tr" height="50px">
        <el-row class="tools" ref="tools" type="flex">
            <!-- 工具栏 -->
            <el-col span="6">
                <el-input size="mini"
                        style="margin-left: 5px"
                        v-model="keyword"
                        placeholder="请输入关键字"
                        :clearable="true"
                ></el-input>
            </el-col>

            <el-col span="1">
                <el-row class="el-button_groud">
                    <el-col span="23" offset="4">
                        <el-button
                                size="mini"
                                type="primary" icon="el-icon-search" @click="searchInfo">
                            查询
                        </el-button>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
    </el-header>
    <el-main class="ms-container">
        <el-table
                v-loading="loading"
                :data="userDataList"
                style="width: 100%"
                height="100%"
                border>
            <el-table-column
                    v-for="item in columns"
                    :key="item.key"
                    :prop="item.key"
                    :label="item.title"
                    :width="item.width">
            </el-table-column>

            <el-table-column
                    prop="action"
                    label="操作">
                <template slot-scope="{row}">
                    <div class="actions" :id="row.id">
                        <@shiro.hasPermission name="qyyhgl:detail">
                        <el-button class="action_btn blue_text" icon="el-icon-search" @click="modifyUser(row.id)">查看
                        </el-button>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="qyyhgl:resetPwd">
                        <el-button class="action_btn blue_text" icon="el-icon-edit" @click="modifyPW(row.id)">重置密码
                        </el-button>
                        </@shiro.hasPermission>
                    </div>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
                :disable="loading"
                background
                @size-change="sizeChange"
                @current-change="currentChange"
                :current-page="current"
                :page-sizes="[10, 20, 30, 40]"
                :page-size="size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
        </el-pagination>
        <#-- 企业用户信息弹窗 -->
        <el-dialog title="企业用户信息"
                   center
                   :visible.sync="modify"
                   width="50%">
            <el-form
                    ref="formData"
                    :model="formData"
                    label-width="150px">
                <el-form-item label="经营者注册名称" prop="realname">
                    <el-input size="mini"
                            v-model="formData.realname"
                            placeholder="请输入经营者注册名称"
                            disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="统一社会信用代码" prop="creditCode">
                    <el-input size="mini"
                            disabled
                            v-model="formData.creditCode"
                            placeholder="请输入统一社会信用代码"
                    ></el-input>
                </el-form-item>
                <el-form-item label="门店名称" prop="storeName">
                    <el-input size="mini"
                            disabled
                            v-model="formData.storeName"
                            placeholder="请输入门店名称">
                    </el-input>
                </el-form-item>
                <el-form-item
                        label="经营场所-所在市"
                        prop="city">
                    <el-input size="mini"
                            v-model="formData.city"
                            placeholder="请选择所属市"
                            disabled>
                    </el-input>
                </el-form-item>

                <el-form-item
                        label="经营场所-所在区县"
                        prop="district">
                    <el-input size="mini"
                            v-model="formData.district"
                            placeholder="请输入经营场所-所在区县"
                            disabled>
                    </el-input>
                </el-form-item>
                <el-form-item label="经营场所-详细地址" prop="address">
                    <el-input size="mini" v-model="formData.address" placeholder="请输入经营场所-详细地址" disabled></el-input>
                </el-form-item>
                <el-form-item label="经营类别" prop="management">
                    <el-input size="mini"
                            v-model="formData.management"
                            placeholder="请输入经营类别"
                            disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="负责人姓名" prop="principal">
                    <el-input size="mini"
                            v-model="formData.principal"
                            placeholder="请输入负责人姓名"
                            disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="负责人电话" prop="principalTel">
                    <el-input size="mini"
                            v-model="formData.principalTel"
                            placeholder="请输入负责人电话"
                            disabled
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" type="info" @click="modify = false">关闭</el-button>
            </span>
        </el-dialog>
        <el-dialog title="新增后台用户"
                   center
                   :visible.sync="newAdd"
                   width="30%">
            <el-form
                    ref="formData"
                    :model="formData"
                    label-width="90px">
                <el-form-item label="用户名" prop="account">
                    <el-input size="mini"
                            v-model="formData.account"
                            placeholder="请输入用户名"
                    ></el-input>
                </el-form-item>
                <el-form-item label="真实姓名" prop="realname">
                    <el-input size="mini"
                            v-model="formData.realname"
                            placeholder="请输入真实姓名"
                    ></el-input>
                </el-form-item>
                <el-form-item label="所属角色" prop="roleId">
                    <el-select v-model="formData.roleId" placeholder="请选择所属角色">
                        <el-option
                                :disabled="roleId == 2 && (item.id === 1 || item.id === 2)"
                                v-for="(item, index) in roleList"
                                :value="item.id"
                                :label="item.name"
                                :key="item.id">
                        </el-option
                        >
                    </el-select>
                </el-form-item>
                <el-form-item
                        label="所属市"
                        prop="city"
                        v-show="formData.roleId == 2 || formData.roleId == 3">
                    <el-select
                            v-model="formData.city"
                            placeholder="请选择所属市"
                            :disabled="formData.roleId == 1 || roleId == 2 || roleId == 3"
                            :clearable="true"
                            filterable
                            @change="cityChange(formData.city)">
                        <el-option
                                v-for="(item, index) in regionData"
                                :key="index"
                                :value="item.name"
                        >{{ item.name }}
                        </el-option
                        >
                    </el-select>
                </el-form-item>

                <el-form-item
                        label="所属区县"
                        prop="district"
                        v-show="formData.roleId == 3 ">
                    <el-select
                            v-model="formData.district"
                            placeholder="请选择所属区县"
                            :disabled="formData.roleId == 1"
                            :clearable="true"
                            filterable
                            @on-change="districtChange(formData.district)">
                        <el-option
                                v-for="item in districtData"
                                :key="item.name"
                                :value="item.name"
                        >{{ item.name }}
                        </el-option
                        >
                    </el-select>
                </el-form-item>
                <el-form-item label="联系电话" prop="phone">
                    <el-input size="mini" v-model="formData.phone" placeholder="请输入联系电话"></el-input>
                </el-form-item>
                <el-form-item label="登录密码" prop="password">
                    <el-input size="mini"
                            type="password"
                            :password="true"
                            v-model="formData.password"
                            placeholder="请输入登录密码"
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="newPassword">
                    <el-input size="mini"
                            type="password"
                            :password="true"
                            v-model="formData.newPassword"
                            placeholder="请输入确认密码"
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" type="primary" @click="sub('newAdd')">提交</el-button>
                <el-button size="small" @click="reset()">重置</el-button>
            </span>
        </el-dialog>
        <el-dialog title="修改密码"
                   center
                   :visible.sync="modifyPw"
                   width="30%">
            <el-form
                    ref="reviseForm"
                    :model="reviseForm"
                    :rules="reviseFormValidate"
                    label-width="120px">
                <el-form-item label="旧密码" prop="oldPassword">
                    <el-input size="mini"
                            v-model="reviseForm.oldPassword"
                            placeholder="请输入旧密码"
                    ></el-input>
                </el-form-item>
                <el-form-item label="设置新密码" prop="newPassword">
                    <el-input size="mini"
                            v-model="reviseForm.newPassword"
                            placeholder="请输入新密码"
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认新密码" prop="reNewPassword">
                    <el-input size="mini"
                            v-model="reviseForm.reNewPassword"
                            placeholder="请输入确认密码"
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="mini" type="primary" @click="modifyPassword()">提交</el-button>
                <el-button size="mini" @click="modifyPw = false">取消</el-button>
            </span>
        </el-dialog>
    </el-main>
</div>
</body>

</html>

<script>
    const backstage = new Vue({
            el: '#form',
            data: function () {
                return {
                    modifyPw: false,
                    from: '',
                    userId: '',
                    loading: false,
                    keyword: "",
                    current: 1,
                    size: 10,
                    total: 0,
                    // 修改的弹出框
                    modify: false,
                    // 新增的弹出框
                    newAdd: false,
                    userDataList: [],
                    roleId: 0,
                    roleList: [

                        {
                            id: 1,
                            name: "省工作人员",
                            explation: "系统管理员",
                            available: null,
                            createTime: null,
                            updateTime: null
                        },

                        {
                            id: 2,
                            name: "地市工作人员",
                            explation: "地市用户",
                            available: null,
                            createTime: null,
                            updateTime: null
                        },

                        {
                            id: 3,
                            name: "区县工作人员",
                            explation: "区县用户",
                            available: null,
                            createTime: null,
                            updateTime: null
                        }
                    ],
                    // 地区信息
                    regionData: [],
                    districtData: [],
                    // 检验密码
                    password: '',
                    // 查看用户信息表单
                    formData: {
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
                    columns: [
                        {
                            title: "用户ID",
                            key: "id",
                            // width: "200px",
                            align: "left",
                        },
                        {
                            title: "账号",
                            key: "account",
                            // width: "150px",
                            align: "left",
                        },

                        {
                            title: "经营注册名称",
                            key: "realname",
                            //   width: "200px",
                            align: "left",
                        },
                        {
                            title: "统一社会信用代码",
                            key: "creditCode",
                            //   width: "200px",
                            align: "left",
                        },
                        {
                            title: "创建时间",
                            key: "createTime",
                            // width: "150px",
                            align: "left",
                        }
                    ],
                    reviseForm: {
                        oldPassword: "",
                        newPassword: "",
                        reNewPassword: "",
                    },
                    reviseFormValidate: {
                        oldPassword: [
                            {
                                required: true,
                                message: "旧密码不能为空",
                                trigger: "blur",
                            },
                            { min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur' },
                            {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                        ],
                        newPassword: [
                            {
                                required: true,
                                message: "新密码不能为空",
                                trigger: "blur",
                            },
                            { min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur' },
                            {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                        ],
                        reNewPassword: [
                            {
                                required: true,
                                message: "密码不能为空",
                                trigger: "blur",
                            },
                            { min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur' },
                            {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                        ],
                    },
                }
            },
            methods: {
                searchInfo(){
                    this.current = 1
                    this.getUserList()
                },
                getUserList() {
                    this.loading = true
                    ms.http.get('/xwh/user/enterpriseList.do', {
                        current: this.current,
                        keyword: this.keyword,
                        size: this.size
                    }).then((res) => {
                        let data = res.data
                        this.userDataList = data.records
                        this.total = Number(data.total)
                        this.loading = false
                    })
                }
                ,
                showEditUser() {
                    this.formData = {
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
                    }
                    this.newAdd = true
                }
                ,
                sizeChange(size) {
                    this.size = size
                    this.getUserList()
                }
                ,
                currentChange(current) {
                    this.current = current
                    this.getUserList()
                }
                ,
                getRegionData() {
                    ms.http.get('/xwh/gd-regin.do').then((res) => {
                        this.regionData = res.data
                    })
                }
                ,
                cityChange: function (name) {
                    // 一级市发生改变
                    if (name) {
                        let cityData_active = this.regionData.find((value) => value.name == name);
                        this.districtData = cityData_active.children;
                        this.district = "";
                        // this.town = "";
                    }
                }
                ,
                districtChange: function (name) {
                    // 二级地 县等发生改变
                    if (name) {
                        let districtData_active = this.districtData.find((value => value.name == name));
                        // this.townData = districtData_active.children;
                        // this.town = "";
                    }
                }
                ,
                // 查看用户信息
                modifyUser(id) {
                    this.modify = true;
                    ms.http.get('/xwh/user/userInfo.do', {id}).then((res) => {
                        if (res.code === '200') {
                            let data = res.data
                            this.formData.id = data.id
                            this.formData = {...this.formData, ...data}
                        }
                    })
                }
                ,
                // 重置修改的用户信息
                reset() {
                    this.formData = {
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
                    }
                },
                modifyPW(id) {
                    this.modifyPw = true
                    this.userId = id
                    ms.http.get('/xwh/user/userInfo.do', {id}).then((res) => {
                        this.formData = res.data
                    })
                },
                //
                modifyPassword() {
                    this.$nextTick(() => {
                        this.$refs['reviseForm'].validate(valid => {
                            if (valid) {
                                this.formData.newPassword = this.reviseForm.newPassword
                                let params = JSON.stringify(this.formData)
                                ms.http.post('/xwh/user/updateById.do', params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                                    if(res.code == 200){
                                        this.$message({
                                            message: '修改成功',
                                            type: 'success'
                                        })
                                    }
                                    if (res.code == 500) {
                                        this.$message.error(res.msg)
                                    }
                                })
                            } else {
                                this.$message.error('请将表单填写完整')
                            }

                        })

                    })

                }
                ,
                // 提交修改
                sub(msg) {
                    if (msg == "modify") {
                        if(this.formData.password == this.formData.newPassword){
                            let params = JSON.stringify(this.formData)
                            ms.http.post('/xwh/user/updateById.do', params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                                if (res.code == '200') {
                                    this.$message({
                                        message: '修改成功',
                                        type: 'success'
                                    })
                                    this.modify = false
                                    this.reset()
                                    this.getUserList()
                                }
                                if (res.code == '500') {
                                    this.$message.error(res.msg)
                                }
                            })
                        }else {
                            this.$message.error("两次密码输入不一致")
                        }
                    } else {
                        if (this.formData.password == this.formData.newPassword) {
                            let params = JSON.stringify(this.formData)
                            ms.http.post('/xwh/user/addUser.do', params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                                if (res.code == '200') {
                                    this.$message({
                                        message: '新增用户成功',
                                        type: 'success'
                                    })
                                    this.newAdd = false
                                    this.reset()
                                    this.getUserList()
                                }
                                if (res.code == '500') {
                                    this.$message.error(res.msg)
                                }
                            })
                        } else {
                            this.$message.error("两次密码输入不一致")
                        }
                    }
                }
                ,
                // 删除用户信息
                deleteBack(id) {
                    this.$confirm('确认删除该项数据?', '删除提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'error',
                        center: true
                    }).then(() => {
                        ms.http.post('/xwh/user/del/' + id + '.do').then(() => {
                            this.$message({
                                type: 'success',
                                message: '删除成功!'
                            });
                            this.getUserList()
                        })
                    }).catch(() => {
                        this.$message({
                            type: 'info',
                            message: '已取消删除'
                        });
                    });
                }
            }
            ,
            mounted() {
                this.getUserList()
                this.getRegionData()
            }
        })
    ;

</script>
<style>
    .user_list {
        width: 100%;
    }

    .user_list .tools {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        font-size: 14px !important;
        margin-top: 10px;
    }

    .user_list .table {
        margin-top: 15px;
        border: 1px solid #e2e7e9;
    }

    .user_list .table .actions {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .user_list .table .actions .action_btn {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 10px 5px;
        cursor: pointer;
    }

    .user_list .table .actions .action_btn img {
        display: block;
        width: 15px;
        margin-right: 5px;
    }

    /* 城市级联 */
    .city_cascade {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .el-button_groud {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .el-button_groud el-button {
        width: 100%;
    }

    .user_list .ivu-table-overflowX {
        overflow-x: hidden;
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

    .green_btn {
        background: #54c698 !important;
        color: #fff !important;
        border: 0;
    }

    .green_btn:hover {
        background: #8ee9c4 !important;
        color: #fff !important;
        border: 0;
        outline: none;
    }

    .red_text {
        color: #f05858;
    }

    .actions {
        display: flex;
        justify-content: flex-start;
        align-items: center;

    }

    .blue_text {
        color: #409eff;
    }

    .actions .action_btn {
        display: flex;
        justify-content: flex-start;
        /*align-items: center;*/
        padding: 10px 5px;
        cursor: pointer;
        background: transparent !important;
        border: none;
    }

    .btns_type {
        width: 90%;
        margin-left: 10% !important;
    }
    .el-pagination {
        text-align: right;
    }
    .el-button_groud{
        line-height: 36px;
    }
    .el-input{
        line-height: 36px;
    }
</style>
