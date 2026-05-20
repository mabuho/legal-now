import type { Config } from 'tailwindcss'

export default {
  content: [
    './index.html',
    './src/**/*.{vue,ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          primary:     '#06b6d4',
          primaryDark: '#0891b2',
          accent:      '#4f46e5',
        },
        surface: {
          base:   '#080f1a',
          raised: '#0d1625',
          card:   '#111d2e',
        },
        border: {
          default: '#1a2f4a',
          subtle:  '#1a2640',
        },
        text: {
          primary:   '#f1f5f9',
          secondary: '#94a3b8',
          muted:     '#475569',
        },
        status: {
          active:  '#34d399',
          payment: '#fbbf24',
          error:   '#f87171',
        },
      },
      borderRadius: {
        card:  '16px',
        btn:   '10px',
        tag:   '20px',
        input: '10px',
      },
      fontFamily: {
        heading: ['"Plus Jakarta Sans"', 'sans-serif'],
        body:    ['Inter', 'sans-serif'],
      },
      boxShadow: {
        glow:        '0 0 0 1px rgba(6,182,212,0.08), 0 4px 20px rgba(6,182,212,0.05)',
        'glow-btn':  '0 0 16px rgba(6,182,212,0.25)',
        'glow-hover':'0 0 0 1px rgba(6,182,212,0.25)',
      },
    },
  },
  plugins: [],
} satisfies Config
