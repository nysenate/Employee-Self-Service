export function txValue(transactions, key) {
  return transactions?.[key]?.value ?? "";
}

export function formattedTxValue(transactions, key, formatter) {
  const value = transactions?.[key]?.value;
  if (value == null || value === "") {
    return "";
  }

  return formatter(value);
}
