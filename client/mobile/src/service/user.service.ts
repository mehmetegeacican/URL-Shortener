
import axios from 'axios';
import { AuthResponse } from '@/types/user.type';
 
const api = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});
 
export const userService = {
  async signUp(username: string, password: string): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/user/sign-up', { username, password });
    return response.data;
  },
  async login(username: string, password: string): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/user/login', { username, password });
    return response.data;
  },
};