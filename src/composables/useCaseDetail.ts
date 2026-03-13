import { ref, onMounted, watch } from 'vue';
import { getResource, searchResource } from '../api/fhirClient';
import type { FhirPatient, FhirBundle, FhirObservation, FhirEncounter } from '../types/fhir';

export function useCaseDetail(patientId: string) {
  const loading = ref(false);
  const error = ref<string | null>(null);
  const patient = ref<FhirPatient | null>(null);
  const observations = ref<FhirObservation[]>([]);
  const encounters = ref<FhirEncounter[]>([]);

  const load = async () => {
    if (!patientId) return;
    loading.value = true;
    error.value = null;

    try {
      const [p, obsBundle, encBundle] = await Promise.all([
        getResource<FhirPatient>('Patient', patientId),
        searchResource<FhirBundle<FhirObservation>>('Observation', {
          patient: patientId,
          _sort: '-date',
          _count: 10
        }),
        searchResource<FhirBundle<FhirEncounter>>('Encounter', {
          patient: patientId,
          _sort: '-date',
          _count: 5
        })
      ]);

      patient.value = p;
      observations.value =
        obsBundle.entry
          ?.map((e) => e.resource)
          .filter((r): r is FhirObservation => !!r && r.resourceType === 'Observation') ?? [];
      encounters.value =
        encBundle.entry
          ?.map((e) => e.resource)
          .filter((r): r is FhirEncounter => !!r && r.resourceType === 'Encounter') ?? [];
    } catch (e: any) {
      error.value = e?.message ?? '讀取個案資料時發生錯誤';
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
    patient,
    observations,
    encounters
  };
}

