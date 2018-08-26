const IdersNormalTemplate = `
<div>
    <el-row>
        <el-col>
            <el-form :v-model="queryIdersForm" :inline="true" size="small">
                <el-form-item>
                    <el-input v-model="queryIdersForm.idCode" clearable placeholder="id编码"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="queryIders">查询</el-button>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
    <el-table :data="iders" v-loading="idersLoading" border stripe>
        <el-table-column prop="idCode" label="id编码"></el-table-column>
        <el-table-column prop="periodType" label="周期类型" width="90px">
            <template slot-scope="{ row }">
                <el-tag v-if="row.periodType === 'HOUR'" type="success">每小时</el-tag>
                <el-tag v-else-if="row.periodType === 'DAY'" type="info">每天</el-tag>
                <el-tag v-else-if="row.periodType === 'MONTH'" type="warning">每月</el-tag>
                <el-tag v-else-if="row.periodType === 'YEAR'" type="danger">每年</el-tag>
                <el-tag v-else-if="row.periodType === 'NONE'">无周期</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="maxId" label="id最大值">
            <template slot-scope="{ row }">
                <div v-if="!row.editing">
                    <span v-if="row.maxId !== null">{{ row.maxId }}</span>
                    <el-tag v-else>无限制</el-tag>
                </div>
                <el-input v-else v-model="row.editingMaxId" size="small" placeholder="请输入id最大值"></el-input>
            </template>
        </el-table-column>
        <el-table-column prop="maxAmount" label="单次获取id最大数量" width="100px">
            <template slot-scope="{ row }">
                <div v-if="!row.editing">
                    <span v-if="row.maxAmount !== null">{{ row.maxAmount }}</span>
                    <el-tag v-else>无限制</el-tag>
                </div>
                <el-input v-else v-model="row.editingMaxAmount" size="small" placeholder="请输入单次获取id最大数量"></el-input>
            </template>
        </el-table-column>
        <el-table-column prop="factor" label="生产者数量（因数）" width="100px">
            <template slot-scope="{ row }">
                <span v-if="!row.editing">{{ row.factor }}</span>
                <el-input v-else v-model="row.editingFactor" size="small" placeholder="请输入生产者数量"></el-input>
            </template>
        </el-table-column>
        <el-table-column prop="producerInfos" label="生产者" header-align="center" width="380">
            <template slot-scope="{ row }">
                <el-table :data="row.producerInfos" border stripe>
                    <el-table-column prop="index" label="序号" width="50px"></el-table-column>
                    <el-table-column prop="currentPeriod" label="当前周期">
                        <template slot-scope="{ row }">
                            <span v-if="row.currentPeriod">{{ new Date(row.currentPeriod).format('yyyy-MM-dd hh:mm') }}</span>
                            <el-tag v-else>无周期</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="currentId" label="当前id"></el-table-column>
                </el-table>
            </template>
        </el-table-column>
        <el-table-column label="操作" header-align="center" width="160px">
            <template slot-scope="{ row }">
                <el-row>
                    <el-col :span="12" style="text-align: center">
                        <el-tooltip v-if="!row.editing" content="修改" placement="top" :open-delay="1000" :hide-after="3000">
                            <el-button @click="startEditing(row)" type="primary" icon="el-icon-edit" size="small" circle></el-button>
                        </el-tooltip>
                        <template v-else>
                            <el-button-group>
                                <el-tooltip content="取消修改" placement="top" :open-delay="1000" :hide-after="3000">
                                    <el-button @click="row.editing = false" type="info" icon="el-icon-close" size="small" circle></el-button>
                                </el-tooltip>
                                <el-popover placement="top" v-model="row.savePopoverShowing">
                                    <p>确定保存修改？</p>
                                    <div style="text-align: right; margin: 0">
                                        <el-button size="mini" type="text" @click="row.savePopoverShowing = false">取消</el-button>
                                        <el-button type="primary" size="mini" @click="saveEditing(row)">确定</el-button>
                                    </div>
                                    <el-tooltip slot="reference" :disabled="row.savePopoverShowing" content="保存修改" placement="top" :open-delay="1000" :hide-after="3000">
                                        <el-button @click="row.savePopoverShowing = true" type="success" icon="el-icon-check" size="small" circle></el-button>
                                    </el-tooltip>
                                </el-popover>
                            </el-button-group>
                        </template>
                    </el-col>
                    <el-col :span="12" style="text-align: center">
                        <el-tooltip content="修改当前周期和id" placement="top" :open-delay="1000" :hide-after="3000">
                            <el-button @click="startModifyCurrent(row)" type="primary" icon="el-icon-edit" size="small" round>C</el-button>
                        </el-tooltip>
                    </el-col>
                </el-row>
            </template>
        </el-table-column>
    </el-table>
    <el-row style="margin-top: 10px">
        <el-col style="text-align: end">
            <el-pagination :total="totalIders" :current-page.sync="queryIdersForm.pageNo" :page-size.sync="queryIdersForm.pageSize" @current-change="queryIders" layout="total,prev,pager,next" small background></el-pagination>
        </el-col>
    </el-row>
    <el-dialog :visible.sync="modifyCurrentDialogVisible" :before-close="closeModifyCurrentDialog" title="修改id提供者当前周期和id" width="40%">
        <el-form ref="modifyCurrentForm" :model="modifyCurrentForm" label-width="30%">
            <el-form-item label="新的当前周期（yyyyMMddHH）" prop="newCurrentPeriod" :rules="[{required:modifyCurrentPeriodEnable(), message:'请输入新的当前周期', trigger:'blur'}]">
                <el-input v-model="modifyCurrentForm.newCurrentPeriod" :disabled="!modifyCurrentPeriodEnable()"></el-input>
            </el-form-item>
            <el-form-item label="新的当前id" prop="newCurrentId" :rules="[{required:true, message:'请输入新的当前id', trigger:'blur'}]">
                <el-input v-model="modifyCurrentForm.newCurrentId"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer">
            <el-button @click="closeModifyCurrentDialog">取消</el-button>
            <el-button type="primary" @click="modifyCurrent">提交</el-button>
        </div>
    </el-dialog>
</div>
`;

const IdersNormal = {
    template: IdersNormalTemplate,
    data: function () {
        return {
            queryIdersForm: {
                pageNo: 1,
                pageSize: 10,
                idCode: null
            },
            idersLoading: false,
            totalIders: 0,
            iders: [],
            modifyCurrentDialogVisible: false,
            modifyCurrentForm: {
                ider: null,
                newCurrentPeriod: null,
                newCurrentId: null
            }
        };
    },
    created: function () {
        this.queryIders();
    },
    methods: {
        queryIders: function () {
            this.idersLoading = true;

            const theThis = this;
            axios.get('../manage/ider/queryManagedIders', {params: this.queryIdersForm})
                .then(function (result) {
                    theThis.idersLoading = false;
                    if (!result.success) {
                        Vue.prototype.$message.error(result.message);
                    }
                    theThis.totalIders = result.totalCount;
                    theThis.iders = result.infos;
                    theThis.iders.forEach(function (ider) {
                        Vue.set(ider, 'editing', false);
                        Vue.set(ider, 'editingMaxId', null);
                        Vue.set(ider, 'editingMaxAmount', null);
                        Vue.set(ider, 'editingFactor', null);
                        Vue.set(ider, 'savePopoverShowing', false);
                    });
                });
        },
        startEditing: function (ider) {
            ider.editing = true;
            ider.editingMaxId = ider.maxId;
            ider.editingMaxAmount = ider.maxAmount;
            ider.editingFactor = ider.factor;
        },
        saveEditing: function (ider) {
            ider.savePopoverShowing = false;

            const theThis = this;
            // 修改max
            axios.post('../manage/ider/modifyMax', {
                idCode: ider.idCode,
                newMaxId: ider.editingMaxId,
                newMaxAmount: ider.editingMaxAmount
            }).then(function (result) {
                if (!result.success) {
                    Vue.prototype.$message.error(result.message);
                    return;
                }
                // 修改factor
                axios.post('../manage/ider/modifyFactor', {
                    idCode: ider.idCode,
                    newFactor: ider.editingFactor
                }).then(function (result) {
                    if (result.success) {
                        Vue.prototype.$message.success(result.message);
                    } else {
                        Vue.prototype.$message.error(result.message);
                    }
                    theThis.queryIders();
                });
            });
        },
        closeAddIderDialog: function () {
            this.addIderDialogVisible = false;
            this.addIderForm.idCode = null;
            this.addIderForm.periodType = null;
            this.addIderForm.maxId = null;
            this.addIderForm.maxAmount = null;
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
                axios.post('../manage/ider/modifyCurrent', {
                    idCode: theThis.modifyCurrentForm.ider.idCode,
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
        }
    }
};