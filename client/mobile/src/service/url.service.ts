import axios from 'axios';
import {Url} from '@/types/url.type';

const api = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const urlService = {
  async getAllUrls(): Promise<Url[]> {
    const response = await api.get<Url[]>("/all");
    return response.data;
  },
  async createUrl(url: string, code?:string): Promise<Url> {
    const response = await api.post<Url>("/create", { url , code : code || undefined });
    return response.data;
  },
};