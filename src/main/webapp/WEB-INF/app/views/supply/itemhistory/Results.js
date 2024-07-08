import styles from "../universalStyles.module.css";
import { formatDate } from "../helpers";
import React, { useState } from "react";

const Results = ({ mapping, openRequisitionHistoryPopup }) => {
    const [details, setDetails] = useState({});

    const toggleDetails = (index) => {
        setDetails((prevDetails) => ({
            ...prevDetails,
            [index]: !prevDetails[index],
        }));
    };

    return (
        <div>
            {!mapping || mapping.length === 0 ? (
                <div className={styles.contentInfo}>
                    <h2 className={styles.darkGray}>No results were found.</h2>
                </div>
            ) : (
                <div>
                    <div className={styles.supplyDivTable}>
                        <div className={styles.supplyDivTableHeader}>
                            <div className={styles.col412}>Commodity Code</div>
                            <div className={styles.col412}>Location Code</div>
                            <div className={styles.col412}>Quantity</div>
                        </div>
                        <div className={styles.supplyDivTableBody}>
                            {mapping.map((value, index) => (
                                <React.Fragment key={index}>
                                    <div
                                        className={`${styles.supplyDivTableRow} ${details[index] ? styles.supplyHighlightRow : ''} ${index % 2 === 0 ? styles.darkBackground : ''}`}
                                        onClick={() => toggleDetails(index)}
                                    >
                                        <div className={styles.col412}>{value.commodityCode}</div>
                                        <div className={styles.col412}>{value.locId}</div>
                                        <div className={styles.col412}>{value.quantity}</div>
                                    </div>
                                    {details[index] && (
                                        <div className={styles.supplySubTable}>
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
                                                    <tr key={req.requisitionId} onClick={() => openRequisitionHistoryPopup(req)}>
                                                        <td>{req.requisitionId}</td>
                                                        <td>{req.customer.lastName}</td>
                                                        <td>{getItemQuantity(req, value.commodityCode)}</td>
                                                        <td>{req.completedDateTime ? formatDate(req.completedDateTime) : ''}</td>
                                                        <td>{req.issuer ? req.issuer.lastName : ''}</td>
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
            )}
        </div>
    );
};

/**
 * Get the quantity of an item ordered in a requisition.
 */
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


export default Results;