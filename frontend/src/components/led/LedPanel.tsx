interface LedPanelProps {
  onLedClick: (ledId: number) => void
  disabled: boolean
}

function LedPanel({ onLedClick, disabled }: LedPanelProps) {
  const ledIds = [1, 2, 3]

  return (
    <section className="led-panel">
      <h2>LED Controller</h2>
      <p>Select a LED to generate an event.</p>

      <div className="led-grid">
        {ledIds.map((ledId) => (
          <button
            key={ledId}
            className="led-button"
            onClick={() => onLedClick(ledId)}
            type="button"
            disabled={disabled}
          >
            <span className="led-indicator" />
            <span>LED {ledId}</span>
          </button>
        ))}
      </div>
    </section>
  )
}

export default LedPanel