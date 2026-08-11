import { useEffect, useRef } from "react";

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

  // Connect to the socket once on mount. The STOMP and SockJS clients are imported
  // on demand so they stay out of every page's initial download; only the supply
  // fulfillment page opens this socket.
  useEffect(() => {
    let client;
    let isMounted = true;

    (async () => {
      const [{ Client }, { default: SockJS }] = await Promise.all([
        import("@stomp/stompjs"),
        import("sockjs-client"),
      ]);
      if (!isMounted) return;

      client = new Client({
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
    })();

    return () => {
      isMounted = false;
      if (client) client.deactivate();
    };
  }, []);
}
