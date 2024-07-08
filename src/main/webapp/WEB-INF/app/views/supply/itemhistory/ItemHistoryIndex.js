import React, { useEffect, useState } from 'react';
import Hero from "../../../components/Hero";
import Header from "./Header";
import { formatDateForApi, getCurrentDate, getOneMonthBeforeDate, getItemHistory } from "../helpers";
import styles from "../universalStyles.module.css";
import LoadingIndicator from "../../../components/LoadingIndicator";
import Pagination from "../../../components/Pagination";
import Results from "./Results";
import { setRequisitionSearchParam } from "../fulfillment/supply-fulfillment-ctrl";
import RequisitionPopup from "../requisitionhistory/RequisitionPopup";

const computeMapping = (result) => {
    const newMapping = {};
    result.forEach(requisition => {
        const locId = requisition.destination.locId;

        requisition.lineItems.forEach(lineItem => {
            const commodityCode = lineItem.item.commodityCode;
            const quantity = lineItem.quantity;
            const key = `${commodityCode}:${locId}`;
            if (!newMapping[key]) {
                newMapping[key] = {
                    commodityCode: commodityCode,
                    locId: locId,
                    quantity: 0,
                    requisitions: []
                };
            }
            newMapping[key].quantity += quantity;
            newMapping[key].requisitions.push(requisition);
        });
    });
    const mappingArray = Object.values(newMapping);
    return mappingArray;
}

export default function ItemHistoryIndex () {
    const [mapping, setMapping] = useState();
    const [filters, setFilters] = useState({
        location: 'All',
        item: 'All',
        from: getOneMonthBeforeDate(),
        to: getCurrentDate(),
    });

    const ordersPerPage = 15;
    const [currentPage, setCurrentPage] = useState(1);
    const [totalOrders, setTotalOrders] = useState(0);
    const [loading, setLoading] = useState(true);

    const [isModalOpen, setIsModalOpen] = useState(false); // State to manage modal visibility
    const [selectedRequisition, setSelectedRequisition] = useState(null); // State to manage the selected requisition

    useEffect(() => {
        const fetchAndComputeMapping = async () => {
            try {
                setLoading(true); // Set loading to true before the fetch
                const response = await getItemHistory(
                    formatDateForApi(new Date(filters.from)),
                    filters.item,
                    ordersPerPage,
                    filters.location,
                    1+(currentPage-1)*ordersPerPage,
                    formatDateForApi(new Date(filters.to))
                );
                let tempMapping = computeMapping(response.result);
                const mappingSize = Object.keys(tempMapping).length;
                setTotalOrders(mappingSize);
                if (Math.ceil(mappingSize / ordersPerPage) < currentPage) {
                    setCurrentPage(Math.ceil(mappingSize / ordersPerPage));
                }
                setMapping(tempMapping);
            } catch (error) {
                console.error('Error fetching order history:', error);
            } finally {
                setLoading(false); // Set loading to false after the fetch completes
            }
        };
        fetchAndComputeMapping();
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
            <Hero>Item History</Hero>
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
                        <Results mapping={mapping} openRequisitionHistoryPopup={openRequisitionHistoryPopup}/>
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
};