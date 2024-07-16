// supply-fulfillment-ctrl.js
import { fetchApiJson } from "app/utils/fetchJson";

/**
 * Fetch requisitions based on provided parameters.
 *
 * @param {Object} params The parameters for the fetch call.
 * @returns {Promise<Object>} The result of the fetch call.
 */
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

/**
 * Fetch supply employees.
 *
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchSupplyEmployees = async () => {
    return fetchApiJson('/supply/employees', { method: 'GET' });
};

/**
 * Fetch supply items.
 *
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchSupplyItems = async () => {
    return fetchApiJson('/supply/items', { method: 'GET' });
};

/**
 * Fetch supply destinations for a given employee ID.
 *
 * @param {String} empId The employee ID.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const fetchSupplyDestinations = async (empId) => {
    return fetchApiJson(`/supply/destinations/${empId}`);
};

/**
 * Initialize most requisitions.
 *
 * @returns {Promise<Array>} The result of the fetch call.
 */
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

/**
 * Initialize rejected requisitions.
 *
 * @returns {Promise<Array>} The result of the fetch call.
 */
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

/**
 * Get the current date and time in the specified format.
 *
 * @returns {String} The current date and time.
 */
const getCurrentDateTime = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // Months are 0-based
    const day = String(now.getDate()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}T00:00:00-04:00`;
    return formattedDate;
};

/**
 * Get the distinct item quantity in a requisition.
 *
 * @param {Object} requisition The requisition object.
 * @returns {Number} The count of distinct items.
 */
export const distinctItemQuantity = (requisition) => {
    return requisition.lineItems.length;
};

/**
 * Calculate highlighting for a requisition.
 *
 * @param {Object} requisition The requisition object.
 * @returns {Object} The highlighting information.
 */
export const calculateHighlighting = (requisition) => {
    return {
        warn: containsItemOverOrderMax(requisition) || isOverPerMonthMax(requisition) || containsSpecialItem(requisition),
        bold: isOverPerMonthMax(requisition),
    };
};

/**
 * Check if the requisition contains any item over the order max.
 *
 * @param {Object} requisition The requisition object.
 * @returns {Boolean} Whether the requisition contains an item over the order max.
 */
const containsItemOverOrderMax = (requisition) => {
    // Define the logic here
};

/**
 * Check if the requisition is over the monthly max.
 *
 * @param {Object} requisition The requisition object.
 * @param {Object} locationStatistics The location statistics object.
 * @returns {Boolean} Whether the requisition is over the monthly max.
 */
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

/**
 * Check if the requisition contains any special item.
 *
 * @param {Object} requisition The requisition object.
 * @returns {Boolean} Whether the requisition contains a special item.
 */
const containsSpecialItem = (requisition) => {
    // Define the logic here
};

/**
 * Set the requisition search parameter in the URL.
 *
 * @param {String} requisitionId The requisition ID.
 */
export const setRequisitionSearchParam = (requisitionId) => {
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.set("requisitionId", requisitionId);
    window.history.replaceState(null, '', '?' + searchParams.toString());
};

/**
 * Remove the requisition search parameter from the URL.
 */
export const removeRequisitionSearchParam = () => {
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.delete("requisitionId");
    const newSearch = searchParams.toString();
    const newUrl = window.location.pathname + (newSearch ? '?' + newSearch : '');
    window.history.replaceState(null, '', newUrl);
};

/**
 * Display the requisition with the given ID.
 *
 * @param {Object} data The data object containing requisitions.
 * @param {String} requisitionId The requisition ID.
 */
function displayRequisitionWithId(data, requisitionId) {
    if (requisitionId != null) {
        var requisition = findRequisitionById(data, requisitionId);
        $scope.openRequisitionModal(requisition);
    }
};

/**
 * Find a requisition by its ID.
 *
 * @param {Object} data The data object containing requisitions.
 * @param {String} requisitionId The requisition ID.
 * @returns {Object} The found requisition object.
 */
function findRequisitionById(data, requisitionId) {
    return data.reqs.map[requisitionId];
};