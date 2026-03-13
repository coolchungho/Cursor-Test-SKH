<template>
  <div class="view-card">
    <div class="view-title">個案資料管理</div>
    <el-skeleton v-if="loading" animated :rows="4" />
    <template v-else>
      <el-alert
        v-if="error"
        type="error"
        :closable="false"
        show-icon
        style="margin-bottom: 12px"
        :title="error"
      />
      <div v-if="patient" class="case-profile-layout">
        <el-card class="case-profile-card basic">
          <template #header>
            <span>基本資料</span>
          </template>
          <div class="case-profile-basic-row">
            <span class="label">姓名</span>
            <span class="value">{{ getDisplayName(patient) }}</span>
          </div>
          <div class="case-profile-basic-row">
            <span class="label">性別</span>
            <span class="value">{{ formatGender(patient.gender) }}</span>
          </div>
          <div class="case-profile-basic-row">
            <span class="label">生日</span>
            <span class="value">{{ patient.birthDate }}</span>
          </div>
          <div class="case-profile-basic-row">
            <span class="label">識別碼</span>
            <span class="value">{{ getFirstIdentifier(patient) }}</span>
          </div>
        </el-card>

        <el-card class="case-profile-card">
          <template #header>
            <span>最近觀察值（Observation）</span>
          </template>
          <el-table
            :data="observations"
            stripe
            size="small"
            empty-text="尚無觀察資料"
          >
            <el-table-column label="項目" min-width="180">
              <template #default="{ row }">
                {{ row.code?.text }}
              </template>
            </el-table-column>
            <el-table-column label="數值" min-width="140">
              <template #default="{ row }">
                <span v-if="row.valueQuantity">
                  {{ row.valueQuantity.value }} {{ row.valueQuantity.unit }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="時間" min-width="200">
              <template #default="{ row }">
                {{ row.effectiveDateTime }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="case-profile-card">
          <template #header>
            <span>最近就醫紀錄（Encounter）</span>
          </template>
          <el-table
            :data="encounters"
            stripe
            size="small"
            empty-text="尚無就醫紀錄"
          >
            <el-table-column label="類型" min-width="160">
              <template #default="{ row }">
                {{ row.type?.[0]?.text || row.class?.code }}
              </template>
            </el-table-column>
            <el-table-column label="狀態" width="120">
              <template #default="{ row }">
                {{ row.status }}
              </template>
            </el-table-column>
            <el-table-column label="起訖時間" min-width="220">
              <template #default="{ row }">
                <span>{{ row.period?.start }}</span>
                <span v-if="row.period?.end"> ~ {{ row.period?.end }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <div v-else>
        <el-empty description="找不到此個案資料" />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { useCaseDetail } from '../composables/useCaseDetail';
import type { FhirPatient } from '../types/fhir';

const props = defineProps<{
  patientId: string;
}>();

const { loading, error, patient, observations, encounters } = useCaseDetail(props.patientId);

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

<style scoped>
.case-profile-layout {
  display: grid;
  grid-template-columns: minmax(260px, 320px) minmax(0, 1.5fr);
  gap: 16px;
}

.case-profile-card.basic {
  align-self: flex-start;
}

.case-profile-basic-row {
  display: flex;
  margin-bottom: 4px;
}

.case-profile-basic-row .label {
  width: 64px;
  color: #6b7280;
}

.case-profile-basic-row .value {
  flex: 1;
}
</style>

