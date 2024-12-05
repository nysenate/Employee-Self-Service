import { fetchApiJson } from "app/api/fetchJson";


export function getEmployeeAlertInfo(empId) {
  return fetchApiJson(`/alert-info?empId=${empId}`)
}

export function updateEmployeeAlertInfo(data) {
  return fetchApiJson(`/alert-info`, { method: "POST", payload: data })
}
