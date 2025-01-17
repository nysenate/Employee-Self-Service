import { fetchApiJson } from "app/api/fetchJson";

export function getSupplyDestinations(empId) {
  return fetchApiJson(`/supply/destinations/${empId}`)
}
