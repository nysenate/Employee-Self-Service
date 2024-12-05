import { fetchApiJson } from "app/api/fetchJson";

export function getEmployeePaychecks(empId, year, useFiscalYear) {
  return fetchApiJson(`/paychecks?empId=${empId}&year=${year}&fiscalYear=${useFiscalYear}`)
}