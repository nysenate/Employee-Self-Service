import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

export function useSearchTaskAssignments(state) {
  const queryParams = searchTaskAssignmentsQueryParams(state)
  return useQuery({
    queryKey: ['tasks', 'assignments', 'search', queryParams],
    queryFn: () => {
      return fetchApiJson(`/personnel/task/emp/search?${queryParams}`)
    },
    cacheTime: 0 // Disable caching for this query.
  })
}

// Converts the state object into string of query parameters for the search task assignments endpoints.
export function searchTaskAssignmentsQueryParams(state) {
  const searchParams = {
    ...state,
    respCtrHead: state.respCtrHead.map(r => r.code)
  }
  return buildQueryString(searchParams)
}

// Serializes params into a string of query parameters.
// Params with empty values are not included.
function buildQueryString(params) {
  const keyValuePairs = [];
  for (const key in params) {
    if (params[key] !== '' && params[key] != null) {
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