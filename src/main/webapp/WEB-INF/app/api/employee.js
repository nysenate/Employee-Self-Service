import { fetchApiJson } from "app/api/fetchJson";


export function getEmployee(empId, detail) {
  return fetchApiJson(`/employees?empId=${empId}&detail=${detail}`)
}