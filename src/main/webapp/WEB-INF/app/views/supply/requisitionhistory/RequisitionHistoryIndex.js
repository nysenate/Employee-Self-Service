import React, { useEffect, useState } from 'react';
import Hero from "../../../components/Hero";
import styles from "../universalStyles.module.css";
import Header from "./Header";
import Pagination from "../../../components/Pagination";
import Results from "./Results";
import RequisitionPopup from "./RequisitionPopup";
import LoadingIndicator from "../../../components/LoadingIndicator";
import { setRequisitionSearchParam } from "../fulfillment/supply-fulfillment-ctrl"
import {
    formatDateForApi,
    getCurrentDate,
    getOneMonthBeforeDate,
    getSupplyRequisitions
} from "../helpers";

export default function RequisitionHistoryIndex() {
    const [shipments, setShipments] = useState([]);
    const [filters, setFilters] = useState({
        location: 'All',
        issuer: 'All',
        commodity: 'All',
        from: getOneMonthBeforeDate(),
        to: getCurrentDate(),
    });

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
                const response = await getSupplyRequisitions(
                  formatDateForApi(new Date(filters.from)),
                  ordersPerPage,
                  filters.location,
                  1+(currentPage-1)*ordersPerPage,
                  formatDateForApi(new Date(filters.to)),
                  filters.issuer,
                  filters.commodity
                );
                setTotalOrders(response.total);
                // Fixed for bug where the page is out of bounds after changing filters
                if (Math.ceil(response.total / ordersPerPage) < currentPage) {
                    setCurrentPage(Math.ceil(response.total / ordersPerPage));
                }
                setShipments(response.result);
            } catch (error) {
                console.error('Error fetching order history:', error);
            } finally {
                setLoading(false); // Set loading to false after the fetch completes
            }
        };
        fetchAndSetShipments();
    }, [filters, currentPage]);

    const handlePageChange = (page) => {
        if (page >= 1 && page <= Math.ceil(totalOrders / ordersPerPage)) {
            setCurrentPage(page);
        }
    };

    const handleFilterChange = (filterType, value) => {
        setFilters({
            ...filters,
            [filterType]: value,
        });
    };

    const openRequisitionHistoryPopup = (requisition) => {
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
            filters={filters}
            handleFilterChange={handleFilterChange}
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
                    <Results shipments={shipments} openRequisitionHistoryPopup={openRequisitionHistoryPopup}/>
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

