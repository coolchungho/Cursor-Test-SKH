export interface FhirReference {
  reference?: string;
  display?: string;
}

export interface FhirHumanName {
  family?: string;
  given?: string[];
  text?: string;
}

export interface FhirIdentifier {
  system?: string;
  value?: string;
}

export interface FhirPatient {
  resourceType: 'Patient';
  id?: string;
  identifier?: FhirIdentifier[];
  name?: FhirHumanName[];
  gender?: string;
  birthDate?: string;
}

export interface FhirBundleEntry<T = any> {
  fullUrl?: string;
  resource?: T;
}

export interface FhirBundle<T = any> {
  resourceType: 'Bundle';
  total?: number;
  entry?: Array<FhirBundleEntry<T>>;
}

export interface FhirObservation {
  resourceType: 'Observation';
  id?: string;
  status?: string;
  code?: {
    text?: string;
  };
  effectiveDateTime?: string;
  valueQuantity?: {
    value?: number;
    unit?: string;
  };
}

export interface FhirEncounter {
  resourceType: 'Encounter';
  id?: string;
  status?: string;
  class?: {
    code?: string;
  };
  type?: Array<{
    text?: string;
  }>;
  period?: {
    start?: string;
    end?: string;
  };
}


