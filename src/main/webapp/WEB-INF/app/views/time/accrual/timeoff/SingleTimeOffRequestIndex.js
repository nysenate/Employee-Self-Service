import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssertPermission from "app/components/AssertPermission";
import TimeOffRequestForm from "app/views/time/accrual/timeoff/TimeOffRequestForm";
import { RequestLoadError } from "app/views/time/accrual/timeoff/TimeOffRequestIndex";
import { useTimeOffRequest } from "app/views/time/accrual/timeoff/useTimeOffRequests";

/**
 * Reviews one time off request, opening read only with an Edit button if it may still change.
 * Ported from the legacy SingleRequestCtrl
 * (assets/js/src/time/accrual/single-time-off-request.js) and
 * WEB-INF/view/template/time/accrual/single-time-off-request.jsp.
 */
export default function SingleTimeOffRequestIndex() {
  const { requestId } = useParams();
  const navigate = useNavigate();
  const request = useTimeOffRequest(requestId);

  return (
    <AssertPermission permission="time:time-off-request-page">
      <div>
        <Hero>Time Off Requests</Hero>

        <div className="p-3">
          <Button
            variant="secondary"
            onPress={() => navigate("/time/accrual/time-off-request")}
          >
            Back to Time Off Requests
          </Button>
        </div>

        {request.isPending ? (
          <LoadingIndicator />
        ) : request.isError ? (
          <RequestLoadError requestId={requestId} />
        ) : (
          <div>
            <h2 className="bg-white px-3 pt-3 text-xl font-semibold text-teal-700">
              Time Off Request Review
            </h2>
            {/*
              Keyed on the request so that reopening a different one starts from its own days
              rather than keeping the ones already on screen.
            */}
            <TimeOffRequestForm
              key={requestId}
              request={request.data}
              initialMode="output"
            />
          </div>
        )}
      </div>
    </AssertPermission>
  );
}
