/** 個案篩選條件選項（案件類別、住院狀態、收案狀態、回覆狀態） */
export const CASE_CATEGORY_OPTIONS = [
  { label: '全部', value: '' },
  { label: '關節置換', value: '關節置換' },
  { label: '疼痛照護', value: '疼痛照護' },
  { label: '乳癌', value: '乳癌' }
] as const;

export const HOSPITALIZATION_STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '住院中', value: '住院中' },
  { label: '已出院', value: '已出院' }
] as const;

export const INTAKE_STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '已收案', value: '已收案' },
  { label: '篩選中', value: '篩選中' },
  { label: '不收案', value: '不收案' }
] as const;

export const REPLY_STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '已回覆', value: '已回覆' },
  { label: '未回覆', value: '未回覆' }
] as const;
