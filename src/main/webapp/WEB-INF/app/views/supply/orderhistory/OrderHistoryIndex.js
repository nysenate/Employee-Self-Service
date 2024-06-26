import React, { useState, useEffect } from "react";
import Hero from "../../../components/Hero";
import SubHero from "./SubHero";
import Results from "./Results";
import styles from './OrderHistoryIndex.module.css';
import useAuth from "app/contexts/Auth/useAuth";
import { fetchApiJson } from "app/utils/fetchJson";  // Import the custom fetch function
import Pagination from "../../../components/Pagination";

const formatDateForInput = (date) => {
    return date.toISOString().split('T')[0];
};

const formatDateForApi = (date) => {
    return date.toISOString();
};

const getCurrentDate = () => {
    const today = new Date();
    return today.toISOString().split('.')[0] + '-04:00'; // Adjust for your timezone offset if needed
};

const getOneMonthBeforeDate = () => {
    const today = new Date();
    today.setMonth(today.getMonth() - 1);
    return today.toISOString().split('.')[0] + '-04:00'; // Adjust for your timezone offset if needed
};

const getOrderHistory = async (customerId, from, limit, location, offset, status, to) => {
    const basePath = '/supply/requisitions/orderHistory';
    const queryParams = new URLSearchParams({
        customerId,
        from,
        to,
        limit,
        location,
        offset,
    });

    if (status === 'ALL') {
        const statuses = ['PENDING', 'PROCESSING', 'COMPLETED', 'APPROVED', 'REJECTED'];
        statuses.forEach(status => queryParams.append('status', status));
    } else {
        queryParams.append('status', status);
    }

    const path = `${basePath}?${queryParams.toString()}`;

    try {
        const response = await fetchApiJson(path);
        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
};

export default function OrderHistoryIndex() {
    const auth = useAuth();
    const [customerId, setCustomerId] = useState();
    const [orderHistory, setOrderHistory] = useState([]);
    const [from, setFrom] = useState(getOneMonthBeforeDate());
    const [to, setTo] = useState(getCurrentDate());
    const [status, setStatus] = useState('ALL');
    const statusOptions = ['ALL', 'PENDING', 'PROCESSING', 'COMPLETED', 'APPROVED', 'REJECTED'];
    const ordersPerPage = 12;
    const [currentPage, setCurrentPage] = useState(1);
    const [totalOrders, setTotalOrders] = useState(0);

    const handlePageChange = (page) => {
        if (page >= 1 && page <= Math.ceil(totalOrders / ordersPerPage)) {
            setCurrentPage(page);
        }
    };

    useEffect(() => {
        const fetchCustomerIdAndOrderHistory = async () => {
            try {
                const customerId = auth.empId();
                setCustomerId(customerId);
                const response = await getOrderHistory(customerId, formatDateForApi(new Date(from)), ordersPerPage, 'A42FB-W', 1+(currentPage-1)*ordersPerPage, status, formatDateForApi(new Date(to)));
                setTotalOrders(response.total);
                // Below fixes a bug persistent in dev. When one a page and change filter s.t. the page is no longer in bounds, no results will appear until refresh
                if(Math.ceil(response.total / ordersPerPage) < currentPage) setCurrentPage(Math.ceil(response.total / ordersPerPage));
                setOrderHistory(response.result);
            } catch (error) {
                console.error('Error fetching order history:', error);
            }
        };

        if (auth) {
            fetchCustomerIdAndOrderHistory();
        }
    }, [auth, from, to, status, currentPage]);

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
            <div className={styles.contentContainer}>
                {totalOrders > ordersPerPage ? (
                    <Pagination
                        currentPage={currentPage}
                        totalPages={Math.ceil(totalOrders / ordersPerPage)}
                        onPageChange={handlePageChange}
                    />) : (<div></div>)
                }
                <Results orderHistory={orderHistory} />
                {totalOrders > ordersPerPage ? (
                    <Pagination
                        currentPage={currentPage}
                        totalPages={Math.ceil(totalOrders / ordersPerPage)}
                        onPageChange={handlePageChange}
                    />) : (<div></div>)
                }
            </div>
        </div>
    );
}
