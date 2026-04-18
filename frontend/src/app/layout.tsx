import type { Metadata } from 'next'
import { Playfair_Display, Lora } from 'next/font/google'
import './globals.css'

const playfair = Playfair_Display({ 
  subsets: ['latin'],
  variable: '--font-playfair',
})

const lora = Lora({ 
  subsets: ['latin'],
  variable: '--font-lora',
})

export const metadata: Metadata = {
  title: 'Capsule — Seal a Memory in Time',
  description: 'A whimsical, time-locked memory platform.',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" className={`${playfair.variable} ${lora.variable}`}>
      <body className="font-sans antialiased text-ink selection:bg-accent-gold/20">
        <main className="relative z-10 min-h-screen">
          {children}
        </main>
      </body>
    </html>
  )
}
