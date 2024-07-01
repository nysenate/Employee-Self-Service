import Popup from "../../../components/Popup";
import { Button } from "../../../components/Button";
import React from "react";
import { useParams, useLocation } from 'react-router-dom';


export function FulfillmentEditing({ isModalOpen, closeModal, onAction }) {
    const { orderId } = useParams(); //get the number <- prob unnecessary
    const location = useLocation();
    const { order } = location.state; //get the object passed in

    console.log(orderId);
    console.log(order);
    const handleCancel = () => {
        onAction(false);
        closeModal();
    };

    const handleYes = () => {
        onAction(true);
        closeModal();
    };

    return (
        <Popup
            isLocked={true}
            isOpen={isModalOpen}
            onClose={closeModal}
            title="Change Destination"
        >
            <p style={{margin: '10px', marginBottom: '10px', marginTop: '20px', padding: '10px'}}>
                You are about to change your destination.
                <br /><br />
                Please note that your shopping cart will be emptied as a result of this operation.
                <br /><br />
                Would you like to continue?</p>
            <div style={{display: 'flex', justifyContent: 'center', gap: '5px'}}>
                <Button style={{backgroundColor: '#8d8d8d'}} onClick={handleCancel}>Cancel</Button>
                <Button onClick={handleYes}>Yes</Button>
            </div>
        </Popup>
    );
}