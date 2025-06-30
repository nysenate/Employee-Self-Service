import React, { useEffect, useRef, useState } from "react";
import { Button } from "app/components/Button";
import { isoToShortDateTime } from "app/utils/dateUtils";
import { Link, useSearchParams } from "react-router-dom";
import { useLocations } from "app/views/supply/useLocations";
import InputAutocomplete from "app/components/InputAutocomplete";
import { useSupplyEmployees } from "app/views/supply/fulfillment/useSupplyEmployees";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import clsx from "clsx";
import { useItemsMap } from "app/views/supply/useItems";
import { useUpdateRequisition } from "app/views/supply/fulfillment/useUpdateRequisition";
import Modal from "app/components/Modal";
import EditableLineItems from "app/views/supply/fulfillment/modal/EditableLineItems";
import EditableFields from "app/views/supply/fulfillment/modal/EditableFields";
import NotesInput from "app/views/supply/fulfillment/modal/NotesInput";
import AddItemInput from "app/views/supply/fulfillment/modal/AddItemInput";

export default function RequisitionEditModal({
  isOpen,
  onResolve,
  requisition,
}) {
  if (isOpen && !requisition) {
    throw Error("Unable to find requested requisition");
  }

  const supplyEmployeesQuery = useSupplyEmployees();
  const locationQuery = useLocations();
  const updateRequisition = useUpdateRequisition();
  const submitAction = useRef("save");
  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors, isDirty, dirtyFields },
  } = useForm({
    mode: "onBlur",
    defaultValues: {
      destinationId: "",
      deliveryMethod: "",
      issuerEmpId: "",
      note: "",
      lineItems: [],
    },
  });
  const { fields, append } = useFieldArray({ control, name: "lineItems" });

  useEffect(() => {
    if (requisition) {
      reset({
        destinationId: requisition.destination?.locId,
        deliveryMethod: requisition.deliveryMethod,
        issuerEmpId: requisition.issuer?.employeeId || null,
        note: requisition.note || "",
        lineItems: requisition.lineItems,
      });
    }
  }, [requisition, reset]);

  const onSubmit = (data) => {
    // Modify a shallow copy.
    const dirtyReq = { ...requisition };
    // Always save changes regardless of action.
    dirtyReq.destination = locationQuery.data.find(
      (loc) => loc.locId === data.destinationId,
    );
    dirtyReq.deliveryMethod = data.deliveryMethod;
    dirtyReq.issuer =
      supplyEmployeesQuery.data?.find(
        (emp) => emp.employeeId === data.issuerEmpId,
      ) || null;
    dirtyReq.note = data.note;
    dirtyReq.lineItems = data.lineItems;

    if (submitAction.current === "save") {
      updateRequisition.mutateAsync(dirtyReq).then(() => onResolve());
    } else if (submitAction.current === "advance") {
      console.log("Advancing...");
    } else if (submitAction.current === "reject") {
      console.log("Rejecting...");
    } else {
      console.error("Unknown action");
    }
  };

  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>
        Editing Requisition #{requisition.requisitionId}
      </Modal.Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Modal.Body>
          <div className="grid w-[54rem] grid-cols-5 items-start gap-8">
            <div className="col-span-3 max-h-96 overflow-auto">
              <EditableLineItems
                register={register}
                fields={fields}
                errors={errors}
              />
              <AddItemInput append={append} />
              <NotesInput register={register} />
            </div>
            <div className="col-span-2">
              <EditableFields
                requisition={requisition}
                register={register}
                control={control}
              />
            </div>
          </div>
        </Modal.Body>
        <Modal.Buttons>
          <div className="flex w-full justify-between">
            <div className="w-20">&nbsp;</div>
            <div className="flex gap-3">
              <Button
                type="button"
                color="secondary"
                className="w-20"
                onClick={() => onResolve()}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                disabled={!isDirty}
                className="w-20"
                onClick={() => (submitAction.current = "save")}
              >
                Save
              </Button>
              <AdvanceButton
                status={requisition.status}
                submitAction={submitAction}
              />
            </div>
            <div>
              <RejectButton
                status={requisition.status}
                submitAction={submitAction}
              />
            </div>
          </div>
        </Modal.Buttons>
      </form>
    </Modal>
  );
}

function AdvanceButton({ status, submitAction }) {
  let label = "";
  let color = "";
  switch (status) {
    case "PENDING":
      label = "Process";
      color = "time";
      break;
    case "PROCESSING":
      label = "Complete";
      color = "myinfo";
      break;
    case "COMPLETED":
      label = "Approve";
      color = "supply";
      break;
    default:
      label = "";
      color = "";
  }
  if (!label) {
    return <></>;
  }

  return (
    <Button
      type="submit"
      color={color}
      className="w-20"
      onClick={() => (submitAction.current = "advance")}
    >
      {label}
    </Button>
  );
}

function RejectButton({ status, submitAction }) {
  if (status === "PENDING" || status === "PROCESSING") {
    return (
      <Button
        type="submit"
        color="error"
        className="w-20"
        onClick={() => (submitAction.current = "reject")}
      >
        Reject
      </Button>
    );
  } else {
    // Only display reject option if currently in pending or processing
    return <></>;
  }
}
