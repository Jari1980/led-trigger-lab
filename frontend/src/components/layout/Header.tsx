import logo from '../../assets/led-trigger-lab-logo.png'

function Header() {
  return (
    <header className="app-header">
      <div className="header-brand">
        <img
          src={logo}
          alt="LedTriggerLab logo"
          className="header-logo"
        />

        <div>
          <h1>LedTriggerLab</h1>
          <p>Queue Trigger vs Timer Trigger</p>
        </div>
      </div>

      <span className="environment-status">
        Local environment
      </span>
    </header>
  )
}

export default Header