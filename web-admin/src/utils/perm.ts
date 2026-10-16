import { useUserStore } from '@/store/user'

/**
 * 权限判断（按钮级）。
 * - 超级管理员永远放行；
 * - 未传权限标识时放行；
 * - 其余按当前用户 permissions 判断（多个为 AND）。
 *
 * 可在模板里用：`v-if="hasPerm('store-info:add')"`，或用 `v-perm="'store-info:add'"` 指令。
 */
export function hasPerm(perm?: string | string[]): boolean {
  if (!perm || (Array.isArray(perm) && perm.length === 0)) return true
  const store = useUserStore()
  if (store.isSuperAdmin) return true
  const need = Array.isArray(perm) ? perm : [perm]
  const list = store.permissions || []
  return need.every((p) => list.includes(p))
}

/** v-perm 指令：无权限时移除元素 */
export const permDirective = {
  mounted(el: HTMLElement, binding: { value?: string | string[] }) {
    if (!hasPerm(binding.value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
