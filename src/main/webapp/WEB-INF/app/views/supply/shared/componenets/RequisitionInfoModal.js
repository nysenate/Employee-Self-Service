import React from "react";
import Modal from "app/components/Modal";
import { isoToShortDateTime } from "app/utils/dateUtils";
import Button from "app/components/Button";
import { Link } from "react-router-dom";

export default function RequisitionInfoModal({
  isOpen,
  requisition,
  onResolve,
}) {
  if (!isOpen || !requisition) {
    return "";
  }

  return (
    <Modal isOpen={isOpen} onSoftReject={onResolve}>
      <Modal.Title>
        <span className="text-xl">
          Requisition #{requisition.requisitionId} requested by{" "}
          {requisition.customer.fullName}
        </span>
      </Modal.Title>
      <Modal.Body>
        <div className="grid w-[54rem] grid-cols-5 items-start gap-8">
          <div className="col-span-3 max-h-96 overflow-auto">
            <ItemsTable requisition={requisition} />
          </div>
          <div className="col-span-2 grid grid-cols-2 gap-2">
            <RequisitionFields requisition={requisition} />
          </div>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={onResolve}>
          Close
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

function ItemsTable({ requisition }) {
  return (
    <>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Commodity Code</th>
            <th className="table__head__cell">Description</th>
            <th className="table__head__cell cell--number">Quantity</th>
          </tr>
        </thead>
        <tbody className="table__body divide-y divide-gray-200/80">
          {requisition.lineItems.map((li) => (
            <tr className="table__row" key={li.item.id}>
              <td className="table__cell">{li.item.commodityCode}</td>
              <td className="table__cell">{li.item.description}</td>
              <td className="table__cell cell--number">{li.quantity}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}

function RequisitionFields({ requisition }) {
  return (
    <>
      <div className="font-semibold">Status:</div>
      <div>{requisition.status}</div>

      <div className="font-semibold">Destination:</div>
      <div>{requisition.destination.locId}</div>

      <div className="font-semibold">Customer:</div>
      <div>{requisition.customer.fullName}</div>

      <div className="font-semibold">Ordered Date:</div>
      <div>{isoToShortDateTime(requisition.orderedDateTime)}</div>

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

      {requisition.specialInstructions && (
        <>
          <div className="font-semibold">Special Instructions:</div>
          <div>{requisition.specialInstructions}</div>
        </>
      )}

      <div className="font-semibold">Actions:</div>
      <div>
        <div>
          <Link to={`/supply/orders/${requisition.requisitionId}`}>
            View full history
          </Link>
        </div>
        <div>
          <Link to={`/supply/orders/${requisition.requisitionId}?print=true`}>
            Print Requisition
          </Link>
        </div>
      </div>
    </>
  );
}
