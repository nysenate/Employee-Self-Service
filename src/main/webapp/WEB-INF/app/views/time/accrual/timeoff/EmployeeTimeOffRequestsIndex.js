import React, { useMemo, useState } from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import Button from "app/components/Button";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssertPermission from "app/components/AssertPermission";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useSupEmpGroup } from "app/views/time/personnel/useSupEmpGroup";
import TimeOffRequestApprovalTable from "app/views/time/accrual/timeoff/TimeOffRequestApprovalTable";
import TimeOffRequestReviewModal from "app/views/time/accrual/timeoff/TimeOffRequestReviewModal";
import {
  useReviewTimeOffRequests,
  useSupervisorActiveRequests,
  useSupervisorPendingRequests,
} from "app/views/time/accrual/timeoff/useTimeOffRequests";
import {
  compareRequests,
  formatRequests,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * A supervisor's time off request queue: the ones waiting on them, and the ones they have
 * already approved that are still ahead.
 *
 * Ported from the legacy RequestApprovalCtrl
 * (assets/js/src/time/accrual/employee-time-off-requests.js) and
 * WEB-INF/view/template/time/accrual/emp-time-off-requests.jsp.
 */
export default function EmployeeTimeOffRequestsIndex() {
  const { data: user } = useRequireAuthedUser();
  const supId = user?.employeeId;

  const supEmpGroup = useSupEmpGroup(supId);
  const pending = useSupervisorPendingRequests(supId);
  const active = useSupervisorActiveRequests(supId);
  const review = useReviewTimeOffRequests();

  const [selections, setSelections] = useState({
    SUBMITTED: new Set(),
    APPROVED: new Set(),
  });
  const [reviewing, setReviewing] = useState(null);

  const { getName } = supEmpGroup;

  const pendingRequests = useMemo(
    () => reviewableRequests(pending.data, getName),
    [pending.data, getName],
  );
  const activeRequests = useMemo(
    () => reviewableRequests(active.data, getName),
    [active.data, getName],
  );

  const isLoading =
    pending.isPending || active.isPending || supEmpGroup.isPending;

  const setSelection = (status, next) =>
    setSelections((current) => ({ ...current, [status]: next }));

  const openReview = (status) => {
    const requests = status === "SUBMITTED" ? pendingRequests : activeRequests;
    setReviewing({
      requests: requests.filter((request) =>
        selections[status].has(request.requestId),
      ),
      alreadyApproved: status === "APPROVED",
    });
  };

  const submitReview = (reviews) => {
    setReviewing(null);
    setSelections({ SUBMITTED: new Set(), APPROVED: new Set() });
    review.mutate(reviews);
  };

  return (
    <AssertPermission permission="time:management-pages">
      <div>
        <Hero>Employee Time Off Requests</Hero>

        {review.isError && (
          <Notification
            level="error"
            title="Your review could not be submitted."
            message="Please try again later."
          />
        )}

        {isLoading ? (
          <LoadingIndicator />
        ) : (
          <div>
            <RequestSection
              title="Time Off Requests Needing Approval"
              format="pending"
              requests={pendingRequests}
              selected={selections.SUBMITTED}
              onSelectedChange={(next) => setSelection("SUBMITTED", next)}
              onReview={() => openReview("SUBMITTED")}
            />

            <RequestSection
              title="Approved and Upcoming Time Off Requests"
              format="approved"
              requests={activeRequests}
              selected={selections.APPROVED}
              onSelectedChange={(next) => setSelection("APPROVED", next)}
              onReview={() => openReview("APPROVED")}
            />
          </div>
        )}

        <TimeOffRequestReviewModal
          requests={reviewing?.requests}
          alreadyApproved={reviewing?.alreadyApproved}
          onSubmit={submitReview}
          onClose={() => setReviewing(null)}
        />
      </div>
    </AssertPermission>
  );
}

function RequestSection({
  title,
  format,
  requests,
  selected,
  onSelectedChange,
  onReview,
}) {
  return (
    <Card className="mt-3">
      <h2 className="px-3 pt-3 text-xl font-semibold text-teal-700">{title}</h2>

      <TimeOffRequestApprovalTable
        requests={requests}
        format={format}
        selected={selected}
        onSelectedChange={onSelectedChange}
      />

      <div className="flex flex-wrap items-center justify-between gap-3 px-3 py-2">
        <ul className="flex gap-4">
          <li>
            <Button
              variant="link"
              onPress={() =>
                onSelectedChange(new Set(requests.map((r) => r.requestId)))
              }
            >
              Select All
            </Button>
          </li>
          <li>
            <Button variant="link" onPress={() => onSelectedChange(new Set())}>
              Select None
            </Button>
          </li>
        </ul>
        <Button
          variant="secondary"
          isDisabled={selected.size === 0}
          onPress={onReview}
        >
          Review Selected
        </Button>
      </div>
    </Card>
  );
}

/**
 * The requests a supervisor can act on, ordered and labelled with the employee's name. The
 * requests carry only an employee id, so the name comes from the supervisor's employee group,
 * as the legacy approval directive did.
 */
function reviewableRequests(requests, getName) {
  return formatRequests(requests)
    .filter(
      (request) =>
        request.status === "SUBMITTED" || request.status === "APPROVED",
    )
    .map((request) => {
      const name = getName(request.employeeId);
      return {
        ...request,
        name: name ? `${name.firstName} ${name.lastName}` : "",
      };
    })
    .sort(compareRequests);
}
