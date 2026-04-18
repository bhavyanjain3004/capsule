import SealFlow from '@/components/SealFlow';
import Link from 'next/link';

export default function SealPage() {
  return (
    <div className="min-h-screen relative py-12">
      {/* Header */}
      <div className="max-w-7xl mx-auto px-6 mb-12 flex justify-between items-center">
        <Link href="/" className="font-serif italic text-2xl text-ink hover:opacity-70 transition-opacity">
          Capsule.
        </Link>
        <div className="h-[1px] flex-grow mx-8 bg-ink/10" />
        <span className="text-[10px] uppercase tracking-[0.3em] font-serif text-ink-muted">
          New Entry
        </span>
      </div>

      <SealFlow />

      {/* Background Decorative Detail */}
      <div className="absolute top-0 right-0 p-12 opacity-10 pointer-events-none select-none">
        <span className="font-serif italic text-[200px] leading-none">S</span>
      </div>
    </div>
  );
}
