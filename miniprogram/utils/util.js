/** 常用工具 */
function formatMoney(v) {
  if (v === null || v === undefined || v === '') return '-'
  return '¥' + Number(v).toFixed(2)
}

function formatDate(v) {
  if (!v) return '-'
  return String(v).replace('T', ' ').slice(0, 16)
}

function parseImages(raw) {
  if (!raw) return []
  try {
    const arr = Array.isArray(raw) ? raw : JSON.parse(raw)
    return Array.isArray(arr) ? arr : []
  } catch (e) {
    return []
  }
}

/** 工单/预约状态 → 中文标签 */
const STATUS_TEXT = {
  0: '待确认', 1: '已确认', 2: '已到店', 3: '施工中', 4: '待质检',
  5: '待结算', 6: '已完成', 7: '已取消',
  待接车: '待接车', 待派工: '待派工', 施工中: '施工中', 待质检: '待质检',
  待结算: '待结算', 已离店: '已离店', 已评价: '已评价', 已取消: '已取消'
}

function statusText(status) {
  return STATUS_TEXT[status] || String(status === undefined || status === null ? '-' : status)
}

module.exports = { formatMoney, formatDate, parseImages, statusText }
