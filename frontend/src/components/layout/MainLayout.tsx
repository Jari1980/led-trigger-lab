import type { ReactNode } from 'react'
import Header from './Header'

interface MainLayoutProps {
  children: ReactNode
}

function MainLayout({ children }: MainLayoutProps) {
  return (
    <div className="app-container">
      <Header />

      <main className="main-content">
        {children}
      </main>
    </div>
  )
}

export default MainLayout