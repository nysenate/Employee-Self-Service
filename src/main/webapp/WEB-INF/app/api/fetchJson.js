/**
 * Make a call to the ESS Java backend API.
 *
 * @param {String} path               The API path to call, omit the "/api/v1" part.
 *                                    i.e. Use "/employees?active=true" to call "/api/v1/employees?active=true".
 * @param {Object} opts               The Options for this fetch call
 * @param {String} [opts.method=GET]  The http method. Defaults to "GET".
 * @param {Object|FormData} [opts.payload] Http payload data for POST, PUT, or PATCH.
 */
export async function fetchApiJson(path, opts = {}) {
  const { payload, headers: suppliedHeaders, ...fetchOptions } = opts;
  const isFormData = payload instanceof FormData;
  const headers = {
    Accept: "application/json",
    ...suppliedHeaders,
  };

  if (!isFormData && payload !== undefined) {
    headers["Content-Type"] = "application/json";
  }

  const init = {
    ...fetchOptions,
    method: opts.method || "GET",
    headers,
    cache: "no-store",
  };

  if (payload !== undefined) {
    init.body = isFormData ? payload : JSON.stringify(payload);
  }

  return fetchJson(`/api/v1${path}`, init);
}

export async function fetchJson(input, init) {
  // Error will be thrown here if network error.
  const response = await fetch(input, init);

  // response.ok is true when res.status is 2xx
  // https://developer.mozilla.org/en-US/docs/Web/API/Response/ok
  if (response.ok) {
    return response.json();
  }

  // Unsuccessful response, throw error.
  const data = await response.json();
  throw new FetchError({
    message: response.statusText,
    response,
    data,
  });
}

export class FetchError extends Error {
  response;
  data;

  constructor({ message, response, data }) {
    // Pass remaining arguments (including vendor specific ones) to parent constructor
    super(message);

    // Maintains proper stack trace for where our error was thrown (only available on V8)
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, FetchError);
    }

    this.name = "FetchError";
    this.response = response;
    this.data = data ?? { message: message };
  }
}
