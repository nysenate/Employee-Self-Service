
/**
 * Format a date string to a readable format.
 *
 * @param {String} dateString The date string to format.
 * @returns {String} The formatted date string.
 */
export function formatDate(dateString) {
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  };
  return new Date(dateString).toLocaleString('en-US', options);
}

/**
 * Format a date string to MM/DD/YYYY.
 *
 * @param {String} dateString The date string to format (in YYYY-MM-DD format).
 * @returns {String} The formatted date string.
 */
export function formatDateToMMDDYYYY(dateString){
  const [year, month, day] = dateString.split('-');
  return `${month}/${day}/${year}`;
};

export function formatDateYYYYMMDD(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}