import React, { useEffect, useState } from 'react';
import styles from '../universalStyles.module.css';
import Hero from "../../../components/Hero";
import PendingOrders from "./PendingOrders";
import ProcessingOrders from "./ProcessingOrders";
import CompletedOrders from "./CompletedOrders";
import ApprovedOrders from "./ApprovedOrders";
import RejectedShipments from "./RejectedShipments";

import { initMostReqs, initRejectedReqs, calculateHighlighting, setRequisitionSearchParam, distinctItemQuantity } from "./supply-fulfillment-ctrl";
import { FulfillmentEditing, FulfillmentImmutable } from "./FulfillmentPopups";
import { formatDateForApi, getCurrentDate } from "../helpers";

export default function FulfillmentIndex() {
    const [data, setData] = useState({
        reqs: {
            map: {},
            pending: [],
            processing: [],
            completed: [],
            approved: [],
            rejected: []
        },
        reqRequest: {
            response: { $resolved: false },
            error: false
        },
        locationStatistics: undefined,
        supplyEmployees: []
    });
    const [error, setError] = useState(false);
    const [selectedRequisition, setSelectedRequisition] = useState(null); // State to manage the selected requisition
    const [isModalOpen, setIsModalOpen] = useState(false); // State to manage modal visibility
    const [isImmutableOpen, setIsImmutableOpen] = useState(false); // State to manage modal visibility

    useEffect(() => {
        const fetchData = async () => {
            try {
                const mostReqs = await initMostReqs();
                const rejectedReqs = await initRejectedReqs(formatDateForApi(new Date(getCurrentDate())));

                const allReqs = [...mostReqs, ...rejectedReqs];

                // Remove duplicates
                const uniqueReqs = allReqs.filter((req, index, self) =>
                        index === self.findIndex((r) => (
                            r.requisitionId === req.requisitionId
                        ))
                );

                const map = {};
                const reqs = { pending: [], processing: [], completed: [], approved: [], rejected: [] };

                uniqueReqs.forEach(req => {
                    map[req.requisitionId] = req;
                    reqs[req.status.toLowerCase()].push(req);
                });

                setData(prevData => ({
                    ...prevData,
                    reqs: { ...reqs, map },
                    reqRequest: { response: { $resolved: true }, error: false }
                }));
            } catch (error) {
                console.error('Error fetching requisitions:', error);
                setError(true);
                setData(prevData => ({
                    ...prevData,
                    reqRequest: { response: { $resolved: true }, error: true }
                }));
            }
        };
        fetchData();
    }, []);

    const handleRowClick = (requisition) => {
        setSelectedRequisition(requisition); // Set the selected requisition
        setRequisitionSearchParam(requisition.requisitionId)
        requisition.status == 'APPROVED' || requisition.status == 'REJECTED' ? setIsImmutableOpen(true) : setIsModalOpen(true); // Open the modal
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setIsImmutableOpen(false);
        setSelectedRequisition(null);
    };

    if (error) {
        return <div>Error loading requisitions. Please reload the page and try again.</div>;
    }

    return (
        <div>
            <Hero>Fulfillment</Hero>
            <PendingOrders
                data={data}
                calculateHighlighting={calculateHighlighting}
                onRowClick={handleRowClick}
                distinctItemQuantity={distinctItemQuantity}
            />
            <ProcessingOrders
                data={data}
                calculateHighlighting={calculateHighlighting}
                onRowClick={handleRowClick}
                distinctItemQuantity={distinctItemQuantity}
            />
            <CompletedOrders
                data={data}
                onRowClick={handleRowClick}
                distinctItemQuantity={distinctItemQuantity}
            />
            <ApprovedOrders
                data={data}
                onRowClick={handleRowClick}
                distinctItemQuantity={distinctItemQuantity}
            />
            <RejectedShipments
                data={data}
                onRowClick={handleRowClick}
                distinctItemQuantity={distinctItemQuantity}
            />
            {selectedRequisition && (
                <FulfillmentEditing
                    requisition={selectedRequisition}
                    isModalOpen={isModalOpen}
                    closeModal={closeModal}
                    onAction={(action) => { console.log(action); }}
                />
            )}
            {selectedRequisition && (
                <FulfillmentImmutable
                    requisition={selectedRequisition}
                    isModalOpen={isImmutableOpen}
                    closeModal={closeModal}
                    onAction={(action) => { console.log(action); }}
                />
            )}
        </div>
    );
}
