import styles from "../universalStyles.module.css";
import React from "react";
import LoadingIndicator from "../../../components/LoadingIndicator";
import { formatDate } from "../helpers";

const CompletedOrders = ({ data, onRowClick, distinctItemQuantity }) => {
    const requisitions = data.reqs.completed;
    if(!data.reqRequest.response.$resolved){
        return (
            <div>
                <div className={styles.contentContainer}>
                    <h1 style={{ backgroundColor: '#799933', color: 'white' }}>Completed Requisition Requests</h1>
                </div>
                <LoadingIndicator />
            </div>
        );
    }
    return (
        <div className={styles.contentContainer}>
            <h1 style={{ backgroundColor: '#799933', color: 'white' }}>Completed Requisition Requests</h1>
            <div className={styles.contentInfo}>
                {requisitions.length === 0 ? (
                    <h2 className={styles.darkGray}>No Completed Requests.</h2>
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
                            <th>Completed Date</th>
                            <th>Issuing Employee</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requisitions.sort((a, b) => b.requisitionId - a.requisitionId).map((requisition) => (
                            <tr
                                key={requisition.requisitionId}
                                onClick={() => onRowClick(requisition)}
                            >
                                <td className={`${requisition.deliveryMethod === 'PICKUP' && styles.supplyPickupIcon}`}></td>
                                <td>{requisition.requisitionId}</td>
                                <td>{requisition.destination.locId}</td>
                                <td>{requisition.customer.lastName}</td>
                                <td>{distinctItemQuantity(requisition)}</td>
                                <td>{formatDate(requisition.orderedDateTime)}</td>
                                <td>{formatDate(requisition.completedDateTime)}</td>
                                <td>{requisition.issuer.lastName}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}

export default CompletedOrders;