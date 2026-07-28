import React from "react";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { isoToShortDate } from "app/utils/dateUtils";

export default function TravelAppReviewForm({ appReview }) {
  let actions = [];

  // Janky way to calculate an action when the app in resubmitted.
  for (let i = 0; i < appReview.actions.length; i++) {
    let action = appReview.actions[i];
    actions.push(action);

    let isLastAction = i === appReview.actions.length - 1;
    if (
      action.isDisapproval &&
      isLastAction &&
      appReview.travelApplication.status.isPending
    ) {
      actions.push({ resubmitted: true }); // create a "Resubmitted by user" action.
    }

    if (action.isDisapproval && !isLastAction) {
      // Was disapproved but has since been other actions, therefore app must have been resubmitted.
      actions.push({ resubmitted: true }); // create a "Resubmitted by user" action.
    }
  }

  return (
    <div className="grid grid-cols-[1fr_334px]">
      <TravelAppForm app={appReview.travelApplication} />
      <div className="border-l p-4">
        <ActionsInfo actions={actions} />
      </div>
    </div>
  );
}

function ActionsInfo({ actions }) {
  if (actions.length === 0) {
    return (
      <div className="text-muted-foreground mb-3 text-center text-xl font-semibold">
        No Actions
      </div>
    );
  }
  return (
    <>
      <div className="mb-3 text-center text-xl font-semibold">
        Previous Actions
      </div>
      {actions.map((action, index) => (
        <React.Fragment key={index}>
          {index === 0 && <div className="h-0.5 bg-gray-200" />}
          <div className="grid grid-cols-[1.1fr_2fr_1.2fr] py-1.5">
            {action.resubmitted ? (
              <ResubmittedActionRow />
            ) : (
              <ReviewerActionRow action={action} />
            )}
          </div>
          <div className="h-0.5 bg-gray-200" />
        </React.Fragment>
      ))}
    </>
  );
}

function ResubmittedActionRow() {
  return (
    <div className="col-span-3 font-semibold text-green-700">
      Resubmitted by user
    </div>
  );
}

function ReviewerActionRow({ action }) {
  return (
    <>
      <div>{isoToShortDate(action.dateTime)}</div>
      <div>{action.user.lastName}</div>
      <div>
        <ActionText action={action} />
      </div>
      {!isEmptyOrWhitespace(action.note) && (
        <>
          <div>&nbsp;</div>
          <div className="text-muted-foreground col-span-2 text-sm">
            {action.note}
          </div>
        </>
      )}
    </>
  );
}

function ActionText({ action }) {
  if (action.isApproval) {
    return <span className="font-semibold text-green-700">Approved</span>;
  }

  if (action.isDisapproval) {
    return <span className="font-semibold text-red-700">Disapproved</span>;
  }
}

function isEmptyOrWhitespace(str) {
  return !str || str.trim().length === 0;
}
