/**
 * Returns true of a requisition should be highlighted in a fulfillment page table.
 * @param requisition
 * @param locaitonStatistics
 * @returns {boolean|void}
 */
export function highlightRequisitionRow(requisition, locaitonStatistics) {
  return (
    containsItemOverOrderMax(requisition) ||
    isAnyItemOverMonthlyMax(requisition, locaitonStatistics) ||
    containsSpecialItem(requisition)
  );
}

/**
 * Returns true if the quantity of any item in this requisition is greater
 * than the recommended per order max quantity.
 * @param requisition
 * @returns {boolean}
 */
function containsItemOverOrderMax(requisition) {
  return requisition.lineItems.some(
    (li) => li.quantity > li.item.perOrderAllowance,
  );
}

/**
 * Returns true if any item in the requisition has already been ordered
 * more than the monthly allowance at this location.
 * @param requisition
 * @param locationStatistics
 */
function isAnyItemOverMonthlyMax(requisition, locationStatistics) {
  requisition.lineItems.some((li) => {
    const monthToDateQty =
      locationStatistics?.[requisition.destination.locId].itemQuantities[
        li.item.commodityCode
      ];
    return monthToDateQty > li.item.perMonthAllowance;
  });
}

/**
 * Returns true if any item in the requisition is a special request item.
 * @param requisition
 * @returns {boolean}
 */
function containsSpecialItem(requisition) {
  return requisition.lineItems.some((li) => li.item.specialRequest);
}

/**
 * Returns true if a requisition should be bolded in a fulfillment page table.
 * @param requisition
 * @param locationStatistics
 */
export function boldRequisitionRow(requisition, locationStatistics) {
  return isAnyItemOverMonthlyMax(requisition, locationStatistics);
}

/**
 * Returns true of an item row should be highlighted in a RequisitionEditModal.
 * @param lineItem
 * @param locationStatistics
 * @param destination
 * @returns {boolean}
 */
export function highlightItemRow(lineItem, locationStatistics, destination) {
  return (
    isItemOverOrderAllowance(lineItem) ||
    isItemOverMonthlyAllowance(lineItem, locationStatistics, destination) ||
    isSpecialItem(lineItem)
  );
}

function isItemOverOrderAllowance(lineItem) {
  return lineItem.quantity > lineItem.item.perOrderAllowance;
}

function isItemOverMonthlyAllowance(lineItem, locationStatistics, destination) {
  return (
    lineItem.quantity >
    locationStatistics?.[destination.locId].itemQuantities[
      li.item.commodityCode
    ]
  );
}

function isSpecialItem(lineItem) {
  return lineItem.item.specialRequest;
}

/**
 * Returns true of an item row should be bolded in a RequisitionEditModal.
 * @param lineItem
 * @param locationStatistics
 * @param destination
 * @returns {boolean}
 */
export function boldItemRow(lineItem, locationStatistics, destination) {
  return isItemOverMonthlyAllowance(lineItem, locationStatistics, destination);
}
