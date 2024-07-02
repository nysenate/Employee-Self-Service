import React, { useEffect, useState } from 'react';
import Hero from "../../../components/Hero";
import styles from "../universalStyles.module.css";
import Pagination from "../../../components/Pagination";
import LoadingIndicator from "../../../components/LoadingIndicator";
import { fetchApiJson } from "../../../utils/fetchJson";
import { distinctItemQuantity } from "../fulfillment/supply-fulfillment-ctrl"

const formatDateForInput = (date) => {
    return date.toISOString().split('T')[0];
};

const formatDateForApi = (date) => {
    return date.toISOString().split('.')[0] + '-04:00'; // Adjust for your timezone offset if needed
};

const getCurrentDate = () => {
    const today = new Date();
    return formatDateForApi(today);
};

const getOneMonthBeforeDate = () => {
    const today = new Date();
    today.setMonth(today.getMonth() - 1);
    return formatDateForApi(today);
};

const fetchRequisitions = async (params) => {
    const queryString = new URLSearchParams();
    Object.keys(params).forEach(key => {
        if (Array.isArray(params[key])) {
            params[key].forEach(value => queryString.append(key, value));
        } else {
            queryString.append(key, params[key]);
        }
    });
    const path = `/supply/requisitions?${queryString.toString()}`;
    return fetchApiJson(path, { method: 'GET' });
};

const getShipments = async (to, from) => {
    const params = {
        from: from,
        to: to,
        limit: 12,
        offset: 1,
        location: 'All',
        status: ['APPROVED', 'REJECTED']
    };
    const data = await fetchRequisitions(params);
    return data.result;
};

export default function RequisitionHistoryIndex() {
    const [shipments, setShipments] = useState([]);
    const [location, setLocation] = useState('');
    const [issuer, setIssuer] = useState('');
    const [commodity, setCommodity] = useState('');
    const [from, setFrom] = useState(getOneMonthBeforeDate());
    const [to, setTo] = useState(getCurrentDate());

    const ordersPerPage = 12;
    const [currentPage, setCurrentPage] = useState(1);
    const [totalOrders, setTotalOrders] = useState(0);
    const [loading, setLoading] = useState(true);

    const handlePageChange = (page) => {
        if (page >= 1 && page <= Math.ceil(totalOrders / ordersPerPage)) {
            setCurrentPage(page);
        }
    };

    useEffect(() => {
        const fetchAndSetShipments = async () => {
            try {
                setLoading(true); // Set loading to true before the fetch
                const response = await getShipments(formatDateForApi(new Date(to)), formatDateForApi(new Date(from)));
                setTotalOrders(response.total);
                // Fix for bug where the page is out of bounds after changing filters
                if (Math.ceil(response.total / ordersPerPage) < currentPage) {
                    setCurrentPage(Math.ceil(response.total / ordersPerPage));
                }
                setShipments(response);
            } catch (error) {
                console.error('Error fetching order history:', error);
            } finally {
                setLoading(false); // Set loading to false after the fetch completes
            }
        };
        fetchAndSetShipments();
    }, [from, to, location, issuer, commodity, currentPage]);

    return (
        <div>
            <Hero>Requisition History</Hero>
            <Header
                fromDate={formatDateForInput(new Date(from))}
                setFromDate={(date) => setFrom(new Date(date))}
                toDate={formatDateForInput(new Date(to))}
                setToDate={(date) => setTo(new Date(date))}
            />
            <div className={styles.contentContainer}>
                {loading ? (
                    <LoadingIndicator />
                ) : (
                    <>
                        {totalOrders > ordersPerPage && (
                            <Pagination
                                currentPage={currentPage}
                                totalPages={Math.ceil(totalOrders / ordersPerPage)}
                                onPageChange={handlePageChange}
                            />
                        )}
                        <Results shipments={shipments} />
                        {totalOrders > ordersPerPage && (
                            <Pagination
                                currentPage={currentPage}
                                totalPages={Math.ceil(totalOrders / ordersPerPage)}
                                onPageChange={handlePageChange}
                            />
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

const Header = ({ fromDate, setFromDate, toDate, setToDate }) => {
    return (
        <div className={`${styles.contentContainer} ${styles.contentControls}`}>
            <h4 className={`${styles.contentInfo} ${styles.supplyText}`} style={{ marginBottom: '0px' }}>
                Search approved and rejected requisitions.
            </h4>
            <div className={styles.grid} style={{ textAlign: 'center' }}>
                <div className={`${styles.col412} ${styles.paddingX}`}>
                    <label className={styles.supplyText}>Location:</label>
                </div>
                <div className={`${styles.col412} ${styles.paddingX}`}>
                    <label className={styles.supplyText}>Issuer:</label>
                </div>
                <div className={`${styles.col412} ${styles.paddingX}`}>
                    <label className={styles.supplyText}>Commodity:</label>
                </div>
                <div className={styles.col612} style={{ padding: '0 10px 10px 10px' }}>
                    <label className={styles.supplyText}>From:</label>
                    <input
                        type="date"
                        id="from-date"
                        name="from-date"
                        value={fromDate}
                        onChange={(e) => setFromDate(e.target.value)}
                    />
                </div>
                <div className={styles.col612} style={{ padding: '0 10px 10px 10px' }}>
                    <label className={styles.supplyText}>To:</label>
                    <input
                        type="date"
                        id="to-date"
                        name="to-date"
                        value={toDate}
                        onChange={(e) => setToDate(e.target.value)}
                    />
                </div>
            </div>
        </div>
    );
}

const Results = ({ shipments }) => {
    console.log(shipments);
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
                                // <tr key={shipment.requisitionId} onClick={() => viewRequisition(shipment)}>
                                <tr key={shipment.requisitionId} onClick={() => console.log(shipment)}>
                                    <td>{shipment.requisitionId}</td>
                                    <td>{shipment.destination.locId}</td>
                                    <td>{shipment.customer.lastName}</td>
                                    <td>{distinctItemQuantity(shipment)}</td>
                                    <td>{new Date(shipment.orderedDateTime).toLocaleString('en-US', {
                                        year: 'numeric',
                                        month: '2-digit',
                                        day: '2-digit',
                                        hour: '2-digit',
                                        minute: '2-digit',
                                        hour12: true
                                    })}</td>
                                    <td>{shipment.completedDateTime ? new Date(shipment.completedDateTime).toLocaleString('en-US', {
                                        year: 'numeric',
                                        month: '2-digit',
                                        day: '2-digit',
                                        hour: '2-digit',
                                        minute: '2-digit',
                                        hour12: true
                                    }): ''}</td>
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