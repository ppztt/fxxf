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
                        size="medium"
                        v-model="keyword"
                        placeholder="请输入关键字"
                        :clearable="true"
                ></el-input>
            </el-col>

            <el-col span="4">
                <el-row class="el-button_groud" type="flex">
                    <el-col>
                        <el-button
                                size="medium"
                                class="blue_btn btns_type" icon="el-icon-search" @click="getUserList">
                            查询
                        </el-button>
                    </el-col>
                    <el-col>
                        <el-button
                                size="medium"
                                class="green_btn btns_type"
                                icon="el-icon-plus"
                                @click="showEditUser()">
                            新增
                        </el-button>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
    </el-header>
    <el-main class="ms-container" ref="sakks">
        <el-table
                v-loading="loading"
                :data="userDataList"
                style="width: 100%"
                border
                height="100%">
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
                        <el-button class="action_btn blue_text" icon="el-icon-edit" @click="modifyUser(row.id)">修改
                        </el-button>
                        <el-button class="action_btn red_text" icon="el-icon-close" @click="deleteBack(row.id)">删除
                        </el-button>
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
        <el-dialog title="修改行业协会用户"
                   center
                   :visible.sync="modify"
                   width="30%"
                   @open="setRef('modifyForm')">
            <el-form
                    ref="modify"
                    :model="formData"
                    :rules="ruleValidate"
                    label-width="90px">
                <el-form-item label="用户名" prop="account">
                    <el-input
                            v-model="formData.account"
                            placeholder="请输入用户名"
                            :disabled="userId != 'none'"
                    ></el-input>
                </el-form-item>
                <el-form-item
                        label="行业协会"
                        prop="realname">
                    <el-input
                            v-model="formData.realname"
                            placeholder="请输入行业协会名称"
                    ></el-input>
                </el-form-item>
                <el-form-item
                        label="所属市"
                        prop="city">
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
                <el-form-item label="联系电话" prop="phone">
                    <el-input v-model="formData.phone" placeholder="请输入联系电话"></el-input>
                </el-form-item>
                <el-form-item label="登录密码" prop="password">
                    <el-input
                            type="password"
                            :password="true"
                            v-model="formData.password"
                            placeholder="请输入登录密码"
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="newPassword">
                    <el-input
                            type="password"
                            :password="true"
                            v-model="formData.newPassword"
                            placeholder="请再次输入密码"
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" type="primary" @click="sub('modify')">提交</el-button>
                <el-button size="small" @click="reset()">重置</el-button>
            </span>
        </el-dialog>
        <el-dialog title="新增行业协会用户"
                   center
                   :visible.sync="newAdd"
                   width="30%">
            <el-form
                    ref="newAdd"
                    :model="formData"
                    :rules="newAddRules"
                    label-width="90px">
                <el-form-item label="用户名" prop="account">
                    <el-input
                            v-model="formData.account"
                            placeholder="请输入用户名"
                    ></el-input>
                </el-form-item>
                <el-form-item
                        label="行业协会"
                        prop="realname">
                    <el-input
                            v-model="formData.realname"
                            placeholder="请输入行业协会名称"
                    ></el-input>
                </el-form-item>
                <el-form-item
                        label="所属市"
                        prop="city">
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
                <el-form-item label="联系电话" prop="phone">
                    <el-input v-model="formData.phone" placeholder="请输入联系电话"></el-input>
                </el-form-item>
                <el-form-item label="登录密码" prop="password">
                    <el-input
                            type="password"
                            :password="true"
                            v-model="formData.password"
                            placeholder="请输入登录密码，为空则表示不修改"
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="newPassword">
                    <el-input
                            type="password"
                            :password="true"
                            v-model="formData.newPassword"
                            placeholder="请确认密码，为空则表示不修改"
                    ></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" type="primary" @click="sub('newAdd')">提交</el-button>
                <el-button size="small" @click="reset()">重置</el-button>
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
                from: 'industry',
                userId: '',
                loading: false,
                keyword: "",
                current: 1,
                size: 10,
                total: 0,
                // 修改页面的路径
                modify: false,
                // 新增的弹出框
                newAdd: false,
                userDataList: [],
                roleId: '',
                roleList: [],
                // 地区信息
                regionData: [],
                districtData: [],
                // 添加用户信息表单
                formData: {
                    account: "", //用户名
                    realname: "", // 行业协会名称
                    // industryUserName: "", // 行业协会用户名
                    email: "", //邮箱
                    city: "", //市
                    district: "", // 区县
                    zipcode: "", //邮政编码
                    phone: "", //手机
                    password: "", //密码
                    newPassword: "", //确认密码
                    roleId: "3", //所属组
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
                    realname: [
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
                            message: '手机号不能为空',
                            trigger: "blur",
                        },
                        {pattern: /^1[3|5|7|8|9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur"}
                    ],
                    password: [
                        {required: false, message: '不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {
                            pattern: /^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,18}$/,
                            message: '至少包含数字、大写字母、小写字母和特殊字符中的三种'
                        }
                    ],
                    newPassword: [
                        {required: false, message: '不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {
                            pattern: /^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,18}$/,
                            message: '至少包含数字、大写字母、小写字母和特殊字符中的三种'
                        }
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
                    realname: [
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
                            message: '手机号不能为空',
                            trigger: "blur",
                        },
                        {pattern: /^1[3|5|7|8|9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur"}
                    ],
                    password: [
                        {required: true, message: '不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {
                            pattern: /^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,18}$/,
                            message: '至少包含数字、大写字母、小写字母和特殊字符中的三种'
                        }
                    ],
                    newPassword: [
                        {required: true, message: '不能为空', trigger: "blur",},
                        {min: 8, max: 18, message: '长度在 8 到 18 个字符', trigger: 'blur'},
                        {
                            pattern: /^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,18}$/,
                            message: '至少包含数字、大写字母、小写字母和特殊字符中的三种'
                        }
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
        watch: {},
        components: {},
        computed: {},
        methods: {

            getUserList() {
                this.loading = true
                ms.http.get('/user/industryAssociationList.do', {
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
                    realname: "", // 行业协会名称
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
                ms.http.get('/gd-regin.do').then((res) => {
                    this.regionData = res.data
                })
            },
            cityChange: function (name) {
                // 一级市发生改变
                if (name) {
                    let cityData_active = this.regionData.find((value) => value.name == name);
                    this.districtData = cityData_active.children;
                    this.district = "";
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
                ms.http.get('/user/userInfo.do', {id}).then((res) => {
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
                    account: "", //用户名
                    realname: "", //真实姓名
                    realname: "", // 行业协会名称
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
            // 提交
            sub(msg) {
                this.$nextTick(() => {
                    this.$refs[msg].validate(valid => {
                        if (valid) {
                            if (msg == "modify") {
                                let params = JSON.stringify(this.formData)
                                ms.http.post('/user/updateById.do', params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                                    if (res.code == '200') {
                                        this.$message({
                                            message: '修改成功',
                                            type: 'success'
                                        })
                                        this.modify = false
                                        this.reset()
                                        this.getUserList()
                                    } else {
                                        this.$message.error(res.msg)
                                    }
                                })
                            } else {
                                if (this.formData.password == this.formData.newPassword) {
                                    let params = JSON.stringify(this.formData)
                                    ms.http.post('/user/addIndustryAssociation.do', params, {headers: {'Content-type': 'application/json;charset=UTF-8'},}).then((res) => {
                                        if (res.code == '200') {
                                            this.$message({
                                                message: '新增用户成功',
                                                type: 'success'
                                            })
                                            this.newAdd = false
                                            this.reset()
                                            this.getUserList()
                                        } else {
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
            },
            // 删除用户信息
            deleteBack(id) {
                this.$confirm('确认删除该项数据?', '删除提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'error',
                    center: true
                }).then(() => {
                    ms.http.post('/user/del/' + id + '.do').then(() => {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getUnitList()
                    })
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            }
        },
        mounted() {
            this.getUserList()
            this.getRegionData()
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
        color: #7b93d2;
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

    .el-form-item__error {
        position: relative !important;
        margin-bottom: -15px !important;
    }
</style>
