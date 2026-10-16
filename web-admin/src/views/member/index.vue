<template>
  <div class="page-container">
    <el-tabs v-model="tab">
      <el-tab-pane label="会员列表" name="member">
        <crud-table url="/admin/member" title="会员" :columns="columns" :form-fields="formFields" :search-fields="searchFields" :default-form="{ status: 1, points: 0, balance: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="会员等级" name="level">
        <crud-table url="/admin/member-level" title="会员等级" :columns="levelColumns" :form-fields="levelForm" :default-form="{ status: 1, minPoints: 0, discount: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="优惠券" name="coupon">
        <crud-table url="/admin/coupon" title="优惠券" :columns="couponColumns" :form-fields="couponForm" :default-form="{ status: 1, totalCount: 0, couponType: '满减' }" />
      </el-tab-pane>
      <el-tab-pane label="充赠规则" name="recharge">
        <crud-table url="/admin/recharge-rule" title="充赠规则" :columns="rechargeColumns" :form-fields="rechargeForm" :default-form="{ status: 1, giftAmount: 0, giftPoints: 0 }" />
      </el-tab-pane>
      <el-tab-pane label="养护套餐" name="package">
        <crud-table url="/admin/maintenance-package" title="养护套餐" :columns="packageColumns" :form-fields="packageForm" :default-form="{ status: 1 }" />
      </el-tab-pane>
      <el-tab-pane label="营销活动" name="activity">
        <crud-table url="/admin/marketing-activity" title="营销活动" :columns="activityColumns" :form-fields="activityForm" :default-form="{ status: 1, activityType: '节日' }" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CrudTable from '@/components/CrudTable.vue'

const tab = ref('member')

const columns = [
  { prop: 'nickname', label: '昵称', minWidth: 120 },
  { prop: 'realName', label: '姓名', width: 100 },
  { prop: 'phone', label: '手机(加密)', width: 140 },
  { prop: 'points', label: '积分', width: 80 },
  { prop: 'balance', label: '储值余额', width: 110, money: true },
  { prop: 'source', label: '来源', width: 90 },
  { prop: 'status', label: '状态', width: 70 }
]
const formFields = [
  { prop: 'nickname', label: '昵称' },
  { prop: 'realName', label: '姓名' },
  { prop: 'phone', label: '手机号' },
  { prop: 'gender', label: '性别', type: 'select', options: [{ label: '未知', value: 0 }, { label: '男', value: 1 }, { label: '女', value: 2 }] },
  { prop: 'points', label: '积分', type: 'number' },
  { prop: 'balance', label: '储值余额', type: 'number' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24 },
  { prop: 'status', label: '正常', type: 'switch' }
]
const searchFields = [{ prop: 'nicknameLike', label: '昵称' }, { prop: 'source', label: '来源', options: ['mp', 'miniapp', 'h5'].map((v) => ({ label: v, value: v })) }]

const levelColumns = [{ prop: 'levelName', label: '等级' }, { prop: 'levelCode', label: '编码' }, { prop: 'minPoints', label: '所需积分' }, { prop: 'discount', label: '折扣' }, { prop: 'status', label: '状态', width: 80 }]
const levelForm = [{ prop: 'levelName', label: '等级名称' }, { prop: 'levelCode', label: '编码' }, { prop: 'minPoints', label: '最低积分', type: 'number' }, { prop: 'discount', label: '折扣率', type: 'number' }, { prop: 'sort', label: '排序', type: 'number' }, { prop: 'status', label: '启用', type: 'switch' }]

const couponColumns = [
  { prop: 'couponName', label: '名称' }, { prop: 'couponType', label: '类型' },
  { prop: 'faceValue', label: '面额', money: true }, { prop: 'minAmount', label: '门槛', money: true },
  { prop: 'totalCount', label: '发行' }, { prop: 'receivedCount', label: '已领' }, { prop: 'usedCount', label: '已用' }, { prop: 'status', label: '状态', width: 80 }
]
const couponForm = [
  { prop: 'couponName', label: '优惠券名称' },
  { prop: 'couponType', label: '类型', type: 'select', options: ['满减', '折扣', '工时抵扣', '洗车券'].map((v) => ({ label: v, value: v })) },
  { prop: 'faceValue', label: '面额', type: 'number' },
  { prop: 'minAmount', label: '使用门槛', type: 'number' },
  { prop: 'discount', label: '折扣率', type: 'number' },
  { prop: 'totalCount', label: '发行总量', type: 'number' },
  { prop: 'validType', label: '有效期类型', type: 'select', options: [{ label: '固定日期', value: '固定' }, { label: '领取后N天', value: '领取后N天' }] },
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

const activityColumns = [{ prop: 'activityName', label: '活动' }, { prop: 'activityType', label: '类型' }, { prop: 'startTime', label: '开始' }, { prop: 'endTime', label: '结束' }, { prop: 'status', label: '状态', width: 80 }]
const activityForm = [
  { prop: 'activityName', label: '活动名称' },
  { prop: 'activityType', label: '类型', type: 'select', options: ['拼团', '节日', '充值赠', '新人'].map((v) => ({ label: v, value: v })) },
  { prop: 'startTime', label: '开始时间' }, { prop: 'endTime', label: '结束时间' },
  { prop: 'cover', label: '封面', type: 'image', span: 24 },
  { prop: 'description', label: '说明', type: 'textarea', span: 24 },
  { prop: 'status', label: '进行中', type: 'switch' }
]
</script>
