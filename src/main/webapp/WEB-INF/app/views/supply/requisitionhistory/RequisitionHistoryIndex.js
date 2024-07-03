import React, { useEffect, useState } from 'react';
import Hero from "../../../components/Hero";
import styles from "../universalStyles.module.css";
import Pagination from "../../../components/Pagination";
import LoadingIndicator from "../../../components/LoadingIndicator";
import { fetchApiJson } from "../../../utils/fetchJson";
import { distinctItemQuantity, setRequisitionSearchParam } from "../fulfillment/supply-fulfillment-ctrl"
import {
    getShipments,
    formatDateForInput,
    formatDateForApi,
    getCurrentDate,
    getOneMonthBeforeDate,
    formatDate, formatDateYY
} from "../helpers";
import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import { FulfillmentEditing } from "../fulfillment/FulfillmentPopups";

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

    const [isModalOpen, setIsModalOpen] = useState(false); // State to manage modal visibility
    const [selectedRequisition, setSelectedRequisition] = useState(null); // State to manage the selected requisition

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

    const handlePageChange = (page) => {
        if (page >= 1 && page <= Math.ceil(totalOrders / ordersPerPage)) {
            setCurrentPage(page);
        }
    };

    const viewRequisition = (requisition) => {
        setSelectedRequisition(requisition); // Set the selected requisition
        setRequisitionSearchParam(requisition.requisitionId)
        setIsModalOpen(true); // Open the modal
    };
    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedRequisition(null);
    };

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
                        <Results shipments={shipments} viewRequisition={viewRequisition}/>
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
            {selectedRequisition && (
              <RequisitionPopup
                requisition={selectedRequisition}
                isModalOpen={isModalOpen}
                closeModal={closeModal}
              />
            )}
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

const Results = ({ shipments, viewRequisition }) => {
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
                                <tr key={shipment.requisitionId} onClick={() => viewRequisition(shipment)}>
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

const RequisitionPopup = ({ requisition, isModalOpen, closeModal }) => {

    const acceptShipment = () => {
        console.log("Implement whatever acceptShipment(requisition) does from fulfillment-immutable-module.jsp");
    }
    const Title = (requisition) => {
        return `Requisition ${requisition.requisitionId} requested by ${requisition.customer.firstName}
      ${requisition.customer.initial} ${requisition.customer.lastName}`;
    }

    return (
      <Popup
        isLocked={false}
        isOpen={isModalOpen}
        onClose={closeModal}
        title={Title(requisition)}
      >
          <div className={`${styles.grid} ${styles.contentInfo}`}>
              <div className={styles.col812}>
                  <div className={styles.contentContainer} style={{ overflowY: 'auto', maxHeight: '300px' }}>
                      <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                          <thead>
                          <tr>
                              <th>Commodity Code</th>
                              <th>Item</th>
                              <th>Quantity</th>
                          </tr>
                          </thead>
                          <tbody>
                          {requisition.lineItems.map(item => (
                            <tr key={item.item.id}>
                                <td>{item.item.commodityCode}</td>
                                <td>{item.item.description}</td>
                                <td>{item.quantity}</td>
                            </tr>
                          ))}
                          </tbody>
                      </table>
                  </div>

                  {/* Note */}
                  {requisition.note && (
                    <div style={{ paddingTop: '10px' }}>
                        <div className={`${styles.col212} ${styles.bold}`}>Note:</div>
                        <div className={styles.col1012}>{requisition.note}</div>
                    </div>
                  )}
              </div>

              {/* Right Margin */}
              <div className={`${styles.col412} ${styles.requisitionModalRightMargin}`} style={{marginBottom: '0px'}}>
                  <h4 style={{marginBottom: '5px'}}>Status</h4>
                  <div>{requisition.status}</div>

                  <h4 style={{marginBottom: '5px'}}>Location</h4>
                  <div>{requisition.destination.locId}</div>

                  <h4 style={{marginBottom: '5px'}}>Ordered Date Time</h4>
                  <div>{formatDateYY(requisition.orderedDateTime)}</div>

                  {requisition.issuer !== null && (
                    <>
                        <h4 className={styles.contentInfo} style={{marginBottom: '5px'}}>Issued By</h4>
                        <div>{requisition.issuer.lastName}</div>
                    </>)
                  }

                  {requisition.status === 'REJECTED' && (
                    <>
                        <h4 style={{marginBottom: '5px'}}>Rejected By</h4>
                        <div>{requisition.modifiedBy.lastName}</div>
                    </>
                  )}

                  {requisition.status === 'APPROVED' && (
                    <>
                        <h4 style={{marginBottom: '5px'}}>Completed Date Time</h4>
                        <div>{formatDateYY(requisition.completedDateTime)}</div>
                    </>
                  )}

                  {requisition.status === 'REJECTED' && (
                    <>
                        <h4 style={{marginBottom: '5px'}}>Rejected Date Time</h4>
                        <div>{formatDateYY(requisition.rejectedDateTime)}</div>
                    </>
                  )}

                  <h4 style={{marginBottom: '5px'}}>Actions</h4>
                  <div style={{ textAlign: 'center' }}>
                      <a target="_blank" href={`order-history/order/${requisition.requisitionId}`}>
                          View full history
                      </a>
                  </div>
                  {requisition.status === 'CANCELED' && (
                    <div style={{ textAlign: 'center' }}>
                        <Button onClick={acceptShipment}>Accept</Button>
                    </div>
                  )}
              </div>
          </div>
          <div style={{ paddingTop: '10px', textAlign: 'center'}}>
              <Button onClick={closeModal} style={{ width: '15%', backgroundColor: '#8d8d8d'}}>Exit</Button>
          </div>
      </Popup>
    );
}