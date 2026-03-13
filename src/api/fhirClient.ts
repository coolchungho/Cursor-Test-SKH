const FHIR_BASE_URL = import.meta.env.VITE_FHIR_BASE_URL as string;

if (!FHIR_BASE_URL) {
  // 在開發期若沒設定，直接拋錯方便偵錯
  // eslint-disable-next-line no-console
  console.error('VITE_FHIR_BASE_URL is not set in environment variables.');
}

export interface SearchParams {
  [key: string]: string | number | boolean | undefined;
}

function buildQuery(params: SearchParams): string {
  const urlParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== '') {
      urlParams.append(key, String(value));
    }
  });
  const query = urlParams.toString();
  return query ? `?${query}` : '';
}

async function request<T>(input: string, init?: RequestInit): Promise<T> {
  const response = await fetch(input, {
    ...init,
    headers: {
      'Accept': 'application/fhir+json',
      'Content-Type': 'application/fhir+json',
      ...(init?.headers ?? {})
    }
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`FHIR request failed: ${response.status} ${response.statusText} - ${text}`);
  }

  return (await response.json()) as T;
}

export async function getResource<T>(resourceType: string, id: string): Promise<T> {
  const url = `${FHIR_BASE_URL}/${resourceType}/${encodeURIComponent(id)}`;
  return request<T>(url);
}

export async function searchResource<T>(
  resourceType: string,
  params: SearchParams
): Promise<T> {
  const query = buildQuery(params);
  const url = `${FHIR_BASE_URL}/${resourceType}${query}`;
  return request<T>(url);
}

