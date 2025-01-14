import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './OrderHistoryIndex.module.css';

const OrderTable = ({ orders }) => {
  const navigate = useNavigate();

  const handleRowClick = (order) => {
    navigate(`/supply/order-history/order/${order.requisitionId}`, { state: { order } });
  };

  return (
    <div className={styles.paddingX}>
      {/*Insert Pagination Here*/}
      <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
        <thead>
          <tr>
            <th>Id</th>
            <th>Ordered By</th>
            <th>Destination</th>
            <th>Order Date</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
        {orders.map(order => (
          <tr key={order.requisitionId} onClick={() => handleRowClick(order)}>
            <td>{order.requisitionId}</td>
            <td>{order.customer.lastName}</td>
            <td>{order.destination.locId}</td>
            <td>{new Date(order.orderedDateTime).toLocaleString()}</td>
            <td className={styles[`cell${order.status}`]}>{order.status}</td>
          </tr>
        ))}
        </tbody>
      </table>
      {/*Insert Pagination Here*/}
    </div>
  );
};

function Results({ orderHistory }) {
  const empty = orderHistory.length === 0;

  return (
    <div>
      {empty ? (
          <div className={styles.contentInfo}>
            <h2 className={styles.darkGray}>No results were found.</h2>
          </div>
      ) : (
         <OrderTable orders={orderHistory} />
       )}
    </div>
  );
}

export default Results;
