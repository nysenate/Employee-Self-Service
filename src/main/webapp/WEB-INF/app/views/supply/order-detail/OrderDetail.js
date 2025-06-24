import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import styles from "app/views/supply/universalStyles.module.css";
import Hero from "app/components/Hero";
import CustomerPopover from "app/views/supply/order-history/CustomPopover";
import { alphabetizeLineItems, formatDate } from "app/views/supply/helpers";
import OrderDetailPrint from "app/views/supply/order-detail/OrderDetailPrint";
import Controls from "app/components/Controls";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useRequisitionHistory } from "app/views/supply/order-detail/useRequisitionHistory";

export default function OrderDetail() {
  const printRef = useRef();
  let { orderId } = useParams();
  const { data, isPending } = useRequisitionHistory(orderId);
  const [currentOrder, setCurrentOrder] = useState(null);
  const [orders, setOrders] = useState(null);

  useEffect(() => {
    setCurrentOrder(data?.result[0]);
    setOrders(data?.result);
  }, [data]);

  useEffect(() => {
    if (print && currentOrder) {
      // handlePrint();
    }
  }, [print, currentOrder]);

  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  if (isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Requisition Order: {currentOrder.requisitionId}</Hero>
      <Controls>
        <VersionFilter
          versions={orders}
          setCurrentOrder={setCurrentOrder}
          handlePrint={handlePrint}
        />
      </Controls>
      <Card className="mt-5">
        <OrderInfo order={currentOrder} />
        <SpecialInstructions order={currentOrder} />
        <ItemTable items={currentOrder.lineItems} />
      </Card>

      {/* Print */}
      <div ref={printRef} className={styles.printOnly}>
        <OrderDetailPrint selectedVersion={currentOrder} />
      </div>
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
    <div className={styles.contentInfo}>
      <div className={`${styles.grid} ${styles.paddingX}`}>
        {/*<div className={styles.col412}>*/}
        {/*  <b>Requested By:</b> {order.customer.fullName}*/}
        {/*</div>*/}
        <CustomerPopover order={order} />
        <div className={styles.col412}>
          <b>Requested Office:</b> {order.destination.locId}
        </div>
        <div className={styles.col412}>
          <b>Requested Date:</b>{" "}
          {new Date(order.orderedDateTime).toLocaleString()}
        </div>
      </div>
      <div className={`${styles.grid} ${styles.paddingX}`}>
        <div className={styles.col412}>
          <b>Status:</b> {order.status}
        </div>
        <div className={styles.col412}>
          <b>
            {order.status === "PENDING" || order.status === "PROCESSING"
              ? "Issuer: "
              : "Issued By: "}
          </b>
          {order.issuer ? order.issuer.lastName : order.issuer}
        </div>
        <div className={styles.col412}>
          <b>Issued Date: </b>
          {order.status === "COMPLETED" || order.status === "APPROVED"
            ? formatDate(order.completedDateTime)
            : ""}
        </div>
      </div>
      <div className={`${styles.grid} ${styles.paddingX}`}>
        <div className={styles.col412}>
          <b>Modified By:</b> {order.modifiedBy.lastName}
        </div>
        <div className={styles.col412}>
          <b>Delivery Method:</b> {order.deliveryMethod}
        </div>
      </div>
    </div>
  );
};

const SpecialInstructions = ({ order }) => {
  return (
    <>
      {order.note || order.specialInstructions ? (
        <div className="">
          <div className={styles.contentInfo}>
            {order.note && (
              <div className={`${styles.grid} ${styles.paddingX}`}>
                <div className={styles.col412} style={{ fontWeight: "700" }}>
                  Supply Note:
                </div>
                <div className={styles.col812}>{order.note}</div>
              </div>
            )}
            {order.note && order.specialInstructions && (
              <div style={{ borderBottom: "black 1px solid" }}></div>
            )}
            {order.specialInstructions && (
              <div className={`${styles.grid} ${styles.paddingX}`}>
                <div className={styles.col412} style={{ fontWeight: "700" }}>
                  Special Instructions:
                </div>
                <div className={styles.col812} style={{ textAlign: "left" }}>
                  {order.specialInstructions}
                </div>
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
    <div className={styles.contentContainer}>
      <div className={styles.paddingX}>
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
