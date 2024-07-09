import React from "react";
import styles from "../universalStyles.module.css";
import { formatDate } from "../helpers";

const Header = () => {
    return (
        <div className={`${styles.supplyDivTable} ${styles.largePrintFontSize}`}>
            <div className={styles.supplyDivTableHeader}>
                <div className={styles.col212}>Commodity Code</div>
                <div className={styles.col712}>Item</div>
                <div className={styles.col212}>Quantity On Hand</div>
                <div className={styles.col112}>
                    {reconciliationStatus.resultErrorMap.size > 0 ? (
                        <span>Difference</span>
                    ):(<span>&nbsp</span>)}
                </div>
            </div>
            {/*Item Rows*/}
            <div className={`${styles.supplyDivTableBody} ${styles.printGrayBottomBorder}`}>
                {filteredItems.map((item, index) => (
                    <React.Fragment key={index}>
                        <div
                            className={`${styles.supplyDivTableRow} ${isItemSelected(item) ? styles.supplyHighlightRow : ''} ${isReconciliationError(item) ? styles.warnImportant : ''} ${index % 2 === 0 ? styles.darkBackground : ''}`}
                            onClick={() => toggleDetails(index)}
                        >
                            <div className={styles.col212}>{item.commodityCode}</div>
                            <div className={styles.col712}>{item.description}</div>
                            <div className={`${styles.col212} ${styles.noPrint}`}>
                                <input
                                    type="number"
                                    style={{ width: '10em' }}
                                    value={inventory.itemQuantities[item.id] || ''}
                                    placeholder="Quantity"
                                    onChange={handleQuantityChange}
                                    className={`${reconciliationStatus.attempted === true && inventory.itemQuantities[item.id] === null ? styles.warnImportant : ''}`}
                                />
                            </div>
                            <div className={styles.col112}>
                                {reconciliationStatus.resultErrorMap.size > 0 ? (<span className={`${styles.boldText} ${styles.noPrint}`}>
                                    {reconciliationStatus.resultErrorMap.get(item.id).expectedQuantity - reconciliationStatus.resultErrorMap.get(item.id).actualQuantity}
                                </span>) : (<span>$nbsp</span>)}
                            </div>
                        </div>
                        {/*Details*/}
                        {isItemSelected(item) && (
                            <div className={styles.supplySubTable}>
                                {/*Detail Header*/}
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
                                        <tr key={shipment.requisitionId} onClick={() => openRequisitionHistoryPopup(req)}>
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
    );
}

export default Header;