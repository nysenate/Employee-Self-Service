import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

export function useSearchTaskAssignments(searchParams) {
  return useQuery({
    queryKey: [ 'tasks', 'assignments', 'search', searchParams ],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/emp/search?${buildQueryString(searchParams)}`)
        .then((body) => body.result)
    },
    cacheTime: 0 // Disable caching for this query.
  })
}

function buildQueryString(params) {
  const keyValuePairs = [];
  for (const key in params) {
    if (params[key] !== '' && params[key] !== null) {
      if (Array.isArray(params[key]) && params[key].length > 0) {
        params[key].forEach(value => {
          keyValuePairs.push(key + '=' + value);
        });
      } else if (!Array.isArray(params[key])) {
        keyValuePairs.push(key + '=' + params[key]);
      }
    }
  }
  let queryString = keyValuePairs.join('&');
  return queryString;
}