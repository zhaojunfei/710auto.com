<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="会员列表" name="member">
        <crud-table url="/admin/member" title="会员" :columns="columns" :form-fields="formFields" :search-fields="searchFields" :default-form="{ status: 1, points: 0, balance: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="会员车辆" name="vehicle">
        <crud-table url="/admin/member-vehicle" title="会员车辆" :columns="vehicleColumns" :form-fields="vehicleForm" :search-fields="[{ prop: 'memberId', label: '会员', lookup: memberSource }, { prop: 'plateNoLike', label: '车牌' }]" :default-form="{ isDefault: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="会员等级" name="level">
        <crud-table url="/admin/member-level" title="会员等级" :columns="levelColumns" :form-fields="levelForm" :default-form="{ status: 1, minPoints: 0, discount: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="优惠券" name="coupon">
        <crud-table url="/admin/coupon" title="优惠券" :columns="couponColumns" :form-fields="couponForm" :default-form="{ status: 1, totalCount: 0, couponType: '满减' }" />
      </el-tab-pane>
      <el-tab-pane label="已领优惠券" name="memberCoupon">
        <crud-table url="/admin/member-coupon" title="已领优惠券" :can-add="false" :can-edit="false" :columns="memberCouponColumns" :search-fields="[{ prop: 'memberId', label: '会员', lookup: memberSource }, { prop: 'status', label: '状态', dict: 'member_coupon_status' }]" />
      </el-tab-pane>
      <el-tab-pane label="充赠规则" name="recharge">
        <crud-table url="/admin/recharge-rule" title="充赠规则" :columns="rechargeColumns" :form-fields="rechargeForm" :default-form="{ status: 1, giftAmount: 0, giftPoints: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="养护套餐" name="package">
        <crud-table url="/admin/maintenance-package" title="养护套餐" :columns="packageColumns" :form-fields="packageForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="已购套餐" name="memberPackage">
        <crud-table url="/admin/member-package" title="已购套餐" :columns="memberPackageColumns" :form-fields="memberPackageForm" :search-fields="[{ prop: 'memberId', label: '会员', lookup: memberSource }]" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="营销活动" name="activity">
        <crud-table url="/admin/marketing-activity" title="营销活动" :columns="activityColumns" :form-fields="activityForm" :default-form="{ status: 1, activityType: '节日' }" />
      </el-tab-pane>
      <el-tab-pane label="积分流水" name="pointsLog">
        <crud-table url="/admin/member-points-log" title="积分流水" :can-add="false" :can-edit="false" :can-delete="false" :columns="pointsLogColumns" :search-fields="[{ prop: 'memberId', label: '会员', lookup: memberSource }, { prop: 'type', label: '类型', dict: 'points_type' }]" />
      </el-tab-pane>
      <el-tab-pane label="储值流水" name="balanceLog">
        <crud-table url="/admin/member-balance-log" title="储值流水" :can-add="false" :can-edit="false" :can-delete="false" :columns="balanceLogColumns" :search-fields="[{ prop: 'memberId', label: '会员', lookup: memberSource }, { prop: 'type', label: '类型', dict: 'balance_type' }]" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('member')

/** 会员下拉（业务界面只显示会员名称，不暴露会员ID） */
const memberSource = { url: '/admin/member', label: 'nickname', value: 'id' }
/** 会员等级下拉 */
const levelSource = { url: '/admin/member-level', label: 'levelName', value: 'id' }
/** 套餐下拉 */
const packageSource = { url: '/admin/maintenance-package', label: 'packageName', value: 'id' }
/** 优惠券下拉 */
const couponSource = { url: '/admin/coupon', label: 'couponName', value: 'id' }

const columns = [
  { prop: 'nickname', label: '昵称', minWidth: 120 },
  { prop: 'realName', label: '姓名', width: 100 },
  { prop: 'phone', label: '手机号', width: 140 },
  { prop: 'points', label: '积分', width: 80 },
  { prop: 'balance', label: '储值余额', width: 110, money: true },
  { prop: 'levelId', label: '会员等级', lookup: levelSource, width: 110 },
  { prop: 'source', label: '来源', dict: 'member_source', width: 100 },
  { prop: 'status', label: '状态', width: 70 }
]
const formFields = [
  { prop: 'nickname', label: '昵称' },
  { prop: 'realName', label: '姓名' },
  { prop: 'phone', label: '手机号' },
  { prop: 'gender', label: '性别', type: 'select', dict: 'gender' },
  { prop: 'birthday', label: '生日', type: 'date' },
  { prop: 'levelId', label: '会员等级', type: 'select', source: levelSource },
  { prop: 'source', label: '来源', type: 'select', dict: 'member_source' },
  { prop: 'points', label: '积分', type: 'number' },
  { prop: 'balance', label: '储值余额', type: 'number' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 },
  { prop: 'status', label: '正常', type: 'switch' }
]
const searchFields = [
  { prop: 'nicknameLike', label: '昵称' },
  { prop: 'realNameLike', label: '姓名' },
  { prop: 'source', label: '来源', dict: 'member_source' },
  { prop: 'status', label: '状态', options: [{ label: '正常', value: 1 }, { label: '禁用', value: 0 }] }
]

/* ---------- 会员车辆 ---------- */
const vehicleColumns = [
  { prop: 'memberId', label: '会员', lookup: memberSource, width: 130 },
  { prop: 'plateNo', label: '车牌', width: 120 },
  { prop: 'brand', label: '品牌', width: 100 },
  { prop: 'model', label: '车型', minWidth: 130 },
  { prop: 'vin', label: '车架号', minWidth: 150 },
  { prop: 'mileage', label: '里程', width: 90 },
  { prop: 'buyDate', label: '购车日期', width: 120 },
  { prop: 'isDefault', label: '默认', width: 70 }
]
const vehicleForm = [
  { prop: 'memberId', label: '所属会员', type: 'select', source: memberSource },
  { prop: 'plateNo', label: '车牌号' },
  { prop: 'plateColor', label: '车牌颜色' },
  { prop: 'brand', label: '品牌' },
  { prop: 'model', label: '车型' },
  { prop: 'vin', label: '车架号VIN' },
  { prop: 'engineNo', label: '发动机号' },
  { prop: 'mileage', label: '当前里程', type: 'number' },
  { prop: 'buyDate', label: '购车日期', type: 'date' },
  { prop: 'isDefault', label: '默认车辆', type: 'switch' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 }
]

/* ---------- 已领优惠券 ---------- */
const memberCouponColumns = [
  { prop: 'memberId', label: '会员', lookup: memberSource, width: 130 },
  { prop: 'couponId', label: '优惠券', lookup: couponSource, width: 150 },
  { prop: 'couponCode', label: '券码', minWidth: 160 },
  { prop: 'status', label: '状态', dict: 'member_coupon_status', width: 100 },
  { prop: 'receiveTime', label: '领取时间', width: 170 },
  { prop: 'useTime', label: '使用时间', width: 170 },
  { prop: 'expireTime', label: '过期时间', width: 170 }
]

const levelColumns = [{ prop: 'levelName', label: '等级' }, { prop: 'levelCode', label: '编码' }, { prop: 'minPoints', label: '所需积分' }, { prop: 'discount', label: '折扣' }, { prop: 'status', label: '状态', width: 80 }]
const levelForm = [{ prop: 'levelName', label: '等级名称' }, { prop: 'levelCode', label: '编码' }, { prop: 'minPoints', label: '最低积分', type: 'number' }, { prop: 'discount', label: '折扣率', type: 'number' }, { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }]

const couponColumns = [
  { prop: 'couponName', label: '名称' }, { prop: 'couponType', label: '类型', dict: 'coupon_type' },
  { prop: 'faceValue', label: '面额', money: true }, { prop: 'minAmount', label: '门槛', money: true },
  { prop: 'totalCount', label: '发行' }, { prop: 'receivedCount', label: '已领' }, { prop: 'usedCount', label: '已用' }, { prop: 'status', label: '状态', width: 80 }
]
const couponForm = [
  { prop: 'couponName', label: '优惠券名称' },
  { prop: 'couponType', label: '类型', type: 'select', dict: 'coupon_type' },
  { prop: 'faceValue', label: '面额', type: 'number' },
  { prop: 'minAmount', label: '使用门槛', type: 'number' },
  { prop: 'discount', label: '折扣率', type: 'number' },
  { prop: 'totalCount', label: '发行总量', type: 'number' },
  { prop: 'validType', label: '有效期类型', type: 'select', dict: 'coupon_valid_type' },
  { prop: 'validDays', label: '有效天数', type: 'number' },
  { prop: 'scope', label: '适用范围' },
  { prop: 'description', label: '使用说明', span: 24 },
  { prop: 'status', label: '启用', type: 'switch' }
]

const rechargeColumns = [{ prop: 'ruleName', label: '规则' }, { prop: 'rechargeAmount', label: '充值', money: true }, { prop: 'giftAmount', label: '赠送', money: true }, { prop: 'giftPoints', label: '赠积分' }, { prop: 'status', label: '状态', width: 80 }]
const rechargeForm = [{ prop: 'ruleName', label: '规则名称' }, { prop: 'rechargeAmount', label: '充值金额', type: 'number' }, { prop: 'giftAmount', label: '赠送金额', type: 'number' }, { prop: 'giftPoints', label: '赠送积分', type: 'number' }, { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }]

const packageColumns = [{ prop: 'packageName', label: '套餐' }, { prop: 'price', label: '套餐价', money: true }, { prop: 'originalPrice', label: '原价', money: true }, { prop: 'times', label: '次数' }, { prop: 'validDays', label: '有效天数' }, { prop: 'status', label: '状态', width: 80 }]
const packageForm = [
  { prop: 'packageName', label: '套餐名称' }, { prop: 'packageNameEn', label: '英文名称' },
  { prop: 'price', label: '套餐价', type: 'number' }, { prop: 'originalPrice', label: '原价', type: 'number' },
  { prop: 'times', label: '包含次数', type: 'number' }, { prop: 'validDays', label: '有效天数', type: 'number' },
  { prop: 'cover', label: '封面', type: 'image', span: 24 },
  { prop: 'description', label: '说明', type: 'richtext', span: 24 },
  { prop: 'status', label: '上架', type: 'switch' }
]

/* ---------- 已购套餐 ---------- */
const memberPackageColumns = [
  { prop: 'memberId', label: '会员', lookup: memberSource, width: 130 },
  { prop: 'packageName', label: '套餐', minWidth: 140 },
  { prop: 'totalTimes', label: '总次数', width: 90 },
  { prop: 'usedTimes', label: '已用', width: 80 },
  { prop: 'remainTimes', label: '剩余', width: 80 },
  { prop: 'buyTime', label: '购买时间', width: 170 },
  { prop: 'expireTime', label: '到期时间', width: 170 },
  { prop: 'status', label: '有效', width: 70 }
]
const memberPackageForm = [
  { prop: 'memberId', label: '所属会员', type: 'select', source: memberSource },
  { prop: 'packageId', label: '套餐', type: 'select', source: packageSource },
  { prop: 'packageName', label: '套餐名称' },
  { prop: 'totalTimes', label: '总次数', type: 'number' },
  { prop: 'usedTimes', label: '已用次数', type: 'number' },
  { prop: 'remainTimes', label: '剩余次数', type: 'number' },
  { prop: 'buyTime', label: '购买时间', type: 'datetime', span: 24 },
  { prop: 'expireTime', label: '到期时间', type: 'datetime', span: 24 },
  { prop: 'status', label: '有效', type: 'switch' }
]

const activityColumns = [{ prop: 'activityName', label: '活动' }, { prop: 'activityType', label: '类型', dict: 'activity_type' }, { prop: 'startTime', label: '开始', width: 170 }, { prop: 'endTime', label: '结束', width: 170 }, { prop: 'status', label: '状态', width: 80 }]
const activityForm = [
  { prop: 'activityName', label: '活动名称' },
  { prop: 'activityType', label: '类型', type: 'select', dict: 'activity_type' },
  { prop: 'startTime', label: '开始时间', type: 'datetime' }, { prop: 'endTime', label: '结束时间', type: 'datetime' },
  { prop: 'cover', label: '封面', type: 'image', span: 24 },
  { prop: 'description', label: '说明', type: 'textarea', span: 24 },
  { prop: 'status', label: '进行中', type: 'switch' }
]

/* ---------- 积分 / 储值流水 ---------- */
const pointsLogColumns = [
  { prop: 'memberId', label: '会员', lookup: memberSource, width: 130 },
  { prop: 'changePoints', label: '变动积分', width: 100 },
  { prop: 'balancePoints', label: '变动后', width: 100 },
  { prop: 'type', label: '类型', dict: 'points_type', width: 90 },
  { prop: 'source', label: '来源', width: 110 },
  { prop: 'bizNo', label: '关联单号', minWidth: 150 },
  { prop: 'createTime', label: '时间', width: 170 }
]
const balanceLogColumns = [
  { prop: 'memberId', label: '会员', lookup: memberSource, width: 130 },
  { prop: 'changeAmount', label: '变动金额', width: 110, money: true },
  { prop: 'balanceAmount', label: '变动后余额', width: 120, money: true },
  { prop: 'type', label: '类型', dict: 'balance_type', width: 90 },
  { prop: 'bizNo', label: '关联单号', minWidth: 150 },
  { prop: 'remark', label: '备注', minWidth: 140 },
  { prop: 'createTime', label: '时间', width: 170 }
]
</script>
