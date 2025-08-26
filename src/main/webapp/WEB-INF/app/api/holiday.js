import { fetchApiJson } from "app/api/fetchJson";

export function getHolidays(year) {
  return fetchApiJson(`/holidays/${year}`);
}
