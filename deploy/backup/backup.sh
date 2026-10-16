#!/bin/sh
# =====================================================================
# 710号汽车之家 数据库定时备份脚本
# 每日 02:00 全量备份，保留 7 份；每周日异地备份
# =====================================================================
set -e

BACKUP_DIR="/backup-data"
KEEP=${BACKUP_KEEP:-7}
STAMP=$(date +%Y%m%d_%H%M%S)
FILE="${BACKUP_DIR}/${MYSQL_DATABASE}_${STAMP}.sql.gz"

mkdir -p "${BACKUP_DIR}"

log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"; }

run_backup() {
  log "开始备份 ${MYSQL_DATABASE} -> ${FILE}"
  mysqldump -h"${MYSQL_HOST}" -u"${MYSQL_USER}" -p"${MYSQL_PASSWORD}" \
    --single-transaction --routines --triggers --set-gtid-purged=OFF \
    "${MYSQL_DATABASE}" | gzip > "${FILE}"
  log "备份完成: $(du -h "${FILE}" | cut -f1)"

  # 保留最近 N 份
  COUNT=$(ls -1 "${BACKUP_DIR}"/${MYSQL_DATABASE}_*.sql.gz 2>/dev/null | wc -l)
  if [ "${COUNT}" -gt "${KEEP}" ]; then
    ls -1t "${BACKUP_DIR}"/${MYSQL_DATABASE}_*.sql.gz | tail -n +$((KEEP + 1)) | xargs -r rm -f
    log "已清理旧备份，当前保留 ${KEEP} 份"
  fi
}

log "备份守护进程启动（每日 02:00）"
while true; do
  NOW=$(date +%H:%M)
  if [ "${NOW}" = "02:00" ]; then
    run_backup || log "备份失败"
    sleep 61
  fi
  # 每周日 03:00 标记异地备份
  if [ "$(date +%u)" = "7" ] && [ "${NOW}" = "03:00" ]; then
    cp -f "${FILE}" "${BACKUP_DIR}/offsite_$(date +%Y%m%d).sql.gz" 2>/dev/null || true
    sleep 61
  fi
  sleep 20
done
