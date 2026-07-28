export function pingApi(isActive) {
  let url = `/api/v1/timeout/ping?active=${isActive}`;
  const options = {
    method: "POST",
    headers: new Headers({ Accept: ["application/json", "text/plain", "*/*"] }),
  };
  return fetchUrl(url, options);
}

async function fetchUrl(url, options) {
  const response = await fetch(url, options);
  const data = await response.json();
  if (!response.ok || !data.success) {
    const error = new Error(data.message || response.statusText);
    error.status = response.status;
    throw error;
  }
  return data;
}
