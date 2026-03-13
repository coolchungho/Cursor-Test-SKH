<template>
  <el-form :inline="true" :model="localCriteria" class="case-search-form" @submit.prevent>
    <el-form-item label="姓名">
      <el-input v-model="localCriteria.name" placeholder="部分姓名即可" clearable />
    </el-form-item>
    <el-form-item label="識別碼">
      <el-input v-model="localCriteria.identifier" placeholder="病歷號或 ID" clearable />
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
      <el-button type="primary" :loading="loading" @click="onSearch">搜尋</el-button>
      <el-button @click="onReset">清除</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import type { CaseSearchCriteria } from '../composables/useCaseSearch';

const props = defineProps<{
  modelValue: CaseSearchCriteria;
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: CaseSearchCriteria): void;
  (e: 'search'): void;
}>();

const localCriteria = reactive<CaseSearchCriteria>({ ...props.modelValue });

const birthdateModel = computed({
  get: () => localCriteria.birthdate || '',
  set: (val: string | null) => {
    localCriteria.birthdate = val || undefined;
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
  syncToParent();
};
</script>

<style scoped>
.case-search-form {
  margin-bottom: 12px;
}
</style>

