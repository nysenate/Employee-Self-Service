import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import SubHero from "app/views/supply/orders/order-list/SubHero";
import Results from "app/views/supply/orders/order-list/Results";
import styles from "app/views/supply/shared/styles/universalStyles.module.css";
import Pagination from "app/components/Pagination";
import LoadingIndicator from "app/components/LoadingIndicator";
import {
  formatDateForApi,
  formatDateForInput,
  getCurrentDate,
  getOneMonthBeforeDate,
  getOrderHistory,
} from "app/views/supply/shared/helpers/helpers";
import { add } from "date-fns";
import useAuthedUser from "app/core/useAuthedUser";

export default function OrderHistoryIndex() {
  const { data: user } = useAuthedUser();
  const [orderHistory, setOrderHistory] = useState([]);
  const [from, setFrom] = useState(getOneMonthBeforeDate());
  const [to, setTo] = useState(getCurrentDate());
  const [status, setStatus] = useState("ALL");
  const statusOptions = [
    "ALL",
    "PENDING",
    "PROCESSING",
    "COMPLETED",
    "APPROVED",
    "REJECTED",
  ];
  const ordersPerPage = 12;
  const [currentPage, setCurrentPage] = useState(1);
  const [totalOrders, setTotalOrders] = useState(0);
  const [loading, setLoading] = useState(false);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(totalOrders / ordersPerPage)) {
      setCurrentPage(page);
    }
  };

  useEffect(() => {
    const fetchCustomerIdAndOrderHistory = async () => {
      try {
        setLoading(true); // Set loading to true before the fetch
        const customerId = user.employeeId;
        const response = await getOrderHistory(
          customerId,
          formatDateForApi(new Date(from)),
          ordersPerPage,
          // userData().employee.empWorkLocation.locId, // TODO fetch user work location to use here.
          1 + (currentPage - 1) * ordersPerPage,
          status,
          formatDateForApi(add(new Date(to), { days: 1 })),
        );
        setTotalOrders(response.total);
        // Below fixes a bug persistent in dev. When one a page and change filter s.t. the page is no longer in bounds, no results will appear until refresh
        if (Math.ceil(response.total / ordersPerPage) < currentPage)
          setCurrentPage(Math.ceil(response.total / ordersPerPage));
        setOrderHistory(response.result);
      } catch (error) {
        console.error("Error fetching order history:", error);
      } finally {
        setLoading(false); // Set loading to false after the fetch completes
      }
    };

    if (user) {
      fetchCustomerIdAndOrderHistory();
    }
  }, [user, from, to, status, currentPage]);

  return (
    <div>
      <Hero>Order History</Hero>
      <SubHero
        fromDate={formatDateForInput(new Date(from))}
        setFromDate={(date) => setFrom(new Date(date))}
        toDate={formatDateForInput(new Date(to))}
        setToDate={(date) => setTo(new Date(date))}
        status={status}
        setStatus={setStatus}
        statusOptions={statusOptions}
      />
      {loading ? (
        <LoadingIndicator />
      ) : (
        <div className={styles.contentContainer}>
          <>
            {totalOrders > ordersPerPage && (
              <Pagination
                currentPage={currentPage}
                totalPages={Math.ceil(totalOrders / ordersPerPage)}
                onPageChange={handlePageChange}
              />
            )}
            <Results orderHistory={orderHistory} />
            {totalOrders > ordersPerPage && (
              <Pagination
                currentPage={currentPage}
                totalPages={Math.ceil(totalOrders / ordersPerPage)}
                onPageChange={handlePageChange}
              />
            )}
          </>
        </div>
      )}
    </div>
  );
}
