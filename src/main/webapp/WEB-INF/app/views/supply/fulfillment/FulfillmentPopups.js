import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css";
import Select from "react-select/base";
import {
    fetchSupplyDestinations,
    fetchSupplyEmployees,
    fetchSupplyItems,
} from "./supply-fulfillment-ctrl";
import useAuth from "../../../contexts/Auth/useAuth";


export function FulfillmentEditing({ requisition, isModalOpen, closeModal, onAction }) {
    const [originalRequisition, setOriginalRequisition] = useState(requisition);
    const [editableRequisition, setEditableRequisition] = useState({ ...requisition });
    const [dirty, setDirty] = useState(false);

    const [displayRejectInstructions, setDisplayRejectInstructions] = useState(false);
    const [warning, setWarning] = useState(false);

    useEffect(() => {
        setDirty(JSON.stringify(originalRequisition) !== JSON.stringify(editableRequisition));
        console.log("editableRequisition: ", editableRequisition);
    }, [editableRequisition]);

    const handleUndo = () => {
        setEditableRequisition({ ...originalRequisition });
        setDirty(false);
    };
    const handleCancel = () => {
        closeModal();
    };
    const handleSave = () => {
        // Save logic
        onAction(editableRequisition);
        closeModal();
    };
    const handleProcess = () => {
        // Process logic
    };
    const handleComplete = () => {
        // Complete logic
    };
    const handleApprove = () => {
        // Approve Logic
    };

    const handleReject = () => {
        if (!editableRequisition.note) {
            setDisplayRejectInstructions(true);
        } else {
            // Reject logic
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
                />
                <ActionButtons
                    originalRequisition={originalRequisition}
                    handleUndo={handleUndo}
                    handleCancel={handleCancel}
                    handleSave={handleSave}
                    handleProcess={handleProcess}
                    handleComplete={handleComplete}
                    handleApprove={handleApprove}
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
                          setEditableRequisition
                      }) => {

    const auth = useAuth();
    const deliveryMethods = ['PICKUP', 'DELIVERY'];
    const [destinations, setDestinations] = useState({
        allowed: [],
        selected: undefined
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
                console.log("supplyEmployees", supplyEmployees);
                setSupplyEmployees(supplyEmployees);

            } catch (error) {
                console.error('Error fetching employees:', error);
            }
        };
        const fetchAndSetItems = async () => {
            try {
                const response = await fetchSupplyItems();
                const result = response.result;
                console.log("items", result);
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
                console.log("destinations", result);
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
        if (editableRequisition.lineItems.find(item => item.id === items.selected.id)) {
            setWarning(true);
        } else {
            setWarning(false);
            setEditableRequisition(prev => ({
                ...prev,
                lineItems: [...prev.lineItems, { item: items.selected, quantity: 1 }]
            }));
        }
    };

    return (
        <div className={`${styles.grid} ${styles.contentInfo}`}>
            <div className={styles.col812}>
                <div style={{ overflowY: 'auto', maxHeight: '300px' }}>
                    <div>Insert editable-order-listing</div>
                </div>

                {/* Add Item */}
                <div className={styles.paddingX}>
                    <label> Add Commodity Code:
                        <span style={{ textAlign: 'left' }}>
                            <Select
                                value={items.selected}
                                onChange={handleItemChange}
                                options={items.all}
                                getOptionLabel={option => option.commodityCode}
                                getOptionValue={option => option.id}
                                formatOptionLabel={option => (
                                    <>
                                        <div>{option.commodityCode}</div>
                                        <small>{option.description}</small>
                                    </>
                                )}
                                styles={{ minWidth: '200px' }}
                                onInputChange={() => {}} // Add dummy function
                                onMenuOpen={() => {}} // Add dummy function
                            />
                        </span>
                    </label>
                    <Button onClick={handleAddItem} className="neutral-button">Add Item</Button>
                    {warning && <p style={{color: "#e64727"}}>Item already exists in this order. Please adjust the quantity if it's not correct.</p>}
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
            <div className={`${styles.col412} ${styles.requisitionModalRightMargin}`}>
                {/* Change Location */}
                <h4>Location</h4>
                <Select
                    value={destinations.selected}
                    onChange={handleDestinationChange}
                    options={destinations.allowed}
                    getOptionLabel={option => (
                        <>
                            <div>{option.code}</div>
                            <small>{option.locationDescription}</small>
                        </>
                    )}
                    getOptionValue={option => option.code}
                    styles={{ minWidth: '175px' }}
                    onInputChange={() => {}} // Add dummy function
                    onMenuOpen={() => {}} // Add dummy function
                />

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
                    <div className="fulfillment-modal-special-instructions">
                        {editableRequisition.specialInstructions}
                    </div>
                )}

                <h4>Ordered Date Time</h4>
                <div>{new Date(editableRequisition.orderedDateTime).toLocaleString('en-US', {
                    year: '2-digit',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true
                })}</div>

                <h4>Actions</h4>
                <div style={{ textAlign: 'center' }}>
                    <a target="_blank" href={`/supply/requisition/requisition-view?requisition=${editableRequisition.requisitionId}&print=true`}>
                        Print Requisition
                    </a>
                </div>
                {editableRequisition.status === 'COMPLETED' && (
                    <div style={{ textAlign: 'center' }}>
                        <a target="_blank" href={`/supply/requisition/requisition-view?requisition=${editableRequisition.requisitionId}`}>
                            View History
                        </a>
                    </div>
                )}

                {/* Assign Issuer */}
                <div style={{ textAlign: 'center', paddingBottom: '20px' }}>
                    <label>Assign to: </label>
                    <select value={editableRequisition.issuer?.employeeId || ''} onChange={handleIssuerChange}>
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

const ActionButtons = ({
                           originalRequisition,
                           handleUndo,
                           handleCancel,
                           handleSave,
                           handleProcess,
                           handleComplete,
                           handleApprove,
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
                <Button style={{ width: '15%' }} onClick={handleProcess}>
                    Process
                </Button>
            )}

            {/* Complete Button */}
            {originalRequisition.status === 'PROCESSING' && (
                <Button style={{ width: '15%' }} onClick={handleComplete}>
                    Complete
                </Button>
            )}

            {/* Approve Button */}
            {hasPermission("SUPPLY_REQUISITION_APPROVE") && originalRequisition.status === 'COMPLETED' && (
                <Button
                    style={{ width: '15%' }}
                    onClick={handleApprove}
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