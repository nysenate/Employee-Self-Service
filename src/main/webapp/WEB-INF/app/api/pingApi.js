import "core-js/stable";
import "regenerator-runtime/runtime";


export function pingApi(isActive) {
  let url = `/api/v1/timeout/ping?active=${isActive}`
  const options = {
    method: "POST",
    headers: new Headers({'Accept': ["application/json", "text/plain", "*/*"]})
  }
  return fetchUrl(url, options)
}

async function fetchUrl(url, options) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
