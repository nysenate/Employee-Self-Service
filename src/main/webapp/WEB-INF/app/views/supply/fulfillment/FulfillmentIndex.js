import React, { useEffect, useState } from 'react';
import styles from '../universalStyles.module.css';
import Hero from "../../../components/Hero";
import PendingOrders from "./PendingOrders";
import ProcessingOrders from "./ProcessingOrders";
import CompletedOrders from "./CompletedOrders";
import ApprovedOrders from "./ApprovedOrders";
import RejectedShipments from "./RejectedShipments";

import {initMostReqs, initRejectedReqs, calculateHighlighting, setRequisitionSearchParam, distinctItemQuantity} from "./supply-fulfillment-ctrl";

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

    useEffect(() => {
        const fetchData = async () => {
            try {
                const mostReqs = await initMostReqs();
                const rejectedReqs = await initRejectedReqs();

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

    if (error) {
        return <div>Error loading requisitions. Please reload the page and try again.</div>;
    }

    return (
        <div>
            <Hero>Fulfillment</Hero>
            <PendingOrders
                data={data}
                calculateHighlighting={calculateHighlighting}
                setRequisitionSearchParam={setRequisitionSearchParam}
                distinctItemQuantity={distinctItemQuantity}
            />
            <ProcessingOrders
                data={data}
                calculateHighlighting={calculateHighlighting}
                setRequisitionSearchParam={setRequisitionSearchParam}
                distinctItemQuantity={distinctItemQuantity}
            />
            <CompletedOrders
                data={data}
                setRequisitionSearchParam={setRequisitionSearchParam}
                distinctItemQuantity={distinctItemQuantity}
            />
            <ApprovedOrders
                data={data}
                setRequisitionSearchParam={setRequisitionSearchParam}
                distinctItemQuantity={distinctItemQuantity}
            />
            <RejectedShipments
                data={data}
                setRequisitionSearchParam={setRequisitionSearchParam}
                distinctItemQuantity={distinctItemQuantity}
            />
        </div>
    );
}

