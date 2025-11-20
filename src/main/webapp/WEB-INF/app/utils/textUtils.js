export function toCurrency(value) {
  const formatter = new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  });

  if (value === null || value === undefined || value === "") {
    return "";
  }

  const numericValue = typeof value === "number" ? value : Number(value);
  if (Number.isNaN(numericValue)) {
    return "";
  }

  return formatter.format(numericValue);
}
