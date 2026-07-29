import { useInfiniteQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";

/** Matches the legacy directive's page size (assets/js/src/common/employee-search-directive.js). */
const PAGE_SIZE = 50;

/**
 * Searches employees by name, one page at a time.
 *
 * The result is a TanStack infinite query: call fetchNextPage to grow the window, and read
 * hasNextPage to know whether more remain. The flattened list of employees loaded so far is
 * exposed as data.employees.
 *
 * @param term The search term, matched against employee full names.
 * @param activeOnly Whether to restrict the search to currently active employees.
 */
export function useEmployeeSearch(term, activeOnly) {
  const query = useInfiniteQuery({
    queryKey: ["employeeSearch", term, activeOnly],
    queryFn: ({ pageParam }) => {
      // The API offset is one based: the first row is offset 1, so each page starts one past
      // however many rows have already been loaded.
      const params = new URLSearchParams({
        term,
        activeOnly: String(activeOnly),
        limit: String(PAGE_SIZE),
        offset: String(pageParam),
      });
      return fetchApiJson(`/employees/search?${params}`);
    },
    initialPageParam: 1,
    getNextPageParam: (lastPage, allPages) => {
      const loaded = allPages.reduce(
        (count, page) => count + page.employees.length,
        0,
      );
      return loaded < lastPage.total ? loaded + 1 : undefined;
    },
    staleTime: 1000 * 30,
    throwOnError: true,
  });

  const employees = query.data?.pages.flatMap((page) => page.employees) ?? [];
  const total = query.data?.pages[0]?.total ?? 0;

  return { ...query, employees, total };
}
