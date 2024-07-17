import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css";
import {
    calculateItemHighlighting,
    fetchSupplyDestinations,
    fetchSupplyEmployees,
    fetchSupplyItems,
} from "./supply-fulfillment-ctrl";
import useAuth from "../../../contexts/Auth/useAuth";
import FilterSelect from "../../../components/FilterSelect";
import {
    alphabetizeLineItems,
    formatDateYY,
    processRequisitionPost, rejectRequisitionPost,
    saveRequisitionPost, undoRequisitionPost
} from "../helpers";
import { useNavigate } from "react-router-dom";


export function FulfillmentEditing({ requisition, isModalOpen, closeModal, refreshData, data }) {
    const originalRequisition = requisition;
    const [editableRequisition, setEditableRequisition] = useState({ ...requisition });
    const [dirty, setDirty] = useState(false);

    const [displayRejectInstructions, setDisplayRejectInstructions] = useState(false);
    const [warning, setWarning] = useState(false);

    useEffect(() => {
        setDirty(JSON.stringify(originalRequisition) !== JSON.stringify(editableRequisition));
    }, [editableRequisition]);

    const handleUndo = async () => {
        // setEditableRequisition({ ...originalRequisition });      // this logically should be how it works
        // await undoRequisitionPost({ ...originalRequisition });   // but 8080 undoes processing while saving the changes
        await undoRequisitionPost(editableRequisition);
        setDirty(false);
        refreshData();
        closeModal();
    };
    const handleCancel = () => {
        closeModal();
    };
    const handleSave = async () => {
        await saveRequisitionPost(editableRequisition);
        refreshData();
        closeModal();
    };
    const handleProcess = async () => {
        await processRequisitionPost(editableRequisition);
        refreshData();
        closeModal();
    };

    const handleReject = async () => {
        if (!editableRequisition.note) {
            setDisplayRejectInstructions(true);
        } else {
            await rejectRequisitionPost(editableRequisition);
            refreshData();
            closeModal();
        }
    };

    const Title = (requisition) => {
        if(!isModalOpen || !requisition) return "Err";
        if(requisition.status === 'PENDING')
            return `Pending Requisition ${requisition.requisitionId} Requested By ${requisition.customer?.fullName}`;
        if(requisition.status === 'PROCESSING')
            return `Processing Requisition ${requisition.requisitionId} Requested By ${requisition.customer?.fullName}`;
        if(requisition.status === 'COMPLETED')
            return `Completed Requisition ${requisition.requisitionId} Requested By ${requisition.customer?.fullName}`;
        return "Unrecognized Status";
    }

    return (
        <Popup
            isLocked={true}
            isOpen={isModalOpen}
            onClose={closeModal}
            title={Title(requisition)}
        >
            <div >
                <OrderContent
                    warning={warning}
                    setWarning={setWarning}
                    editableRequisition={editableRequisition}
                    displayRejectInstructions={displayRejectInstructions}
                    setEditableRequisition={setEditableRequisition}
                    originalRequisition={originalRequisition}
                    data={data}
                />
                <ActionButtons
                    originalRequisition={originalRequisition}
                    handleUndo={handleUndo}
                    handleCancel={handleCancel}
                    handleSave={handleSave}
                    handleProcess={handleProcess}
                    handleReject={handleReject}
                    dirty={dirty}
                    hasPermission={() => true} // Replace with actual permission checking logic
                />
            </div>
        </Popup>
    );
}

const OrderContent = ({
                          warning, setWarning,
                          editableRequisition,
                          displayRejectInstructions,
                          setEditableRequisition,
                          originalRequisition, data
                      }) => {

    const auth = useAuth();
    const navigate = useNavigate();
    const deliveryMethods = ['PICKUP', 'DELIVERY'];
    const [destinations, setDestinations] = useState({
        allowed: [],
        selected: editableRequisition.destination
    });
    const [supplyEmployees, setSupplyEmployees] = useState([]);
    const [items, setItems] = useState({
        all: [],
        selected: undefined
    });

    useEffect( () => {
        const fetchAndSetEmployees = async () => {
            try {
                //Get Employees
                let response = await fetchSupplyEmployees();
                let result = response.result;
                const supplyEmployees = []
                result.forEach(r => {
                    supplyEmployees.push(r);
                })
                setSupplyEmployees(supplyEmployees);

            } catch (error) {
                console.error('Error fetching employees:', error);
            }
        };
        const fetchAndSetItems = async () => {
            try {
                const response = await fetchSupplyItems();
                const result = response.result;
                setItems(prevItems => ({
                    ...prevItems,
                    all: result
                }));
            } catch (error) {
                console.error("Error fetching supply items:", error);
            }
        };
        const fetchAndSetDestinations = async () => {
            try {
                //Get Destinations
                const response = await fetchSupplyDestinations(auth.empId());
                const result = response.result;
                setDestinations(prevDestinations => ({
                    ...prevDestinations,
                    allowed: result
                }));
            } catch (error) {
                console.error('Error fetching destinations:', error);
            }
        };
        fetchAndSetEmployees();
        fetchAndSetItems();
        fetchAndSetDestinations();
    }, [auth]);

    const handleItemChange = (selectedOption) => {
        setWarning(false);
        setItems(prev => ({ ...prev, selected: selectedOption }));
    };
    const handleDestinationChange = (selectedOption) => {
        setDestinations(prev => ({ ...prev, selected: selectedOption }));
        setEditableRequisition(prev => ({ ...prev, destination: selectedOption }));
    };
    const handleDeliveryMethodChange = (event) => {
        setEditableRequisition(prev => ({ ...prev, deliveryMethod: event.target.value }));
    };
    const handleIssuerChange = (event) => {
        const selectedEmployee = supplyEmployees.find(emp => emp.employeeId == event.target.value);
        setEditableRequisition(prev => ({ ...prev, issuer: selectedEmployee }));
    };
    const handleNoteChange = (event) => {
        setEditableRequisition(prev => ({ ...prev, note: event.target.value }));
    };

    const handleAddItem = () => {
        if (!items.selected) return;
        if (editableRequisition.lineItems.find(obj => obj.item.id === items.selected.id)) {
            setWarning(true);
            return;
        } else {
            setWarning(false);
            setEditableRequisition(prev => ({
                ...prev,
                lineItems: [...prev.lineItems, { item: items.selected, quantity: 1 }]
            }));
        }
    };

    const redirectToFullHistory = () => {
        navigate(`/supply/order-history/order/${originalRequisition.requisitionId}`, { state: { order: originalRequisition } });
    };

    return (
        <div className={`${styles.grid} ${styles.contentInfo}`}>
            <div className={styles.col812}>
                <div style={{ overflowY: 'auto', maxHeight: '300px' }}>
                    <EditableOrderListing
                      editableRequisition={editableRequisition}
                      setEditableRequisition={setEditableRequisition}
                      data={data}
                    />
                </div>

                {/* Add Item */}
                <div className={styles.paddingX} style={{display: 'block'}}>
                    <label style={{ display: 'inline-block'}}> Add Commodity Code:
                        <span style={{textAlign: 'left', flex: '1', minWidth: '200px'}}>
                            <FilterSelect
                              data={items.all}
                              initialItem={items.selected}
                              valueField="commodityCode"
                              labelField="description"
                              handleSelect={handleItemChange}
                              minWidth={200}
                            />
                        </span>
                    </label>
                    <button className={styles.neutralButton} type="button" style={{display: 'inline-block !important'}} onClick={handleAddItem}>Add Item</button>
                    {warning && (<p style={{color: "#e64727"}}>Item already exists in this order. Please adjust the quantity if it's not correct.</p>)}
                </div>

                {/* Add Note */}
                {displayRejectInstructions && <div style={{ color: '#ff0000' }}>A note must be given when rejecting a requisition.</div>}
                <div style={{ paddingTop: '10px' }}>
                    <label className={styles.col112}>Note:</label>
                    <textarea
                        className={styles.col1112}
                        value={editableRequisition.note || ''}
                        onChange={handleNoteChange}
                        className={displayRejectInstructions ? 'warn-option' : ''}
                    />
                </div>
            </div>

            {/* Right Margin */}
            <div className={`${styles.col412} ${styles.requisitionModalRightMargin}`} style={{marginBottom: '0px'}}>
                {/* Change Location */}
                <h4>Location</h4>
                <span style={{textAlign: 'left'}}>
                    <FilterSelect
                      data={destinations.allowed}
                      initialItem={destinations.selected}
                      valueField="code"
                      labelField="address.addr1"
                      handleSelect={handleDestinationChange}
                      minWidth={175}
                    />
                </span>

                <h4>Delivery Method</h4>
                <select value={editableRequisition.deliveryMethod || ''} onChange={handleDeliveryMethodChange}>
                    {deliveryMethods.map(method => (
                        <option key={method} value={method}>
                            {method}
                        </option>
                    ))}
                </select>

                <h4>Special Instructions</h4>
                {!editableRequisition.specialInstructions ? (
                    <div>No instructions provided for this requisition.</div>
                ) : (
                    <div className={styles.fulfillmentModalSpecialInstructions}>
                        {editableRequisition.specialInstructions}
                    </div>
                )}

                <h4>Ordered Date Time</h4>
                <div>{formatDateYY(editableRequisition.orderedDateTime)}</div>

                <h4>Actions</h4>
                <div style={{ textAlign: 'center' }}>
                    <a target="_blank" href={`/supply/requisition/requisition-view?requisition=${editableRequisition.requisitionId}&print=true`}>
                        Print Requisition
                    </a>
                </div>
                {editableRequisition.status === 'COMPLETED' && (
                    <div style={{ textAlign: 'center' }}>
                        <a
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                redirectToFullHistory();
                            }}
                        >
                            View full history
                        </a>
                    </div>
                )}

                {/* Assign Issuer */}
                <div style={{ textAlign: 'center', paddingBottom: '20px' }}>
                    <label>Assign to: </label>
                    <select value={editableRequisition.issuer?.employeeId || ''} onChange={handleIssuerChange}>
                        <option value="" disabled></option>
                        {supplyEmployees.map(emp => (
                            <option key={emp.employeeId} value={emp.employeeId}>
                                {emp.fullName}
                            </option>
                        ))}
                    </select>
                </div>
            </div>
        </div>
    );
}

const EditableOrderListing = ({ editableRequisition, setEditableRequisition, data }) => {
    const [quantities, setQuantities] = useState({});

    useEffect(() => {
        const newQuantities = editableRequisition.lineItems.reduce((acc, obj) => {
            acc[obj.item.id] = obj.quantity !== undefined ? String(obj.quantity) : '';
            return acc;
        }, {});
        setQuantities(newQuantities);
    }, [editableRequisition.lineItems]);

    const handleQuantityChange = (id, newQuantity) => {
        const numericQuantity = parseInt(newQuantity, 10); // Convert input to number

        setQuantities(prevQuantities => ({
            ...prevQuantities,
            [id]: isNaN(numericQuantity) ? '' : String(numericQuantity)
        }));

        const updatedLineItems = editableRequisition.lineItems.map(obj =>
          obj.item.id === id ? { ...obj, quantity: isNaN(numericQuantity) ? '' : numericQuantity } : obj
        );

        setEditableRequisition(prev => ({
            ...prev,
            lineItems: updatedLineItems
        }));
    };

    const sortedLineItems = editableRequisition.lineItems ? alphabetizeLineItems(editableRequisition.lineItems, 'commodityCode') : [];

    return (
      <div className={styles.contentContainer}>
          <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
              <thead>
              <tr>
                  <th>Commodity Code</th>
                  <th>Item</th>
                  <th>Quantity</th>
              </tr>
              </thead>
              <tbody>
              {sortedLineItems.map(item => (
                <tr
                  key={item.item.id}
                  className={calculateItemHighlighting(item, data.locationStatistics, editableRequisition.destination.locId)}
                >
                    <td>{item.item.commodityCode}</td>
                    <td>{item.item.description}</td>
                    <td>
                        <input
                          type="number"
                          value={quantities[item.item.id] || ''}
                          onChange={e => handleQuantityChange(item.item.id, e.target.value)}
                          min="0"
                          step="1"
                          maxLength="4"
                          style={{ width: '50px' }}
                        />
                    </td>
                </tr>
              ))}
              </tbody>
          </table>
      </div>
    );
};

const ActionButtons = ({
                           originalRequisition,
                           handleUndo,
                           handleCancel,
                           handleSave,
                           handleProcess,
                           handleReject,
                           dirty,
                           hasPermission
                       }) => {
    return (
        <div style={{ paddingTop: '10px', textAlign: 'center' }}>
            {/* Undo Button */}
            {hasPermission("SUPPLY_REQUISITION_APPROVE") && originalRequisition.status !== 'PENDING' && (
                <Button className={styles.linkButton} style={{ width: '15%' }} onClick={handleUndo}>
                    Undo
                </Button>
            )}

            {/* Cancel Button */}
            <Button style={{ width: '15%', backgroundColor: 'grey' }} onClick={handleCancel}>
                Cancel
            </Button>

            {/* Save Button */}
            <Button
                style={{ width: '15%' }}
                onClick={handleSave}
                disabled={!dirty}
            >Save</Button>

            {/* Process Button */}
            {originalRequisition.status === 'PENDING' && (
                <Button style={{ width: '15%', backgroundColor: '#4196a7' }} onClick={handleProcess}>
                    Process
                </Button>
            )}

            {/* Complete Button */}
            {originalRequisition.status === 'PROCESSING' && (
                <Button style={{ width: '15%' }} onClick={handleProcess}>
                    Complete
                </Button>
            )}

            {/* Approve Button */}
            {hasPermission("SUPPLY_REQUISITION_APPROVE") && originalRequisition.status === 'COMPLETED' && (
                <Button
                    style={{ width: '15%', backgroundColor: '#6270bd' }}
                    onClick={handleProcess}
                    disabled={originalRequisition.customer.employeeId == JSON.parse(localStorage.getItem('ess.auth.empId'))}
                    onMouseEnter={() => {
                        // Show popover logic
                    }}
                >Approve</Button>
            )}

            {/* Reject Button */}
            {(originalRequisition.status === 'PENDING' || originalRequisition.status === 'PROCESSING') && (
                <Button
                    style={{ backgroundColor: '#E64727FF', width: '15%', float: 'right' }}
                    onClick={handleReject}
                >Reject</Button>
            )}
        </div>
    );
};

export function FulfillmentImmutable({ requisition, isModalOpen, closeModal }) {
    const acceptShipment = () => {
        console.log("Implement whatever acceptShipment(requisition) does from fulfillment-immutable-module.jsp");
    }

    const Title = (requisition) => {
        if(!isModalOpen || !requisition) return "Err";
        if(requisition.status === 'REJECTED')
            return `Rejected Requisition ${requisition.requisitionId} Requested By ${requisition.customer?.fullName}`;
        if(requisition.status === 'APPROVED' && requisition.savedInSfms == false && requisition.lastSfmsSyncDateTime != null)
            return `Sync Failed Requisition ${requisition.requisitionId} Requested By ${requisition.customer?.fullName}`;
        if(requisition.status === 'APPROVED'  && (requisition.savedInSfms == true || (requisition.savedInSfms == false && requisition.lastSfmsSyncDateTime == null) ))
            return `Approved Requisition ${requisition.requisitionId} Requested By ${requisition.customer?.fullName}`;
        return "Unrecognized Status";
    }

    return (
      <Popup
        isLocked={true}
        isOpen={isModalOpen}
        onClose={closeModal}
        title={Title(requisition)}
      >
          <div className={`${styles.grid} ${styles.contentInfo}`}>
              <div className={styles.col812}>
                  <div className={styles.contentContainer} style={{ overflowY: 'auto', maxHeight: '300px' }}>
                      <table className={`${styles.essTable} ${styles.supplyListingTable}`}>
                          <thead>
                          <tr>
                              <th>Commodity Code</th>
                              <th>Item</th>
                              <th>Quantity</th>
                          </tr>
                          </thead>
                          <tbody>
                          {requisition.lineItems.map(item => (
                            <tr key={item.item.id}>
                                <td>{item.item.commodityCode}</td>
                                <td>{item.item.description}</td>
                                <td>{item.quantity}</td>
                            </tr>
                          ))}
                          </tbody>
                      </table>
                  </div>

                  {/* Note */}
                  {requisition.note && (
                      <div style={{ paddingTop: '10px' }}>
                          <div className={`${styles.col212} ${styles.bold}`}>Note:</div>
                          <div className={styles.col1012}>{requisition.note}</div>
                      </div>
                  )}
              </div>

              {/* Right Margin */}
              <div className={`${styles.col412} ${styles.requisitionModalRightMargin}`} style={{marginBottom: '0px'}}>
                  <h4>Location</h4>
                  <div>{requisition.destination.locId}</div>

                  <h4>Delivery Method</h4>
                  <div>{requisition.deliveryMethod}</div>

                  <h4>Special Instructions</h4>
                  {(!requisition.specialInstructions || requisition.specialInstructions.length === 0) && (
                    <div>No instructions provided for this requisition.</div>)}

                  <h4 className={styles.contentInfo}>Issued By</h4>
                  <div>{requisition.issuer?.lastName}</div>

                  <h4>Ordered Date Time</h4>
                  <div>{formatDateYY(requisition.orderedDateTime)}</div>

                  {requisition.status !== 'CANCELED' && (
                    <>
                        <h4>Approved Date Time</h4>
                        <div>{requisition.approvedDateTime ? formatDateYY(requisition.approvedDateTime) : ''}</div>
                    </>
                  )}

                  <h4>Actions</h4>
                  <div style={{ textAlign: 'center' }}>
                      <a target="_blank" href={`/supply/requisition/requisition-view?requisition=${requisition.requisitionId}&print=true`}>
                          Print Requisition
                      </a>
                  </div>
                  {requisition.status === 'CANCELED' && (
                    <div style={{ textAlign: 'center' }}>
                        <Button onClick={acceptShipment}>Accept</Button>
                    </div>
                  )}
              </div>
              <div style={{ paddingTop: '10px', textAlign: 'center'}}>
                  <Button onClick={closeModal} style={{ width: '15%', backgroundColor: 'grey'}}>Exit</Button>
              </div>
          </div>
      </Popup>
    );
}