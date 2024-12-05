import { fetchApiJson } from "app/api/fetchJson";

export function getEmployeeActiveYears(empId, useFiscalYears) {
  return fetchApiJson(`/employees/activeYears?empId=${empId}&fiscalYear=${useFiscalYears}`)
}