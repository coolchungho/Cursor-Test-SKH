<template>
  <div class="view-card">
    <div class="view-title">收案清單</div>
    <el-alert
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 12px"
      title="此清單為雛型示範，只儲存在瀏覽器 localStorage，不會寫入 FHIR Server。"
    />
    <el-table :data="items" stripe style="width: 100%" empty-text="目前沒有收案個案">
      <el-table-column label="姓名" min-width="160">
        <template #default="{ row }">
          <span>{{ getDisplayName(row.patient) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="識別碼" min-width="160">
        <template #default="{ row }">
          <span>{{ getFirstIdentifier(row.patient) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="addedAt" label="收案時間" width="200">
        <template #default="{ row }">
          <span>{{ formatDateTime(row.addedAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="goToProfile(row.patientId)">
            個案資料
          </el-button>
          <el-button type="primary" link size="small" @click="goToVisits(row.patientId)">
            訪視追蹤
          </el-button>
          <el-button type="danger" link size="small" @click="remove(row.patientId)">
            移出收案
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useIntakeList } from '../composables/useIntakeList';
import type { FhirPatient } from '../types/fhir';

const router = useRouter();
const { items, removePatient } = useIntakeList();

const remove = (patientId: string) => {
  removePatient(patientId);
};

const goToProfile = (patientId: string) => {
  router.push(`/cases/${patientId}/profile`);
};

const goToVisits = (patientId: string) => {
  router.push(`/cases/${patientId}/visits`);
};

function getDisplayName(patient?: FhirPatient): string {
  if (!patient) return '(未提供姓名)';
  const name = patient.name?.[0];
  if (!name) return '(未提供姓名)';
  if (name.text) return name.text;
  const given = (name.given ?? []).join(' ');
  return `${name.family ?? ''} ${given}`.trim() || '(未提供姓名)';
}

function getFirstIdentifier(patient?: FhirPatient): string {
  const id = patient?.identifier?.[0];
  if (!id) return '';
  return id.value ?? '';
}

function formatDateTime(iso: string): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(
    d.getDate()
  ).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(
    d.getMinutes()
  ).padStart(2, '0')}`;
}
</script>

