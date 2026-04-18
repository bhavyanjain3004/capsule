'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { api } from '@/lib/api';
import LetterStudio from './LetterStudio';
import DoodleCanvas from './DoodleCanvas';
import Link from 'next/link';

type Step = 'content' | 'extra' | 'lock' | 'success';

export default function SealFlow() {
  const [step, setStep] = useState<Step>('content');
  const [loading, setLoading] = useState(false);
  const [capsuleData, setCapsuleData] = useState({
    title: '',
    content: '',
    creatorEmail: '',
    recipients: [] as string[],
    unlockAt: '',
    backgroundTexture: 'paper-default'
  });
  const [token, setToken] = useState<string | null>(null);
  const [shareUrl, setShareUrl] = useState<string | null>(null);

  const [mediaFiles, setMediaFiles] = useState<{file: File, type: 'audio' | 'video'}[]>([]);

  const handleNext = () => {
    if (step === 'content') setStep('lock');
  };

  const handleAddMedia = (type: 'audio' | 'video') => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = type === 'audio' ? 'audio/*' : 'video/*';
    input.onchange = (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (file) {
        setMediaFiles(prev => [...prev, { file, type }]);
      }
    };
    input.click();
  };

  const handleSeal = async () => {
    setLoading(true);
    try {
      // 1. Create capsule
      const res = await api.createCapsule({
        ...capsuleData,
        unlockAt: new Date(capsuleData.unlockAt).toISOString().slice(0, 19),
        recipients: capsuleData.recipients.filter(e => e.trim() !== '')
      });

      // 2. Upload media files
      for (const item of mediaFiles) {
        await api.uploadFile(res.token, item.file);
      }

      setToken(res.token);
      setShareUrl(res.shareUrl);
      setStep('success');
    } catch (err) {
      alert('Failed to seal capsule.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto py-12 px-6 overflow-hidden">
      <AnimatePresence mode="wait">
        {step === 'content' && (
          <motion.div
            key="content"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="space-y-8 flex flex-col items-center"
          >
            <div className="text-center space-y-2">
              <h2 className="text-4xl font-serif italic text-ink-bleed">Compose your letter</h2>
              <p className="text-ink-muted">Drag, draw, and write your digital legacy.</p>
            </div>

            <div className="flex gap-4 mb-4">
              <button 
                onClick={() => handleAddMedia('audio')}
                className="flex items-center gap-2 px-4 py-2 border border-ink/10 text-xs uppercase tracking-widest font-serif hover:bg-paper-dark"
              >
                <span>🎙️</span> Add Audio
              </button>
              <button 
                onClick={() => handleAddMedia('video')}
                className="flex items-center gap-2 px-4 py-2 border border-ink/10 text-xs uppercase tracking-widest font-serif hover:bg-paper-dark"
              >
                <span>🎬</span> Add Video
              </button>
            </div>

            {mediaFiles.length > 0 && (
              <div className="flex gap-2 mb-4">
                {mediaFiles.map((m, i) => (
                  <div key={i} className="px-3 py-1 bg-accent-gold text-paper text-[10px] uppercase tracking-widest font-serif">
                   {m.type === 'audio' ? 'Audio Note' : 'Video Memory'} Attached
                  </div>
                ))}
              </div>
            )}
            
            <LetterStudio onSave={(data) => {
              setCapsuleData(prev => ({ 
                ...prev, 
                content: 'Interactive Stationery Compose', 
                canvasJson: data.canvasJson 
              }));
              handleNext();
            }} />
          </motion.div>
        )}

        {step === 'extra' && (
          <motion.div
            key="extra"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="space-y-8"
          >
            <div className="space-y-2 text-center">
              <h2 className="text-4xl font-serif italic text-ink-bleed">A touch of whim</h2>
              <p className="text-ink-muted">Draw something that captures this moment.</p>
            </div>

            <DoodleCanvas onSave={(svg) => {
              // Optionally handle doodle save here or in a combined API call
              // For now, we'll just move forward as doodles are a secondary endpoint
              handleNext();
            }} />

            <div className="text-center pt-4">
              <button 
                onClick={handleNext}
                className="text-ink-muted underline font-serif tracking-widest uppercase text-xs"
              >
                Skip Drawing
              </button>
            </div>
          </motion.div>
        )}

        {step === 'lock' && (
          <motion.div
            key="lock"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="space-y-8"
          >
            <div className="space-y-2">
              <h2 className="text-4xl font-serif italic text-ink-bleed">Final details</h2>
              <p className="text-ink-muted">Who should see this, and when?</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <div className="space-y-4">
                <label className="block uppercase text-xs tracking-widest font-serif text-ink-muted">Your Email</label>
                <input
                  type="email"
                  className="w-full bg-paper-light border-b border-ink/20 py-2 focus:outline-none focus:border-accent-gold"
                  value={capsuleData.creatorEmail}
                  onChange={(e) => setCapsuleData({ ...capsuleData, creatorEmail: e.target.value })}
                />
              </div>

              <div className="space-y-4">
                <label className="block uppercase text-xs tracking-widest font-serif text-ink-muted">Unlock Date</label>
                <input
                  type="datetime-local"
                  className="w-full bg-paper-light border-b border-ink/20 py-2 focus:outline-none focus:border-accent-gold"
                  value={capsuleData.unlockAt}
                  onChange={(e) => setCapsuleData({ ...capsuleData, unlockAt: e.target.value })}
                />
              </div>
            </div>

            <div className="space-y-4">
              <label className="block uppercase text-xs tracking-widest font-serif text-ink-muted">Recipient Emails</label>
              <input
                type="text"
                placeholder="Separate by commas"
                className="w-full bg-paper-light border-b border-ink/20 py-2 focus:outline-none focus:border-accent-gold"
                onChange={(e) => setCapsuleData({ ...capsuleData, recipients: e.target.value.split(',') })}
              />
            </div>

            <button
              onClick={handleSeal}
              disabled={loading || !capsuleData.creatorEmail || !capsuleData.unlockAt}
              className="w-full py-5 bg-accent-burgundy text-paper uppercase tracking-widest font-serif disabled:opacity-50 relative paper-shadow"
            >
              {loading ? 'Sealing...' : 'Seal into the Future'}
              <div className="absolute top-1/2 left-4 -translate-y-1/2 w-6 h-6 border-4 border-paper/20 rounded-full border-t-paper animate-spin hidden" />
            </button>
          </motion.div>
        )}

        {step === 'success' && (
          <motion.div
            key="success"
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="text-center space-y-12 py-12"
          >
            <div className="relative inline-block">
               <motion.div 
                 initial={{ scale: 2, opacity: 0 }}
                 animate={{ scale: 1, opacity: 1 }}
                 transition={{ delay: 0.5, type: 'spring' }}
                 className="w-32 h-32 bg-accent-burgundy rounded-full flex items-center justify-center text-paper text-4xl shadow-xl border-4 border-paper italic font-serif"
               >
                 Seal
               </motion.div>
            </div>

            <div className="space-y-4">
              <h2 className="text-4xl font-serif italic text-ink-bleed">It is sealed.</h2>
              <p className="max-w-md mx-auto text-ink-muted font-sans text-lg">
                Your memory is now time-locked. We'll keep it safe until the bells toll.
              </p>
            </div>

            <div className="bg-paper-dark p-6 paper-shadow border border-ink/5 inline-block">
              <p className="text-xs uppercase tracking-widest mb-2 font-serif text-ink-muted">Private Link</p>
              <code className="text-ink font-mono text-sm break-all">{shareUrl}</code>
            </div>

            <div>
              <Link href="/">
                <button className="text-ink-muted hover:text-ink transition-colors font-serif uppercase tracking-[0.2em] text-xs">
                  Return to Sanctuary
                </button>
              </Link>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
