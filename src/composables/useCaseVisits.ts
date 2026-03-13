import { ref, onMounted, watch } from 'vue';
import { searchResource } from '../api/fhirClient';
import type { FhirBundle, FhirEncounter } from '../types/fhir';

export function useCaseVisits(patientId: string) {
  const loading = ref(false);
  const error = ref<string | null>(null);
  const visits = ref<FhirEncounter[]>([]);

  const load = async () => {
    if (!patientId) return;
    loading.value = true;
    error.value = null;

    try {
      const bundle = await searchResource<FhirBundle<FhirEncounter>>('Encounter', {
        patient: patientId,
        _sort: '-date'
      });
      visits.value =
        bundle.entry
          ?.map((e) => e.resource)
          .filter((r): r is FhirEncounter => !!r && r.resourceType === 'Encounter') ?? [];
    } catch (e: any) {
      error.value = e?.message ?? '讀取訪視紀錄時發生錯誤';
    } finally {
      loading.value = false;
    }
  };

  onMounted(load);
  watch(
    () => patientId,
    () => {
      load();
    }
  );

  return {
    loading,
    error,
    visits,
    reload: load
  };
}

