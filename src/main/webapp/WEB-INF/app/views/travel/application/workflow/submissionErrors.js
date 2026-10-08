/** Only explicit access or validation rejections establish a known outcome. */
export function classifyApplicationSubmissionError(error) {
  const status = error?.response?.status;
  if (status === 401 || status === 403) return "access";
  if (status === 400 || status === 422) return "correction";
  return "unknown-outcome";
}
