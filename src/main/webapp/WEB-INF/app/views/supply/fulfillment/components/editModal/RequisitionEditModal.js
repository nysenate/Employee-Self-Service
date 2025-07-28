import React, { useEffect, useRef, useState } from "react";
import { useLocations } from "app/views/supply/shared/hooks/useLocations";
import { useSupplyEmployees } from "app/views/supply/fulfillment/hooks/useSupplyEmployees";
import { useFieldArray, useForm } from "react-hook-form";
import { useUpdateRequisition } from "app/views/supply/fulfillment/hooks/useUpdateRequisition";
import Modal from "app/components/Modal";
import EditableLineItems from "app/views/supply/fulfillment/components/editModal/EditableLineItems";
import EditableFields from "app/views/supply/fulfillment/components/editModal/EditableFields";
import NotesInput from "app/views/supply/fulfillment/components/editModal/NotesInput";
import AddItemInput from "app/views/supply/fulfillment/components/editModal/AddItemInput";
import { useAdvanceRequisition } from "app/views/supply/fulfillment/hooks/useAdvanceRequisition";
import { useRejectRequisition } from "app/views/supply/fulfillment/hooks/useRejectRequisition";
import { useUndoRequisition } from "app/views/supply/fulfillment/hooks/useUndoRequisition";
import { EssPopoverPanel } from "app/components/EssPopover";
import Button from "app/components/Button";
import { Popover, PopoverButton } from "@headlessui/react";

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
  const advanceRequisition = useAdvanceRequisition();
  const rejectRequisition = useRejectRequisition();
  const undoRequisition = useUndoRequisition();
  const submitAction = useRef("save");
  const {
    register,
    handleSubmit,
    control,
    reset,
    setError,
    formState: { errors, isDirty, dirtyFields, isSubmitting },
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

  const onSubmit = async (data) => {
    // Make a copy.
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

    switch (submitAction.current) {
      case "save":
        await updateRequisition.mutateAsync(dirtyReq);
        onResolve();
        break;
      case "advance":
        await advanceRequisition.mutateAsync(dirtyReq);
        onResolve();
        break;
      case "reject":
        if (dirtyFields.note && data.note) {
          // Require a note when rejecting.
          await rejectRequisition.mutateAsync(dirtyReq);
          onResolve();
        } else {
          console.log("SETTING ERRRO");
          setError("note", {
            type: "custom",
            message: "A note is required to reject.",
          });
        }
        break;
      case "undo":
        await undoRequisition.mutateAsync(dirtyReq);
        onResolve();
        break;
      default:
        throw Error("Unknown action taken in edit requisition modal");
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
                destination={requisition.destination}
              />
              <AddItemInput append={append} />
              <NotesInput register={register} errors={errors} />
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
            <div className="flex items-baseline gap-3">
              <UndoButton
                status={requisition.status}
                submitAction={submitAction}
                isSubmitting={isSubmitting}
              />
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
                disabled={!isDirty || isSubmitting}
                className="w-20"
                onClick={() => (submitAction.current = "save")}
              >
                Save
              </Button>
              <AdvanceButton
                status={requisition.status}
                submitAction={submitAction}
                isSubmitting={isSubmitting}
              />
            </div>
            <div>
              <RejectButton
                status={requisition.status}
                submitAction={submitAction}
                handleSubmit={handleSubmit(onSubmit)}
                isSubmitting={isSubmitting}
              />
            </div>
          </div>
        </Modal.Buttons>
      </form>
    </Modal>
  );
}

function AdvanceButton({ status, submitAction, isSubmitting }) {
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
      disabled={isSubmitting}
      className="w-20"
      onClick={() => (submitAction.current = "advance")}
    >
      {label}
    </Button>
  );
}

function RejectButton({ status, submitAction, handleSubmit, isSubmitting }) {
  // Only display reject option if status is currently pending or processing
  if (status === "PENDING" || status === "PROCESSING") {
    return (
      <Popover className="relative">
        {({ open, close }) => (
          <>
            <PopoverButton as="div">
              <Button
                type="button"
                color="error"
                disabled={isSubmitting}
                className="w-20"
              >
                Reject
              </Button>
            </PopoverButton>
            {open && (
              <EssPopoverPanel>
                <div className="">
                  <div>Are you sure?</div>
                  <div className="mt-3 flex gap-3">
                    <Button color="secondary" onClick={close}>
                      Cancel
                    </Button>
                    <Button
                      color="error"
                      type="submit"
                      onClick={() => {
                        close();
                        submitAction.current = "reject";
                        handleSubmit();
                      }}
                    >
                      Reject
                    </Button>
                  </div>
                </div>
              </EssPopoverPanel>
            )}
          </>
        )}
      </Popover>
    );
  } else {
    return <></>;
  }
}

function UndoButton({ status, submitAction, isSubmitting }) {
  if (status === "PROCESSING" || status === "COMPLETED") {
    return (
      <Button
        type="submit"
        variant="text"
        color="link"
        disabled={isSubmitting}
        className="w-20"
        onClick={() => (submitAction.current = "undo")}
      >
        Undo
      </Button>
    );
  } else {
    return <></>;
  }
}
