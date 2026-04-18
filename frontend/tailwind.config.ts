import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        paper: {
          light: '#fdfbf7',
          DEFAULT: '#f9f6f0',
          dark: '#f2ece0',
        },
        ink: {
          DEFAULT: '#1a1a1a',
          muted: '#4a4a4a',
        },
        accent: {
          gold: '#c5a059',
          burgundy: '#800000',
        }
      },
      fontFamily: {
        serif: ['var(--font-playfair)', 'serif'],
        sans: ['var(--font-lora)', 'serif'],
      },
    },
  },
  plugins: [],
}

export default config
