import axios from 'axios';

export interface FormError {
  message: string;
  fieldErrors: Record<string, string>;
}

const fail = (message: string): FormError => ({ message, fieldErrors: {} });

// The backend sends { error } (401/409) or { detail } (ResponseStatusException)
function extractMessage(data: any): string | null {
  if (typeof data?.error === 'string') return data.error;
  if (typeof data?.detail === 'string') return data.detail;
  return null;
}

/** Turns whatever login/sign-up threw into something a form can show. */
export function mapAuthError(e: unknown): FormError {
  if (!axios.isAxiosError(e)) {
    return fail('Something went wrong.');
  }
  if (!e.response) {
    return fail("Can't reach the server. Check your Wi-Fi and API address.");
  }

  const { status, data } = e.response;
  const message = extractMessage(data);

  switch (status) {
    case 400:
      if (message) return fail(message);
      // Bean validation: { username: "...", password: "..." }
      if (data && typeof data === 'object') {
        return { message: 'Please fix the highlighted fields.', fieldErrors: data as Record<string, string> };
      }
      return fail('Invalid request.');
    case 401:
      return fail(message ?? 'Invalid username or password.');
    case 409:
      return fail(message ?? 'That username is already taken.');
    default:
      return fail(message ?? `Server error (${status}).`);
  }
}