<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>文章</title>
    <#include "../../include/head-file.ftl">
</head>
<body style="overflow: hidden">
<div id="index" v-cloak>
    <!--左侧-->
    <el-container class="index-menu">
        <div class="left-tree" style="position:relative;">
            <el-scrollbar style="height: 100%;">
                <el-tree
                        :indent="5"
                        v-loading="loading"
                        ref="tree"
                        highlight-current
                        v-model="selectNode"
                        :expand-on-click-node="false"
                        default-expand-all
                        :empty-text="emptyText"
                        :data="treeData"
                        :props="defaultProps"
                        @node-click="handleNodeClick"
                        style="padding: 10px;height: 100%;">
                    <template slot-scope="{ node, data }">
                        <span ref="nodeSpan" class="custom-tree-node" :class="{el : data.categoryType == '3'}">
							<span :style="data.categoryType == '3' ? 'color: #dcdfe6' : ''" :title="data.categoryTitle">{{ data.categoryTitle }}</span>
						</span>
                    </template>

                </el-tree>
            </el-scrollbar>
        </div>
        <iframe :src="action" class="ms-iframe-style" v-loading.fullscreen.lock="fullscreenLoading">
        </iframe>
    </el-container>
</div>
</body>
</html>
<script>
    var indexVue = new Vue({
        el: "#index",
        data() {
            return {
                fullscreenLoading: false,
                action: "",
                //跳转页面
                defaultProps: {
                    children: 'children',
                    label: 'categoryTitle',
                    disabled: (data, node) => {
                        if (data.categoryListUrl === "") {

                        }
                    }
                },
                treeData: [],
                loading: true,
                emptyText: '',
                selectNode: 0
            }
        },
        methods: {
            handleNodeClick: function (data, node, el) {
                this.fullscreenLoading = true;
                if (data.categoryType == '3') {
                    this.action = this.action
                    this.$nextTick(() => {
                        this.fullscreenLoading = false;
                    })
                    return;
                }
                if (data.categoryType == '1') {
                    this.action = ms.manager + "/cms/content/main.do?categoryId=" + data.id + "&leaf=" + data.leaf;
                    this.$nextTick(() => {
                        this.fullscreenLoading = false;
                        this.selectNode = node
                    })
                } else if (data.categoryType == '2') {
                    this.action = ms.manager + "/cms/content/form.do?categoryId=" + data.id + "&type=2";
                    this.$nextTick(() => {
                        this.fullscreenLoading = false;
                        this.selectNode = node
                    })
                    //id=0时为最顶级节点全部节点
                } else if (data.id == 0) {
                    this.action = ms.manager + "/cms/content/main.do?leaf=false";
                    this.$nextTick(() => {
                        this.fullscreenLoading = false;
                        this.selectNode = node
                    })
                }
            },
            treeList: function () {
                var that = this;
                this.loadState = false;
                this.loading = true;
                ms.http.get(ms.manager + "/cms/category/list.do", {
                    pageSize: 999
                }).then(function (res) {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true;
                    }

                    if (!res.result || res.data.total <= 0) {
                        that.emptyText = '暂无数据';
                        that.treeData = [];
                    } else {
                        that.emptyText = '';
                        // 过滤掉栏目类型为链接属性
                        that.treeData = res.data.rows;
                        that.treeData = ms.util.treeData(that.treeData, 'id', 'categoryId', 'children');
                        that.treeData = [{
                            id: 0,
                            categoryTitle: '全部',
                            children: that.treeData
                        }];
                    }
                    that.$nextTick(() => {
                        console.log(that.$refs['nodeSpan'])
                        console.log(document.getElementsByClassName('el'))
                        const els = document.getElementsByClassName('el')
                        for (let i = 0; i < els.length; i++) {
                            els[i].parentNode.classList.add('els')
                            els[i].parentNode.parentNode.addEventListener('click', (event) => {
                                event.preventDefault();
                                els[i].parentNode.parentNode.classList.remove('is-current')
                            })

                            els[i].parentNode.addEventListener("mouseover", function (event) {
                                els[i].style.cursor = "not-allowed";
                            });
                        }
                    })
                    console.log(that.treeData)
                });
                setTimeout(function () {
                    if (that.loadState) {
                        that.loading = false;
                    } else {
                        that.loadState = true;
                    }
                }, 500);
            }
        },
        mounted: function () {
            this.fullscreenLoading = true;
            let that = this
            this.action = ms.manager + "/cms/content/main.do";
            this.treeList();
            this.$nextTick(() => {
                this.fullscreenLoading = false;

            })

        }
    });
</script>
<style>
    #index .index-menu {
        height: 100vh;
        min-height: 100vh;
        min-width: 140px;
    }

    #index .ms-iframe-style {
        width: 100%;
        height: 100%;
        border: 0;
    }

    #index .index-menu .el-main {
        padding: 0;
    }

    #index .left-tree {
        min-height: 100vh;
        background: #fff;
        width: 220px;
        border-right: solid 1px #e6e6e6;
    }

    #index .index-menu .el-main .index-menu-menu .el-menu-item {
        min-width: 140px;
        width: 100%;
    }

    #index .index-menu .el-main .index-material-item {
        min-width: 100% !important
    }

    #index .index-menu-menu-item, .el-submenu__title {
        height: 40px !important;
        line-height: 46px !important;
    }

    #index .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
        background-color: rgb(137 140 145);
        color: #fff;
        border-radius: 2px;
    }

    body {
        overflow: hidden;
    }

    .el:hover {
        cursor: not-allowed;
    }

    .els:hover {
        cursor: not-allowed;
    }

</style>
