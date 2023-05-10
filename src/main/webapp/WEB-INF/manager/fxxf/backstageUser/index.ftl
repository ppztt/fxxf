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
                <el-input
                        style="margin-left: 5px"
                        size="mini"
                        v-model="keyword"
                        placeholder="请输入关键字"
                        :clearable="true"
                ></el-input>
            </el-col>

            <el-col span="2">
                <el-row class="el-button_groud">
                    <el-col span="23">
                        <el-button
                                size="mini"
                                class="blue_btn btns_type" icon="el-icon-search" @click="searchInfo">
                            查询
                        </el-button>
                    </el-col>
                    <@shiro.hasPermission name="manage:user">
                    <el-col span="23" offset="1">
                        <el-button
                                size="mini"
                                class="blue_btn btns_type"
                                icon="el-icon-plus"
                                @click="showEditUser()">
                            新增
                        </el-button>
                    </el-col>
                    </@shiro.hasPermission>
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
                        <@shiro.hasPermission name="manage:userinfo">
                        <el-button class="action_btn blue_text" icon="el-icon-edit" @click="modifyUser(row.id)">修改
                        </el-button>
                        </@shiro.hasPermission>
                        <@shiro.hasPermission name="manage:user">
                        <el-button class="action_btn red_text" icon="el-icon-delete" @click="deleteBack(row.id)">删除
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
        <#-- 修改用户弹窗 -->
        <el-dialog title="修改后台用户"
                   center
                   :visible.sync="modify"
                   width="30%">
            <el-form
                    ref="formData"
                    :model="formData"
                    :rules="ruleValidate"
                    label-width="90px">
                <el-form-item label="用户名" prop="account">
                    <el-input
                            size="mini"
                            v-model="formData.account"
                            placeholder="请输入用户名"
                            :disabled="userId != 'none'"
                    ></el-input>
                </el-form-item>
                <el-form-item label="真实姓名" prop="realname">
                    <el-input
                            size="mini"
                            v-model="formData.realname"
                            placeholder="请输入真实姓名"
                    ></el-input>
                </el-form-item>
                <el-form-item label="所属角色" prop="roleId">
                    <el-select size="mini" v-model="formData.roleId" placeholder="请选择所属角色">
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
                        v-if="formData.roleId != 1">
                    <el-select
                            size="mini"
                            v-model="formData.city"
                            placeholder="请选择所属市"
                            :disabled="formData.roleId == 1 || roleId == 2 || roleId == 3"
                            :clearable="true"
                            filterable
                            @change="cityChange(formData.city)"
                            @clear="clear">
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
                        v-if="formData.roleId == 3 ">
                    <el-select
                            size="mini"
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
                    <el-input
                            size="mini"
                            type="password"
                            :password="true"
                            v-model="formData.password"
                            placeholder="请输入登录密码，为空则表示不修改"
                            show-password
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="newPassword">
                    <el-input
                            size="mini"
                            type="password"
                            :password="true"
                            v-model="formData.newPassword"
                            placeholder="请确认密码，为空则表示不修改"
                            show-password
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="mini" type="primary" @click="sub('modify')">提交</el-button>
                <el-button size="mini" @click="reset">重置</el-button>
            </span>
        </el-dialog>
        <el-dialog title="新增后台用户"
                   center
                   :visible.sync="newAdd"
                   width="30%">
            <el-form
                    ref="formData"
                    :model="formData"
                    :rules="newAddRules"
                    label-width="90px">
                <el-form-item label="用户名" prop="account">
                    <el-input
                            size="mini"
                            v-model="formData.account"
                            placeholder="请输入用户名"
                    ></el-input>
                </el-form-item>
                <el-form-item label="真实姓名" prop="realname">
                    <el-input
                            size="mini"
                            v-model="formData.realname"
                            placeholder="请输入真实姓名"
                    ></el-input>
                </el-form-item>
                <el-form-item label="所属角色" prop="roleId">
                    <el-select size="mini" v-model="formData.roleId" placeholder="请选择所属角色" style="width: 100%">
                        <el-option
                                :disabled="userInfo.roleId == 2 && (item.id === 1 || item.id === 2)"
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
                        v-if="formData.roleId == 2 || formData.roleId == 3">
                    <el-select
                            size="mini"
                            v-model="formData.city"
                            placeholder="请选择所属市"
                            :disabled="formData.roleId == 1 || roleId == 2 || roleId == 3"
                            :clearable="true"
                            filterable
                            @change="cityChange(formData.city)"
                            @clear="clear">
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
                        v-if="formData.roleId == 3 ">
                    <el-select
                            size="mini"
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
                        >
                        </el-option
                        >
                    </el-select>
                </el-form-item>
                <el-form-item label="联系电话" prop="phone">
                    <el-input size="mini" v-model="formData.phone" placeholder="请输入联系电话"></el-input>
                </el-form-item>
                <el-form-item label="登录密码" prop="password">
                    <el-input
                            size="mini"
                            type="password"
                            :password="true"
                            v-model="formData.password"
                            placeholder="请输入登录密码"
                            show-password
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="newPassword">
                    <el-input
                            size="mini"
                            type="password"
                            :password="true"
                            v-model="formData.newPassword"
                            placeholder="请输入确认密码"
                            show-password
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="mini" type="primary" @click="sub('newAdd')">提交</el-button>
                <el-button size="mini" @click="reset()">重置</el-button>
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
                userInfo:{},
                loading: false,
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
                // 添加用户信息表单
                formData: {
                    usertype: 1,
                    account: "", //用户名
                    realname: "", //真实姓名
                    industryName: "", // 行业协会名称
                    // industryUserName: "", // 行业协会用户名
                    email: "", //邮箱
                    city: "", //市
                    district: "", // 区县
                    zipcode: "", //邮政编码
                    phone: "", //手机
                    password: "", //密码
                    newPassword: "", //确认密码
                    roleId: 3, //所属组
                },
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
                    industryName: [
                        {
                            required: true,
                            message: "行业协会名称不能为空",
                            trigger: "blur",
                        },
                    ],
                    city: [
                        {
                            required: true,
                            message: "所属市不能为空",
                            trigger: "change",
                        },
                    ],
                    district: [
                        {
                            required: true,
                            message: "所属区县不能为空",
                            trigger: "change",
                        },
                    ],
                    phone: [
                        {
                            required: true,
                            message: "联系电话不能为空",
                            trigger: "blur",
                        },
                        {pattern: /^1[3|5|7|8|9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur"}
                    ],
                    password: [
                        {
                            required: false,
                            message: '密码不能为空',
                            trigger: "blur",
                        },
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                    ],
                    newPassword: [
                        {required: false, message: '不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                    ],
                    roleId: [
                        {
                            required: true,
                            type: "number",
                            message: "所属组不能为空",
                            trigger: "change",
                        },
                    ],
                },
                newAddRules: {
                    account: [
                        {required: true, message: "用户名不能为空", trigger: "blur",},
                    ],
                    realname: [
                        {required: true, message: "真实姓名不能为空", trigger: "blur",},
                    ],
                    industryName: [
                        {required: true, message: "行业协会名称不能为空", trigger: "blur",},
                    ],
                    city: [
                        {required: true, message: "所属市不能为空", trigger: "change",},
                    ],
                    district: [
                        {required: true, message: "所属区县不能为空", trigger: "change",},
                    ],
                    phone: [
                        {required: true, message: "联系电话不能为空", trigger: "blur",},
                        {pattern: /^1[3|5|7|8|9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur"}
                    ],
                    password: [
                        {required: true, message: '密码不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                    ],
                    newPassword: [
                        {required: true, message: '不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {pattern: /^(?=.*[a-zA-Z])(?=.*[1-9])(?=.*[\W]).{6,}$/, message: '至少包含数字、大写字母、小写字母和特殊字符中的三种', trigger: 'blur'}
                    ],
                    roleId: [
                        {required: true, type: "number", message: "所属组不能为空", trigger: "change",},
                    ],
                },
                columns: [
                    {
                        title: "用户名",
                        key: "account",
                        // width: "200px",
                        align: "left",
                    },
                    {
                        title: "角色",
                        key: "roleName",
                        // width: "150px",
                        align: "left",
                    },

                    {
                        title: "所属地市",
                        key: "city",
                        //   width: "200px",
                        align: "left",
                    },
                    {
                        title: "所属区县",
                        key: "district",
                        //   width: "200px",
                        align: "left",
                    },

                    {
                        title: "电话",
                        key: "phone",
                        // width: "150px",
                        align: "left",
                    },
                    {
                        title: "创建时间",
                        key: "createTime",
                        // width: "150px",
                        align: "left",
                    }
                ],
            }
        },
        methods: {
            searchInfo(){
                this.current = 1
                this.getUserList()
            },
            getUserList() {
                this.loading = true
                ms.http.get('/xwh/user/userList.do', {
                    current: this.current,
                    keyword: this.keyword,
                    size: this.size
                }).then((res) => {
                    let data = res.data
                    this.userDataList = data.records
                    this.total = Number(data.total)
                    this.loading = false
                })
            },
            showEditUser() {
                this.formData = {
                    account: "", //用户名
                    realname: "", //真实姓名
                    industryName: "", // 行业协会名称
                    // industryUserName: "", // 行业协会用户名
                    email: "", //邮箱
                    city: "", //市
                    district: "", // 区县
                    zipcode: "", //邮政编码
                    phone: "", //手机
                    password: "", //密码
                    newPassword: "", //确认密码
                    roleId: '', //所属组
                }
                this.newAdd = true
            },
            sizeChange(size) {
                this.size = size
                this.getUserList()
            },
            currentChange(current) {
                this.current = current
                this.getUserList()
            },
            getRegionData() {
                ms.http.get('/xwh/gd-regin.do').then((res) => {
                    if(res.code == 200){
                        this.regionData = res.data
                    }
                })
            },
            clear(){
                this.district = "";
                this.formData.district = ""
            },
            cityChange: function (name) {
                // 一级市发生改变
                if (name) {
                    let cityData_active = this.regionData.find((value) => value.name == name);
                    this.districtData = cityData_active.children;
                    this.district = "";
                    this.formData.district = ""
                    // this.town = "";
                }
            },
            districtChange: function (name) {
                // 二级地 县等发生改变
                if (name) {
                    let districtData_active = this.districtData.find((value => value.name == name));
                    // this.townData = districtData_active.children;
                    // this.town = "";
                }
            },
            // 修改用户信息
            modifyUser(id) {
                this.modify = true;
                ms.http.get('/xwh/user/userInfo.do', {id}).then((res) => {
                    if (res.code === '200') {
                        let data = res.data
                        this.formData.id = data.id
                        this.formData = {...this.formData, ...data}
                    }
                })
            },
            // 重置修改的用户信息
            reset() {
                this.formData = {
                    ...this.formData, //用户名
                    realname: "", //真实姓名
                    industryName: "", // 行业协会名称
                    // industryUserName: "", // 行业协会用户名
                    email: "", //邮箱
                    city: "", //市
                    district: "", // 区县
                    zipcode: "", //邮政编码
                    phone: "", //手机
                    password: "", //密码
                    newPassword: "", //确认密码
                    roleId: '', //所属组
                    id: -1
                }
            },
            // 提交修改
            sub(msg) {
                if(this.formData.password == this.formData.newPassword){
                    this.$nextTick(() => {
                        this.$refs["formData"].validate(valid => {
                            if (valid) {
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
                                    }else{
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
                            } else {
                                this.$message.error("请按规则正确填写信息")
                            }
                        })
                    })
                }else{
                    this.$message.error('两次密码不一致')
                }
            },
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
            },
            // 获取用户信息
            getUserInfo() {
                let id = sessionStorage.getItem('userId')
                ms.http.get('/xwh/user/userInfo.do', {id}).then((res) => {
                    if (res.code == 200) {
                        this.userInfo = {...res.data, id}
                    }
                })
            }

        },
        mounted() {
            this.getUserList()
            this.getRegionData()
            this.getUserInfo()
        }
    });

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
        background: #409eff !important;
        color: #fff !important;
        border: 0;
    }

    .blue_btn:hover {
        background: #409eff !important;
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
        width: 100%;
        margin-left: 10px !important;
    }
    .el-col-2{
        line-height: 36px;
    }

    .el-form-item__error {
        position: relative !important;
        margin-bottom: -15px !important;
    }
    .el-pagination {
        text-align: right;
    }
    .el-input{
        line-height: 36px;
    }
</style>
