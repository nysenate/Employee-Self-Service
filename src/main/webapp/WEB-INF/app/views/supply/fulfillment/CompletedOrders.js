import styles from "../universalStyles.module.css";
import React from "react";
import LoadingIndicator from "../../../components/LoadingIndicator";

const CompletedOrders = ({ data, setRequisitionSearchParam, distinctItemQuantity }) => {
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
                                onClick={() => setRequisitionSearchParam(requisition.requisitionId)}
                            >
                                <td
                                    className={requisition.deliveryMethod === 'PICKUP' ? 'supply-pickup-icon' : ''}
                                    title={requisition.deliveryMethod}
                                ></td>
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
                                <td>{new Date(requisition.completedDateTime).toLocaleString('en-US', {
                                    year: 'numeric',
                                    month: '2-digit',
                                    day: '2-digit',
                                    hour: '2-digit',
                                    minute: '2-digit',
                                    hour12: true
                                })}</td>
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