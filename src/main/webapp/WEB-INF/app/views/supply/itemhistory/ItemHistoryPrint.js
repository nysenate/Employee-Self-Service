import React from "react";
import styles from "../universalStyles.module.css";
import { formatDate } from "app/views/supply/helpers";

const ItemHistoryPrint = ({ params, itemHistories }) => {
  function getTotalQuantity(itemHistories) {
    return itemHistories.reduce((sum, item) => sum + item.quantity, 0);
  }
  return (
    <div className={styles.printOnly} style={{margin: '30px 40px'}}>
      <div style={{ background: 'white' }}>
        <div className={`${styles.supplySimpleContainer} ${styles.bold} `} style={{ marginBottom: '20px' }}>
          <h3 className={styles.supplySimpleItem} style={{ width: '30%' }}>
            Report parameters:
          </h3>
          <div className={styles.supplySimpleItem} style={{ width: '70%' }}>
            <div className={styles.supplySimpleContainer}>
              <h3 className={styles.supplySimpleItem} style={{ width: '50%' }}>
                Item: {params.item}
              </h3>
              <h3 className={styles.supplySimpleItem} style={{ width: '50%' }}>
                Location: {params.locId}
              </h3>
              <h3 className={styles.supplySimpleItem} style={{ width: '50%', marginTop: '0px' }}>
                From: {formatDate(params.from)}
              </h3>
              <h3 className={styles.supplySimpleItem} style={{ width: '50%', marginTop: '0px' }}>
                To: {formatDate(params.to)}
              </h3>
            </div>
          </div>
        </div>

        <div className={styles.supplyItemPrintHeaderRow}>
          <div className={styles.supplyItemPrintItem} style={{ width: '100px' }}>
            Location
          </div>
          <div className={styles.supplyItemPrintItem} style={{ width: '100px' }}>
            Commodity
          </div>
          <div className={styles.supplyItemPrintItem} style={{ width: '115px' }}>
            Ordered By
          </div>
          <div className={styles.supplyItemPrintItem} style={{ width: '160px' }}>
            Completed Date
          </div>
          <div className={styles.supplyItemPrintItem} style={{ width: '115px' }}>
            Issued By
          </div>
          <div className={styles.supplyItemPrintItem} style={{ width: '80px' }}>
            Quantity
          </div>
        </div>

        {itemHistories.map((pair, index) => (
          <div key={index} className={styles.supplyItemPrintRow}>
            <div className={styles.supplyItemPrintItem} style={{ width: '100px' }}>
              {pair.locationCode}
            </div>
            <div className={styles.supplyItemPrintItem} style={{ width: '100px' }}>
              {pair.commodityCode}
            </div>
            <div className={styles.supplyItemPrintItem} style={{ width: '470px' }}>
              {pair.requisitions.map((req, reqIndex) => (
                <div key={reqIndex} className={styles.supplyItemPrintInnerRow}>
                  <div className={styles.supplyItemPrintItem} style={{ width: '115px' }}>
                    {req.customer.lastName}
                  </div>
                  <div className={styles.supplyItemPrintItem} style={{ width: '160px' }}>
                    {formatDate(req.completedDateTime)}
                  </div>
                  <div className={styles.supplyItemPrintItem} style={{ width: '115px' }}>
                    {req.issuer?.lastName}
                  </div>
                  <div className={styles.supplyItemPrintItem} style={{ width: '80px' }}>
                    {getItemQuantity(req, pair.commodityCode)}
                  </div>
                </div>
              ))}
              <div className={styles.supplyItemPrintInnerRow}>
                <div className={styles.supplyItemPrintItem} style={{ width: '275px' }}>
                  &nbsp;
                </div>
                <div className={`${styles.supplyItemPrintItem} ${styles.bold}`} style={{ width: '115px' }}>
                  Total:
                </div>
                <div className={`${styles.supplyItemPrintItem} ${styles.bold}`} style={{ width: '80px' }}>
                  {pair.quantity}
                </div>
              </div>
            </div>
          </div>
        ))}

        <div className={styles.supplyItemPrintTotalRow}>
          <div className={styles.supplyItemPrintItem} style={{ width: '475px' }}>
            &nbsp;
          </div>
          <div className={`${styles.supplyItemPrintItem} ${styles.bold}`}
               style={{ width: '115px', paddingLeft: '10px', border: 'black solid', borderWidth: '1px 0 1px 1px' }}>
            Total:
          </div>
          <div className={`${styles.supplyItemPrintItem} ${styles.bold}`}
               style={{ width: '80px', border: 'black solid', borderWidth: '1px 1px 1px 0' }}>
            {getTotalQuantity(itemHistories)}
          </div>
        </div>
      </div>
    </div>
  );
}
const getItemQuantity = (requisition, commodityCode) => {
  let qty = 0;
  requisition.lineItems.forEach(li => {
    if (li.item.commodityCode === commodityCode) {
      qty = li.quantity;
      return;
    }
  });
  return qty;
};

export default ItemHistoryPrint;