import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import React, { useState } from "react";
import styles from "../universalStyles.module.css";
import Select from "react-select/base";


export function FulfillmentEditing({ requstition, isModalOpen, closeModal, onAction }) {
    console.log(requstition);
    const [originalRequisition, setOriginalRequisition] = useState(requstition);
    const [dirty, setDirty] = useState(false);

    const handleUndo = () => {
        // Undo logic
    };

    const handleCancel = () => {
        // Cancel logic
        closeModal();
    };

    const handleSave = () => {
        // Save logic
    };

    const handleProcess = () => {
        // Process logic
    };

    const handleComplete = () => {
        // Complete logic
    };

    const handleApprove = () => {
        // Approve logic
    };

    const handleReject = () => {
        // Reject logic
    };

    const hasPermission = (permission) => {
        // Check if the user has the given permission
        return true; // Replace with actual permission checking logic
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
            title={Title(requstition)}
        >
            <div style={{display: 'flex', justifyContent: 'center', gap: '5px'}}>
                <OrderContent/>
                <ActionButtons
                    originalRequisition={requstition}
                    handleUndo={handleUndo}
                    handleCancel={handleCancel}
                    handleSave={handleSave}
                    handleProcess={handleProcess}
                    handleComplete={handleComplete}
                    handleApprove={handleApprove}
                    handleReject={handleReject}
                    dirty={dirty}
                    hasPermission={hasPermission}
                />
            </div>
        </Popup>
    );
}

const OrderContent = () => {
    return(
        <div className={`${styles.grid} ${styles.contentInfo}`}>
            <div className={styles.col812}>
                <div style={{overflowY: 'auto', maxHeight: '300px'}}>
                    <div>Insert editable-order-listing</div>
                </div>

                {/* Add Item */}
                <div className={styles.paddingX}>
                    <label> Add Commodity Code:
                        {/*<span style={{textAlign: 'left'}}>*/}
                        {/*    <Select*/}
                        {/*        value={selectedItem}*/}
                        {/*        onChange={handleChange}*/}
                        {/*        onInputChange={handleInputChange}*/}
                        {/*        options={items}*/}
                        {/*        getOptionLabel={(option) => option.commodityCode}*/}
                        {/*        getOptionValue={(option) => option.commodityCode}*/}
                        {/*        formatOptionLabel={formatOptionLabel}*/}
                        {/*        styles={{ minWidth: '200px' }}*/}
                        {/*    />*/}
                        {/*</span>*/}
                    </label>
                </div>

                {/* Add Note*/}
                {/*some ngshow*/}
                <div styles={{paddingTop: '10px'}}>
                    <label className={styles.col112}>Note:</label>
                    {/*<textarea*/}
                    {/*    className={styles.col1112}*/}
                    {/*    value={note}*/}
                    {/*    onChange={handleChange}*/}
                    {/*/>*/}
                </div>
            </div>

            {/*    Right Margin*/}
            <div className={`${styles.col412} ${styles.requisitionModalRightMargin}`}>

                {/* Change Location */}
                <h4>Location</h4>
                {/*<div style={{ textAlign: 'left', minWidth: '175px' }}>*/}
                {/*    <Select*/}
                {/*        value={selectedDestination}*/}
                {/*        onChange={handleChange}*/}
                {/*        onInputChange={handleInputChange}*/}
                {/*        options={filteredDestinations}*/}
                {/*        getOptionLabel={(option) => (*/}
                {/*            <div>*/}
                {/*                <div>{option.code}</div>*/}
                {/*                <small>{option.locationDescription}</small>*/}
                {/*            </div>*/}
                {/*        )}*/}
                {/*        getOptionValue={(option) => option.code}*/}
                {/*        styles={{*/}
                {/*            option: (provided) => ({*/}
                {/*                ...provided,*/}
                {/*                marginBottom: '0px'*/}
                {/*            })*/}
                {/*        }}*/}
                {/*    />*/}
                {/*</div>*/}

                <h4>Delivery Method</h4>
                {/*<select value={editableRequisition.deliveryMethod} onChange={handleChange}>*/}
                {/*    {deliveryMethods.map(method => (*/}
                {/*        <option key={method} value={method}>*/}
                {/*            {method}*/}
                {/*        </option>*/}
                {/*    ))}*/}
                {/*</select>*/}

                <h4>Special Instructions</h4>
                {/*<div>ng if => no instructions</div>*/}
                {/*<div>ng if => class = fulfillment-modal-special-instructions</div>*/}

                <h4>Ordered Date Time</h4>
                {/*<div>{new Date(originalRequisition.orderedDateTime).toLocaleString('en-US', {*/}
                {/*    year: '2-digit',*/}
                {/*    month: '2-digit',*/}
                {/*    day: '2-digit',*/}
                {/*    hour: '2-digit',*/}
                {/*    minute: '2-digit',*/}
                {/*    hour12: true*/}
                {/*})}</div>*/}

                <h4>Actions</h4>
                {/*<div style={{textAlign: 'center'}}>*/}
                {/*    /!*111-114*!/*/}
                {/*    <a target={"_blank"}*/}
                {/*       href={"/supply/requisition/requisition-view?requisition={{originalRequisition.requisitionId}}&print=true"}>*/}
                {/*        Print Requisition*/}
                {/*    </a>*/}
                {/*</div>*/}
                {/*{originalRequisition.status === 'COMPLETED' && (*/}
                {/*    <div style={{textAlign: 'center'}}>\*/}
                {/*        /!*117-120*!/*/}
                {/*        <a target="_blank"*/}
                {/*           href="/supply/requisition/requisition-view?requisition={{originalRequisition.requisitionId}}">*/}
                {/*            View History*/}
                {/*        </a>*/}
                {/*    </div>*/}
                {/*)}*/}
                {/*/!* Assign Issuer*!/*/}
                {/*<div style={{textAlign: 'center', paddingBottom: '20px'}}>*/}
                {/*    <label>Assign to: </label>*/}
                {/*    /!*127-130*!/*/}
                {/*    <select value={editableRequisition.issuer?.employeeId || ''} onChange={handleChange}>*/}
                {/*        {supplyEmployees.map(emp => (*/}
                {/*            <option key={emp.employeeId} value={emp.employeeId}>*/}
                {/*                {emp.fullName}*/}
                {/*            </option>*/}
                {/*        ))}*/}
                {/*    </select>*/}
                {/*</div>*/}
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
    if(!originalRequisition) return;
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
                    onClick={() => {
                        // Show reject confirmation popover
                    }}
                >Reject</Button>
            )}
        </div>
    );
};