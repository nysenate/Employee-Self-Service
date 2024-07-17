import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css";
import './printStyles.css';
import { alphabetizeLineItems, formatDate } from "app/views/supply/helpers";

const OrderDetailPrint = ({ selectedVersion }) => {
  const [sortedLineItems, setSortedLineItems] = useState([]);
  console.log('init selectedVersion: ', selectedVersion)
  useEffect(() => {
    console.log("USEEFFECT::");
    console.log('selectedVersion: ', selectedVersion);
    const sorted = sortSelectedVersionLineItems(selectedVersion);
    console.log('sorted: ', sorted);
    setSortedLineItems(sorted);
  }, [selectedVersion]);

  return (
    <div className={styles.printOnly}>
      <style type={"text/css"} media={"print"}></style>
      <div className={styles.supplyOrderHero}>
        <h2>Requisition Order: {selectedVersion.requisitionId}</h2>
      </div>

      {/*  General Information  */}
      <div className={`${styles.contentContainer} ${styles.largePrintFontSize}`}>

        <div className={styles.contentInfo}>
          <div className={`${styles.grid} ${styles.paddingX}`}>
            <b>Requesting Office</b>
            <span style={{paddingLeft:'10px'}}>{selectedVersion.destination.locId}</span>
            <span style={{paddingLeft:'10px'}}>{selectedVersion.destination.respCenterHead.shortName}</span>
            <span style={{paddingLeft:'10px'}}>
              {selectedVersion.destination.address.addr1}{selectedVersion.destination.address.city}
              {selectedVersion.destination.address.state}{selectedVersion.destination.address.zip5}
            </span>
          </div>
        </div>

        <div className={styles.contentInfo}>
          <div className={`${styles.grid} ${styles.paddingV}`}>
            <div className={styles.col412}>
              <b>Requested By:</b> {selectedVersion.customer.fullName}
            </div>
            <div className={styles.col412}>
              <b>Requested Date:</b> {formatDate(selectedVersion.orderedDateTime)}
            </div>
            <div className={styles.col412}>
              <b>Status:</b> {selectedVersion.status}
            </div>
          </div>
        </div>

        <div className={styles.contentInfo}>
          <div className={`${styles.grid} ${styles.paddingV}`}>
            <div className={styles.col412}>
              {selectedVersion.status === 'PENDING' || selectedVersion.status === 'PROCESSING' ?
               (<b>Issuer: </b>) : (<b>Issued By: </b>)}
              {selectedVersion.issuer.lastName}
            </div>
            <div className={styles.col412}>
              <b>Delivery Method:</b> {selectedVersion.deliveryMethod}
            </div>
            <div className={styles.col412}>
              <b>Modified By:</b> {selectedVersion.modifiedBy.lastName}
            </div>

          </div>
        </div>
      </div>

      {/* Notes */}
      {selectedVersion.note || selectedVersion.specialInstructions &&
      (<div className={`${styles.contentContainer} ${styles.largePrintFontSize}`}>
        <div className={styles.contentInfo}>
          {selectedVersion.note && (<div className={`${styles.grid} ${styles.paddingV}`}>
            <div className={`${styles.col212} ${styles.bold}`}>
              Supply Note:
            </div>
            <div className={styles.col1012}>
              {selectedVersion.note}
            </div>
          </div>)}
          {selectedVersion.specialInstructions && (<div className={`${styles.grid} ${styles.paddingV}`}>
            <div className={`${styles.col412} ${styles.bold}`}>
              Special Instructions:
            </div>
            <div className={styles.col812} style={{ textAlign: 'left' }}>
              {selectedVersion.specialInstructions}
            </div>
          </div>)}
        </div>
      </div>)}

      {/* Order Items */}
      <div className={`${styles.contentContainer} ${styles.closeTo}`}>
        <div className={styles.paddingV}>
          <table className={`${styles.essTable} ${styles.supplyListingTablePrintOnly}`}>
            <thead>
            <tr style={{pageBreakInside: 'avoid'}}>
              <th>Commodity Code</th>
              <th>Item</th>
              <th>Quantity</th>
            </tr>
            </thead>
            <tbody>
            {sortedLineItems.map((lineItem, index) => (
              <tr key={index}>
                <td>{lineItem.item.commodityCode}</td>
                <td>{lineItem.item.description}</td>
                <td>{lineItem.quantity}</td>
              </tr>
            ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className={styles.largePrintFontSize} style={{ marginTop: '60px', padding: '20px' }}>
        Received By: _______________________________ Received Date: ________________________________
      </div>
    </div>
  );
}

export default OrderDetailPrint;


function sortSelectedVersionLineItems(selectedVersion) {
  if (selectedVersion && selectedVersion.lineItems) {
    return alphabetizeLineItems(selectedVersion.lineItems, "description");
  }
}