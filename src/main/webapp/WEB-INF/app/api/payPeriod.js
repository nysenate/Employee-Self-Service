import { fetchApiJson } from "app/api/fetchJson";

export function getPayPeriods(payPeriodType, year) {
  return fetchApiJson(`/periods/${payPeriodType}?year=${year}`);
}
