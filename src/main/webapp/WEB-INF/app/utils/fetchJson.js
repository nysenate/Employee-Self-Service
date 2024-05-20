/**
 * Make a call to the ESS Java backend API.
 *
 * @param {String} path               The API path to call, omit the "/api/v1" part.
 *                                    i.e. Use "/employees?active=true" to call "/api/v1/employees?active=true".
 * @param {Object} opts               The Options for this fetch call
 * @param {String} [opts.method=GET]  The http method. Defaults to "GET".
 * @param {Object} [opts.payload]     Http payload data. Only needed/used if opts.method = POST.
 */
export async function fetchApiJson(path, opts) {
  const init = {
    method: opts?.method || "GET",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json",
    },
    cache: 'no-store',
  }

  if (opts?.method === "POST") {
    init.body = JSON.stringify(opts.payload);
  }

  return fetchJson(`/api/v1${path}`, init)
}

export async function fetchJson(input, init) {
  const response = await fetch(input, init);

  // if the server replies, there's always some data in json
  // if there's a network error, it will throw at the previous line
  const data = await response.json();

  // response.ok is true when res.status is 2xx
  // https://developer.mozilla.org/en-US/docs/Web/API/Response/ok
  if (response.ok) {
    return data;
  }

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

