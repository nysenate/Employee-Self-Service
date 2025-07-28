export const itemKeys = {
  all: () => ["supply", "items"],
  list: () => [...itemKeys.all(), "list"],
  search: (query) => [...itemKeys.all(), "search", query],
};
