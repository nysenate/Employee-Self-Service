// supply-fulfillment-ctrl.js
import { fetchApiJson } from "app/utils/fetchJson";

const fetchRequisitions = async (params) => {
    const queryParams = new URLSearchParams();

    Object.keys(params).forEach(key => {
        if (Array.isArray(params[key])) {
            params[key].forEach(value => queryParams.append(key, value));
        } else {
            queryParams.append(key, params[key]);
        }
    });

    const path = `/supply/requisitions?${queryParams.toString()}`;

    try {
        const response = await fetchApiJson(path, { method: 'GET' });
        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
};

export const fetchSupplyEmployees = async () => {
    return fetchApiJson('/supply/employees', { method: 'GET' });
};

export const fetchSupplyItems = async () => {
    return fetchApiJson('/supply/items', { method: 'GET' });
};

export const fetchSupplyDestinations = async (empId) => {
    return fetchApiJson(`/supply/destinations/${empId}`);
};

export const initMostReqs = async () => {
    const params = {
        status: ['PENDING', 'PROCESSING', 'COMPLETED', 'APPROVED'],
        reconciled: 'false',
        from: '1969-12-31T19:00:01-05:00',
        limit: 'ALL',
        offset: 0
    };
    const data = await fetchRequisitions(params);
    return data.result;
};

export const initRejectedReqs = async () => {
    const today = getCurrentDateTime();
    const params = {
        status: 'REJECTED',
        from: today,
        dateField: "rejected_date_time",
        limit: 'ALL',
        offset: 0
    };
    const data = await fetchRequisitions(params);
    return data.result;
};

const getCurrentDateTime = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // Months are 0-based
    const day = String(now.getDate()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}T00:00:00-04:00`;
    return formattedDate;
};

export const distinctItemQuantity = (requisition) => {
    return requisition.lineItems.length;
};

export const calculateHighlighting = (requisition) => {
    return {
        warn: containsItemOverOrderMax(requisition) || isOverPerMonthMax(requisition) || containsSpecialItem(requisition),
        bold: isOverPerMonthMax(requisition),
    };
};

const containsItemOverOrderMax = (requisition) => {
    // Define the logic here
};

const isOverPerMonthMax = (requisition, locationStatistics) => {
    if (!locationStatistics) {
        return false;
    }
    let isOver = false;
    requisition.lineItems.forEach(lineItem => {
        const monthToDateQty = locationStatistics.getQuantityForLocationAndItem(requisition.destination.locId, lineItem.item.commodityCode);
        if (monthToDateQty > lineItem.item.perMonthAllowance) {
            isOver = true;
        }
    });
    return isOver;
};

const containsSpecialItem = (requisition) => {
    // Define the logic here
};

export const setRequisitionSearchParam = (requisitionId) => {
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.set("requisitionId", requisitionId);
    window.history.replaceState(null, '', '?' + searchParams.toString());
};

export const removeRequisitionSearchParam = () => {
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.delete("requisitionId");
    const newSearch = searchParams.toString();
    const newUrl = window.location.pathname + (newSearch ? '?' + newSearch : '');
    window.history.replaceState(null, '', newUrl);
};

function displayRequisitionWithId(data, requisitionId) {
    if (requisitionId != null) {
        var requisition = findRequisitionById(data, requisitionId);
        $scope.openRequisitionModal(requisition);
    }
};

function findRequisitionById(data, requisitionId) {
    return data.reqs.map[requisitionId];
};