'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

export default function UnlockPage() {
  const [token, setToken] = useState('');
  const router = useRouter();

  const handleUnlock = (e: React.FormEvent) => {
    e.preventDefault();
    if (token.trim()) {
      router.push(`/capsule/${token.trim()}`);
    }
  };

  return (
    <div className="min-h-screen relative py-12 px-6 flex flex-col items-center justify-center">
      <div className="max-w-7xl w-full absolute top-0 left-0 p-12 flex justify-between items-center pointer-events-none">
         <Link href="/" className="font-serif italic text-2xl text-ink pointer-events-auto hover:opacity-70 transition-opacity">
          Capsule.
        </Link>
      </div>

      <div className="max-w-md w-full space-y-12 text-center">
        <div className="space-y-4">
          <h1 className="text-5xl font-serif italic text-ink-bleed">The Archive.</h1>
          <p className="text-ink-muted font-sans italic">Enter your unique frequency to retrieve a memory.</p>
        </div>

        <form onSubmit={handleUnlock} className="space-y-8 bg-paper-light p-10 paper-shadow border border-ink/5">
          <div className="space-y-2">
            <label className="text-[10px] uppercase font-serif tracking-[0.4em] text-accent-gold">Access Token</label>
            <input
              type="text"
              placeholder="00000000-0000-0000-0000-000000000000"
              className="w-full bg-transparent border-b border-ink/20 py-2 text-center focus:outline-none focus:border-accent-gold transition-colors font-mono text-sm"
              value={token}
              onChange={(e) => setToken(e.target.value)}
              required
            />
          </div>
          
          <button
            type="submit"
            className="w-full py-5 bg-ink text-paper uppercase tracking-widest font-serif paper-shadow hover:bg-ink/90 transition-colors"
          >
            Locate Memory
          </button>
        </form>

        <Link href="/">
          <button className="text-xs uppercase tracking-widest font-serif text-ink-muted hover:text-ink transition-colors underline decoration-accent-gold/30 underline-offset-4">
            Return to Sanctuary
          </button>
        </Link>
      </div>
    </div>
  );
}
