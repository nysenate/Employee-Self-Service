import React, { useEffect, useState } from 'react';
import Hero from '../../../components/Hero';
import { fetchApiJson } from '../../../utils/fetchJson';
import styles from '../universalStyles.module.css';
import LoadingIndicator from '../../../components/LoadingIndicator';
import { formatDate } from '../helpers';
import { Button } from '../../../components/Button';
import { useNavigate } from "react-router-dom";
import Popup from "../../../components/Popup";

export default function ReconciliationIndex() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [selectedItem, setSelectedItem] = useState(null);
  const [viewItems, setViewItems] = useState([]);
  const [reconcilableSearch, setReconcilableSearch] = useState({
    matches: [],
    items: [],
    response: {},
    error: false
  });
  const [activeItemGroup, setActiveItemGroup] = useState(1);
  const [reconcilableItemMap, setReconcilableItemMap] = useState({});
  const [reconciliationStatus, setReconciliationStatus] = useState({
    attempted: false,
    result: {},
    resultErrorMap: new Map(),
  });
  const [inventory, setInventory] = useState({ itemQuantities: {} });

  useEffect(() => {
    const fetchAndSetRec = async () => {
      try {
        setLoading(true);
        const response = await fetchSupplyReconciliation();
        setInventory(response.result.inventory);
        return response;
      } catch (error) {
        console.error('Error fetching reconciliation:', error);
      } finally {
        setLoading(false);
      }
    };

    const initReconciliationData = (response) => {
      const dto = response.result;
      const updatedReconcilableSearch = {
        matches: dto.requisitions,
        items: alphabetizeItemsByCommodityCode(dto.items),
        response: response,
        error: false,
      };
      setReconcilableSearch(updatedReconcilableSearch);

      const updatedReconcilableItemMap = dto.itemIdToRequisitions;
      setReconcilableItemMap(updatedReconcilableItemMap);

      const updatedInventory = {
        ...dto.inventory,
        isComplete() {
          for (const itemId in this.itemQuantities) {
            if (this.itemQuantities.hasOwnProperty(itemId)) {
              if (this.itemQuantities[itemId] === null || this.itemQuantities[itemId] === '') {
                return false;
              }
            }
          }
          return true;
        },
      };
      setInventory(updatedInventory);
    };

    const initialize = async () => {
      const response = await fetchAndSetRec();
      if (response) {
        initReconciliationData(response);
      }
    };

    initialize();
  }, []);

  const handleQuantityChange = (e, itemId) => {
    const { value } = e.target;
    setInventory(prev => ({
      ...prev,
      itemQuantities: {
        ...prev.itemQuantities,
        [itemId]: value
      }
    }));
  };

  const print = () => {
    window.print();
  };

  const toggleDetails = (item) => {
    setSelectedItem(selectedItem === item ? null : item);
  };

  const isItemSelected = (item) => selectedItem === item;
  const isReconciliationError = (item) => reconciliationStatus.resultErrorMap.get(item.id) != null;

  const getShipmentsWithItem = (item) => {
    return reconcilableItemMap[item.id]
  };

  const getOrderedQuantity = (shipment, item) => {
    const lineItems = shipment.lineItems;
    for (let i = 0; i < lineItems.length; i++) {
      if (lineItems[i].item.id === item.id) {
        return lineItems[i].quantity;
      }
    }
  };

  const viewShipment = (shipment) => {
    navigate(`/supply/order-history/order/${shipment.requisitionId}`, { state: { order: shipment } });
  };

  const reconcile = async () => {
    setReconciliationStatus({
      attempted: true,
      result: {},
      resultErrorMap: new Map()
    });

    if (!inventory.isComplete()) {
      return;
    }

    try {
      // THIS NEEDS TO BE DONE
      const response = await saveReconciliation(inventory); //This is the POST

      // saveResults(response)
      setReconciliationStatus(prevStatus => ({
        ...prevStatus,
        result: response.result
      }));
      response.result.errors.forEach(error => {
        setReconciliationStatus(prevStatus => ({
          ...prevStatus,
          resultErrorMap: prevStatus.resultErrorMap.set(error.itemId, error)
        }));
      });

      // displayReconciliationResults
      console.log(response.result);
      if (response.result.success) {
        setIsModalOpen(true);  //This is supposed to be success popop
      } else {
        setIsModalOpen(true);  //This is supposed to be error popop
      }
    } catch (error) {
      console.error('Error during reconciliation:', error);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  if (loading) {
    return (
        <div>
          <Hero>Reconciliation</Hero>
          <LoadingIndicator />
        </div>
    );
  }

  if (!loading && reconcilableSearch.items.length === 0) {
    return (
        <div>
          <Hero>Reconciliation</Hero>
          <div className={styles.contentContainer}>
            <div className={styles.contentInfo}>
              <h2 className={styles.darkGray}>Reconciliation Not Required</h2>
            </div>
          </div>
        </div>
    );
  }

  return (
      <div>
        <Hero>Reconciliation</Hero>
        <div className={styles.contentContainer}>
          <div className={styles.paddingX} style={{ textAlign: 'center' }}>
            {reconciliationStatus.attempted && !inventory.isComplete() && (
                <div>
                  <div className={styles.essNotification}>
                      <h2>Missing item quantities.</h2>
                      <p style={{ margin: '13px 0px' }}>To reconcile, you must enter a quantity for all items on both pages.</p>
                </div>
                </div>
            )}
          </div>

          <div className={styles.specialUlContainer} style={{ display: 'inline-block', width: '100%' }}>
            <ul className={styles.reconciliationTabLinks}>
              <li className={activeItemGroup === 1 ? styles.activeReconciliationTab : ''}><a href="#" onClick={() => setActiveItemGroup(1)}>Item group 1</a></li>
              <li className={activeItemGroup === 2 ? styles.activeReconciliationTab : ''}><a href="#" onClick={() => setActiveItemGroup(2)}>Item group 2</a></li>
            </ul>
            <a className={styles.noPrint} style={{ margin: '10px', float: 'right' }} onClick={print}>Print</a>
            <Button style={{ float: 'right' }} onClick={reconcile}>Reconcile</Button>
          </div>

          {/* HEADER */}
          <div className={`${styles.supplyDivTable} ${styles.largePrintFontSize}`}>
            <div className={styles.supplyDivTableHeader}>
              <div className={styles.col212}>Commodity Code</div>
              <div className={styles.col712}>Item</div>
              <div className={styles.col212}>Quantity On Hand</div>
              <div className={styles.col112}>
                {reconciliationStatus.resultErrorMap.size > 0 ? (
                    <span>Difference</span>
                ) : (
                    <span>&nbsp;</span>
                )}
              </div>
            </div>
            {/* Item Rows */}
            <div className={`${styles.supplyDivTableBody} ${styles.printGrayBottomBorder}`}>
              {reconcilableSearch.items.filter(item => item.reconciliationPage === activeItemGroup).map((item, index) => (
                  <React.Fragment key={item.id}>
                    <div
                        className={`${styles.supplyDivTableRow} ${isItemSelected(item) ? styles.supplyHighlightRow : ''} ${isReconciliationError(item) ? styles.warnImportant : ''} ${index % 2 === 0 ? styles.darkBackground : ''}`}
                    >
                      <div className={styles.col212} onClick={() => toggleDetails(item)}>{item.commodityCode}</div>
                      <div className={styles.col712} style={{ overflow: 'hidden' }} onClick={() => toggleDetails(item)}>{item.description}</div>
                      <div className={`${styles.col212} ${styles.noPrint}`}>
                        <input
                            type="number"
                            style={{ width: '10em' }}
                            value={inventory.itemQuantities[item.id] || ''}
                            placeholder="Quantity"
                            onChange={(e) => handleQuantityChange(e, item.id)}
                            className={`${reconciliationStatus.attempted === true && (inventory.itemQuantities[item.id] === null || inventory.itemQuantities[item.id] === '') ? styles.warnImportant : ''}`}
                        />
                      </div>
                      <div className={styles.col112}>
                        {reconciliationStatus.resultErrorMap.size > 0 ? (
                            <span className={`${styles.boldText} ${styles.noPrint}`}>
                        {reconciliationStatus.resultErrorMap.get(item.id).expectedQuantity - reconciliationStatus.resultErrorMap.get(item.id).actualQuantity}
                      </span>
                        ) : (
                            <span>&nbsp;</span>
                        )}
                      </div>
                    </div>

                    {/* Details */}
                    {isItemSelected(item) && (
                        <div className={styles.supplySubTable}>
                          {/* Detail Header */}
                          <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                            <thead>
                            <tr>
                              <th>Id</th>
                              <th>Location</th>
                              <th>Quantity</th>
                              <th>Issued By</th>
                              <th>Approved Date</th>
                            </tr>
                            </thead>
                            <tbody>
                            {getShipmentsWithItem(item).map((shipment) => (
                                <tr key={shipment.requisitionId} onClick={() => viewShipment(shipment)}>
                                  <td>{shipment.requisitionId}</td>
                                  <td>{shipment.destination.locId}</td>
                                  <td>{getOrderedQuantity(shipment, item)}</td>
                                  <td>{shipment.issuer ? shipment.issuer.lastName : ''}</td>
                                  <td>{shipment.approvedDateTime ? formatDate(shipment.approvedDateTime) : ''}</td>
                                </tr>
                            ))}
                            </tbody>
                          </table>
                        </div>
                    )}
                  </React.Fragment>
              ))}
            </div>
          </div>
        </div>
        <ReconciliationErrorPopup isModalOpen={isModalOpen} closeModal={closeModal} />
      </div>
  );
}

export const fetchSupplyReconciliation = async () => {
  return fetchApiJson('/supply/reconciliation', { method: 'GET' });
};

const alphabetizeItemsByCommodityCode = (items) => {
  items.sort((a, b) => {
    if (a.commodityCode < b.commodityCode) return -1;
    if (a.commodityCode > b.commodityCode) return 1;
    return 0;
  });
  return items;
};

const ReconciliationErrorPopup = ({ isModalOpen, closeModal }) => {
  const handleReviewErrors = () => {
    console.log("Implement");
    closeModal();
  };
  return (
      <Popup
          isLocked={true}
          isOpen={isModalOpen}
          onClose={closeModal}
          title={"Errors occurred in reconciliation"}
      >
        <div className={styles.confirmModal}>
          <div className={styles.confirmationMessage}>
            <h4>One or more of the quantities entered is incorrect. Errors will be highlighted red.</h4>
            <div className={styles.inputContainer}>
              <Button onClick={handleReviewErrors} style={{ backgroundColor: '#6270bd' }}>Review Errors</Button>
            </div>
          </div>
        </div>
      </Popup>
  );
};
