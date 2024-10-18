import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function OwnerSession() {
    const { sessionId } = useParams();
    const [sessionData, setSessionData] = useState({});
    const [customers, setCustomers] = useState([]);
    const [connectionStatus, setConnectionStatus] = useState('Disconnected');
    const [connectionError, setConnectionError] = useState('');
    const [logs, setLogs] = useState([]);  // To display detailed logs on the page

    const addLog = (message) => {
        setLogs((prevLogs) => [...prevLogs, message]);
    };

    useEffect(() => {
        let socket;

        const establishWebSocketConnection = () => {
            socket = new WebSocket(`ws://localhost:8080/ws/restaurant/${sessionId}`);

            socket.onopen = () => {
                setConnectionStatus('Connected');
                addLog('WebSocket connection established.');
            };

            socket.onmessage = (event) => {
                const data = JSON.parse(event.data);
                addLog(`Received data: ${JSON.stringify(data)}`);

                // Update session data with the broadcasted data
                setSessionData(data.session);
                setCustomers(data.customers);  // Assuming customers include both in queue and eating
            };

            socket.onerror = (error) => {
                setConnectionStatus('Error');
                setConnectionError(error.message || 'WebSocket error');
                addLog(`WebSocket error: ${error.message || 'Unknown error'}`);
            };

            socket.onclose = (event) => {
                setConnectionStatus('Disconnected');
                addLog(`WebSocket connection closed: ${event.reason || 'No reason'}`);
            };
        };

        establishWebSocketConnection();

        return () => {
            if (socket) {
                socket.close();
                addLog('WebSocket connection closed manually.');
            }
        };
    }, [sessionId]);

    return (
        <div>
            <h2>Restaurant Owner Dashboard - Session {sessionId}</h2>

            {/* WebSocket connection status */}
            <h3>WebSocket Connection Status: {connectionStatus}</h3>
            {connectionStatus === 'Error' && <p style={{ color: 'red' }}>Error: {connectionError}</p>}

            {/* Display logs */}
            <div style={{ backgroundColor: '#f0f0f0', padding: '10px', marginTop: '20px' }}>
                <h4>Logs:</h4>
                <ul>
                    {logs.map((log, index) => (
                        <li key={index}>{log}</li>
                    ))}
                </ul>
            </div>

            <h3>Restaurant Information</h3>
            <p>Total Seats: {sessionData.seats}</p>
            <p>Available Seats: {sessionData.availableSeats}</p>
            <p>Customers Waiting: {sessionData.customersWaiting}</p>
            <p>Customers Eating: {sessionData.customersEating}</p>
            <p>Customers Left: {sessionData.customersLeft}</p>
 
            <h3>Customer Details</h3>
            <ul>
                {customers.map((customer) => (
                    <li key={customer.id}>
                        Customer ID: {customer.id} | Waiting Tolerance: {customer.waitingTolerance}s | Eating Speed: {customer.eatingSpeed}s
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default OwnerSession;
