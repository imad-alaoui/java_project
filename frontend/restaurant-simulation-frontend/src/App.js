import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import CreateRestaurant from './pages/CreateRestaurant';
import JoinSession from './pages/JoinSession';
import Session from './pages/Session';
import OwnerSession from './pages/OwnerSession';

function App() {
    const [buttonsVisible, setButtonsVisible] = useState(true);

    const handleButtonClick = () => {
        setButtonsVisible(false);
    };

    return (
        <Router>
            <div>
                <h1>Restaurant Simulation</h1>
                
                {/* Conditional buttons */}
                {buttonsVisible && (
                    <div>
                        <Link to="/create-restaurant">
                            <button onClick={handleButtonClick}>Create Restaurant</button>
                        </Link>
                        <Link to="/join-session">
                            <button onClick={handleButtonClick}>Join a Session</button>
                        </Link>
                    </div>
                )}

                {/* Define Routes */}
                <Routes>
                    <Route path="/create-restaurant" element={<CreateRestaurant />} />
                    <Route path="/join-session" element={<JoinSession />} />
                    <Route path="/session/:sessionId" element={<Session />} />
                    <Route path="/owner-session/:sessionId" element={<OwnerSession />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
