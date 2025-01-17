/**
 * Saves the given key, value pair in local storage.
 * @param key
 * @param value
 */
export function save(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch (e) {
    throw e
  }
}

/**
 * Attempts to load the given key from local storage.
 * Throws an error if key's value cannot be parsed into a JSON object.
 *
 * @param key
 * @returns {any|null} The key's value parsed into an object or null if key was not found.
 */
export function load(key) {
  const item = localStorage.getItem(key)
  if (item == null) {
    return null
  }
  try {
    return JSON.parse(item);
  } catch (e) {
    throw e
  }
}

/**
 * Remove the given key from local storage.
 * @param key
 */
export function remove(key) {
  localStorage.removeItem(key)
}
