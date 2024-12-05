import { fetchApiJson } from "app/api/fetchJson";

export function getTrainings(activeOnly) {
  return fetchApiJson(`/personnel/task?activeOnly=${activeOnly}`)
}