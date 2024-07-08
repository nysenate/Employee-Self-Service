import styles from "../universalStyles.module.css";
import { distinctItemQuantity } from "../fulfillment/supply-fulfillment-ctrl";
import { formatDate } from "../helpers";
import React, { useState } from "react";

const Results = ({ value, openRequisitionHistoryPopup }) => {
    const [showDetails, setShowDetails] = useState(false);

    return (
        <div>
            {!value || value.length === 0 ? (
                <div className={styles.contentInfo}>
                    <h2 className={styles.darkGray}>No results were found.</h2>
                </div>
            ) : (
                <div>
                    <div className={styles.supplyDivTable}>
                        <div className={`${styles.supplyDivTableHeader}`}>
                            <div className={styles.col412}>Commodity Code</div>
                            <div className={styles.col412}>Location Code</div>
                            <div className={styles.col412}>Quantity</div>
                        </div>
                        <div className={styles.supplyDivTableBody}>
                            <div className={`${styles.supplyDivTableRow} ${showDetails ? styles.supplyHighlightRow : ''} ${styles.darkBackground}`}
                            onClick={()=>{setShowDetails(prevShowDetails => !prevShowDetails)}}>
                                <div className={styles.col412}>{value.commodityCode}</div>
                                <div className={styles.col412}>{value.locId}</div>
                                <div className={styles.col412}>{value.quantity}</div>
                            </div>
                        </div>
                        {showDetails && (<div className={styles.supplySubTable}>
                            <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                                <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Ordered By</th>
                                    <th>Quantity</th>
                                    <th>Complete Date</th>
                                    <th>Issued By</th>
                                </tr>
                                </thead>

                                <tbody>
                                {value.requisitions.map((req) => (
                                    <tr key={req.requisitionId}
                                        onClick={() => openRequisitionHistoryPopup(req)}>
                                        <td>{req.requisitionId}</td>
                                        <td>{req.customer.lastName}</td>
                                        <td>{getItemQuantity(req, value.commodityCode)}</td>
                                        <td>{req.completedDateTime ? formatDate(req.completedDateTime) : ''}</td>
                                        <td>{req.issuer ? req.issuer.lastName : ''}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>)}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Results;
