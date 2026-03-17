<template>
  <div class="view-card">
    <div class="view-title">個案篩選</div>

    <el-tabs v-model="activeTab" class="case-search-tabs" @tab-change="onTabChange">
      <el-tab-pane label="近期紀錄" name="recent" />
      <el-tab-pane label="待處理回覆" name="pending-reply" />
      <el-tab-pane label="已收案追蹤" name="tracking" />
    </el-tabs>

    <CaseSearchForm
      v-model="criteria"
      :loading="loading"
      @search="onSearch"
      @manual-add="onManualAdd"
    />
    <el-alert
      v-if="error"
      type="error"
      :closable="false"
      show-icon
      style="margin-bottom: 12px"
      :title="error"
    />
    <CaseTable
      :rows="filteredRows"
      :loading="loading"
      :is-in-intake="isInIntake"
      @view-profile="goToProfile"
      @view-visits="goToVisits"
      @edit-reply="openReplyDialog"
      @add-to-intake="addToIntake"
      @remove-from-intake="removeFromIntake"
    />

    <CaseReplyDialog
      v-model="replyDialogVisible"
      :row="replyDialogRow"
      @saved="onReplySaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import CaseSearchForm from '../components/CaseSearchForm.vue';
import CaseTable from '../components/CaseTable.vue';
import CaseReplyDialog from '../components/CaseReplyDialog.vue';
import { useCaseSearch, type CaseSearchCriteria } from '../composables/useCaseSearch';
import { useIntakeList } from '../composables/useIntakeList';
import { useCaseListMeta } from '../composables/useCaseListMeta';
import type { FhirPatient } from '../types/fhir';
import type { CaseRowMeta } from '../types/caseList';

type EnrichedRow = FhirPatient & Partial<CaseRowMeta>;

const router = useRouter();

const criteria = ref<CaseSearchCriteria>({});
const activeTab = ref<'recent' | 'pending-reply' | 'tracking'>('recent');

const { loading, error, patients, search } = useCaseSearch();
const { hasPatient, addPatient, removePatient } = useIntakeList();
const { metaMap, getEnrichedRows, setMeta } = useCaseListMeta();

const replyDialogVisible = ref(false);
const replyDialogRow = ref<EnrichedRow | null>(null);

const enrichedRows = computed(() => {
  void metaMap.value; /* 依賴 meta 變更以觸發重新計算 */
  return getEnrichedRows(patients.value);
});

/** 將日期字串（支援 ISO 或 "YYYY-MM-DD HH:mm"）轉為 timestamp，無效則回傳 0 */
function parseDateToTime(str: string | undefined): number {
  if (!str?.trim()) return 0;
  const normalized = str.includes(' ') ? str.replace(' ', 'T') : str;
  const ts = new Date(normalized).getTime();
  return Number.isNaN(ts) ? 0 : ts;
}

const filteredRows = computed(() => {
  const list = enrichedRows.value;
  const c = criteria.value;

  return list.filter((row) => {
    if (c.caseCategory && row.caseCategory !== c.caseCategory) return false;
    if (c.hospitalizationStatus && row.hospitalizationStatus !== c.hospitalizationStatus) return false;
    if (c.intakeStatus && row.intakeStatus !== c.intakeStatus) return false;
    if (c.replyStatus && row.replyStatus !== c.replyStatus) return false;

    const adm = parseDateToTime(row.admissionDate);
    const admStart = parseDateToTime(c.admissionDateStart);
    const admEnd = c.admissionDateEnd ? parseDateToTime(c.admissionDateEnd + 'T23:59:59') : 0;
    if (admStart && adm !== 0 && adm < admStart) return false;
    if (admEnd && adm !== 0 && adm > admEnd) return false;

    const dis = parseDateToTime(row.dischargeDate);
    const disStart = parseDateToTime(c.dischargeDateStart);
    const disEnd = c.dischargeDateEnd ? parseDateToTime(c.dischargeDateEnd + 'T23:59:59') : 0;
    if (disStart && dis !== 0 && dis < disStart) return false;
    if (disEnd && dis !== 0 && dis > disEnd) return false;

    return true;
  });
});

const TAB_PRESETS: Record<string, Partial<CaseSearchCriteria>> = {
  recent: {},
  'pending-reply': { replyStatus: '未回覆' },
  tracking: { intakeStatus: '已收案' }
};

function onTabChange(name: string) {
  const preset = TAB_PRESETS[name] ?? {};
  criteria.value = { ...preset };
  onSearch();
}

function onSearch() {
  search(criteria.value);
}

const isInIntake = (patientId?: string) => {
  if (!patientId) return false;
  return hasPatient(patientId);
};

const addToIntake = (patient: FhirPatient) => {
  addPatient(patient);
  if (patient.id) setMeta(patient.id, { intakeStatus: '已收案' });
};

const removeFromIntake = (patientId?: string) => {
  if (!patientId) return;
  removePatient(patientId);
  setMeta(patientId, { intakeStatus: '篩選中' });
};

const goToProfile = (patientId?: string) => {
  if (!patientId) return;
  router.push(`/cases/${patientId}/profile`);
};

const goToVisits = (patientId?: string) => {
  if (!patientId) return;
  router.push(`/cases/${patientId}/visits`);
};

function onManualAdd() {
  ElMessage.info('POC 階段：手動新增功能尚未實作，可從 FHIR 查詢結果加入收案。');
}

function openReplyDialog(row: EnrichedRow) {
  replyDialogRow.value = row;
  replyDialogVisible.value = true;
}

function onReplySaved(
  patientId: string,
  data: { replyNote?: string; replyTime?: string; fixReason?: string }
) {
  setMeta(patientId, {
    fixReason: data.fixReason,
    replyNote: data.replyNote,
    lastUpdated: data.replyTime,
    replyStatus: (data.replyNote || data.replyTime) ? '已回覆' : undefined
  });
}
</script>

<style scoped>
.case-search-tabs {
  margin-bottom: 12px;
}
.case-search-tabs :deep(.el-tabs__header) {
  margin-bottom: 8px;
}
</style>

