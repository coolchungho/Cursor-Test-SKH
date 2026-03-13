import { ref, computed } from 'vue';
import type { FhirPatient } from '../types/fhir';

const STORAGE_KEY = 'case-intake-list';

interface IntakeItem {
  patientId: string;
  patient?: FhirPatient;
  addedAt: string;
}

const intakeItems = ref<IntakeItem[]>(loadFromStorage());

function loadFromStorage(): IntakeItem[] {
  if (typeof window === 'undefined') return [];
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw) as IntakeItem[];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function saveToStorage() {
  if (typeof window === 'undefined') return;
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(intakeItems.value));
}

export function useIntakeList() {
  const items = computed(() => intakeItems.value);

  const hasPatient = (patientId: string) =>
    intakeItems.value.some((x) => x.patientId === patientId);

  const addPatient = (patient: FhirPatient) => {
    if (!patient.id || hasPatient(patient.id)) return;
    intakeItems.value.push({
      patientId: patient.id,
      patient,
      addedAt: new Date().toISOString()
    });
    saveToStorage();
  };

  const removePatient = (patientId: string) => {
    intakeItems.value = intakeItems.value.filter((x) => x.patientId !== patientId);
    saveToStorage();
  };

  return {
    items,
    hasPatient,
    addPatient,
    removePatient
  };
}

