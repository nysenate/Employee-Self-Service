import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import Hero from "app/components/Hero";
import {
  alphabetizeLineItems,
  formatDate,
} from "app/views/supply/shared/helpers/helpers";
import Controls from "app/components/Controls";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useRequisitionHistory } from "app/views/supply/orders/order-detail/useRequisitionHistory";

export default function OrderDetail() {
  const printRef = useRef();
  let { orderId } = useParams();
  const { data, isPending } = useRequisitionHistory(orderId);
  const [selectedVersion, setSelectedVersion] = useState(null);
  const [versions, setVersions] = useState(null);

  useEffect(() => {
    setSelectedVersion(data?.result[0]);
    setVersions(data?.result);
  }, [data]);

  useEffect(() => {
    if (print && selectedVersion) {
      // handlePrint();
    }
  }, [print, selectedVersion]);

  // const handlePrint = useReactToPrint({
  //   content: () => printRef.current,
  // });

  if (isPending || !selectedVersion) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Requisition Order: {selectedVersion.requisitionId}</Hero>
      <Controls>
        <VersionFilter
          versions={versions}
          setCurrentOrder={setSelectedVersion}
          handlePrint={() => undefined}
        />
      </Controls>
      <Card className="mt-5">
        <OrderInfo order={selectedVersion} />
        <SpecialInstructions order={selectedVersion} />
        <ItemTable items={selectedVersion.lineItems} />
      </Card>

      {/* Print */}
      {/*<div ref={printRef} className={styles.printOnly}>*/}
      {/*  <OrderDetailPrint selectedVersion={selectedVersion} />*/}
      {/*</div>*/}
    </div>
  );
}

const VersionFilter = ({ versions, setCurrentOrder, handlePrint }) => {
  const [selectedIndex, setSelectedIndex] = useState(versions.length - 1); // Default to "Current"

  useEffect(() => {
    setCurrentOrder(versions[selectedIndex]);
  }, [selectedIndex, setCurrentOrder, versions]);

  const handleVersionSelect = (e) => {
    const index = parseInt(e.target.value, 10);
    setSelectedIndex(index);
    setCurrentOrder(versions[index]);
  };

  return (
    <div className="flex items-center justify-between p-3">
      <div></div>
      <div>
        <label>Selected Version: </label>
        <select
          onChange={handleVersionSelect}
          value={selectedIndex}
          className="input cursor-pointer"
        >
          {versions.length > 1 && (
            <option value={versions.length - 1}>Current</option>
          )}
          {versions
            .slice()
            .reverse()
            .map((order, reversedIndex) => {
              const index = versions.length - 1 - reversedIndex;
              return (
                index > 0 &&
                index < versions.length - 1 && (
                  <option value={index} key={index}>
                    {index + 1}
                  </option>
                )
              );
            })}
          <option value="0">Original</option>
        </select>
      </div>
      <div className="">
        <a onClick={handlePrint}>Print Page</a>
      </div>
    </div>
  );
};

const OrderInfo = ({ order }) => {
  return (
    <div className="grid grid-cols-3 gap-3 p-3">
      <div className="">
        <div className="">
          <b>Requested by:</b> {order.customer.fullName}
        </div>
        <div className="">
          <b>Status:</b> {order.status}
        </div>
        <div className="">
          <b>Modified By:</b> {order.modifiedBy.lastName}
        </div>
      </div>

      <div className="">
        <div className="">
          <b>Requested Office:</b> {order.destination.locId}
        </div>
        <div className="">
          <b>
            {order.status === "PENDING" || order.status === "PROCESSING"
              ? "Issuer: "
              : "Issued By: "}
          </b>
          {order.issuer ? order.issuer.lastName : order.issuer}
        </div>
        <div className="">
          <b>Delivery Method:</b> {order.deliveryMethod}
        </div>
      </div>
      <div className="">
        <div>
          <b>Requested Date:</b>{" "}
          {new Date(order.orderedDateTime).toLocaleString()}
        </div>
        <div className="">
          <b>Issued Date: </b>
          {order.status === "COMPLETED" || order.status === "APPROVED"
            ? formatDate(order.completedDateTime)
            : ""}
        </div>
      </div>
    </div>
  );
};

const SpecialInstructions = ({ order }) => {
  return (
    <>
      {order.note || order.specialInstructions ? (
        <div className="p-3">
          <div className="">
            {order.specialInstructions && (
              <div className="">
                <span className="mr-1 font-semibold">
                  Special Instructions:
                </span>
                <span className="">{order.specialInstructions}</span>
              </div>
            )}
            {order.note && (
              <div className="">
                <span className="mr-1 font-semibold">Supply Note:</span>
                <span className="">{order.note}</span>
              </div>
            )}
          </div>
        </div>
      ) : null}
    </>
  );
};

const ItemTable = ({ items }) => {
  const sortedLineItems = items ? alphabetizeLineItems(items) : [];
  return (
    <div className="">
      <div className="p-3">
        <table className="table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell">Commodity Code</th>
              <th className="table__head__cell">Item</th>
              <th className="table__head__cell">Quantity</th>
            </tr>
          </thead>
          <tbody className="table__body table__body--striped table__body--highlight">
            {sortedLineItems.map((item) => (
              <tr className="table__row" key={item.item.id}>
                <td className="table__cell">{item.item.commodityCode}</td>
                <td className="table__cell">{item.item.description}</td>
                <td className="table__cell">{item.quantity}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
