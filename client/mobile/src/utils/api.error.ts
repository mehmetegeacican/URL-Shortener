import axios from 'axios';

export type FormErrors = {
  codeError?: string;
  generalError?: string;
};

type ErrorBody = { error?: string; code?: string; url?: string };

const NETWORK_MESSAGE = "Can't reach the server. Check your Wi-Fi and API address.";

export function mapCreateUrlError(e: unknown): FormErrors {
  if (!axios.isAxiosError(e)) {
    return { generalError: 'Failed to create URL' };
  }

  if (!e.response) {
    return { generalError: NETWORK_MESSAGE };
  }

  const { status } = e.response;
  const data = e.response.data as ErrorBody | undefined;

  switch (status) {
    case 409:
      return { codeError: 'That code is already taken. Try another one.' };

    case 400:
      if (data?.code || data?.url) {
        return { codeError: data.code, generalError: data.url };
      }
      return { generalError: 'Invalid request' };

    default:
      return { generalError: data?.error ?? `Server error (${status})` };
  }
}