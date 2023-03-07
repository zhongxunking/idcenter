// id提供者管理组件
const IdersTemplate = `
<div id="idersApp">
    <el-row>
        <el-col>
            <el-form :v-model="queryIdersForm" :inline="true" size="small">
                <el-form-item label="数据源:">
                    <el-select v-model="currentDataSource" placeholder="请选择数据源">
                        <el-option v-for="dataSource in dataSources" :value="dataSource" :label="dataSource"
                                   :key="dataSource"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="id编码:">
                    <el-input v-model="queryIdersForm.iderId" clearable placeholder="id编码"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="queryIders">查询</el-button>
                </el-form-item>
                <el-form-item v-if="manager.type === 'ADMIN'">
                    <el-button type="primary" icon="el-icon-plus" @click="openAddIderDialogVisible">新增</el-button>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
    <el-table :data="iders" v-loading="idersLoading" border>
        <el-table-column prop="iderId" label="id编码"></el-table-column>
        <el-table-column prop="iderName" label="名称">
            <template slot-scope="{ row }">
                <el-input v-if="row.editing" v-model="row.editingIderName" type="textarea" autosize size="mini"
                          placeholder="请输入名称"></el-input>
                <span v-else>{{ row.iderName }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="periodType" label="周期类型">
            <template slot-scope="{ row }">
                <el-tag v-if="row.periodType === 'NONE'" size="medium">无周期</el-tag>
                <el-tag v-else-if="row.periodType === 'HOUR'" type="success" size="medium">每小时</el-tag>
                <el-tag v-else-if="row.periodType === 'DAY'" type="info" size="medium">每天</el-tag>
                <el-tag v-else-if="row.periodType === 'MONTH'" type="warning" size="medium">每月</el-tag>
                <el-tag v-else-if="row.periodType === 'YEAR'" type="danger" size="medium">每年</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="maxId" label="id最大值（不包含）">
            <template slot-scope="{ row }">
                <el-input v-if="row.editing" v-model="row.editingMaxId" type="textarea" autosize size="mini"
                          placeholder="无限制"></el-input>
                <div v-else>
                    <span v-if="row.maxId !== null">{{ row.maxId }}</span>
                    <el-tag v-else size="medium">无限制</el-tag>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="maxAmount" label="单次获取id最大数量">
            <template slot-scope="{ row }">
                <el-input v-if="row.editing" v-model="row.editingMaxAmount" type="textarea" autosize size="mini"
                          placeholder="无限制"></el-input>
                <div v-else>
                    <span v-if="row.maxAmount !== null">{{ row.maxAmount }}</span>
                    <el-tag v-else size="medium">无限制</el-tag>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="currentPeriod" label="当前周期">
            <template slot-scope="{ row }">
                <span v-if="toShowingCurrentPeriod(row)">{{ toShowingCurrentPeriod(row) }}</span>
                <el-tag v-else size="medium">无周期</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="currentId" label="当前id"></el-table-column>
        <el-table-column label="操作" header-align="center" width="200px">
            <template slot-scope="{ row }">
                <el-row>
                    <el-col :span="10" style="text-align: center">
                        <el-tooltip v-if="!row.editing" content="修改" placement="top" :open-delay="1000"
                                    :hide-after="3000">
                            <el-button @click="startEditing(row)" type="primary" icon="el-icon-edit" size="mini"
                                       circle></el-button>
                        </el-tooltip>
                        <template v-else>
                            <el-button-group>
                                <el-tooltip content="取消修改" placement="top" :open-delay="1000" :hide-after="3000">
                                    <el-button @click="row.editing = false" type="info" icon="el-icon-close" size="mini"
                                               circle></el-button>
                                </el-tooltip>
                                <el-tooltip content="保存修改" placement="top" :open-delay="1000" :hide-after="3000">
                                    <el-button @click="saveEditing(row)" type="success" icon="el-icon-check" size="mini"
                                               circle></el-button>
                                </el-tooltip>
                            </el-button-group>
                        </template>
                    </el-col>
                    <el-col :span="7" style="text-align: center">
                        <el-tooltip content="修改当前周期和id" placement="top" :open-delay="1000"
                                    :hide-after="3000">
                            <el-button @click="startModifyCurrent(row)" type="warning" icon="el-icon-edit"
                                       size="mini" round>C
                            </el-button>
                        </el-tooltip>
                    </el-col>
                    <el-col :span="7" style="text-align: center">
                        <el-tooltip content="删除" placement="top" :open-delay="1000" :hide-after="3000">
                            <el-button @click="deleteIder(row)" :disabled="manager.type !== 'ADMIN'" type="danger"
                                       icon="el-icon-delete" size="mini" circle></el-button>
                        </el-tooltip>
                    </el-col>
                </el-row>
            </template>
        </el-table-column>
    </el-table>
    <el-row style="margin-top: 10px">
        <el-col style="text-align: end">
            <el-pagination :total="totalIders" :current-page.sync="queryIdersForm.pageNo"
                           :page-size.sync="queryIdersForm.pageSize" @current-change="queryIders"
                           layout="total,prev,pager,next" small background></el-pagination>
        </el-col>
    </el-row>
    <el-dialog :visible.sync="addIderDialogVisible" :before-close="closeAddIderDialog" title="新增id提供者" width="40%">
        <el-form ref="addIderForm" :model="addIderForm" label-width="30%">
            <el-form-item label="数据源">
                <el-select v-model="addIderForm.dataSource" placeholder="请选择数据源" style="width: 90%">
                    <el-option value="__allDataSource" label="所有数据源" key="_allDataSource"></el-option>
                    <el-option v-for="dataSource in dataSources" :value="dataSource" :label="dataSource"
                               :key="dataSource"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="id编码" prop="iderId"
                          :rules="[{required:true, message:'请输入id编码', trigger:'blur'}]">
                <el-input v-model="addIderForm.iderId" clearable placeholder="请输入id编码"
                          style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="名称" prop="iderName">
                <el-input v-model="addIderForm.iderName" clearable placeholder="请输入名称"
                          style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="周期类型" prop="periodType"
                          :rules="[{required:true, message:'请选择周期类型', trigger:'blur'}]">
                <el-select v-model="addIderForm.periodType" placeholder="请选择周期类型" style="width: 90%">
                    <el-option value="NONE" label="无周期"></el-option>
                    <el-option value="HOUR" label="每小时"></el-option>
                    <el-option value="DAY" label="每天"></el-option>
                    <el-option value="MONTH" label="每月"></el-option>
                    <el-option value="YEAR" label="每年"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="id最大值">
                <el-input v-model="addIderForm.maxId" clearable placeholder="不填表示不限制"
                          style="width: 90%"></el-input>
            </el-form-item>
            <el-form-item label="单次最大数量">
                <el-input v-model="addIderForm.maxAmount" clearable placeholder="不填表示不限制"
                          style="width: 90%"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="closeAddIderDialog">取消</el-button>
            <el-button type="primary" @click="addIder">提交</el-button>
        </div>
    </el-dialog>
    <el-dialog :visible.sync="modifyCurrentDialogVisible" :before-close="closeModifyCurrentDialog"
               title="修改当前周期和id" width="40%">
        <el-form ref="modifyCurrentForm" :model="modifyCurrentForm" label-width="30%">
            <el-form-item label="数据源:">
                {{ currentDataSource }}
            </el-form-item>
            <el-form-item label="id编码:">
                {{ modifyCurrentForm.ider ? modifyCurrentForm.ider.iderId : ''}}
            </el-form-item>
            <el-form-item label="新的当前周期:" prop="newCurrentPeriod"
                          :rules="[{required:modifyCurrentPeriodEnable(), message:'请输入新的当前周期', trigger:'blur'}]">
                <el-input v-model="modifyCurrentForm.newCurrentPeriod" :disabled="!modifyCurrentPeriodEnable()"
                          clearable
                          :placeholder="modifyCurrentForm.ider ? getPeriodFormat(modifyCurrentForm.ider.periodType) : ''"></el-input>
            </el-form-item>
            <el-form-item label="新的当前id:" prop="newCurrentId"
                          :rules="[{required:true, message:'请输入新的当前id', trigger:'blur'}]">
                <el-input v-model="modifyCurrentForm.newCurrentId" clearable placeholder="请输入新的当前id"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="closeModifyCurrentDialog">取消</el-button>
            <el-button type="primary" @click="modifyCurrent">提交</el-button>
        </div>
    </el-dialog>
</div>`;

const Iders = {
    template: IdersTemplate,
    data: function () {
        return {
            manager: CURRENT_MANAGER,
            dataSources: [],
            currentDataSource: null,
            queryIdersForm: {
                pageNo: 1,
                pageSize: 10,
                iderId: null
            },
            idersLoading: false,
            totalIders: 0,
            iders: [],
            addIderDialogVisible: false,
            addIderForm: {
                dataSource: null,
                iderId: null,
                iderName: null,
                periodType: 'NONE',
                maxId: null,
                maxAmount: null
            },
            modifyCurrentDialogVisible: false,
            modifyCurrentForm: {
                ider: null,
                newCurrentPeriod: null,
                newCurrentId: null
            }
        };
    },
    created: function () {
        const theThis = this;
        this.findDataSources(function (dataSources) {
            if (dataSources.length > 0) {
                theThis.currentDataSource = dataSources[0];
            }
        });
    },
    watch: {
        currentDataSource: function () {
            this.queryIders();
        }
    },
    methods: {
        findDataSources: function (callback) {
            const theThis = this;
            axios.get('../manage/dataSource/findDataSources', {params: {}})
                .then(function (result) {
                    if (!result.success) {
                        Vue.prototype.$message.error(result.message);
                        return;
                    }
                    theThis.dataSources = result.dataSources;
                    callback(result.dataSources);
                });
        },
        queryIders: function () {
            this.idersLoading = true;

            const theThis = this;
            axios.get('../manage/ider/queryManagedIders', {
                params: {
                    dataSource: this.currentDataSource,
                    pageNo: this.queryIdersForm.pageNo,
                    pageSize: this.queryIdersForm.pageSize,
                    iderId: this.queryIdersForm.iderId

                }
            }).then(function (result) {
                theThis.idersLoading = false;
                if (!result.success) {
                    Vue.prototype.$message.error(result.message);
                    return;
                }
                theThis.totalIders = result.totalCount;
                theThis.iders = result.infos;
                theThis.iders.forEach(function (ider) {
                    Vue.set(ider, 'editing', false);
                    Vue.set(ider, 'editingIderName', null);
                    Vue.set(ider, 'editingMaxId', null);
                    Vue.set(ider, 'editingMaxAmount', null);
                    Vue.set(ider, 'savePopoverShowing', false);
                });
            });
        },
        startEditing: function (ider) {
            ider.editing = true;
            ider.editingIderName = ider.iderName;
            ider.editingMaxId = ider.maxId;
            ider.editingMaxAmount = ider.maxAmount;
        },
        saveEditing: function (ider) {
            ider.savePopoverShowing = false;

            const theThis = this;
            let haveChange;
            if (ider.editingIderName !== ider.iderName) {
                haveChange = true;
                // 修改名称
                axios.post('../manage/ider/modifyIderName', {
                    dataSource: this.currentDataSource,
                    iderId: ider.iderId,
                    newIderName: ider.editingIderName
                }).then(function (result) {
                    if (!result.success) {
                        Vue.prototype.$message.error(result.message);
                        return;
                    }
                    Vue.prototype.$message.success(result.message);
                    theThis.queryIders();
                });
            }
            if (ider.editingMaxId !== ider.maxId || ider.editingMaxAmount !== ider.maxAmount) {
                haveChange = true;
                // 修改max
                axios.post('../manage/ider/modifyIderMax', {
                    dataSource: this.currentDataSource,
                    iderId: ider.iderId,
                    newMaxId: ider.editingMaxId,
                    newMaxAmount: ider.editingMaxAmount
                }).then(function (result) {
                    if (!result.success) {
                        Vue.prototype.$message.error(result.message);
                        return;
                    }
                    Vue.prototype.$message.success(result.message);
                    theThis.queryIders();
                });
            }
            if (!haveChange) {
                Vue.prototype.$message.error("无任何修改");
            }
        },
        deleteIder: function (ider) {
            const theThis = this;
            Vue.prototype.$confirm('确定删除id提供者？', '警告', {type: 'warning'})
                .then(function () {
                    axios.post('../manage/ider/deleteIder', {
                        dataSource: theThis.currentDataSource,
                        iderId: ider.iderId
                    }).then(function (result) {
                        if (!result.success) {
                            Vue.prototype.$message.error(result.message);
                            return;
                        }
                        Vue.prototype.$message.success(result.message);
                        theThis.queryIders();
                    });
                });
        },
        addIder: function () {
            const theThis = this;
            this.$refs.addIderForm.validate(function (valid) {
                if (!valid) {
                    return;
                }
                let doAddIder = function (dataSource) {
                    axios.post('../manage/ider/addIder', {
                        dataSource: dataSource,
                        iderId: theThis.addIderForm.iderId,
                        iderName: theThis.addIderForm.iderName,
                        periodType: theThis.addIderForm.periodType,
                        maxId: theThis.addIderForm.maxId,
                        maxAmount: theThis.addIderForm.maxAmount
                    }).then(function (result) {
                        if (!result.success) {
                            Vue.prototype.$message.error(result.message);
                            return;
                        }
                        Vue.prototype.$message.success(result.message);
                        theThis.closeAddIderDialog();
                        theThis.queryIders();
                    });
                }
                if (theThis.addIderForm.dataSource === '__allDataSource') {
                    for (let i = 0; i < theThis.dataSources.length; i++) {
                        doAddIder(theThis.dataSources[i]);
                    }
                } else {
                    doAddIder(theThis.addIderForm.dataSource);
                }
            });
        },
        openAddIderDialogVisible: function () {
            this.addIderForm.dataSource = this.currentDataSource;
            this.addIderForm.iderId = null;
            this.addIderForm.iderName = null;
            this.addIderForm.periodType = 'NONE';
            this.addIderForm.maxId = null;
            this.addIderForm.maxAmount = null;
            this.addIderDialogVisible = true;
        },
        closeAddIderDialog: function () {
            this.$refs.addIderForm.clearValidate();
            this.addIderDialogVisible = false;
        },
        startModifyCurrent: function (ider) {
            this.modifyCurrentDialogVisible = true;
            this.modifyCurrentForm.ider = ider;
            this.modifyCurrentForm.newCurrentPeriod = null;
            this.modifyCurrentForm.newCurrentId = null;
        },
        modifyCurrent: function () {
            const theThis = this;
            this.$refs.modifyCurrentForm.validate(function (valid) {
                if (!valid) {
                    return;
                }
                axios.post('../manage/ider/modifyIderCurrent', {
                    dataSource: theThis.currentDataSource,
                    iderId: theThis.modifyCurrentForm.ider.iderId,
                    newCurrentPeriod: theThis.modifyCurrentForm.newCurrentPeriod,
                    newCurrentId: theThis.modifyCurrentForm.newCurrentId
                }).then(function (result) {
                    if (!result.success) {
                        Vue.prototype.$message.error(result.message);
                        return;
                    }
                    Vue.prototype.$message.success(result.message);
                    theThis.closeModifyCurrentDialog();
                    theThis.queryIders();
                });
            });
        },
        closeModifyCurrentDialog: function () {
            this.$refs.modifyCurrentForm.clearValidate();
            this.modifyCurrentDialogVisible = false;
            this.modifyCurrentForm.ider = null;
            this.modifyCurrentForm.newCurrentPeriod = null;
            this.modifyCurrentForm.newCurrentId = null;
        },
        modifyCurrentPeriodEnable: function () {
            if (!this.modifyCurrentForm.ider) {
                return true;
            }
            return this.modifyCurrentForm.ider.periodType !== 'NONE';
        },
        toShowingCurrentPeriod: function (ider) {
            let format = this.getPeriodFormat(ider.periodType);
            if (!format) {
                return null;
            }
            let currentPeriod = new Date(ider.currentPeriod);
            return currentPeriod.format(format);
        },
        getPeriodFormat: function (periodType) {
            switch (periodType) {
                case 'HOUR':
                    return 'yyyyMMddhh';
                case 'DAY':
                    return 'yyyyMMdd';
                case 'MONTH':
                    return 'yyyyMM';
                case 'YEAR':
                    return 'yyyy';
                default:
                    return null;
            }
        }
    }
};