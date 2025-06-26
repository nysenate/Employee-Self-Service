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

  console.log(isDirty);
  console.log(dirtyFields);

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
    } else if (submitAction.current === "process") {
      console.log("Processing:");
    }
  };

  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Nice Modal Cool?</Modal.Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Modal.Body>
          <div className="grid w-[54rem] grid-cols-5 items-start gap-8">
            <div className="col-span-3 max-h-96 overflow-auto">
              <EditableLineItemTable
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
              <Button
                type="submit"
                color="time"
                className="w-20"
                onClick={() => (submitAction.current = "process")}
              >
                Process
              </Button>
            </div>
            <div>
              <Button
                type="submit"
                color="error"
                className="w-20"
                onClick={() => (submitAction.current = "reject")}
              >
                Reject
              </Button>
            </div>
          </div>
        </Modal.Buttons>
      </form>
    </Modal>
  );
}

function EditableLineItemTable({ register, fields, errors }) {
  return (
    <div>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell table__head__cell--text">
              Commodity Code
            </th>
            <th className="table__head__cell table__head__cell--text">
              Description
            </th>
            <th className="table__head__cell table__head__cell--text">
              Quantity
            </th>
          </tr>
        </thead>
        <tbody className="table__body divide-y divide-gray-200/80">
          {fields.map((li, index) => (
            <tr className="table__row" key={li.id}>
              <td className="table__cell table__cell--text">
                {li.item.commodityCode}
              </td>
              <td className="table__cell table__cell--text">
                {li.item.description}
              </td>
              <td className="table__cell table__cell--number">
                <input
                  {...register(`lineItems.${index}.quantity`, {
                    required: "Line Item must have a quantity.",
                  })}
                  className={clsx(
                    "input w-16",
                    errors?.lineItems?.[index] && "input--invalid",
                  )}
                  type="number"
                  autoComplete="off"
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div></div>
    </div>
  );
}

function AddItemInput({ append }) {
  const itemsQuery = useItemsMap();
  const [item, setItem] = useState(null);

  const appendItem = () => {
    if (itemsQuery.data.has(item?.id)) {
      append({ item: item, quantity: 0 });
    }
  };

  return (
    <div className="mt-4 flex items-baseline justify-center gap-2">
      <label htmlFor="addItem" className="font-light">
        Add Commodity Code
      </label>
      <InputAutocomplete
        id="addItem"
        name="addItem"
        value={item}
        onChange={(value) => setItem(value)}
        options={Array.from(itemsQuery.data?.values() || [])}
        displayValue={(item) => item?.commodityCode}
        className="inline-block w-44"
      />
      <Button color="secondary" onClick={appendItem}>
        Add Item
      </Button>
    </div>
  );
}

function NotesInput({ register }) {
  return (
    <div className="mt-4 flex items-baseline justify-center gap-2">
      <label htmlFor="note" className="font-light">
        Note:
      </label>
      <textarea
        {...register("note")}
        id="note"
        name="note"
        rows="3"
        className="input w-4/5"
      />
    </div>
  );
}

function EditableFields({ requisition, register, control }) {
  const locationQuery = useLocations();
  const supplyEmployeesQuery = useSupplyEmployees();
  return (
    <div className="grid grid-cols-2 gap-2">
      <div className="font-semibold">Destination:</div>
      <div>
        <Controller
          name="destinationId"
          control={control}
          render={({ field }) => (
            <InputAutocomplete
              {...field}
              id="destinationId"
              name="destinationId"
              options={locationQuery.data?.map((loc) => loc.locId)}
            />
          )}
        />
      </div>

      <div className="font-semibold">Delivery Method:</div>
      <div>
        <select className="select" {...register("deliveryMethod")}>
          <option value="DELIVERY">DELIVERY</option>
          <option value="PICKUP">PICKUP</option>
        </select>
      </div>

      <div className="font-semibold">Special Instructions:</div>
      <div>{requisition.specialInstructions || "None"}</div>

      <div className="font-semibold">Ordered Date:</div>
      <div>{isoToShortDateTime(requisition.orderedDateTime)}</div>

      <div className="font-semibold">Assigned To:</div>
      <div>
        <select
          className="select w-full"
          {...register("issuerEmpId", {
            setValueAs: (value) => (value === "" ? null : parseInt(value, 10)),
          })}
        >
          <option></option>
          {supplyEmployeesQuery.data?.map((emp) => (
            <option value={emp.employeeId} key={emp.employeeId}>
              {emp.fullName}
            </option>
          ))}
        </select>
      </div>

      {requisition.status === "APPROVED" && (
        <>
          <div className="font-semibold">Completed Date:</div>
          <div>{isoToShortDateTime(requisition.completedDateTime)}</div>
        </>
      )}

      {requisition.status === "REJECTED" && (
        <>
          <div className="font-semibold">Rejected By:</div>
          <div>{requisition.modifiedBy.lastName}</div>

          <div className="font-semibold">Rejected Date:</div>
          <div>{isoToShortDateTime(requisition.rejectedDateTime)}</div>
        </>
      )}

      <div className="font-semibold">Actions:</div>
      <div>
        <Link to={`/supply/order-history/order/${requisition.requisitionId}`}>
          View full history
        </Link>
      </div>
    </div>
  );
}
