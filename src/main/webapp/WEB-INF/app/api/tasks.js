import { fetchApiJson } from "app/api/fetchJson";


export function getTasks(activeOnly) {
  return fetchApiJson(`/personnel/task?activeOnly=${activeOnly}`)
}