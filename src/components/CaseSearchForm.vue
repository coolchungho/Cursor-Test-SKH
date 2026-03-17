<template>
  <el-form :inline="true" :model="localCriteria" class="case-search-form" @submit.prevent>
    <el-form-item label="病歷號">
      <el-input v-model="localCriteria.identifier" placeholder="病歷號或 ID" clearable style="width: 140px" />
    </el-form-item>
    <el-form-item label="姓名">
      <el-input v-model="localCriteria.name" placeholder="部分姓名即可" clearable style="width: 120px" />
    </el-form-item>
    <el-form-item label="住院日起">
      <el-date-picker
        v-model="admissionStartModel"
        type="date"
        placeholder="請選擇住院日起"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 160px"
      />
    </el-form-item>
    <el-form-item label="住院日迄">
      <el-date-picker
        v-model="admissionEndModel"
        type="date"
        placeholder="請選擇住院日迄"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 160px"
      />
    </el-form-item>
    <el-form-item label="出院日起">
      <el-date-picker
        v-model="dischargeStartModel"
        type="date"
        placeholder="請選擇出院日起"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 160px"
      />
    </el-form-item>
    <el-form-item label="出院日迄">
      <el-date-picker
        v-model="dischargeEndModel"
        type="date"
        placeholder="請選擇出院日迄"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 160px"
      />
    </el-form-item>
    <el-form-item label="案件類別">
      <el-select v-model="localCriteria.caseCategory" placeholder="全部" clearable style="width: 120px">
        <el-option
          v-for="opt in caseCategoryOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="住院狀態">
      <el-select v-model="localCriteria.hospitalizationStatus" placeholder="全部" clearable style="width: 110px">
        <el-option
          v-for="opt in hospitalizationOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="收案狀態">
      <el-select v-model="localCriteria.intakeStatus" placeholder="全部" clearable style="width: 110px">
        <el-option
          v-for="opt in intakeStatusOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="回覆狀態">
      <el-select v-model="localCriteria.replyStatus" placeholder="全部" clearable style="width: 110px">
        <el-option
          v-for="opt in replyStatusOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="生日">
      <el-date-picker
        v-model="birthdateModel"
        type="date"
        placeholder="選擇生日"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 160px"
      />
    </el-form-item>
    <el-form-item label="性別">
      <el-select v-model="localCriteria.gender" placeholder="全部" clearable style="width: 100px">
        <el-option label="男" value="male" />
        <el-option label="女" value="female" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button @click="$emit('manualAdd')">手動新增</el-button>
      <el-button type="primary" :loading="loading" @click="onSearch">Q 查詢</el-button>
      <el-button @click="onReset">清除</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { computed, reactive, watch } from 'vue';
import type { CaseSearchCriteria } from '../composables/useCaseSearch';
import {
  CASE_CATEGORY_OPTIONS,
  HOSPITALIZATION_STATUS_OPTIONS,
  INTAKE_STATUS_OPTIONS,
  REPLY_STATUS_OPTIONS
} from '../constants/caseSearchOptions';

const props = defineProps<{
  modelValue: CaseSearchCriteria;
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: CaseSearchCriteria): void;
  (e: 'search'): void;
  (e: 'manualAdd'): void;
}>();

const caseCategoryOptions = CASE_CATEGORY_OPTIONS;
const hospitalizationOptions = HOSPITALIZATION_STATUS_OPTIONS;
const intakeStatusOptions = INTAKE_STATUS_OPTIONS;
const replyStatusOptions = REPLY_STATUS_OPTIONS;

const CRITERIA_KEYS: (keyof CaseSearchCriteria)[] = [
  'name',
  'identifier',
  'birthdate',
  'gender',
  'caseCategory',
  'hospitalizationStatus',
  'intakeStatus',
  'replyStatus',
  'admissionDateStart',
  'admissionDateEnd',
  'dischargeDateStart',
  'dischargeDateEnd'
];

const localCriteria = reactive<CaseSearchCriteria>({ ...props.modelValue });

watch(
  () => props.modelValue,
  (v) => {
    const newVal = v ?? {};
    CRITERIA_KEYS.forEach((k) => {
      localCriteria[k] = newVal[k] ?? undefined;
    });
  },
  { deep: true }
);

const birthdateModel = computed({
  get: () => localCriteria.birthdate || null,
  set: (val: string | null) => {
    localCriteria.birthdate = val || undefined;
  }
});

const admissionStartModel = computed({
  get: () => localCriteria.admissionDateStart || null,
  set: (val: string | null) => {
    localCriteria.admissionDateStart = val || undefined;
  }
});

const admissionEndModel = computed({
  get: () => localCriteria.admissionDateEnd || null,
  set: (val: string | null) => {
    localCriteria.admissionDateEnd = val || undefined;
  }
});

const dischargeStartModel = computed({
  get: () => localCriteria.dischargeDateStart || null,
  set: (val: string | null) => {
    localCriteria.dischargeDateStart = val || undefined;
  }
});

const dischargeEndModel = computed({
  get: () => localCriteria.dischargeDateEnd || null,
  set: (val: string | null) => {
    localCriteria.dischargeDateEnd = val || undefined;
  }
});

const syncToParent = () => {
  emit('update:modelValue', { ...localCriteria });
};

const onSearch = () => {
  syncToParent();
  emit('search');
};

const onReset = () => {
  localCriteria.name = undefined;
  localCriteria.identifier = undefined;
  localCriteria.birthdate = undefined;
  localCriteria.gender = undefined;
  localCriteria.caseCategory = undefined;
  localCriteria.hospitalizationStatus = undefined;
  localCriteria.intakeStatus = undefined;
  localCriteria.replyStatus = undefined;
  localCriteria.admissionDateStart = undefined;
  localCriteria.admissionDateEnd = undefined;
  localCriteria.dischargeDateStart = undefined;
  localCriteria.dischargeDateEnd = undefined;
  syncToParent();
};
</script>

<style scoped>
.case-search-form {
  margin-bottom: 12px;
}
</style>

