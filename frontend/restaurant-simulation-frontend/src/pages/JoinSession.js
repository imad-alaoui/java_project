import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function JoinSession() {
    const [sessions, setSessions] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetch('/api/sessions')
            .then(response => response.json())
            .then(data => setSessions(data))
            .catch(error => console.error('Error:', error));
    }, []);

    const handleJoin = (sessionId) => {
        navigate(`/session/${sessionId}`);
    };

    return (
        <div>
            <h2>Join a Restaurant Session</h2>
            <ul>
                {Object.keys(sessions).map(sessionId => (
                    <li key={sessionId}>
                        Session ID: {sessionId}, Seats: {sessions[sessionId].seats}, Available: {sessions[sessionId].availableSeats}
                        <button onClick={() => handleJoin(sessionId)}>Join Session</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default JoinSession;
