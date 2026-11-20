import { useEffect, useRef, useState } from 'react'
import './App.css'

import MainLayout from './components/layout/MainLayout'
import LedPanel from './components/led/LedPanel'
import ExperimentPanel from './components/experiments/ExperimentPanel'

import type { ExperimentResult } from './types/ExperimentResult'
import {
  publishLedEvent,
  getExperimentResults,
} from './services/ledApi'

type ExperimentStatus =
  | 'IDLE'
  | 'RUNNING'
  | 'PROCESSING'
  | 'COMPLETED'

const POLLING_INTERVAL = 2000

function App() {
  const [status, setStatus] = useState<ExperimentStatus>('IDLE')
  const [experimentId, setExperimentId] = useState('')
  const [results, setResults] = useState<ExperimentResult[]>([])

  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [resultsError, setResultsError] = useState('')

  const [pendingRequests, setPendingRequests] = useState(0)
  const [submittedEvents, setSubmittedEvents] = useState(0)

  const pendingRequestsRef = useRef(0)

  // Poll experiment results
  useEffect(() => {
    if (!experimentId) return

    let active = true

    const fetchResults = async () => {
      try {
        const data = await getExperimentResults(experimentId)

        if (!active) return

        setResults(data)
        setResultsError('')

        if (
          status === 'PROCESSING' &&
          pendingRequestsRef.current === 0
        ) {
          const queueCount = data.filter(
            (result) => result.engineType === 'QUEUE',
          ).length

          const timerCount = data.filter(
            (result) => result.engineType === 'TIMER',
          ).length

          if (
            queueCount >= submittedEvents &&
            timerCount >= submittedEvents
          ) {
            setStatus('COMPLETED')
            setMessage('All events have been processed.')
          }
        }
      } catch (err) {
        if (active) {
          setResultsError(
            err instanceof Error
              ? err.message
              : 'Failed to fetch experiment results',
          )
        }
      }
    }

    void fetchResults()

    const intervalId = window.setInterval(
      fetchResults,
      POLLING_INTERVAL,
    )

    return () => {
      active = false
      window.clearInterval(intervalId)
    }
  }, [experimentId, status, submittedEvents])

  // Start a new experiment
  const handleStartExperiment = () => {
    const newExperimentId = crypto.randomUUID()

    pendingRequestsRef.current = 0

    setExperimentId(newExperimentId)
    setResults([])

    setPendingRequests(0)
    setSubmittedEvents(0)

    setMessage('')
    setError('')
    setResultsError('')

    setStatus('RUNNING')
  }

  // Stop accepting new LED clicks
  const handleStopExperiment = () => {
    setStatus('PROCESSING')
    setMessage('Experiment stopped. Waiting for pending events.')
  }

  // Publish LED event
  const handleLedClick = async (ledId: number) => {
    if (status !== 'RUNNING') return

    setMessage('')
    setError('')

    pendingRequestsRef.current += 1
    setPendingRequests(pendingRequestsRef.current)

    try {
      const response = await publishLedEvent({
        experimentId,
        ledId,
      })

      setSubmittedEvents((current) => current + 1)

      setMessage(
        `LED ${response.ledId} accepted. Event ID: ${response.eventId}`,
      )
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Failed to publish LED event',
      )
    } finally {
      pendingRequestsRef.current -= 1
      setPendingRequests(pendingRequestsRef.current)
    }
  }

  return (
    <MainLayout>
      <section className="experiment-controls">
        <h2>Experiment Control</h2>

        <p>Status: {status}</p>

        {experimentId && (
          <>
            <p>Experiment ID: {experimentId}</p>
            <p>Events submitted: {submittedEvents}</p>
            <p>Pending requests: {pendingRequests}</p>
          </>
        )}

        {status === 'IDLE' || status === 'COMPLETED' ? (
          <button
            type="button"
            onClick={handleStartExperiment}
          >
            Start Experiment
          </button>
        ) : status === 'RUNNING' ? (
          <button
            type="button"
            onClick={handleStopExperiment}
          >
            Stop Experiment
          </button>
        ) : (
          <p>Waiting for experiment processing...</p>
        )}
      </section>

      <LedPanel
        onLedClick={handleLedClick}
        disabled={status !== 'RUNNING'}
      />

      {message && (
        <p className="success-message">{message}</p>
      )}

      {error && (
        <p className="error-message">{error}</p>
      )}

      {resultsError && (
        <p className="error-message">{resultsError}</p>
      )}

      <div className="experiment-grid">
        <ExperimentPanel
          title="Queue Trigger"
          engineType="QUEUE"
          results={results}
          expectedEvents={submittedEvents}
        />

        <ExperimentPanel
          title="Timer Trigger"
          engineType="TIMER"
          results={results}
          expectedEvents={submittedEvents}
        />
      </div>
    </MainLayout>
  )
}

export default App