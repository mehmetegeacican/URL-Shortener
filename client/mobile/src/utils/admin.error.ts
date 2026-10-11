import axios from 'axios';

export function mapAdminError(e: unknown): string {
  if (!axios.isAxiosError(e)) {
    return 'Something went wrong.';
  }
  if (!e.response) {
    return "Can't reach the server. Check your connection and the API address.";
  }

  const { status, data } = e.response;

  switch (status) {
    case 400:
      return typeof data?.detail === 'string' ? data.detail : 'Invalid request.';
    case 401:
      return 'Your session has expired or you are not logged in. Log in again.';
    case 403:
      return 'This account is not an admin.';
    default:
      return `Server error (${status}).`;
  }
}