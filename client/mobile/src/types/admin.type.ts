// Matches AdminUrlResponse from the backend
export interface AdminUrl {
  code: string;
  url: string;
  deleted: boolean;
  ownerId: string | null;
  ownerUsername: string | null;
  createdAt: string | null; 
  clickCount: number;
  lastClickedAt: string | null; 
}

// Matches PageResponse<T> from the backend
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type AdminUrlStatus = 'active' | 'deleted' | 'all';

export interface AdminUrlQuery {
  page?: number;
  size?: number;
  search?: string;
  status?: AdminUrlStatus;
  userId?: string;
  sort?: 'id' | 'code';
  direction?: 'asc' | 'desc';
}

export interface UrlStats {
  total: number;
  active: number;
  deleted: number;
  anonymous: number;
  createdToday: number;
}
 
export interface UserStats {
  total: number;
  admins: number;
}
 
export interface ClickStats {
  total: number;
  today: number;
  uniqueIps: number;
}
 
export interface TopUrl {
  code: string;
  url: string;
  clicks: number;
}
 
export interface TopIp {
  ip: string;
  clicks: number;
}
 
export interface DailyClicks {
  date: string; // yyyy-MM-dd
  clicks: number;
}
 
export interface AdminStats {
  urls: UrlStats;
  users: UserStats;
  clicks: ClickStats;
  topUrls: TopUrl[];
  topIps: TopIp[];
  clicksPerDay: DailyClicks[];
}