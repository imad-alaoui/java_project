import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function Session() {
    const { sessionId } = useParams();
    const [customerStatus, setCustomerStatus] = useState('Not in queue');
    const [waitTime, setWaitTime] = useState(0);
    const [eatingTime, setEatingTime] = useState(0);
    const [waitingTolerance, setWaitingTolerance] = useState(0);
    const [eatingSpeed, setEatingSpeed] = useState(0);
    const [socket, setSocket] = useState(null);

    useEffect(() => {
        // Establish WebSocket connection
        const newSocket = new WebSocket(`ws://localhost:8080/ws/restaurant/${sessionId}`);
        setSocket(newSocket);

        newSocket.onopen = () => {
            console.log('WebSocket connection established.');
        };

        newSocket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            console.log('Received data:', data);
        };

        newSocket.onerror = (error) => {
            console.error('WebSocket error:', error);
        };

        newSocket.onclose = () => {
            console.log('WebSocket connection closed.');
        };

        return () => {
            if (newSocket) newSocket.close();
        };
    }, [sessionId]);

    const handleEnterQueue = () => {
        fetch(`/api/sessions/${sessionId}/join`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({})
        })
        .then(response => response.json())
        .then(data => {
            setCustomerStatus('Waiting in queue...');
            setWaitingTolerance(data.waitingTolerance);
            setEatingSpeed(data.eatingSpeed);
            setWaitTime(5); // Simulating 5-second wait for now
    
            // Disable button after joining the queue to prevent multiple joins
            if (socket) {
                const message = {
                    action: 'updateCustomerStatus',
                    status: 'Waiting in queue',
                    sessionId: sessionId,
                };
                socket.send(JSON.stringify(message));
            }
        })
        .catch(error => console.error('Error:', error));
    };

    useEffect(() => {
        // Make sure to only trigger the WebSocket connection once
        if (socket && customerStatus !== 'Finished eating') {
            const message = {
                action: 'updateCustomerStatus',
                status: customerStatus,
                sessionId: sessionId,
            };
            socket.send(JSON.stringify(message));
        }
    }, [customerStatus, sessionId]);
    
    

    useEffect(() => {
        if (waitTime > 0) {
            const timer = setInterval(() => setWaitTime(waitTime - 1), 1000);
            return () => clearInterval(timer); // Ensure the previous timer is cleared
        } else if (waitTime === 0 && customerStatus === 'Waiting in queue...') {
            setCustomerStatus('Eating');
            setEatingTime(eatingSpeed);
    
            // Send WebSocket message about status change
            if (socket) {
                const message = {
                    action: 'updateCustomerStatus',
                    status: 'Eating',
                    sessionId: sessionId,
                };
                socket.send(JSON.stringify(message));
            }
        }
    }, [waitTime, customerStatus, eatingSpeed, socket, sessionId]);
    

    useEffect(() => {
        if (eatingTime > 0) {
            const timer = setInterval(() => setEatingTime(eatingTime - 1), 10);
            return () => clearInterval(timer);
        } else if (eatingTime === 0 && customerStatus === 'Eating') {
            setCustomerStatus('Finished eating');

            // Send WebSocket message about status change
            if (socket) {
                const message = {
                    action: 'updateCustomerStatus',
                    status: 'Finished eating',
                    sessionId: sessionId,
                };
                socket.send(JSON.stringify(message));
            }
        }
    }, [eatingTime, customerStatus, socket, sessionId]);

    return (
        <div>
            <h2>Session: {sessionId}</h2>
            <p>Status: {customerStatus}</p>
            {customerStatus === 'Not in queue' && <button onClick={handleEnterQueue}>Enter Queue</button>}
            {waitTime > 0 && <p>Wait Time: {waitTime}s</p>}
            <p>Waiting Tolerance: {waitingTolerance}s</p>
            <p>Eating Speed: {eatingSpeed}s</p>
            {eatingTime > 0 && <p>Eating Time: {eatingTime}s</p>}
        </div>
    );
}

export default Session;
