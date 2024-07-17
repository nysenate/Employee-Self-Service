import React from 'react';
import styles from '../universalStyles.module.css';
import { formatDate } from "app/views/supply/helpers";

const ReconciliationPrint = ({ reconcilableSearch, inventory, activeItemGroup, handleQuantityChange, reconciliationStatus, toggleDetails, isItemSelected, isReconciliationError, getShipmentsWithItem, getOrderedQuantity, viewShipment }) => {
  return (
    <div className={styles.printOnly}>
      <div className={styles.supplyOrderHero}>
        <h2>Reconciliation</h2>
      </div>
      <div className={styles.contentContainer}>

        <div className={styles.specialUlContainer} style={{ display: 'inline-block', width: '100%' }}>
          <ul className={styles.reconciliationTabLinks}>
            <li className={styles.activeReconciliationTab}><a>{activeItemGroup === 1 ? "Item group 1" : "Item group 2"}</a></li>
          </ul>
        </div>

        <div className={`${styles.supplyDivTable} ${styles.largePrintFontSize}`}>
          <div className={styles.supplyDivTableHeader}>
            <div className={styles.col212}>Commodity Code</div>
            <div className={styles.col712}>Item</div>
            <div className={styles.col212}>Quantity On Hand</div>
            <div className={styles.col112}>
              {reconciliationStatus.resultErrorMap.size > 0 ? (
                <span>Difference</span>
              ) : (
                 <span>&nbsp;</span>
               )}
            </div>
          </div>
          <div className={`${styles.supplyDivTableBody} ${styles.printGrayBottomBorder}`}>
            {reconcilableSearch.items.filter(item => item.reconciliationPage === activeItemGroup).map((item, index) => (
              <React.Fragment key={item.id}>
                <div
                  className={`${styles.supplyDivTableRow} ${isItemSelected(item) ? styles.supplyHighlightRow : ''} ${isReconciliationError(item) ? styles.warnImportant : ''} ${index % 2 === 0 ? styles.darkBackground : ''}`}
                >
                  <div className={styles.col212} onClick={() => toggleDetails(item)}>{item.commodityCode}</div>
                  <div className={styles.col712} style={{ overflow: 'hidden' }} onClick={() => toggleDetails(item)}>{item.description}</div>
                  <div className={`${styles.col212} ${styles.noPrint}`}>
                    <input
                      type="number"
                      style={{ width: '10em' }}
                      value={inventory.itemQuantities[item.id] || ''}
                      placeholder="Quantity"
                      onChange={(e) => handleQuantityChange(e, item.id)}
                      className={`${reconciliationStatus.attempted === true && (inventory.itemQuantities[item.id] === null || inventory.itemQuantities[item.id] === '') ? styles.warnImportant : ''}`}
                    />
                  </div>
                  <div className={styles.col112}>
                    {reconciliationStatus.resultErrorMap.size > 0 ? (
                      <span className={`${styles.boldText} ${styles.noPrint}`}>
                      {reconciliationStatus.resultErrorMap.get(item.id).expectedQuantity - reconciliationStatus.resultErrorMap.get(item.id).actualQuantity}
                    </span>
                    ) : (
                       <span>&nbsp;</span>
                     )}
                  </div>
                </div>
                {isItemSelected(item) && (
                  <div className={styles.supplySubTable}>
                    <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                      <thead>
                      <tr>
                        <th>Id</th>
                        <th>Location</th>
                        <th>Quantity</th>
                        <th>Issued By</th>
                        <th>Approved Date</th>
                      </tr>
                      </thead>
                      <tbody>
                      {getShipmentsWithItem(item).map((shipment) => (
                        <tr key={shipment.requisitionId} onClick={() => viewShipment(shipment)}>
                          <td>{shipment.requisitionId}</td>
                          <td>{shipment.destination.locId}</td>
                          <td>{getOrderedQuantity(shipment, item)}</td>
                          <td>{shipment.issuer ? shipment.issuer.lastName : ''}</td>
                          <td>{shipment.approvedDateTime ? formatDate(shipment.approvedDateTime) : ''}</td>
                        </tr>
                      ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </React.Fragment>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReconciliationPrint;
