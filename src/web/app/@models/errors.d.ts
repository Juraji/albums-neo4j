interface ErrorDto {
  type: ErrorType;
  requestId: string;
  status: string;
  message: string;
}

type ErrorType = 'GENERIC' | 'VALIDATION' | 'FIELD_VALIDATION';

interface FieldValidationErrorDto extends ErrorDto {
  type: 'FIELD_VALIDATION';
  fieldErrors: Record<string, string>; // {[Field name]: [Message]}
}
