import { ref } from 'vue';
import { searchResource } from '../api/fhirClient';
import type { FhirBundle, FhirPatient } from '../types/fhir';

export interface CaseSearchCriteria {
  name?: string;
  identifier?: string;
  birthdate?: string;
  gender?: string;
  /** 案件類別：關節置換、疼痛照護、乳癌 */
  caseCategory?: string;
  /** 住院狀態：住院中、已出院 */
  hospitalizationStatus?: string;
  /** 收案狀態：已收案、篩選中、不收案 */
  intakeStatus?: string;
  /** 回覆狀態：已回覆、未回覆 */
  replyStatus?: string;
  admissionDateStart?: string;
  admissionDateEnd?: string;
  dischargeDateStart?: string;
  dischargeDateEnd?: string;
}

export function useCaseSearch() {
  const loading = ref(false);
  const error = ref<string | null>(null);
  const patients = ref<FhirPatient[]>([]);

  const search = async (criteria: CaseSearchCriteria) => {
    loading.value = true;
    error.value = null;
    patients.value = [];

    try {
      const params: Record<string, string> = {};
      if (criteria.name) params['_name'] = criteria.name;
      if (criteria.identifier) params['identifier'] = criteria.identifier;
      if (criteria.birthdate) params['birthdate'] = criteria.birthdate;
      if (criteria.gender) params['gender'] = criteria.gender;

      const bundle = await searchResource<FhirBundle<FhirPatient>>('Patient', params);
      const entries = bundle.entry ?? [];
      patients.value = entries
        .map((e) => e.resource)
        .filter((r): r is FhirPatient => !!r && r.resourceType === 'Patient');
    } catch (e: any) {
      error.value = e?.message ?? '搜尋個案時發生錯誤';
    } finally {
      loading.value = false;
    }
  };

  return {
    loading,
    error,
    patients,
    search
  };
}

