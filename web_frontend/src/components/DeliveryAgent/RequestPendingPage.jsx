import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from "../../context/AuthContext";
import '../../styles/DeliveryAgent/RequestPendingPage.css';

const RequestPendingPage = () => {
    const navigate = useNavigate();
    const [countdown, setCountdown] = useState(5);
    const { logout } = useAuth();

    useEffect(() => {

        const timer = setInterval(() => {
            setCountdown((prev) => prev - 1);
        }, 1000);

        const timeout = setTimeout(() => {
            logout();
            navigate('/customer/dashboard');
        }, 5000);

        // Cleanup
        return () => {
            clearInterval(timer);
            clearTimeout(timeout);
        };
    }, [navigate]);

    return (

        <div className="pending-container">

            <h1 className="pending-title">Request Under Review</h1>

            <p className="pending-message">
                Your request to become a Delivery Agent is still pending approval.
            </p>

            <p className="pending-message">
                Once approved, you will be able to access your dashboard.
            </p>
            
            <p className="pending-message redirect-timer">
                You will be redirected to the customer dashboard in <strong>{countdown}</strong> second{countdown !== 1 ? 's' : ''}.
            </p>
        </div>

    );
};

export default RequestPendingPage;
