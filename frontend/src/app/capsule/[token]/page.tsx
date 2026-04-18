'use client';

import { useCapsule } from '@/hooks/useCapsule';
import { useState, useEffect, use } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import Link from 'next/link';
import LetterStudio from '@/components/LetterStudio';

interface PageProps {
  params: Promise<{ token: string }>;
}

export default function CapsulePage({ params }: PageProps) {
  const { token } = use(params);
  const { preview, revealed, loading, error, reveal } = useCapsule(token);
  const [email, setEmail] = useState('');
  const [timeLeft, setTimeLeft] = useState<{ d: number; h: number; m: number; s: number } | null>(null);

  useEffect(() => {
    if (!preview || preview.status !== 'SEALED') return;

    const timer = setInterval(() => {
      const now = new Date().getTime();
      const target = new Date(preview.unlockAt).getTime();
      const diff = target - now;

      if (diff <= 0) {
        setTimeLeft(null);
        window.location.reload(); // Refresh to show unlock state
        clearInterval(timer);
      } else {
        setTimeLeft({
          d: Math.floor(diff / (1000 * 60 * 60 * 24)),
          h: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
          m: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
          s: Math.floor((diff % (1000 * 60)) / 1000),
        });
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [preview]);

  if (loading && !revealed) return (
    <div className="min-h-screen flex items-center justify-center bg-paper">
      <div className="w-8 h-8 border-4 border-accent-gold/20 border-t-accent-gold rounded-full animate-spin" />
    </div>
  );

  if (error) return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-paper px-6 text-center">
      <h1 className="text-4xl font-serif italic mb-4">Capsule Not Found</h1>
      <p className="text-ink-muted mb-8">This frequency seems to be lost in time.</p>
      <Link href="/">
        <button className="text-xs uppercase tracking-widest font-serif underline decoration-accent-gold">Return to Sanctuary</button>
      </Link>
    </div>
  );

  return (
    <div className="min-h-screen relative py-12 px-6">
      {/* Header */}
      <div className="max-w-7xl mx-auto mb-20 flex flex-col items-center">
        <Link href="/" className="font-serif italic text-2xl text-ink hover:opacity-70 transition-opacity mb-2">
          Capsule.
        </Link>
        <div className="h-[1px] w-24 bg-ink/10" />
      </div>

      <main className="max-w-3xl mx-auto">
        <AnimatePresence mode="wait">
          {!revealed ? (
            <motion.div
              key="sealed"
              initial={{ opacity: 0, scale: 0.98 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 1.05 }}
              className="text-center space-y-12"
            >
              <div className="space-y-4">
                <h2 className="text-5xl md:text-7xl font-serif italic text-ink-bleed">
                  {preview?.title || 'A Message from the Past'}
                </h2>
                <p className="text-sm uppercase tracking-[0.4em] text-accent-gold font-serif">
                  {preview?.status === 'SEALED' ? 'Time-Locked' : 'Ready for Witness'}
                </p>
              </div>

              {preview?.status === 'SEALED' && timeLeft && (
                <div className="grid grid-cols-4 gap-4 max-w-sm mx-auto font-serif">
                  {[
                    { label: 'Days', val: timeLeft.d },
                    { label: 'Hrs', val: timeLeft.h },
                    { label: 'Min', val: timeLeft.m },
                    { label: 'Sec', val: timeLeft.s }
                  ].map((t) => (
                    <div key={t.label} className="flex flex-col items-center">
                      <span className="text-4xl tabular-nums italic">{String(t.val).padStart(2, '0')}</span>
                      <span className="text-[10px] uppercase tracking-widest opacity-40">{t.label}</span>
                    </div>
                  ))}
                </div>
              )}

              {preview?.status === 'UNLOCKED' && !revealed && (
                <motion.div 
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="space-y-8 bg-paper-light p-12 paper-shadow border border-ink/5"
                >
                  <p className="text-ink-muted font-sans italic">
                    The lock has faded. To witness what lies within, please identify yourself.
                  </p>
                  <div className="space-y-4">
                    <input
                      type="email"
                      placeholder="Your registered email"
                      className="w-full bg-transparent border-b border-ink/20 py-2 text-center focus:outline-none focus:border-accent-gold transition-colors font-serif"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                    <button
                      onClick={() => reveal(email)}
                      className="w-full py-5 bg-accent-burgundy text-paper uppercase tracking-widest font-serif paper-shadow hover:bg-accent-burgundy/90 transition-colors"
                    >
                      Break the Seal
                    </button>
                  </div>
                </motion.div>
              )}
            </motion.div>
          ) : (
            <motion.div
              key="revealed"
              initial={{ opacity: 0, y: 40 }}
              animate={{ opacity: 1, y: 0 }}
              className="space-y-12"
            >
              <div className="bg-paper-light p-8 md:p-12 paper-shadow border border-ink/5 space-y-12 relative overflow-hidden flex flex-col items-center">
                
                {revealed.canvasJson ? (
                  <LetterStudio 
                    isReadOnly 
                    initialData={revealed.canvasJson} 
                    onSave={() => {}} 
                  />
                ) : (
                  <div className="text-xl font-sans leading-relaxed text-ink whitespace-pre-wrap max-w-xl">
                    {revealed.content}
                  </div>
                )}

                {/* Media Attachments Section */}
                {revealed.files?.length > 0 && (
                  <div className="w-full max-w-xl space-y-8 pt-12 border-t border-ink/5">
                    <h3 className="text-xs uppercase tracking-widest font-serif text-accent-gold text-center">Attached Media</h3>
                    <div className="space-y-6">
                      {revealed.files.map((f: any, i: number) => (
                        <div key={i} className="bg-paper p-4 border border-ink/5 flex flex-col items-center gap-4">
                          {f.fileType.startsWith('audio') ? (
                            <audio controls src={f.storageUrl} className="w-full" />
                          ) : f.fileType.startsWith('video') ? (
                            <video controls src={f.storageUrl} className="w-full paper-shadow" />
                          ) : (
                            <a href={f.storageUrl} target="_blank" className="text-xs font-serif underline">Download File</a>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {revealed.aiReflection && (
                  <div className="pt-8 border-t border-ink/5 w-full max-w-xl">
                    <h3 className="text-xs uppercase tracking-widest font-serif text-accent-gold mb-4">AI Reflection</h3>
                    <p className="font-serif italic text-lg text-ink-muted leading-relaxed">
                      {revealed.aiReflection}
                    </p>
                  </div>
                )}
              </div>

              {revealed.doodles?.length > 0 && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  {revealed.doodles.map((d: any, idx: number) => (
                    <div key={idx} className="bg-paper p-6 border border-ink/5 paper-shadow flex items-center justify-center">
                       <div dangerouslySetInnerHTML={{ __html: d.svgData }} className="w-full max-w-[200px]" />
                    </div>
                  ))}
                </div>
              )}

              <div className="text-center pt-8">
                <Link href="/">
                  <button className="text-ink-muted hover:text-ink transition-colors font-serif uppercase tracking-[0.2em] text-xs underline decoration-accent-gold/30">
                    Return to the Sanctuary
                  </button>
                </Link>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </main>

      {/* Decorative Accent */}
      <div className="fixed bottom-0 right-0 p-12 opacity-5 pointer-events-none">
        <div className="w-24 h-24 border border-ink transform rotate-45" />
      </div>
    </div>
  );
}
