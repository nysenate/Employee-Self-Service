// Serializes params into a string of query parameters.
// Params with empty values are not included. // TODO include them as empty strings???
export function buildQueryString(params) {
  const keyValuePairs = [];
  for (const key in params) {
    if (params[key] !== "" && params[key] != null) {
      if (Array.isArray(params[key]) && params[key].length > 0) {
        params[key].forEach((value) => {
          keyValuePairs.push(key + "=" + value);
        });
      } else if (!Array.isArray(params[key])) {
        keyValuePairs.push(key + "=" + params[key]);
      }
    }
  }
  let queryString = keyValuePairs.join("&");
  return queryString;
}
