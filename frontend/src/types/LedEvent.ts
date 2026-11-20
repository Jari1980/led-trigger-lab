export interface LedEventRequest {
  experimentId: string
  ledId: number
}

export interface LedEventResponse {
  eventId: string
  ledId: number
  status: string
  message: string
}