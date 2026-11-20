import type {
  LedEventRequest,
  LedEventResponse,
} from '../types/LedEvent'

import type { ExperimentResult } from '../types/ExperimentResult'

export async function publishLedEvent(
  request: LedEventRequest,
): Promise<LedEventResponse> {
  const response = await fetch('/api/led', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })

  if (!response.ok) {
    const errorMessage = await response.text()

    throw new Error(
      errorMessage || `HTTP error: ${response.status}`,
    )
  }

  return response.json() as Promise<LedEventResponse>
}

export async function getExperimentResults(
  experimentId: string,
): Promise<ExperimentResult[]> {
  const response = await fetch(
    `/api/experiments/${encodeURIComponent(experimentId)}/results`,
  )

  if (!response.ok) {
    throw new Error(
      `Failed to fetch experiment results: HTTP ${response.status}`,
    )
  }

  return response.json() as Promise<ExperimentResult[]>
}