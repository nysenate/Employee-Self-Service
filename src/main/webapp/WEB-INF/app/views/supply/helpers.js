import { fetchApiJson } from "../../utils/fetchJson";

/**
 * Fetch orderable items for a given location ID.
 *
 * @param {String} locId The location ID to fetch items for.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const getItems = async (locId) => {
    return await fetchApiJson(`/supply/items/orderable/${locId}`).then((body) => body.result);
};

/**
 * Restrict input to numeric characters only.
 *
 * @param {Object} e The event object.
 */
export const restrictNumericInput = (e) => {
    const charCode = e.charCode;
    // Allow only numeric characters (0-9)
    if (charCode < 48 || charCode > 57) {
        e.preventDefault();
    }
};

/**
 * Fetch requisitions based on the provided parameters.
 *
 * @param {Object} params The parameters to filter requisitions.
 * @returns {Promise<Object>} The result of the fetch call.
 */
const fetchRequisitions = async (params) => {
    const queryString = new URLSearchParams();
    Object.keys(params).forEach(key => {
        if (Array.isArray(params[key])) {
            params[key].forEach(value => queryString.append(key, value));
        } else {
            queryString.append(key, params[key]);
        }
    });
    const path = `/supply/requisitions?${queryString.toString()}`;
    return fetchApiJson(path, { method: 'GET' });
};


/**
 * Format a date string to a readable format.
 *
 * @param {String} dateString The date string to format.
 * @returns {String} The formatted date string.
 */
export function formatDate(dateString) {
    const options = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    };
    return new Date(dateString).toLocaleString('en-US', options);
}

export function formatDateYY(dateString) {
    const options = {
        year: '2-digit',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    };
    return new Date(dateString).toLocaleString('en-US', options);
}

/**
 * Alphabetize line items based on their description.
 *
 * @param {Array} lineItems The line items to alphabetize.
 * @param {String} props.valueField The field name sorting on.
 * @returns {Array} The alphabetized line items.
 */
export function alphabetizeLineItems(lineItems, valueField) {
    if(valueField) {
        return lineItems.sort((a, b) => {
            return a.item[valueField] < b.item[valueField] ? -1 : a.item[valueField] > b.item[valueField] ? 1 : 0;
        });
    }
    return lineItems.sort((a, b) => {
        return a.item.description < b.item.description ? -1 : a.item.description > b.item.description ? 1 : 0;
    });
}

/**
 * Format a date to the YYYY-MM-DD format for input fields.
 *
 * @param {Date} date The date to format.
 * @returns {String} The formatted date string.
 */
export const formatDateForInput = (date) => {
    return date.toISOString().split('T')[0];
};

/**
 * Format a date to an ISO string for API calls.
 *
 * @param {Date} date The date to format.
 * @returns {String} The formatted date string.
 */
export const formatDateForApi = (date) => {
    return date.toISOString();
};

/**
 * Get the current date in ISO format adjusted for the timezone offset.
 *
 * @returns {String} The current date in ISO format with timezone offset.
 */
export const getCurrentDate = () => {
    const today = new Date();
    return today.toISOString().split('.')[0] + '-04:00'; // Adjust for your timezone offset if needed
};

/**
 * Get the date one month before the current date in ISO format adjusted for the timezone offset.
 *
 * @returns {String} The date one month before in ISO format with timezone offset.
 */
export const getOneMonthBeforeDate = () => {
    const today = new Date();
    today.setMonth(today.getMonth() - 1);
    return today.toISOString().split('.')[0] + '-04:00'; // Adjust for your timezone offset if needed
};

/**
 * Fetch order history based on the provided parameters.
 *
 * @param {String} customerId The customer ID to fetch order history for.
 * @param {String} from The start date for fetching order history.
 * @param {Number} limit The number of results to limit.
 * @param {String} location The location to fetch order history for.
 * @param {Number} offset The offset for pagination.
 * @param {String} status The status to filter order history.
 * @param {String} to The end date for fetching order history.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const getOrderHistory = async (customerId, from, limit, location, offset, status, to) => {
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

/**
 * Fetch supply requisitions based on the provided parameters.
 *
 * @param {String} from The start date for fetching supply requisitions.
 * @param {Number} limit The number of results to limit.
 * @param {String} location The location to fetch supply requisitions for.
 * @param {Number} offset The offset for pagination.
 * @param {String} to The end date for fetching supply requisitions.
 * @param {String} [issuerId] The issuer ID to filter supply requisitions.
 * @param {String} [itemId] The item ID to filter supply requisitions.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const getSupplyRequisitions = async (from, limit, location, offset, to, issuerId, itemId) => {
    const basePath = '/supply/requisitions';
    const queryParams = new URLSearchParams({
        from,
        to,
        limit,
        location,
        offset,
    });

    if (issuerId && issuerId !== 'All') queryParams.append('issuerId', issuerId);
    if (itemId && itemId !== 'All') queryParams.append('itemId', itemId);

    ['APPROVED', 'REJECTED'].forEach(status => queryParams.append('status', status));

    const path = `${basePath}?${queryParams.toString()}`;

    try {
        const response = await fetchApiJson(path);
        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
};

/**
 * Fetch locations for a given employee ID.
 *
 * @param {String} empId The employee ID to fetch locations for.
 * @returns {Promise<Object>} The result of the fetch call.
 */
export const getLocations = async (empId) => {
    return await fetchApiJson(`/supply/destinations/${empId}`).then((body) => body);
};