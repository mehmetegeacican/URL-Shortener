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

// Every field is optional; the backend applies its own defaults
export interface AdminUrlQuery {
  page?: number;
  size?: number;
  search?: string;
  status?: AdminUrlStatus;
  userId?: string;
  sort?: 'id' | 'code';
  direction?: 'asc' | 'desc';
}