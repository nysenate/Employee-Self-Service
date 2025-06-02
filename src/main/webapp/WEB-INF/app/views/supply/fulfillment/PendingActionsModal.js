import React, { useEffect, useRef, useState } from "react";
import NiceModal, { useModal } from "@ebay/nice-modal-react";
import TestModal from "app/components/TestModal";
import { Button } from "app/components/Button";
import { isoToShortDateTime } from "app/utils/dateUtils";
import { Link } from "react-router-dom";
import { useLocations } from "app/views/supply/useLocations";
import InputAutocomplete from "app/components/InputAutocomplete";
import { useSupplyEmployees } from "app/views/supply/fulfillment/useSupplyEmployees";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import clsx from "clsx";
import { useItemsMap } from "app/views/supply/useItems";
import { useMutateRequisition } from "app/views/supply/useRequisition";

const PendingActionsModal = NiceModal.create(({ requisition }) => {
  const modal = useModal();
  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isDirty },
  } = useForm({
    mode: "onBlur",
    defaultValues: {
      destinationId: requisition.destination.locId,
      deliveryMethod: requisition.deliveryMethod,
      issuer: requisition.issuer || "",
      note: requisition.note || "",
      lineItems: requisition.lineItems,
    },
  });

  const { fields, append } = useFieldArray({ control, name: "lineItems" });
  const submitAction = useRef("save");
  const locationQuery = useLocations();
  const useUpdateRequisitionMutation = useMutateRequisition();

  const onSubmit = (data) => {
    requisition.destination = locationQuery.data.find(
      (loc) => loc.locId === data.destinationId,
    );
    requisition.deliveryMethod = data.deliveryMethod;
    requisition.issuer = data.issuer || null;
    requisition.note = data.note;
    requisition.lineItems = data.lineItems;

    if (submitAction.current === "save") {
      useUpdateRequisitionMutation
        .mutateAsync(requisition)
        .then(() => modal.hide());
    } else if (submitAction.current === "process") {
      console.log("Processing:");
    }
  };

  return (
    <TestModal allowSoftReject={true}>
      <TestModal.Title>Nice Modal Cool?</TestModal.Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <TestModal.Body>
          <div className="grid w-[54rem] grid-cols-5 items-start gap-8">
            <div className="col-span-3 max-h-96 overflow-auto">
              <EditableLineItemTable
                lineItems={requisition.lineItems}
                register={register}
                fields={fields}
                append={append}
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
        </TestModal.Body>
        <TestModal.Buttons>
          <Button
            type="submit"
            disabled={!isDirty}
            onClick={() => (submitAction.current = "save")}
          >
            Save
          </Button>
          <Button
            type="submit"
            onClick={() => (submitAction.current = "process")}
          >
            Process
          </Button>
        </TestModal.Buttons>
      </form>
    </TestModal>
  );
});

export default PendingActionsModal;

function EditableLineItemTable({
  lineItems,
  register,
  fields,
  append,
  errors,
}) {
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
  const modal = useModal();
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
        <select className="select w-full" {...register("issuer")}>
          <option></option>
          {supplyEmployeesQuery.data?.map((emp) => (
            <option value={emp} key={emp.employeeId}>
              {emp.fullName}
            </option>
          ))}
        </select>
      </div>

      {requisition.issuer && (
        <>
          <div className="font-semibold">Issuer:</div>
          <div>{requisition.issuer.lastName}</div>
        </>
      )}

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
        <Link
          to={`/supply/order-history/order/${requisition.requisitionId}`}
          onClick={() => modal.hide()}
        >
          View full history
        </Link>
      </div>
    </div>
  );
}
