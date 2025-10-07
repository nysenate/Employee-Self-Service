import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

/**
 * Connects to the Requisition Update Event web socket.
 * @param onMessage callback function is called whenever an event is received.
 *                  It is passed the changed requisition.
 */
export function useRequisitionSocket(onMessage) {
  const onMessageRef = useRef(onMessage);

  // Save updates to onMessage.
  useEffect(() => {
    onMessageRef.current = onMessage;
  }, [onMessage]);

  // Connect to the socket once on mount.
  useEffect(() => {
    const client = new Client({
      // Use SockJS client instead of brokerURL
      webSocketFactory: () => new SockJS("http://localhost:8080/socket"),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("Connected to STOMP over SockJS");
        client.subscribe("/event/requisition", (message) => {
          try {
            const requisition = JSON.parse(message.body);
            onMessageRef.current(requisition);
          } catch (err) {
            console.error("Error parsing requisition message", err);
          }
        });
      },
      onStompError: (frame) => {
        console.error("Broker error: " + frame.headers["message"]);
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);
}
