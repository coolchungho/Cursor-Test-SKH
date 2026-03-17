import { ref, computed } from 'vue';
import type { CaseRowMeta } from '../types/caseList';
import type { FhirPatient } from '../types/fhir';
import { useIntakeList } from './useIntakeList';
import {
  CASE_CATEGORY_OPTIONS,
  HOSPITALIZATION_STATUS_OPTIONS,
  INTAKE_STATUS_OPTIONS,
  REPLY_STATUS_OPTIONS
} from '../constants/caseSearchOptions';

const STORAGE_KEY = 'case-list-meta';

type MetaMap = Record<string, CaseRowMeta>;

function loadFromStorage(): MetaMap {
  if (typeof window === 'undefined') return {};
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as MetaMap;
    return typeof parsed === 'object' && parsed !== null ? parsed : {};
  } catch {
    return {};
  }
}

function saveToStorage(map: MetaMap) {
  if (typeof window === 'undefined') return;
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(map));
}

/** 依 patientId 產生穩定 mock 值（POC 用） */
function getMockMeta(patientId: string): CaseRowMeta {
  const hash = (patientId || '').split('').reduce((a, c) => a + c.charCodeAt(0), 0);
  const categories = CASE_CATEGORY_OPTIONS.filter((o) => o.value).map((o) => o.value);
  const intakeOpts = INTAKE_STATUS_OPTIONS.filter((o) => o.value).map((o) => o.value);
  const replyOpts = REPLY_STATUS_OPTIONS.filter((o) => o.value).map((o) => o.value);
  const ward = ['心臟內科', '骨科', '腫瘤科'][hash % 3];
  const bed = `${Math.floor((hash % 9) + 1)}0${(hash % 10) + 1}`;
  const daysAgo = (hash % 14) + 1;
  const start = new Date();
  start.setDate(start.getDate() - daysAgo);
  const end = hash % 3 === 0 ? new Date() : null;
  return {
    caseCategory: categories[hash % categories.length],
    hospitalizationStatus: end ? '已出院' : '住院中',
    intakeStatus: intakeOpts[hash % intakeOpts.length],
    replyStatus: replyOpts[hash % replyOpts.length],
    ward,
    bed: `${ward}/${bed}`,
    diagnosis: `${ward}相關診斷`,
    /** ISO 8601 格式供 new Date() 正確解析 */
    admissionDate: start.toISOString().slice(0, 16),
    dischargeDate: end ? end.toISOString().slice(0, 16) : undefined,
    attendingPhysician: `醫師${(hash % 100).toString().padStart(3, '0')}`,
    fixReason: hash % 4 === 0 ? '心律不整 PAC 頻發' : undefined,
    keyObservations: `Troponin I: ${((hash % 50) / 10).toFixed(4)} ng/mL`,
    lastUpdated: new Date().toISOString().slice(0, 16)
  };
}

const metaMap = ref<MetaMap>(loadFromStorage());

export function useCaseListMeta() {
  const { hasPatient } = useIntakeList();

  const getMeta = (patient: FhirPatient): CaseRowMeta => {
    const id = patient.id ?? '';
    const stored = metaMap.value[id];
    const mock = getMockMeta(id);
    const intakeStatus = hasPatient(id) ? '已收案' : (stored?.intakeStatus ?? mock.intakeStatus);
    return {
      ...mock,
      ...stored,
      intakeStatus
    };
  };

  const setMeta = (patientId: string, partial: Partial<CaseRowMeta>) => {
    const current = metaMap.value[patientId] ?? {};
    metaMap.value = {
      ...metaMap.value,
      [patientId]: { ...current, ...partial }
    };
    saveToStorage(metaMap.value);
  };

  const getEnrichedRows = (patients: FhirPatient[]): Array<FhirPatient & CaseRowMeta> => {
    return patients.map((p) => ({ ...p, ...getMeta(p) }));
  };

  return {
    metaMap,
    getMeta,
    setMeta,
    getEnrichedRows
  };
}
