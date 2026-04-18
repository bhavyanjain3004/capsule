'use client';

import { useEffect, useRef, useState } from 'react';
import { fabric } from 'fabric';

interface DoodleCanvasProps {
  onSave: (svgData: string) => void;
}

export default function DoodleCanvas({ onSave }: DoodleCanvasProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const fabricCanvas = useRef<fabric.Canvas | null>(null);
  const [color, setColor] = useState('#1a1a1a');

  useEffect(() => {
    if (!canvasRef.current) return;

    fabricCanvas.current = new fabric.Canvas(canvasRef.current, {
      isDrawingMode: true,
      width: 400,
      height: 400,
      backgroundColor: 'transparent',
    });

    // Configure brush
    const brush = new fabric.PencilBrush(fabricCanvas.current);
    brush.color = color;
    brush.width = 3;
    fabricCanvas.current.freeDrawingBrush = brush;

    return () => {
      fabricCanvas.current?.dispose();
    };
  }, []);

  useEffect(() => {
    if (fabricCanvas.current && fabricCanvas.current.freeDrawingBrush) {
      fabricCanvas.current.freeDrawingBrush.color = color;
    }
  }, [color]);

  const handleClear = () => {
    fabricCanvas.current?.clear();
  };

  const handleExport = () => {
    if (fabricCanvas.current) {
      const svg = fabricCanvas.current.toSVG();
      onSave(svg);
    }
  };

  return (
    <div className="flex flex-col items-center gap-4">
      <div className="border border-ink/10 paper-shadow bg-paper-light rounded-sm overflow-hidden p-4">
        <canvas ref={canvasRef} className="cursor-crosshair" />
      </div>
      
      <div className="flex items-center gap-6">
        <div className="flex gap-2">
          {['#1a1a1a', '#800000', '#c5a059'].map((c) => (
            <button
              key={c}
              onClick={() => setColor(c)}
              className={`w-6 h-6 rounded-full border border-ink/10 ${color === c ? 'ring-2 ring-accent-gold' : ''}`}
              style={{ backgroundColor: c }}
            />
          ))}
        </div>
        
        <div className="flex gap-3">
          <button 
            onClick={handleClear}
            className="text-xs uppercase tracking-widest font-serif text-ink-muted hover:text-ink transition-colors"
          >
            Clear
          </button>
          <button 
            onClick={handleExport}
            className="px-4 py-2 bg-accent-gold text-paper text-xs uppercase tracking-widest font-serif hover:bg-accent-gold/90 transition-colors"
          >
            Save Doodle
          </button>
        </div>
      </div>
    </div>
  );
}
