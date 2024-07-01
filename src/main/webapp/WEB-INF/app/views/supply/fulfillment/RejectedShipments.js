import styles from "../universalStyles.module.css";
import React from "react";
import CompletedOrders from "./CompletedOrders";

const RejectedShipments = ({ data, onRowClick, distinctItemQuantity }) => {
    const requisitions = data.reqs.rejected;
    if(!data.reqRequest.response.$resolved || requisitions.length === 0) return;
    return (
        <div className={styles.contentContainer}>
            <h1 style={{ backgroundColor: '#8D9892', color: 'white' }}>Rejected Requisition Requests</h1>
            <div className={styles.contentInfo}>
                <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Location</th>
                        <th>Employee</th>
                        <th>Item Count</th>
                        <th>Order Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requisitions.sort((a, b) => b.requisitionId - a.requisitionId).map((requisition) => (
                        <tr
                            key={requisition.requisitionId}
                            onClick={() => onRowClick(requisition)}
                        >
                            <td>{requisition.requisitionId}</td>
                            <td>{requisition.destination.locId}</td>
                            <td>{requisition.customer.lastName}</td>
                            <td>{distinctItemQuantity(requisition)}</td>
                            <td>{new Date(requisition.orderedDateTime).toLocaleString('en-US', {
                                year: 'numeric',
                                month: '2-digit',
                                day: '2-digit',
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: true
                            })}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default RejectedShipments;