import React from "react";
import styles from "../universalStyles.module.css";
import LoadingIndicator from "../../../components/LoadingIndicator";

const PendingOrders = ({ data, calculateHighlighting, setRequisitionSearchParam, distinctItemQuantity }) => {
    const requisitions = data.reqs.pending;
    if (!data.reqRequest.response.$resolved) {
        return (
            <div>
                <div className={styles.contentContainer}>
                    <h1 style={{ backgroundColor: '#d19525', color: 'white' }}>Pending Requisition Requests</h1>
                </div>
                <LoadingIndicator />
            </div>
        );
    }
    return (
        <div className={styles.contentContainer}>
            <h1 style={{ backgroundColor: '#d19525', color: 'white' }}>Pending Requisition Requests</h1>
            <div className={styles.contentInfo}>
                {requisitions.length === 0 ? (
                    <h2 className={styles.darkGray}>No Pending Requests.</h2>
                ) : (
                    <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                        <thead>
                        <tr>
                            <th></th>
                            <th>Id</th>
                            <th>Location</th>
                            <th>Employee</th>
                            <th>Item Count</th>
                            <th>Order Date</th>
                            <th>Assigned To</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requisitions.sort((a, b) => b.requisitionId - a.requisitionId).map((requisition) => (
                            <tr
                                key={requisition.requisitionId}
                                className={calculateHighlighting(requisition)}
                                onClick={() => setRequisitionSearchParam(requisition.requisitionId)}
                            >
                                <td
                                    className={requisition.deliveryMethod === 'PICKUP' ? 'supply-pickup-icon' : ''}
                                    title={requisition.deliveryMethod}
                                ></td>
                                <td>{requisition.requisitionId}</td>
                                <td>{requisition.destination?.locId || '-'}</td>
                                <td>{requisition.customer?.lastName || '-'}</td>
                                <td>{distinctItemQuantity(requisition)}</td>
                                <td>{new Date(requisition.orderedDateTime).toLocaleString('en-US', {
                                    year: 'numeric',
                                    month: '2-digit',
                                    day: '2-digit',
                                    hour: '2-digit',
                                    minute: '2-digit',
                                    hour12: true
                                })}</td>
                                <td>{requisition.issuer?.lastName || '-'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default PendingOrders;
