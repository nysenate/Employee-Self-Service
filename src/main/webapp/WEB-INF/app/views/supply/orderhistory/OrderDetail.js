import React from 'react';
import { useParams, useLocation } from 'react-router-dom';
import styles from './OrderDetail.module.css';
import Hero from "../../../components/Hero";

function print() {
  console.log("implement print plz!");
}

//"selected Version" choose box("Original") blue "Print Page" button (no box)
const SelectVersion = () => {
  return (
    <div className={styles.contentContainer}>
      <div className={styles.contentInfo}>
        <label>Selected Version:</label>
        {/*Insert Button for Original*/}
        <div className={styles.a1} style={{float: 'right', padding: '5px 20px 0px 0px'}} onClick={print}>
          Print Page
        </div>
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
  const orderId = "fake od";
  return (
    <div className={styles.contentContainer}>
      <div className={styles.contentInfo}>
        <div className={`${styles.grid} ${styles.paddingX}`}>
          <div className={styles.col412}>
            <b>Requested By:</b> {order.customer.fullName}
          </div>
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
            <b>Issuer:</b> {order.issuer}
          </div>
          <div className={styles.col412}>
            <b>Issued Date:</b> unknown field?
          {/*  need to find what this is???
             Something about displayIssuedDate(selectedVersion)
          */}
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

/* Yet unseen
* */
const SpecialInstructions = () => {
  return (
    <div className={styles.contentContainer}>
      <div className={styles.contentInfo}>

      </div>
    </div>
  );
}

/* Commodity Code     Item        Quantity
*   {order.lineItems[0].item.commodityCode} {.item.description} {order.lineItems[0].quantity}
* */
const ItemTable = ({items}) => {
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
          {items.map(item => (
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
  const { orderId } = useParams();
  const location = useLocation();
  const { order } = location.state;

  return (
    <div>
      <Hero>Requisition Order: {orderId}</Hero>
      <SelectVersion/>
      <OrderInfo order={order} />
      <SpecialInstructions/>
      <ItemTable items={order.lineItems}/>
    </div>
  );
};

 OrderDetail;
