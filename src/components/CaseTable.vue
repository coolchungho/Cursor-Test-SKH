<template>
  <el-table :data="rows" stripe style="width: 100%" v-loading="loading" empty-text="目前沒有資料">
    <el-table-column label="姓名" min-width="140">
      <template #default="{ row }">
        <span>{{ getDisplayName(row) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="gender" label="性別" width="80">
      <template #default="{ row }">
        <span>{{ formatGender(row.gender) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="birthDate" label="生日" width="120" />
    <el-table-column label="識別碼" min-width="160">
      <template #default="{ row }">
        <span>{{ getFirstIdentifier(row) }}</span>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="180" fixed="right">
      <template #default="{ row }">
        <el-button type="primary" link size="small" @click="$emit('viewProfile', row.id)">
          個案資料
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

const props = defineProps<{
  rows: FhirPatient[];
  loading: boolean;
  isInIntake: (patientId?: string) => boolean;
}>();

defineEmits<{
  (e: 'viewProfile', patientId?: string): void;
  (e: 'addToIntake', patient: FhirPatient): void;
  (e: 'removeFromIntake', patientId?: string): void;
}>();

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
</script>

