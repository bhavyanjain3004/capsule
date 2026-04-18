const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const api = {
  async createCapsule(data: any) {
    const res = await fetch(`${BASE_URL}/capsule/create`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return res.json();
  },

  async uploadFile(token: string, file: File) {
    const formData = new FormData();
    formData.append('file', file);
    const res = await fetch(`${BASE_URL}/capsule/${token}/files`, {
      method: 'POST',
      body: formData,
    });
    return res.json();
  },

  async saveDoodle(token: string, doodle: any) {
    const res = await fetch(`${BASE_URL}/capsule/${token}/doodles`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doodle),
    });
    return res.json();
  },

  async getPreview(token: string) {
    const res = await fetch(`${BASE_URL}/capsule/${token}/preview`);
    return res.json();
  },

  async verifyAndReveal(token: string, email: string) {
    const res = await fetch(`${BASE_URL}/capsule/${token}/verify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email }),
    });
    if (!res.ok) throw new Error('Access Denied');
    return res.json();
  }
};
