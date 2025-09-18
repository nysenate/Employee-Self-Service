import React, { useEffect, useMemo, useState } from "react";
import { Tab, TabGroup, TabList, TabPanel, TabPanels } from "@headlessui/react";
import clsx from "clsx";
import { STATUS } from "app/views/supply/reconciliation/ReconciliationIndex";
import { isoToShortDateTime } from "app/utils/dateUtils";
import { Link, useNavigate } from "react-router-dom";

export default function ReconciliationTabs({ data, status }) {
  const pageOneItemsIds = useMemo(
    () =>
      data?.items
        ?.filter((i) => i.reconciliationPage === 1)
        .sort((a, b) => a.commodityCode.localeCompare(b.commodityCode))
        .map((i) => i.id),
    [data],
  );
  const pageTwoItemsIds = useMemo(
    () =>
      data?.items
        ?.filter((i) => i.reconciliationPage !== 1)
        .sort((a, b) => a.commodityCode.localeCompare(b.commodityCode))
        .map((i) => i.id),
    [data],
  );

  const tabClasses = clsx(
    "ml-3 bg-gray-100 px-3 py-2",
    "border-1 border-gray-300 border-b-purple-500",
    "data-[selected]:bg-white  data-[selected]:border-purple-500 data-[selected]:border-b-white",
    "data-[selected]:text-purple-700 data-[selected]:font-bold",
    "print:[&:not([data-selected])]:hidden",
  );

  return (
    <TabGroup className="pt-5">
      <TabList className="">
        <Tab className={tabClasses}>Item Group 1</Tab>
        <Tab className={tabClasses}>Item Group 2</Tab>
      </TabList>
      <TabPanels className="-mt-px border-t-1 border-purple-500">
        <TabPanel>
          <ItemTable
            data={data}
            pageItemIds={pageOneItemsIds}
            status={status}
          />
        </TabPanel>
        <TabPanel>
          <ItemTable
            data={data}
            pageItemIds={pageTwoItemsIds}
            status={status}
          />
        </TabPanel>
      </TabPanels>
    </TabGroup>
  );
}

function ItemTable({ data, pageItemIds, status }) {
  const { items, itemIdToRequisitions } = data;
  const [selectedItemId, setSelectedItemId] = useState(); // The itemId of the selected row.

  const handleRowClick = (itemId) => {
    if (itemId === selectedItemId) {
      setSelectedItemId(null);
    } else {
      setSelectedItemId(itemId);
    }
  };

  return (
    <div className="p-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Commodity Code</th>
            <th className="table__head__cell">Item</th>
            <th className="table__head__cell">Quantity on Hand</th>
            <th
              className={clsx(
                "table__head__cell",
                status !== STATUS.ERRORS && "invisible",
                status === STATUS.ERRORS && "visible",
              )}
            >
              Difference
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--highlight divide-y divide-gray-200/80 print:divide-gray-300">
          {pageItemIds
            .map((id) => items.find((i) => i.id === id))
            .map((item) => (
              <ItemRow
                key={item.id}
                item={item}
                status={status}
                requisitions={itemIdToRequisitions[item.id]}
                showRequisitions={item.id === selectedItemId}
                handleRowClick={handleRowClick}
              />
            ))}
        </tbody>
      </table>
    </div>
  );
}

function ItemRow({
  item,
  status,
  requisitions,
  showRequisitions,
  handleRowClick,
}) {
  const showReqClasses = "border-1 border-gray-500";
  const rowError =
    status === STATUS.ERRORS &&
    item.expectedQuantity - item.actualQuantity != 0;
  return (
    <>
      <tr
        key={item.id}
        className={clsx(
          "table__row",
          showRequisitions && showReqClasses,
          rowError && "table__row--error",
        )}
      >
        <td className="table__cell" onClick={() => handleRowClick(item.id)}>
          {item.commodityCode}
        </td>
        <td className="table__cell" onClick={() => handleRowClick(item.id)}>
          {item.description}
        </td>
        <td className="table__cell">
          <QtyOnHandInput item={item} status={status} />
        </td>
        <td className="table__cell">
          {/*// Wrap in div to avoid issues with bg-color disappearing when invisible*/}
          <div
            className={clsx(
              "font-semibold",
              status !== STATUS.ERRORS && "invisible",
              status === STATUS.ERRORS && "visible",
            )}
          >
            {item.expectedQuantity - item.actualQuantity || ""}
          </div>
        </td>
      </tr>
      {showRequisitions && (
        <tr className={showReqClasses}>
          <td colSpan="4">
            <ItemRequisitionTable item={item} requisitions={requisitions} />
          </td>
        </tr>
      )}
    </>
  );
}

function QtyOnHandInput({ item, status }) {
  const [value, setValue] = useState(item.expectedQuantity);
  const handleChange = (e) => {
    setValue(parseInt(e.target.value));
  };

  useEffect(() => {
    // Update expectedInventory quantity.
    item.expectedQuantity = value || undefined;
  }, [value]);

  return (
    <input
      id={`${item.commodityCode}-qtyOnHand`}
      type="number"
      className={clsx(
        "input w-14",
        "p-0",
        status === STATUS.ERRORS && "input--invalid",
        status === STATUS.FORM_ERROR && "input--invalid",
      )}
      value={value}
      onChange={handleChange}
    />
  );
}

function ItemRequisitionTable({ item, requisitions }) {
  const navigate = useNavigate();
  const getItemQuantity = (req) => {
    return req.lineItems.find((li) => li.item.id === item.id).quantity;
  };
  return (
    <div>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Id</th>
            <th className="table__head__cell">Location</th>
            <th className="table__head__cell">Quantity</th>
            <th className="table__head__cell">Issued By</th>
            <th className="table__head__cell">Approved Date</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped table__body--highlight">
          {requisitions.map((req) => (
            <tr
              key={req.requisitionId}
              className="cursor-pointer"
              onClick={() =>
                navigate(`/supply/order-history/order/${req.requisitionId}`)
              }
            >
              <td className="table__cell">{req.requisitionId}</td>
              <td className="table__cell">{req.destination.locId}</td>
              <td className="table__cell">{getItemQuantity(req)}</td>
              <td className="table__cell">{req.issuer.fullName}</td>
              <td className="table__cell">
                {isoToShortDateTime(req.approvedDateTime)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
