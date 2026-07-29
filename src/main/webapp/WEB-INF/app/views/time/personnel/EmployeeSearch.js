import React, { useRef } from "react";
import { cn } from "app/utils/cn";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import InputDebounced from "app/components/InputDebounced";
import { useEmployee } from "app/views/useEmployee";
import { useEmployeeSearch } from "app/views/time/personnel/useEmployeeSearch";

/**
 * Employee search: a search bar over employee names with an infinite-scrolling result list,
 * which collapses to a details card once an employee is picked.
 * Ported from the legacy employeeSearch directive (assets/js/src/common/employee-search-directive.js).
 *
 * @param selectedEmpId The currently selected employee id, or null while searching.
 * @param term The current search term.
 * @param activeOnly Whether the search is restricted to active employees.
 * @param onTermChange Called with the new term when the search box changes.
 * @param onActiveOnlyChange Called with the new value when the active-only checkbox toggles.
 * @param onSelect Called with an employee id when a result is picked.
 * @param onClear Called when the selected employee is deselected.
 */
export default function EmployeeSearch({
  selectedEmpId,
  term,
  activeOnly,
  onTermChange,
  onActiveOnlyChange,
  onSelect,
  onClear,
}) {
  return (
    <div className="bg-white shadow-sm">
      <div className="p-3">
        {selectedEmpId ? (
          <SelectedEmployee empId={selectedEmpId} onClear={onClear} />
        ) : (
          <SearchForm
            term={term}
            activeOnly={activeOnly}
            onTermChange={onTermChange}
            onActiveOnlyChange={onActiveOnlyChange}
            onSelect={onSelect}
          />
        )}
      </div>
    </div>
  );
}

function SearchForm({
  term,
  activeOnly,
  onTermChange,
  onActiveOnlyChange,
  onSelect,
}) {
  const search = useEmployeeSearch(term, activeOnly);
  const resultsRef = useRef(null);

  // Pull the next page in as the list nears the bottom, matching the legacy infinite scroll.
  const handleScroll = () => {
    const el = resultsRef.current;
    if (!el || !search.hasNextPage || search.isFetchingNextPage) {
      return;
    }
    if (el.scrollTop + el.clientHeight >= el.scrollHeight - 100) {
      search.fetchNextPage();
    }
  };

  const hasResults = search.employees.length > 0;

  return (
    <div className="flex flex-col items-center text-center">
      <InputDebounced
        id="employee-search"
        type="search"
        value={term}
        delay={300}
        placeholder="Search for an employee"
        onChange={onTermChange}
        className="w-80 text-base"
      />

      <label className="mt-3 flex items-center gap-2">
        <input
          type="checkbox"
          checked={activeOnly}
          onChange={(e) => onActiveOnlyChange(e.target.checked)}
        />
        Show only active employees
      </label>

      <div className="mt-3 h-6">
        {search.isFetching && !search.isFetchingNextPage && (
          <LoadingIndicator variant="sm" />
        )}
      </div>

      {hasResults ? (
        <ul
          ref={resultsRef}
          onScroll={handleScroll}
          className="my-2 max-h-[30em] w-[23em] overflow-auto border-y border-teal-600/25 text-sm"
        >
          {search.employees.map((emp) => (
            <li
              key={emp.empId}
              onClick={() => onSelect(emp.empId)}
              className="m-[2.5px] cursor-pointer px-2 py-1 hover:bg-teal-700 hover:text-gray-50"
              dangerouslySetInnerHTML={{
                __html: highlight(emp.fullName, term),
              }}
            />
          ))}
        </ul>
      ) : (
        term &&
        !search.isFetching && (
          <p className="my-2">No results found for &quot;{term}&quot;</p>
        )
      )}
    </div>
  );
}

function SelectedEmployee({ empId, onClear }) {
  const { data: employee, isPending } = useEmployee(empId);

  if (isPending) {
    return <LoadingIndicator variant="sm" />;
  }

  const status = employee.personnelStatus;

  return (
    <div className="flex flex-row flex-wrap justify-around gap-6">
      <table>
        <tbody>
          <InfoRow label="Selected">{employee.fullName}</InfoRow>
          <InfoRow label="Status">
            <span
              className={cn(
                "capitalize",
                status && !status.employed && "text-[#e64727]",
                status &&
                  status.description !== "ACTIVE" &&
                  status.employed &&
                  "text-orange-600",
              )}
            >
              {status?.description?.toLowerCase()}
            </span>
          </InfoRow>
          <InfoRow label="Emp. Id">{employee.employeeId}</InfoRow>
          <InfoRow label="Pay Type">{employee.payType}</InfoRow>
        </tbody>
      </table>

      <table>
        <tbody>
          <InfoRow label="Work Phone">{employee.workPhone}</InfoRow>
          <InfoRow label="Email">{employee.email}</InfoRow>
          <InfoRow label="Resp. Ctr.">
            {employee.respCtr?.respCenterHead?.name}
          </InfoRow>
        </tbody>
      </table>

      <div className="flex flex-col justify-around">
        <Button variant="secondary" onPress={onClear}>
          Select Another Employee
        </Button>
      </div>
    </div>
  );
}

function InfoRow({ label, children }) {
  return (
    <tr>
      <th className="pr-[5px] text-left align-top font-semibold">{label}</th>
      <td className="text-left">{children}</td>
    </tr>
  );
}

/** Escapes HTML so an employee's name can be injected, then wraps the matched term in <b>. */
function highlight(text, term) {
  const escaped = escapeHtml(text || "");
  const needle = (term || "").trim();
  if (!needle) {
    return escaped;
  }
  const pattern = new RegExp(`(${escapeRegExp(needle)})`, "gi");
  return escaped.replace(pattern, "<b>$1</b>");
}

function escapeHtml(text) {
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function escapeRegExp(text) {
  return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}
