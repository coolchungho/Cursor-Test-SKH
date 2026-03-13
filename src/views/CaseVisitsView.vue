<template>
  <div class="view-card">
    <div class="view-title">訪視追蹤</div>
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
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 12px"
        title="此頁目前為只讀示範，從 Encounter 讀取訪視紀錄，暫不寫入 FHIR。"
      />
      <el-timeline v-if="visits.length" reverse>
        <el-timeline-item
          v-for="v in visits"
          :key="v.id"
          :timestamp="v.period?.start"
          :type="v.status === 'finished' ? 'success' : 'primary'"
        >
          <div class="visit-item">
            <div class="visit-item__title">
              {{ v.type?.[0]?.text || v.class?.code || '不明訪視類型' }}
            </div>
            <div class="visit-item__meta">
              <span>狀態：{{ v.status }}</span>
              <span v-if="v.period?.end">結束：{{ v.period?.end }}</span>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="尚無訪視紀錄" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { useCaseVisits } from '../composables/useCaseVisits';

const props = defineProps<{
  patientId: string;
}>();

const { loading, error, visits } = useCaseVisits(props.patientId);
</script>

<style scoped>
.visit-item__title {
  font-weight: 600;
  margin-bottom: 4px;
}

.visit-item__meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}
</style>

