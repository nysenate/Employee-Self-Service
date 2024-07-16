// supply-fulfillment-ctrl.js
import { fetchApiJson } from "app/utils/fetchJson";
import styles from "../universalStyles.module.css";

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

export const fetchLocationStatistics = async () => {
    const moment = new Date();
    const year = moment.getFullYear();
    const month = moment.getMonth() + 1;
    console.log("year: ", year, " month: ", month);
    try {
        const response = await fetchApiJson(`/supply/statistics/locations?month=${month}&year=${year}`);
        console.log("calculateLocationStatistics response:", response);
        console.log("Returning response.result.items: ", response.result.items);
        return response.result.items;
    } catch (err) {
        console.error("calculateLocationStatistics Error: ", err);
    }
}

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
export const calculateHighlighting = (requisition, locationStatistics) => {
    const { warn, bold } = {
        warn: containsItemOverOrderMax(requisition) || isOverPerMonthMax(requisition, locationStatistics) || containsSpecialItem(requisition),
        bold: isOverPerMonthMax(requisition, locationStatistics),
    };
    let className = '';
    if (warn) className += `${styles.warn} `;
    if (bold) className += `${styles.bold} `;

    return className.trim();
};
/**
 * Calculate highlighting for an item.
 *
 * @param {Object} item The item object.
 * @param {Object} locationStatistics The location statistics object.
 * @param {String} locId The location ID.
 * @returns {Object} The highlighting information.
 */
export const calculateItemHighlighting = (item, locationStatistics, locId) => {
    const warn = isItemOverOrderMax(item) || isItemOverPerMonthMax(item, locationStatistics, locId) || isSpecialItem(item);
    const bold = isItemOverPerMonthMax(item, locationStatistics, locId);

    let className = '';
    if (warn) className += `${styles.warn} `;
    if (bold) className += `${styles.bold} `;

    return className.trim();
};


/**
 * Check if the requisition contains any item over the order max.
 *
 * @param {Object} requisition The requisition object.
 * @returns {Boolean} Whether the requisition contains an item over the order max.
 */
const containsItemOverOrderMax = (requisition) => {
    return requisition.lineItems.some(obj => obj.quantity > obj.item.perOrderAllowance);
};
const isItemOverOrderMax = (item) => {
    return item.quantity > item.item.perOrderAllowance;
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
        const monthToDateQty = getQuantityForLocationAndItem(requisition.destination.locId, lineItem.item.commodityCode, locationStatistics);
        if (monthToDateQty > lineItem.item.perMonthAllowance) {
            isOver = true;
        }
    });
    return isOver;
};
const isItemOverPerMonthMax = (item, locationStatistics, locId) => {
    if (!locationStatistics) {
        return false;
    }

    const monthToDateQty = getQuantityForLocationAndItem(locId, item.item.commodityCode, locationStatistics);
    return monthToDateQty > item.item.perMonthAllowance;
};

export const getQuantityForLocationAndItem = (locId, item, locationStatistics) => {
    if (locationStatistics[locId]) {
        return locationStatistics[locId].itemQuantities[item];
    }
}

/**
 * Check if the requisition contains any special item.
 *
 * @param {Object} requisition The requisition object.
 * @returns {Boolean} Whether the requisition contains a special item.
 */
const containsSpecialItem = (requisition) => {
    return requisition.lineItems.some(obj => obj.item.specialRequest);
};
const isSpecialItem = (item) => {
    return item.item.specialRequest;
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