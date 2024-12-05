import { fetchApiJson } from "app/api/fetchJson";


export function getEmployeeTransactions(empId) {
  return fetchApiJson(`/empTransactions/snapshot/current?empId=${empId}`)
}