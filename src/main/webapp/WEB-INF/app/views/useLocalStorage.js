import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function useLocalStorage() {
  const { data: user } = useRequireAuthedUser();

  // Prefixes the key with the logged in empId.
  const getKey = (key) => {
    return `${user?.employeeId}-${key}`;
  };

  /**
   * Saves the given key value pair in local storage.
   * @param key
   * @param value
   */
  const save = (key, value) => {
    try {
      localStorage.setItem(getKey(key), JSON.stringify(value));
    } catch (e) {
      throw e;
    }
  };

  /**
   * Attempts to load the given key from local storage.
   * Throws an error if key's value cannot be parsed into a JSON object.
   *
   * @param key
   * @returns {any|null} The key's value parsed into an object or null if key was not found.
   */
  const load = (key) => {
    const item = localStorage.getItem(getKey(key));
    if (item == null) {
      return null;
    }
    try {
      return JSON.parse(item);
    } catch (e) {
      throw e;
    }
  };

  /**
   * Remove the given key from local storage.
   * @param key
   */
  const remove = (key) => {
    localStorage.removeItem(getKey(key));
  };

  return { save, load, remove };
}
