import styles from "../universalStyles.module.css";
import { distinctItemQuantity } from "../fulfillment/supply-fulfillment-ctrl";
import { formatDate } from "../helpers";
import React from "react";

const Results = ({ shipments, openRequisitionHistoryPopup }) => {
    return (
        <div>
            {!shipments || shipments.length === 0 ? (
                <div className={styles.contentInfo}>
                    <h2 className={styles.darkGray}>No results were found.</h2>
                </div>
            ) : (
                <div>
                    <div className={styles.paddingX}>
                        <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                            <thead>
                            <tr>
                                <th>Id</th>
                                <th>Location</th>
                                <th>Ordered By</th>
                                <th>Item Count</th>
                                <th>Order Date</th>
                                <th>Complete Date</th>
                                <th>Issued By</th>
                            </tr>
                            </thead>
                            <tbody>
                            {shipments.map((shipment) => (
                                <tr key={shipment.requisitionId} onClick={() => openRequisitionHistoryPopup(shipment)}>
                                    <td>{shipment.requisitionId}</td>
                                    <td>{shipment.destination.locId}</td>
                                    <td>{shipment.customer.lastName}</td>
                                    <td>{distinctItemQuantity(shipment)}</td>
                                    <td>{formatDate(shipment.orderedDateTime)}</td>
                                    <td>{shipment.completedDateTime ? formatDate(shipment.completedDateTime) : ''}</td>
                                    <td>{shipment.issuer ? shipment.issuer.lastName : ''}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Results;
