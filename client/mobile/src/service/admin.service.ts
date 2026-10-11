import axios from 'axios';
import { AdminStats, AdminUrl, AdminUrlQuery, PageResponse } from '@/types/admin.type';

const api = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

const authHeaders = (token: string) => ({ Authorization: `Bearer ${token}` });

export const adminService = {
  async getUrls(token: string, query: AdminUrlQuery = {}): Promise<PageResponse<AdminUrl>> {
    console.log(authHeaders(token))
    const response = await api.get<PageResponse<AdminUrl>>('/admin/urls/all', {
      params: { ...query, search: query.search?.trim() || undefined },
      headers: authHeaders(token),
    });
    return response.data;
  },
  async restoreUrl(token: string, code: string): Promise<void> {
    await api.post(`/admin/urls/${encodeURIComponent(code)}/restore`, undefined, {
      headers: authHeaders(token),
    });
  },
  async getStats(token: string): Promise<AdminStats> {
    const response = await api.get<AdminStats>('/admin/stats', {
      headers: authHeaders(token),
    });
    return response.data;
  },
};