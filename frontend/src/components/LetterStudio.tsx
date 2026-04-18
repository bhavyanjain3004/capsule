'use client';

import { useEffect, useRef, useState } from 'react';
import { fabric } from 'fabric';

interface LetterStudioProps {
  onSave: (data: { canvasJson: string; svgData: string }) => void;
  isReadOnly?: boolean;
  initialData?: string;
}

const DEFAULT_INKS = [
  { name: 'Ink Black', value: '#1a1a1a' },
  { name: 'Midnight Gold', value: '#b08b41' },
  { name: 'Vintage Burgundy', value: '#631010' },
  { name: 'Wax Red', value: '#800000' }
];

const STICKERS = [
  { id: 'seal', emoji: '🔏', label: 'Wax Seal' },
  { id: 'heart', emoji: '❤️', label: 'Heart' },
  { id: 'watch', emoji: '⌚', label: 'Watch' },
  { id: 'plane', emoji: '✈️', label: 'Plane' },
];

export default function LetterStudio({ onSave, isReadOnly = false, initialData }: LetterStudioProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const colorInputRef = useRef<HTMLInputElement>(null);
  const fabricCanvas = useRef<fabric.Canvas | null>(null);
  
  const [tool, setTool] = useState<'select' | 'pen' | 'text'>('select');
  const [color, setColor] = useState('#1a1a1a');
  const [userInks, setUserInks] = useState<string[]>(['#ffffff', '#ffffff', '#ffffff', '#ffffff']);
  const [archive, setArchive] = useState<string[]>([]); // Array of SVG strings

  // Initialize Canvas and Archive
  useEffect(() => {
    if (!canvasRef.current) return;

    fabricCanvas.current = new fabric.Canvas(canvasRef.current, {
      width: 500,
      height: 600,
      backgroundColor: '#f9f6f0',
    });

    // Load Archive from LocalStorage
    const savedArchive = localStorage.getItem('capsule_doodle_archive');
    if (savedArchive) {
      try {
        setArchive(JSON.parse(savedArchive));
      } catch (e) {
        console.error('Failed to load archive');
      }
    }

    fabricCanvas.current.on('path:created', (opt) => {
      const path = opt.path;
      if (path) {
        path.set({
          selectable: true,
          hasControls: true,
          hasBorders: true,
        });
        
        // Save to Archive Logic
        const svg = path.toSVG();
        setArchive(prev => {
          const newArchive = [svg, ...prev.slice(0, 5)]; // Keep last 6
          localStorage.setItem('capsule_doodle_archive', JSON.stringify(newArchive));
          return newArchive;
        });

        fabricCanvas.current?.setActiveObject(path);
        setTool('select');
      }
    });

    if (initialData) {
      fabricCanvas.current.loadFromJSON(initialData, () => {
        fabricCanvas.current?.renderAll();
      });
    } else if (!isReadOnly) {
      const text = new fabric.IText('Compose your legacy...', {
        left: 50,
        top: 50,
        fontFamily: 'serif',
        fontSize: 24,
        fill: '#1a1a1a',
      });
      fabricCanvas.current.add(text);
    }

    if (isReadOnly) {
      fabricCanvas.current.selection = false;
      fabricCanvas.current.forEachObject((obj) => {
        obj.selectable = false;
        obj.evented = false;
      });
    }

    return () => {
      fabricCanvas.current?.dispose();
    };
  }, [initialData, isReadOnly]);

  useEffect(() => {
    if (!fabricCanvas.current || isReadOnly) return;

    if (tool === 'pen') {
      fabricCanvas.current.isDrawingMode = true;
      const brush = new fabric.PencilBrush(fabricCanvas.current);
      brush.color = color;
      brush.width = 3;
      fabricCanvas.current.freeDrawingBrush = brush;
    } else {
      fabricCanvas.current.isDrawingMode = false;
    }
  }, [tool, color, isReadOnly]);

  const addText = () => {
    const text = new fabric.IText('Write here...', {
      left: 100,
      top: 150,
      fontFamily: 'serif',
      fontSize: 20,
      fill: color,
    });
    fabricCanvas.current?.add(text);
    fabricCanvas.current?.setActiveObject(text);
    setTool('select');
  };

  const addSticker = (emoji: string) => {
    const text = new fabric.Text(emoji, {
      left: 200,
      top: 200,
      fontSize: 60,
    });
    fabricCanvas.current?.add(text);
    fabricCanvas.current?.setActiveObject(text);
    setTool('select');
  };

  const addFromArchive = (svgString: string) => {
    fabric.loadSVGFromString(svgString, (objects, options) => {
      const obj = fabric.util.groupSVGElements(objects, options);
      obj.set({
        left: 150,
        top: 150,
        selectable: true
      });
      fabricCanvas.current?.add(obj);
      fabricCanvas.current?.setActiveObject(obj);
      fabricCanvas.current?.renderAll();
    });
    setTool('select');
  };

  const deleteActive = () => {
    const active = fabricCanvas.current?.getActiveObject();
    if (active) {
      fabricCanvas.current?.remove(active);
      fabricCanvas.current?.renderAll();
    }
  };

  const handleCustomColor = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newColor = e.target.value;
    setColor(newColor);
    setUserInks(prev => [newColor, ...prev.slice(0, 3)]);
  };

  const handleExport = () => {
    if (fabricCanvas.current) {
      onSave({
        canvasJson: JSON.stringify(fabricCanvas.current.toObject()),
        svgData: fabricCanvas.current.toSVG()
      });
    }
  };

  return (
    <div className="flex flex-col items-center gap-6 select-none">
      {!isReadOnly && (
        <div className="flex flex-col gap-4 bg-paper-dark p-4 paper-shadow border border-ink/5 w-full max-w-xl">
          <div className="flex items-center justify-between">
            <div className="flex bg-paper-light rounded-sm overflow-hidden border border-ink/10">
              <button onClick={() => setTool('select')} className={`px-4 py-2 text-[10px] uppercase tracking-widest font-serif ${tool === 'select' ? 'bg-ink text-paper' : ''}`}>Pointer</button>
              <button onClick={() => setTool('pen')} className={`px-4 py-2 text-[10px] uppercase tracking-widest font-serif ${tool === 'pen' ? 'bg-ink text-paper' : ''}`}>Doodle</button>
              <button onClick={addText} className="px-4 py-2 text-[10px] uppercase tracking-widest font-serif hover:bg-paper-dark">+ Text</button>
            </div>
            
            <div className="flex gap-4 items-center">
              <div className="flex gap-2">
                {STICKERS.map(s => (
                  <button key={s.id} onClick={() => addSticker(s.emoji)} className="text-xl hover:scale-125 transition-transform" title={s.label}>{s.emoji}</button>
                ))}
              </div>
              <div className="w-[1px] h-6 bg-ink/10" />
              <div className="flex gap-2">
                <button 
                  onClick={deleteActive} 
                  className="p-2 hover:bg-accent-burgundy hover:text-paper rounded-full transition-colors" 
                  title="Erase Selection"
                >
                  🗑️
                </button>
                <button 
                  onClick={() => { if(confirm('Clear the entire paper?')) fabricCanvas.current?.clear(); }} 
                  className="p-2 hover:bg-ink hover:text-paper rounded-full transition-colors" 
                  title="Reset Paper"
                >
                  🧹
                </button>
              </div>
            </div>
          </div>

          <div className="flex flex-col gap-4 border-t border-ink/5 pt-4">
             {/* Ink Lab */}
             <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase tracking-widest font-serif text-ink/40 italic">Ink Laboratory</span>
                <button 
                  onClick={() => colorInputRef.current?.click()}
                  className="text-[10px] uppercase tracking-widest font-serif text-accent-gold underline hover:text-ink transition-colors"
                >
                  Mix Custom Color
                </button>
                <input type="color" ref={colorInputRef} className="hidden" onChange={handleCustomColor} value={color} />
             </div>

             <div className="flex items-center gap-6">
                <div className="flex gap-1.5 items-center">
                  <span className="text-[9px] uppercase tracking-[0.2em] font-serif text-ink/30">Presets:</span>
                  <div className="flex gap-1">
                    {DEFAULT_INKS.map(c => (
                      <button key={c.value} onClick={() => setColor(c.value)} className={`w-5 h-5 rounded-full border border-ink/10 ${color === c.value ? 'ring-2 ring-ink ring-offset-1' : ''}`} style={{ backgroundColor: c.value }} />
                    ))}
                  </div>
                </div>
                <div className="flex gap-1.5 items-center">
                  <span className="text-[9px] uppercase tracking-[0.2em] font-serif text-ink/30">Your Mixes:</span>
                  <div className="flex gap-1">
                    {userInks.map((c, i) => (
                      <button key={i} onClick={() => setColor(c)} className={`w-5 h-5 rounded-full border border-ink/10 transition-all ${color === c ? 'ring-2 ring-accent-gold ring-offset-1 scale-110' : ''}`} style={{ backgroundColor: c }} />
                    ))}
                  </div>
                </div>
             </div>

             {/* Doodle Archive */}
             {archive.length > 0 && (
               <div className="flex flex-col gap-2 border-t border-ink/5 pt-4">
                  <span className="text-[10px] uppercase tracking-widest font-serif text-ink/40">Doodle Archive (Session)</span>
                  <div className="flex gap-3 overflow-x-auto pb-2">
                    {archive.map((svg, i) => (
                      <button 
                        key={i} 
                        onClick={() => addFromArchive(svg)}
                        className="flex-shrink-0 w-12 h-12 bg-paper-light border border-ink/10 hover:border-accent-gold transition-colors p-1 flex items-center justify-center overflow-hidden"
                      >
                         <div dangerouslySetInnerHTML={{ __html: svg }} className="w-full h-full scale-[0.4]" />
                      </button>
                    ))}
                  </div>
               </div>
             )}
          </div>
        </div>
      )}

      <div className="relative border-[12px] border-paper-dark paper-shadow bg-[#f9f6f0]">
        <canvas ref={canvasRef} />
        <div className="absolute inset-0 pointer-events-none opacity-20 bg-[url('https://www.transparenttextures.com/patterns/natural-paper.png')]" />
        
        {!isReadOnly && tool === 'pen' && (
          <div className="absolute top-4 right-4 flex items-center gap-2">
            <div className="w-2 h-2 rounded-full bg-accent-gold animate-ping" />
            <span className="text-[10px] uppercase tracking-widest font-serif text-accent-gold bg-paper-light px-2 py-1 paper-shadow">Ink Mode</span>
          </div>
        )}
      </div>

      {!isReadOnly && (
        <button 
          onClick={handleExport}
          className="px-12 py-5 bg-ink text-paper rounded-none font-serif text-lg paper-shadow uppercase tracking-[0.4em] hover:bg-accent-gold transition-all"
        >
          Preserve this composing
        </button>
      )}
    </div>
  );
}
