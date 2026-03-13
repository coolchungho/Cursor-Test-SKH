<template>
  <div class="view-card">
    <div class="view-title">個案篩選</div>
    <CaseSearchForm
      v-model="criteria"
      :loading="loading"
      @search="onSearch"
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
      :rows="patients"
      :loading="loading"
      :is-in-intake="isInIntake"
      @view-profile="goToProfile"
      @add-to-intake="addToIntake"
      @remove-from-intake="removeFromIntake"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import CaseSearchForm from '../components/CaseSearchForm.vue';
import CaseTable from '../components/CaseTable.vue';
import { useCaseSearch, type CaseSearchCriteria } from '../composables/useCaseSearch';
import { useIntakeList } from '../composables/useIntakeList';
import type { FhirPatient } from '../types/fhir';

const router = useRouter();

const criteria = ref<CaseSearchCriteria>({});
const { loading, error, patients, search } = useCaseSearch();
const { hasPatient, addPatient, removePatient } = useIntakeList();

const onSearch = () => {
  search(criteria.value);
};

const isInIntake = (patientId?: string) => {
  if (!patientId) return false;
  return hasPatient(patientId);
};

const addToIntake = (patient: FhirPatient) => {
  addPatient(patient);
};

const removeFromIntake = (patientId?: string) => {
  if (!patientId) return;
  removePatient(patientId);
};

const goToProfile = (patientId?: string) => {
  if (!patientId) return;
  router.push(`/cases/${patientId}/profile`);
};
</script>

