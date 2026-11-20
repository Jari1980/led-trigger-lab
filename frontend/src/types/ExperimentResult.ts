export interface ExperimentResult {
  experimentId: string
  eventId: string
  engineType: 'QUEUE' | 'TIMER'
  ledId: number

  publishedAt: string
  receivedAt: string
  processedAt: string

  processingDurationMs: number
  processingOrder: number
}