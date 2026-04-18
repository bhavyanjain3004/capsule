'use client';

import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { fabric } from 'fabric';
import { api } from '@/lib/api';
import { useRouter } from 'next/navigation';

export default function CreateCapsule() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  
  // Data State
  const [formData, setFormData] = useState({
    title: '',
    creatorEmail: '',
    unlockAt: '',
    content: '',
    recipients: [''],
    backgroundTexture: 'paper'
  });
  const [files, setFiles] = useState<File[]>([]);
  const [token, setToken] = useState<string | null>(null);

  // Canvas State
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [fabricCanvas, setFabricCanvas] = useState<fabric.Canvas | null>(null);

  useEffect(() => {
    if (step === 4 && canvasRef.current && !fabricCanvas) {
      const c = new fabric.Canvas(canvasRef.current, {
        width: 600,
        height: 400,
        backgroundColor: '#fffdfa'
      });
      setFabricCanvas(c);
    }
  }, [step]);

  const addSticker = (id: string, url: string) => {
    fabric.Image.fromURL(url, (img) => {
      img.scale(0.2);
      fabricCanvas?.add(img);
      fabricCanvas?.centerObject(img);
      fabricCanvas?.setActiveObject(img);
    });
  };

  const handleCreate = async () => {
    setLoading(true);
    try {
      const { token, shareUrl } = await api.createCapsule({
        ...formData,
        unlockAt: new Date(formData.unlockAt).toISOString().split('.')[0]
      });
      setToken(token);
      
      // Upload Files
      for (const file of files) {
        await api.uploadFile(token, file);
      }
      
      setStep(4);
    } catch (err) {
      alert('Creation failed');
    } finally {
      setLoading(false);
    }
  };

  const finalize = async () => {
    if (!fabricCanvas || !token) return;
    
    // Save Doodles
    const objects = fabricCanvas.getObjects();
    for (const obj of objects) {
      await api.saveDoodle(token, {
        type: 'sticker',
        stickerId: 's1',
        svgData: JSON.stringify(obj.toObject()),
        positionX: obj.left,
        positionY: obj.top,
        scale: obj.scaleX,
        rotation: obj.angle
      });
    }
    
    router.push(`/capsule/${token}`);
  };

  return (
    <div className="max-w-4xl mx-auto py-20 px-6">
      <AnimatePresence mode="wait">
        {step === 1 && (
          <motion.div 
            key="step1"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="card-stack-effect bg-white border border-stone-200 p-12 rounded-lg"
          >
            <h1 className="text-4xl font-playfair mb-8">Metadata & Time</h1>
            <div className="space-y-6">
              <input 
                type="text" placeholder="Title of this memory"
                className="w-full text-2xl border-b border-stone-200 py-2 focus:outline-none font-playfair"
                value={formData.title}
                onChange={e => setFormData({...formData, title: e.target.value})}
              />
              <div className="grid grid-cols-2 gap-8">
                <input 
                  type="email" placeholder="Your Email"
                  className="w-full border-b border-stone-200 py-2 focus:outline-none"
                  value={formData.creatorEmail}
                  onChange={e => setFormData({...formData, creatorEmail: e.target.value})}
                />
                <input 
                  type="datetime-local"
                  className="w-full border-b border-stone-200 py-2 focus:outline-none"
                  value={formData.unlockAt}
                  onChange={e => setFormData({...formData, unlockAt: e.target.value})}
                />
              </div>
              <button 
                onClick={() => setStep(2)}
                className="bg-stone-900 text-stone-50 px-8 py-3 rounded-full hover:bg-stone-800 transition"
              >
                Continue
              </button>
            </div>
          </motion.div>
        )}

        {step === 2 && (
          <motion.div 
            key="step2"
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white border border-stone-200 p-12 rounded-lg shadow-xl"
          >
            <h1 className="text-4xl font-playfair mb-8 text-whimsical">The Secret Message</h1>
            <textarea 
              placeholder="What must be remembered?"
              className="w-full h-64 text-xl font-lora border-none focus:ring-0 resize-none leading-relaxed"
              value={formData.content}
              onChange={e => setFormData({...formData, content: e.target.value})}
            />
            <div className="mt-8 flex justify-between">
              <button onClick={() => setStep(1)} className="text-stone-500">Back</button>
              <button 
                onClick={() => setStep(3)}
                className="bg-stone-900 text-stone-50 px-8 py-3 rounded-full"
              >
                Continue
              </button>
            </div>
          </motion.div>
        )}

        {step === 3 && (
          <motion.div key="step3" className="bg-white p-12 rounded-lg border border-stone-200">
             <h1 className="text-4xl font-playfair mb-8">Attachments</h1>
             <input 
               type="file" multiple 
               onChange={e => setFiles(Array.from(e.target.files || []))}
               className="mb-8"
             />
             <div className="flex justify-between">
                <button onClick={() => setStep(2)}>Back</button>
                <button 
                  onClick={handleCreate}
                  disabled={loading}
                  className="bg-stone-900 text-stone-50 px-8 py-3 rounded-full disabled:opacity-50"
                >
                  {loading ? 'Sealing...' : 'Seal the Capsule'}
                </button>
             </div>
          </motion.div>
        )}

        {step === 4 && (
          <motion.div key="step4" className="flex flex-col items-center">
            <h1 className="text-4xl font-playfair mb-8">Final Flourishes</h1>
            <div className="flex gap-8 mb-8">
               <button 
                 onClick={() => addSticker('s1', 'https://api.dicebear.com/7.x/bottts/svg?seed=sticker')}
                 className="p-4 border border-stone-200 rounded-lg hover:bg-stone-50"
               >
                 Add Sticker
               </button>
            </div>
            <div className="border-4 border-white shadow-2xl rounded-lg overflow-hidden bg-white">
               <canvas ref={canvasRef} />
            </div>
            <button 
              onClick={finalize}
              className="mt-12 bg-stone-900 text-stone-50 px-12 py-4 rounded-full text-xl font-playfair"
            >
              Finish & Share
            </button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
