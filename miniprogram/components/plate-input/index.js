/**
 * 车牌号专用输入组件（小程序，全局统一）
 * 行政区简称下拉 + 号码输入 + 油车(蓝牌 7 位) / 新能源(绿牌 8 位) 切换，按类型校验格式。
 * 用法：<plate-input id="plate" value="{{form.plateNo}}" bindchange="onPlateChange" />
 * 校验：const r = this.selectComponent('#plate').validate()
 */
const PROVINCES = [
  '京', '津', '冀', '晋', '蒙', '辽', '吉', '黑', '沪', '苏', '浙', '皖', '闽', '赣', '鲁',
  '豫', '鄂', '湘', '粤', '桂', '琼', '渝', '川', '贵', '云', '藏', '陕', '甘', '青', '宁', '新'
]

Component({
  properties: {
    value: { type: String, value: '' },
    required: { type: Boolean, value: false }
  },
  data: {
    provinces: PROVINCES,
    provinceIndex: -1,
    province: '',
    rest: '',
    type: 'blue',
    error: ''
  },
  lifetimes: {
    attached() {
      this.parse(this.data.value || '')
    }
  },
  observers: {
    value(v) {
      if ((v || '').toUpperCase() !== this.compose()) this.parse(v || '')
    }
  },
  methods: {
    parse(v) {
      const val = (v || '').toUpperCase().replace(/\s/g, '')
      if (!val) {
        this.setData({ province: '', provinceIndex: -1, rest: '', type: 'blue', error: '' })
        return
      }
      const idx = PROVINCES.indexOf(val.charAt(0))
      const province = idx >= 0 ? val.charAt(0) : ''
      const rest = idx >= 0 ? val.slice(1) : val
      this.setData({
        province,
        provinceIndex: idx,
        rest,
        type: rest.length > 6 ? 'green' : 'blue',
        error: ''
      })
    },
    compose() {
      const r = (this.data.rest || '').toUpperCase().replace(/\s/g, '')
      return this.data.province && r ? this.data.province + r : ''
    },
    emit() {
      this.triggerEvent('change', { value: this.compose() })
    },
    onProvince(e) {
      const idx = Number(e.detail.value)
      this.setData({ provinceIndex: idx, province: PROVINCES[idx] })
      this.emit()
    },
    onInput(e) {
      const up = (e.detail.value || '').toUpperCase().replace(/\s/g, '')
      this.setData({ rest: up })
      this.emit()
    },
    onType(e) {
      const type = e.currentTarget.dataset.t
      const max = type === 'green' ? 7 : 6
      this.setData({ type, rest: (this.data.rest || '').slice(0, max) })
      this.emit()
    },
    /** 校验：蓝牌 = 简称 + 字母 + 5 位（共 7 位）；绿牌 = 简称 + 字母 + 6 位（共 8 位） */
    validate() {
      const p = this.data.province
      const r = (this.data.rest || '').toUpperCase().replace(/\s/g, '')
      let msg = ''
      if (!p || !r) {
        if (this.data.required) msg = '请完整填写车牌号（省份简称 + 号码）'
      } else {
        const len = this.data.type === 'green' ? 7 : 6
        if (!/^[A-Z][A-Z0-9]*$/.test(r)) msg = '车牌号码须以字母开头，仅含字母与数字'
        else if (r.length !== len) msg = this.data.type === 'green' ? '新能源绿牌共 8 位（简称+字母+6位）' : '蓝牌共 7 位（简称+字母+5位）'
        else if (/[IO]/.test(r)) msg = '车牌号码不含字母 I、O'
      }
      this.setData({ error: msg })
      return { ok: !msg, msg }
    }
  }
})
