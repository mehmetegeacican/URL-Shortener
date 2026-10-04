export const CODE_MIN_LENGTH = 4;
export const CODE_MAX_LENGTH = 20;

const CODE_PATTERN = /^[A-Za-z0-9_-]+$/;

export function validateCode(code: string): string | null {
  if (code === '') return null;

  if (code.length < CODE_MIN_LENGTH || code.length > CODE_MAX_LENGTH) {
    return `Code must be ${CODE_MIN_LENGTH}-${CODE_MAX_LENGTH} characters long.`;
  }
  if (!CODE_PATTERN.test(code)) {
    return 'Use only letters, numbers, "-" and "_".';
  }
  
  return null;
}