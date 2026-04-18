import { useState, useEffect } from 'react';
import { api } from '@/lib/api';

export function useCapsule(token: string) {
  const [preview, setPreview] = useState<any>(null);
  const [revealed, setRevealed] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function load() {
      try {
        const data = await api.getPreview(token);
        setPreview(data);
      } catch (err) {
        setError('Capsule not found');
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [token]);

  const reveal = async (email: string) => {
    setLoading(true);
    try {
      const data = await api.verifyAndReveal(token, email);
      setRevealed(data);
      return data;
    } catch (err: any) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { preview, revealed, loading, error, reveal };
}
