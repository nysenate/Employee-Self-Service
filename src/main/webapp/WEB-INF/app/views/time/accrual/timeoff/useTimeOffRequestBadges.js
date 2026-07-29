import { useMemo } from "react";
import { format } from "date-fns";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import {
  useEmployeeTimeOffRequests,
  useSupervisorPendingRequests,
} from "app/views/time/accrual/timeoff/useTimeOffRequests";
import { countRequestStatuses } from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * The counts shown on the time off request links in the side navigation.
 *
 * Ported from the badgeService values the legacy pages published
 * ("activeRequestCount", "activeApprovedRequestCount", "activeRejectedRequestCount" and
 * "pendingRequestCount"). In the legacy app these were only ever set once the corresponding
 * page had been opened, so the badges stayed blank until then; here they are queried directly.
 */
export function useTimeOffRequestBadges() {
  const { data: user } = useRequireAuthedUser();
  const empId = user?.employeeId;

  const today = format(new Date(), "yyyy-MM-dd");
  const active = useEmployeeTimeOffRequests(empId, today, null);

  return useMemo(() => countRequestStatuses(active.data), [active.data]);
}

/** The number of requests waiting on the user as a supervisor. */
export function useSupervisorRequestBadge() {
  const { data: user } = useRequireAuthedUser();
  const pending = useSupervisorPendingRequests(user?.employeeId);
  return pending.data?.length ?? 0;
}
