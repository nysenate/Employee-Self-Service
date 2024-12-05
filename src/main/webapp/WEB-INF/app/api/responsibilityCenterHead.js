import { fetchApiJson } from "app/api/fetchJson";


export function searchResponsibilityCenterHeads(term) {
  return fetchApiJson(`/respctr/head/search?limit=ALL&offset=1&term=${term}`)
}