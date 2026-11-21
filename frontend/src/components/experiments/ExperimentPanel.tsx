import type { ExperimentResult } from "../../types/ExperimentResult";

interface ExperimentPanelProps {
  title: string;
  engineType: "QUEUE" | "TIMER";
  results: ExperimentResult[];
  expectedEvents: number;
}

function ExperimentPanel({
  title,
  engineType,
  results,
  expectedEvents,
}: ExperimentPanelProps) {
  const engineResults = results
    .filter((result) => result.engineType === engineType)
    .sort((a, b) => a.processingOrder - b.processingOrder);

  const processedEvents = engineResults.length;

  const progress =
    expectedEvents > 0
      ? Math.min((processedEvents / expectedEvents) * 100, 100)
      : 0;

  return (
    <section className="experiment-panel">
      <h2>{title}</h2>

      <p>
        Processed events: {processedEvents}/{expectedEvents}
      </p>

      <div className="progress-bar">
        <div className="progress-bar-fill" style={{ width: `${progress}%` }} />
      </div>

      {engineResults.length === 0 ? (
        <p className="empty-state">No events processed yet.</p>
      ) : (
        <div className="experiment-results">
          {engineResults.map((result) => {
            const totalDelayMs =
              new Date(result.processedAt).getTime() -
              new Date(result.publishedAt).getTime();

            return (
              <div key={result.eventId} className="experiment-result">
                <span>LED {result.ledId}</span>
                <span>Order: {result.processingOrder}</span>
                <span>Processing: {result.processingDurationMs} ms</span>
                <span>Total delay: {totalDelayMs} ms</span>
                <span className="event-id">{result.eventId}</span>
              </div>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default ExperimentPanel;
