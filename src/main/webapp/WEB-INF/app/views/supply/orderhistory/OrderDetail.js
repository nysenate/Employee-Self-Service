import React, { useEffect, useState } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import styles from './OrderDetail.module.css';
import Hero from "../../../components/Hero";
import CustomerPopover from './CustomPopover';
import { alphabetizeLineItems, formatDate} from "../helpers";
import { fetchApiJson } from "app/utils/fetchJson";

function print() {
  console.log("implement print plz!");
}

//"selected Version" choose box("Original") blue "Print Page" button (no box)
const SelectVersion = ({ orders, setCurrentOrder }) => {
  const [selectedIndex, setSelectedIndex] = useState(orders.result.length - 1); // Default to "Current"

  useEffect(() => {
    setCurrentOrder(orders.result[selectedIndex]);
  }, [selectedIndex, setCurrentOrder, orders.result]);

  const handleVersionSelect = (e) => {
    const index = parseInt(e.target.value, 10);
    setSelectedIndex(index);
    setCurrentOrder(orders.result[index]);
  };

  return (
    <div className={styles.contentContainer}>
      <div className={styles.contentInfo}>
        <label>Selected Version: </label>
        <select onChange={handleVersionSelect} value={selectedIndex}>
          {orders.result.length > 1 && (
            <option value={orders.result.length - 1}>Current</option>
          )}
          {orders.result.slice().reverse().map((order, reversedIndex) => {
            const index = orders.result.length - 1 - reversedIndex;
            return (
              index > 0 && index < orders.result.length - 1 && (
                <option value={index} key={index}>{index + 1}</option>
              )
            );
          })}
          <option value="0">Original</option>
        </select>
        <a style={{ float: 'right', padding: '5px 20px 0px 0px' }} onClick={print}>
          Print Page
        </a>
      </div>
    </div>
  );
}

/* Right Left Down: (bold all fields, regular answers font)
Requested By: {order.customer.fullName}   Requested Office: {order.destination.locId} Requested Date: {new Date(order.orderedDateTime).toLocaleString()}
Status: {order.status}                    Issuer: {order.issuer}                      Issued Date: {?}
Modified By: {order.modifiedBy.lastName}  Delivery Method: {order.deliveryMethod}

Hoever Details include (Requested By                                    Requested Office)
                        ^Phone Number: order.customer.workPhone         ^Office Name: order.destination.respCenterHead.name
                        ^Email: order.customer.email                    ^Address: order.destination.address.formattedAddressWithCounty (clip the part after zip " (Albany County)")
Also ^ bold field and regular font respond
*/
const OrderInfo = ({ order }) => {
  return (
      <div className={styles.contentContainer}>
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
              <b>Requested Date:</b> {new Date(order.orderedDateTime).toLocaleString()}
            </div>
          </div>
          <div className={`${styles.grid} ${styles.paddingX}`}>
            <div className={styles.col412}>
              <b>Status:</b> {order.status}
            </div>
            <div className={styles.col412}>
              <b>{order.status === 'PENDING' || order.status === 'PROCESSING' ? 'Issuer: ' : 'Issued By: '}</b>
              {order.issuer ? (order.issuer.lastName) : (order.issuer)}
            </div>
            <div className={styles.col412}>
              <b>Issued Date:</b>
                {order.status === 'COMPLETED' || order.status === 'APPROVED' ? formatDate(order.completedDateTime) : ('')}
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
      </div>
  );
}

// Notes
const SpecialInstructions = ({order}) => {
    return (
        <>
            {order.note || order.specialInstructions ? (
                <div className={styles.contentContainer}>
                    <div className={styles.contentInfo}>
                        {order.note && (
                            <div className={`${styles.grid} ${styles.paddingX}`}>
                                <div className={styles.col412} style={{ fontWeight: '700' }}>Supply Note:</div>
                                <div className={styles.col812}>{order.note}</div>
                            </div>
                        )}
                        {order.note && order.specialInstructions && (
                            <div style={{ borderBottom: 'black 1px solid' }}></div>
                        )}
                        {order.specialInstructions && (
                            <div className={`${styles.grid} ${styles.paddingX}`}>
                                <div className={styles.col412} style={{ fontWeight: '700' }}>Special Instructions:</div>
                                <div className={styles.col812} style={{ textAlign: 'left' }}>{order.specialInstructions}</div>
                            </div>
                        )}
                    </div>
                </div>
            ) : null}
        </>
    );
};

/* Commodity Code     Item        Quantity
*   {order.lineItems[0].item.commodityCode} {.item.description} {order.lineItems[0].quantity}
* */
const ItemTable = ({items}) => {
  const sortedLineItems = items ? alphabetizeLineItems(items) : [];
  return (
      <div className={styles.contentContainer}>
        <div className={styles.paddingX}>
          <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
            <thead>
            <tr>
              <th>Commodity Code</th>
              <th>Item</th>
              <th>Quantity</th>
            </tr>
            </thead>
            <tbody>
            {sortedLineItems.map(item => (
                <tr key={item.item.id}>
                    <td>{item.item.commodityCode}</td>
                    <td>{item.item.description}</td>
                    <td>{item.quantity}</td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>
      </div>
  );
}

export default function OrderDetail () {
  const location = useLocation();
  const { order } = location.state || {};
  const [currentOrder, setCurrentOrder] = useState(null);
  const [orders, setOrders] = useState(null);

  useEffect(() => {
    const fetchRequisitionHistory = async () => {
      try {
        const response = await fetchApiJson(`/supply/requisitions/history/${order.requisitionId}`);
        setCurrentOrder(order);
        setOrders(response);
      } catch (err) {
        console.error("Issue fetching order history: ", err);
      }
    }
    fetchRequisitionHistory();
  }, []);

  if (!currentOrder) {
    return <div>No requisition data available.</div>;
  }

  return (
      <div>
        <Hero>Requisition Order: {order.requisitionId}</Hero>
        <SelectVersion orders={orders} setCurrentOrder={setCurrentOrder}/>
        <OrderInfo order={currentOrder} />
        <SpecialInstructions order={currentOrder}/>
        <ItemTable items={currentOrder.lineItems}/>
      </div>
  );
};
