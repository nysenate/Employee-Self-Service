import React from "react";
import { useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import AssertPermission from "app/components/AssertPermission";
import TimeOffRequestForm from "app/views/time/accrual/timeoff/TimeOffRequestForm";

/** A request that has not been started yet. */
const BLANK_REQUEST = { days: [], comments: [], status: null };

/**
 * Creates a new time off request.
 * Ported from the legacy NewRequestCtrl (assets/js/src/time/accrual/new-time-off-request.js)
 * and WEB-INF/view/template/time/accrual/new-time-off-request.jsp.
 */
export default function NewTimeOffRequestIndex() {
  const navigate = useNavigate();

  return (
    <AssertPermission permission="time:time-off-request-page">
      <div>
        <Hero>New Time Off Request</Hero>
        <div className="p-3">
          <Button
            variant="secondary"
            onPress={() => navigate("/time/accrual/time-off-request")}
          >
            Back to Time Off Requests
          </Button>
        </div>
        <TimeOffRequestForm request={BLANK_REQUEST} initialMode="input" />
      </div>
    </AssertPermission>
  );
}
