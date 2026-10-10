export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInSeconds: number;
  userId: string;
  username: string;
  admin: boolean;
}