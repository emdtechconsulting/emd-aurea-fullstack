export interface LoginResponse {
  username: string;
  role: string;
  message: string;
  token: string;
  tokenType: string;
  expiresIn: number;
}
