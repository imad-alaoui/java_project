import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function CreateRestaurant() {
    const [seats, setSeats] = useState('');
    const [openingHours, setOpeningHours] = useState('');
    const [servingRate, setServingRate] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();  // Hook to handle navigation

    const handleSubmit = (e) => {
        e.preventDefault();

        fetch('/api/create-restaurant', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ seats, openingHours, servingRate })
        })
        .then(response => response.json())
        .then(data => {
            setMessage(`Restaurant created with ID: ${data.id}`);
            
            // Adding a small delay before navigating to ensure everything is fully set up
            setTimeout(() => {
                navigate(`/owner-session/${data.id}`);  // Navigate to the OwnerSession page with sessionId
            }, 1000);  // 1-second delay
        })
        .catch(error => console.error('Error:', error));
    };

    return (
        <div>
            <h2>Create a Restaurant</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Number of Seats:</label>
                    <input type="number" value={seats} onChange={e => setSeats(e.target.value)} required />
                </div>
                <div>
                    <label>Opening Hours:</label>
                    <input type="number" value={openingHours} onChange={e => setOpeningHours(e.target.value)} required />
                </div>
                <div>
                    <label>Serving Rate:</label>
                    <input type="number" value={servingRate} onChange={e => setServingRate(e.target.value)} required />
                </div>
                <button type="submit">Create Restaurant</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
}

export default CreateRestaurant;
