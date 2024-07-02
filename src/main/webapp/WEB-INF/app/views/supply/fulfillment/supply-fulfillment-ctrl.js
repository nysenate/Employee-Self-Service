// supply-fulfillment-ctrl.js
import { fetchApiJson } from "app/utils/fetchJson";

const fetchRequisitions = async (params) => {
    return fetchApiJson('/supply/requisitions', { method: 'GET', payload: params });
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
    const params = {
        status: "REJECTED",
        from: '1969-12-31T19:00:01-05:00',
        dateField: "rejected_date_time",
        limit: 'ALL',
        offset: 0
    };
    const data = await fetchRequisitions(params);
    return data.result;
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

function displayRequisitionWithId(data, requisitionId) {
    if (requisitionId != null) {
        var requisition = findRequisitionById(data, requisitionId);
        $scope.openRequisitionModal(requisition);
    }
};

function findRequisitionById(data, requisitionId) {
    return data.reqs.map[requisitionId];
};