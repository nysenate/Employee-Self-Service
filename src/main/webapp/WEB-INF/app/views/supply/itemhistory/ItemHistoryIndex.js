import React, { useEffect, useRef, useState } from 'react';
import Hero from "../../../components/Hero";
import Header from "./Header";
import { formatDateForApi, getCurrentDate, getOneMonthBeforeDate, getItemHistory } from "../helpers";
import styles from "../universalStyles.module.css";
import LoadingIndicator from "../../../components/LoadingIndicator";
import Pagination from "../../../components/Pagination";
import Results from "./Results";
import { setRequisitionSearchParam } from "../fulfillment/supply-fulfillment-ctrl";
import RequisitionPopup from "../requisitionhistory/RequisitionPopup";
import ItemHistoryPrint from "app/views/supply/itemhistory/ItemHistoryPrint";
import { useReactToPrint } from "react-to-print";

const computeMapping = (result, filters) => {
    const newMapping = {};
    result.forEach(requisition => {
        const locId = requisition.destination.locId;
        requisition.lineItems.forEach(lineItem => {
            if(filters.item == lineItem.item.id || filters.item == 'All') {
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
            }
        });
    });
    const mappingArray = Object.values(newMapping);
    return mappingArray;
}

export default function ItemHistoryIndex () {
    const printRef = useRef();
    const [mapping, setMapping] = useState([]);
    const [paginatedMapping, setPaginatedMapping] = useState([]);
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
                    filters.location,
                    formatDateForApi(new Date(filters.to))
                );
                let tempMapping = computeMapping(response.result, filters);
                const mappingSize = tempMapping.length;
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
    }, [filters]);

    useEffect(() => {
        const paginate = (data, pageSize, pageNumber) => {
            return data.slice((pageNumber - 1) * pageSize, pageNumber * pageSize);
        }
        const paginatedData = paginate(mapping, ordersPerPage, currentPage);
        setPaginatedMapping(paginatedData);
    }, [mapping, currentPage]);


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

    const handlePrint = useReactToPrint({
        content: () => printRef.current,
    });

    return (
        <div>
            <Hero>Item History</Hero>
            <Header
                filters={filters}
                handleFilterChange={handleFilterChange}
                handlePrint={handlePrint}
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
                        <Results mapping={paginatedMapping} openRequisitionHistoryPopup={openRequisitionHistoryPopup}/>
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
            <div style={{ display: 'none' }}>
                <div ref={printRef}>
                    <ItemHistoryPrint
                      params={filters}
                      itemHistories={mapping}
                    />
                </div>
            </div>
        </div>
    );
};
