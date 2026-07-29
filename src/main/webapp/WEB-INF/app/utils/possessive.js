/**
 * Converts a noun into its possessive form: "Smith" becomes "Smith's", "Jones" becomes "Jones'".
 * Ported from the legacy possessive filter (assets/js/src/common/possessive-filter.js).
 */
export function possessive(noun) {
  if (typeof noun !== "string" || noun.length === 0) {
    return noun;
  }
  return noun.at(-1).toLowerCase() === "s" ? `${noun}'` : `${noun}'s`;
}
