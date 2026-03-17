<template>
  <el-table :data="rows" stripe style="width: 100%" v-loading="loading" empty-text="目前沒有資料">
    <el-table-column label="病歷號/姓名" min-width="140">
      <template #default="{ row }">
        <div class="cell-primary">
          <span class="identifier">{{ getFirstIdentifier(row) }}</span>
          <span class="name">{{ getDisplayName(row) }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="性別" width="80">
      <template #default="{ row }">
        <span>{{ formatGender(row.gender) }}</span>
      </template>
    </el-table-column>
    <el-table-column label="住院資訊" min-width="120">
      <template #default="{ row }">
        <span>{{ getEnriched(row, 'ward', '-') }} {{ getEnriched(row, 'bed', '') }}</span>
      </template>
    </el-table-column>
    <el-table-column label="入/出院日" min-width="160">
      <template #default="{ row }">
        <span>{{ getAdmissionDischarge(row) }}</span>
      </template>
    </el-table-column>
    <el-table-column label="主治醫師" width="120">
      <template #default="{ row }">
        <span>{{ getEnriched(row, 'attendingPhysician', '-') }}</span>
      </template>
    </el-table-column>
    <el-table-column label="住院狀態" width="100">
      <template #default="{ row }">
        <el-tag size="small" :type="getHospitalizationTagType(row)">
          {{ getEnriched(row, 'hospitalizationStatus', '-') }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="案件類別" width="110">
      <template #default="{ row }">
        <el-tag size="small" :type="getCaseCategoryTagType(row)">
          {{ getEnriched(row, 'caseCategory', '-') }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="收案狀態" width="100">
      <template #default="{ row }">
        <el-tag size="small" :type="getIntakeTagType(row)">
          {{ getEnriched(row, 'intakeStatus', '-') }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="回覆狀態" width="100">
      <template #default="{ row }">
        <el-tag size="small" :type="getReplyTagType(row)">
          {{ getEnriched(row, 'replyStatus', '-') }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="固定建案" min-width="140">
      <template #default="{ row }">
        <span>{{ getEnriched(row, 'fixReason', '-') }}</span>
      </template>
    </el-table-column>
    <el-table-column label="關鍵檢驗" min-width="180">
      <template #default="{ row }">
        <span class="key-observations">{{ getEnriched(row, 'keyObservations', '-') }}</span>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="240" fixed="right">
      <template #default="{ row }">
        <el-button type="primary" link size="small" @click="$emit('viewProfile', row.id)">
          個案資料
        </el-button>
        <el-button type="info" link size="small" @click="$emit('viewVisits', row.id)">
          訪視追蹤
        </el-button>
        <el-button type="warning" link size="small" @click="$emit('editReply', row)">
          回覆/固定建案
        </el-button>
        <el-button
          v-if="!isInIntake(row.id)"
          type="success"
          link
          size="small"
          @click="$emit('addToIntake', row)"
        >
          加入收案
        </el-button>
        <el-button
          v-else
          type="danger"
          link
          size="small"
          @click="$emit('removeFromIntake', row.id)"
        >
          移出收案
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import type { FhirPatient } from '../types/fhir';
import type { CaseRowMeta } from '../types/caseList';

type EnrichedRow = FhirPatient & Partial<CaseRowMeta>;

const props = defineProps<{
  rows: EnrichedRow[];
  loading: boolean;
  isInIntake: (patientId?: string) => boolean;
}>();

defineEmits<{
  (e: 'viewProfile', patientId?: string): void;
  (e: 'viewVisits', patientId?: string): void;
  (e: 'editReply', row: EnrichedRow): void;
  (e: 'addToIntake', patient: FhirPatient): void;
  (e: 'removeFromIntake', patientId?: string): void;
}>();

function getEnriched(row: EnrichedRow, key: keyof CaseRowMeta, fallback: string): string {
  const v = row[key];
  return (v != null && v !== '') ? String(v) : fallback;
}

function getDisplayName(patient: FhirPatient): string {
  const name = patient.name?.[0];
  if (!name) return '(未提供姓名)';
  if (name.text) return name.text;
  const given = (name.given ?? []).join(' ');
  return `${name.family ?? ''} ${given}`.trim() || '(未提供姓名)';
}

function getFirstIdentifier(patient: FhirPatient): string {
  const id = patient.identifier?.[0];
  if (!id) return '';
  return id.value ?? '';
}

function formatGender(gender?: string): string {
  if (gender === 'male') return '男';
  if (gender === 'female') return '女';
  return '';
}

function getAdmissionDischarge(row: EnrichedRow): string {
  const fmt = (s: string) => s.replace('T', ' ');
  const start = row.admissionDate ? fmt(row.admissionDate) : '-';
  const end = row.dischargeDate ? fmt(row.dischargeDate) : '';
  return end ? `${start} ~ ${end}` : start;
}

function getHospitalizationTagType(row: EnrichedRow): string {
  const s = getEnriched(row, 'hospitalizationStatus', '');
  return s === '住院中' ? 'danger' : s === '已出院' ? 'success' : 'info';
}

function getCaseCategoryTagType(row: EnrichedRow): string {
  const s = getEnriched(row, 'caseCategory', '');
  if (s === '關節置換') return 'warning';
  if (s === '疼痛照護') return '';
  if (s === '乳癌') return 'danger';
  return 'info';
}

function getIntakeTagType(row: EnrichedRow): string {
  const s = getEnriched(row, 'intakeStatus', '');
  if (s === '已收案') return 'success';
  if (s === '篩選中') return 'warning';
  if (s === '不收案') return 'info';
  return '';
}

function getReplyTagType(row: EnrichedRow): string {
  return getEnriched(row, 'replyStatus', '') === '已回覆' ? 'success' : 'warning';
}
</script>

<style scoped>
.cell-primary .identifier {
  display: block;
  font-size: 0.85em;
  color: var(--el-text-color-secondary);
}
.cell-primary .name {
  font-weight: 500;
}
.key-observations {
  font-size: 0.9em;
}
</style>
