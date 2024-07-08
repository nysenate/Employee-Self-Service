import styles from "../universalStyles.module.css";
import React from "react";
import LoadingIndicator from "../../../components/LoadingIndicator";
import CompletedOrders from "./CompletedOrders";
import { formatDate } from "../helpers";

const ApprovedOrders = ({ data, onRowClick, distinctItemQuantity }) => {
    const requisitions = data.reqs.approved;
    if(!data.reqRequest.response.$resolved){
        return (
            <div>
                <div className={styles.contentContainer}>
                    <h1 style={{ backgroundColor: '#6270BD', color: 'white' }}>Approved Requisition Requests</h1>
                </div>
                <LoadingIndicator />
            </div>
        );
    }
    return (
        <div className={styles.contentContainer}>
            <h1 style={{ backgroundColor: '#6270BD', color: 'white' }}>Approved Requisition Requests</h1>
            <div className={styles.contentInfo}>
                {requisitions.length === 0 ? (
                    <h2 className={styles.darkGray}>No Approved Requests.</h2>
                ) : (
                    <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                        <thead>
                        <tr>
                            <th></th>
                            <th>Id</th>
                            <th>Location</th>
                            <th>Employee</th>
                            <th>Item Count</th>
                            <th>Approved Date</th>
                            <th>Issuing Employee</th>
                            <th>Sync Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        {requisitions.sort((a, b) => b.requisitionId - a.requisitionId).map((requisition) => (
                            <tr
                                key={requisition.requisitionId}
                                onClick={() => onRowClick(requisition)}
                            >
                                <td
                                    className={requisition.deliveryMethod === 'PICKUP' ? 'supply-pickup-icon' : ''}
                                    title={requisition.deliveryMethod}
                                ></td>
                                <td>{requisition.requisitionId}</td>
                                <td>{requisition.destination.locId}</td>
                                <td>{requisition.customer.lastName}</td>
                                <td>{distinctItemQuantity(requisition)}</td>
                                <td>{formatDate(requisition.approvedDateTime)}</td>
                                <td>{requisition.issuer.lastName}</td>
                                <td>
                                    {requisition.lastSfmsSyncDateTime && requisition.savedInSfms && <span className="tick"></span>}
                                    {requisition.lastSfmsSyncDateTime && !requisition.savedInSfms && <span className="cross"></span>}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}

export default ApprovedOrders;