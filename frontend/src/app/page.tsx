'use client';

import { motion } from 'framer-motion';
import Link from 'next/link';

export default function HomePage() {
  return (
    <div className="relative flex flex-col items-center justify-center min-h-screen px-4 overflow-hidden">
      {/* Cinematic Background Layer */}
      <div className="fixed inset-0 z-[-1] pointer-events-none overflow-hidden bg-paper">
        <img 
          src="/cinematic_bg.png"
          alt="Cinematic Background"
          className="w-full h-full object-cover scale-105 opacity-80"
        />
        {/* Subtle Vignette Overlay */}
        <div className="absolute inset-0 bg-gradient-to-tr from-paper/30 via-transparent to-paper-dark/50" />
      </div>

      {/* Handmade Paper Texture Overlay */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 0.1 }}
        transition={{ duration: 2 }}
        className="absolute inset-0 z-[1] pointer-events-none"
        style={{
          backgroundImage: `url("https://www.transparenttextures.com/patterns/handmade-paper.png")`,
        }}
      />

      <main className="relative z-10 max-w-4xl text-center">
        <motion.div
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1.2, ease: [0.22, 1, 0.36, 1] }}
        >
          <span className="block mb-4 text-sm tracking-[0.4em] uppercase text-accent-gold font-serif font-bold drop-shadow-sm">
            Sealed in Time. Released by Fate.
          </span>

          <h1 className="text-7xl md:text-9xl mb-8 font-serif italic text-ink-bleed drop-shadow-2xl">
            Capsule.
          </h1>

          <p className="max-w-xl mx-auto mb-12 text-lg md:text-xl text-ink font-sans leading-relaxed drop-shadow-sm">
            A sanctuary for your most cherished memories. Seal a message, a drawing, or a moment,
            and set it free into the future. Some things are better left for the version of you that hasn't arrived yet.
          </p>

          <div className="flex flex-col md:flex-row items-center justify-center gap-6">
            <Link href="/seal">
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                className="px-10 py-5 bg-ink text-paper rounded-none font-serif text-lg paper-shadow hover:bg-ink/90 transition-colors uppercase tracking-widest relative"
              >
                Seal a Memory
                <div className="absolute -bottom-1 -right-1 w-full h-full border border-ink/20 -z-10 translate-x-2 translate-y-2 pointer-events-none" />
              </motion.button>
            </Link>

            <Link href="/unlock">
              <button className="px-10 py-5 border border-ink/20 text-ink font-serif hover:bg-paper-dark transition-colors uppercase tracking-widest">
                Retrieve a Secret
              </button>
            </Link>
          </div>
        </motion.div>
      </main>

      {/* Decorative Footer Detail */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 0.1 }}
        transition={{ delay: 1, duration: 2 }}
        className="absolute bottom-12 left-1/2 -translate-x-1/2 flex items-center gap-4 text-xs tracking-widest uppercase font-serif text-ink-muted"
      >
        <div className="h-[1px] w-12 bg-ink/20" />
        EST. 2026 — Digital Longevity
        <div className="h-[1px] w-12 bg-ink/20" />
      </motion.div>
    </div>
  );
}
