import React from 'react';
import { useNavigate } from 'react-router-dom';
import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import styles from "../universalStyles.module.css";
import { formatDateYY } from "../helpers";

const RequisitionPopup = ({ requisition, isModalOpen, closeModal }) => {
  const navigate = useNavigate();

  const redirectToFullHistory = () => {
    navigate(`/supply/order-history/order/${requisition.requisitionId}`, { state: { order: requisition } });
  };

  const acceptShipment = () => {
    console.log("Implement whatever acceptShipment(requisition) does from fulfillment-immutable-module.jsp");
  };

  const Title = (requisition) => {
    return `Requisition ${requisition.requisitionId} requested by ${requisition.customer.firstName}
      ${requisition.customer.initial} ${requisition.customer.lastName}`;
  };

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
        <div className={`${styles.col412} ${styles.requisitionModalRightMargin}`} style={{ marginBottom: '0px' }}>
          <h4 style={{ marginBottom: '5px' }}>Status</h4>
          <div>{requisition.status}</div>

          <h4 style={{ marginBottom: '5px' }}>Location</h4>
          <div>{requisition.destination.locId}</div>

          <h4 style={{ marginBottom: '5px' }}>Ordered Date Time</h4>
          <div>{formatDateYY(requisition.orderedDateTime)}</div>

          {requisition.issuer !== null && (
            <>
              <h4 className={styles.contentInfo} style={{ marginBottom: '5px' }}>Issued By</h4>
              <div>{requisition.issuer.lastName}</div>
            </>
          )}

          {requisition.status === 'REJECTED' && (
            <>
              <h4 style={{ marginBottom: '5px' }}>Rejected By</h4>
              <div>{requisition.modifiedBy.lastName}</div>
            </>
          )}

          {requisition.status === 'APPROVED' && (
            <>
              <h4 style={{ marginBottom: '5px' }}>Completed Date Time</h4>
              <div>{formatDateYY(requisition.completedDateTime)}</div>
            </>
          )}

          {requisition.status === 'REJECTED' && (
            <>
              <h4 style={{ marginBottom: '5px' }}>Rejected Date Time</h4>
              <div>{formatDateYY(requisition.rejectedDateTime)}</div>
            </>
          )}

          <h4 style={{ marginBottom: '5px' }}>Actions</h4>
          <div style={{ textAlign: 'center' }}>
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                redirectToFullHistory();
              }}
            >
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
      <div style={{ paddingTop: '10px', textAlign: 'center' }}>
        <Button onClick={closeModal} style={{ width: '15%', backgroundColor: '#8d8d8d' }}>Exit</Button>
      </div>
    </Popup>
  );
};

export default RequisitionPopup;
