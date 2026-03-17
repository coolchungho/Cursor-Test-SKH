<template>
  <el-dialog
    v-model="visible"
    title="回覆 / 固定建案"
    width="480px"
    destroy-on-close
    @close="onClose"
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="回覆備註">
        <el-input v-model="form.replyNote" type="textarea" :rows="3" placeholder="請輸入回覆備註" />
      </el-form-item>
      <el-form-item label="回覆時間">
        <el-date-picker
          v-model="form.replyTime"
          type="datetime"
          placeholder="選擇回覆時間"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="固定建案原因">
        <el-input v-model="form.fixReason" type="textarea" :rows="2" placeholder="請輸入固定建案原因" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="onSave">儲存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import type { FhirPatient } from '../types/fhir';
import type { CaseRowMeta } from '../types/caseList';

type EnrichedRow = FhirPatient & Partial<CaseRowMeta>;

const props = defineProps<{
  modelValue: boolean;
  row: EnrichedRow | null;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', v: boolean): void;
  (e: 'saved', patientId: string, data: { replyNote?: string; replyTime?: string; fixReason?: string }): void;
}>();

const visible = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => { visible.value = v; }
);
watch(visible, (v) => emit('update:modelValue', v));

const form = reactive({
  replyNote: '',
  replyTime: '',
  fixReason: ''
});

watch(
  () => props.row,
  (r) => {
    if (r) {
      form.replyNote = r.replyNote ?? '';
      form.replyTime = r.lastUpdated ?? '';
      form.fixReason = r.fixReason ?? '';
    }
  },
  { immediate: true }
);

function onClose() {
  visible.value = false;
  emit('update:modelValue', false);
}

function onSave() {
  if (!props.row?.id) return;
  emit('saved', props.row.id, {
    replyNote: form.replyNote?.trim() || undefined,
    replyTime: form.replyTime || undefined,
    fixReason: form.fixReason?.trim() || undefined
  });
  onClose();
}
</script>
