import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'Capsule — Seal a Memory in Time',
  description:
    'A whimsical, time-locked memory platform. Seal photos, voice notes, and decorated pages into a digital capsule that unlocks on a future date.',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  )
}
