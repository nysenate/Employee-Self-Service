import React, { useEffect, useMemo, useState } from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import {
  useReconciliation,
  useSubmitReconciliation,
} from "app/views/supply/reconciliation/useReconciliation";
import LoadingIndicator from "app/components/LoadingIndicator";
import Button from "app/components/Button";
import ErrorBanner from "app/components/ErrorBanner";
import ReconciliationTabs from "app/views/supply/reconciliation/ReconciliationTabs";
import ModalNotice from "app/components/ModalNotice";
import { useQueryClient } from "@tanstack/react-query";

export const STATUS = {
  TYPING: 1,
  FORM_ERROR: 2, // Submitting before all qtys are entered.
  SUBMITTING: 3,
  ERRORS: 4,
  SUCCESS: 5,
};

export default function ReconciliationIndex() {
  const queryClient = useQueryClient();
  const [status, setStatus] = useState(STATUS.TYPING);
  const submitReconciliationApi = useSubmitReconciliation();
  const { isPending, data } = useReconciliation();

  const handleReconcile = () => {
    setStatus(STATUS.TYPING);
    // All quantity inputs must have a value in order to be submitted.
    const anyMissingQtys = data.items.some((item) => !item.expectedQuantity);
    if (anyMissingQtys) {
      setStatus(STATUS.FORM_ERROR);
      return;
    }

    // Submit reconciliation attempt.
    const itemQuantities = {};
    data.items.forEach((item) => {
      itemQuantities[item.id] = item.expectedQuantity;
    });
    submitReconciliationApi
      .mutateAsync({ itemQuantities: itemQuantities })
      .then((r) => {
        if (!r.result.success) {
          setStatus(STATUS.ERRORS);
          // There are errors in the counts.
          r.result.errors.forEach((error) => {
            const item = (data.items.find(
              (i) => i.id === error.itemId,
            ).actualQuantity = error.actualQuantity);
          });
        } else {
          setStatus(STATUS.SUCCESS);
          queryClient.invalidateQueries({
            queryKey: ["supply", "reconciliation"],
          });
        }
      });
  };

  return (
    <div>
      <Hero>Reconciliation</Hero>
      {isPending || submitReconciliationApi.isPending ? (
        <LoadingIndicator />
      ) : (
        <Card className="mt-5">
          <FormErrorMsg status={status} />
          <InvalidReconciliationErrorMsg status={status} />
          <div className="float-right p-3">
            <Button onClick={handleReconcile} className="print:hidden">
              Reconcile
            </Button>
            <Button
              className="ml-3 print:hidden"
              variant="text"
              onClick={() => window.print()}
            >
              Print
            </Button>
          </div>
          <ReconciliationTabs data={data} status={status} />
        </Card>
      )}
      <ModalNotice
        isOpen={status === STATUS.SUCCESS}
        onResolve={() => setStatus(STATUS.TYPING)}
        title="Success"
        body="The reconciliation was successful!"
      />
    </div>
  );
}

function InvalidReconciliationErrorMsg({ status }) {
  if (status !== STATUS.ERRORS) {
    return <></>;
  }
  return (
    <div className="p-3">
      <ErrorBanner>
        One or more of the quantities entered is incorrect. Please review the
        below errors
      </ErrorBanner>
    </div>
  );
}

function FormErrorMsg({ status }) {
  if (status !== STATUS.FORM_ERROR) {
    return <></>;
  }
  return (
    <div className="p-3">
      <ErrorBanner>
        <span className="text-xl">Missing item quantities</span>
        <br />
        <br />
        To reconcile, you must enter a quantity for all items on both pages
      </ErrorBanner>
    </div>
  );
}
