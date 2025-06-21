// // Author: Hitiksha Patel
// // Description: Delivery page of delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout'
import DeliverySummary from "../../components/DeliveryAgent/DeliverySummary";
import PendingDeliveries from "../../components/DeliveryAgent/PendingDeliveries";
import TodayDeliveries from "../../components/DeliveryAgent/TodayDeliveries";
import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/SummaryCard.css';
import { BsController } from 'react-icons/bs';

const DeliveryPage = () => {
    const [user, setUser] = useState(null);
    const [summary, setSummary] = useState(null);
    const [pending, setPending] = useState([]);
    const [today, setToday] = useState([]);
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem("token");
    console.log(token);

    const axiosInstance = axios.create({
        baseURL: "http://localhost:8080",
        headers: { Authorization: `Bearer ${token}` },
    });

    useEffect(() => {
        const fetchAllData = async () => {
            if (!token) return;

            let decoded;
            try {
                decoded = jwtDecode(token);
                // console.log(decoded)
                console.log("user Id : ", decoded.id)
            } catch (err) {
                console.error('Invalid token:', err);
                return;
            }

            const userId = decoded.userId || decoded.id;

            try {
                // Fetch all data in parallel
                const [userRes, summaryRes, pendingRes, todayRes] = await Promise.all([
                    // axiosInstance.get(`/user-detail/${userId}`),
                    axiosInstance.get(`/user-detail/${userId}`),
                    axiosInstance.get("/deliveries/summary"),
                    axiosInstance.get("/deliveries/pending"),
                    axiosInstance.get("/deliveries/today"),
                ]);

                setUser(userRes.data);
                console.log("User data : " ,userRes.data);
                setSummary(summaryRes.data);
                setPending(pendingRes.data);
                setToday(todayRes.data);
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchAllData();
    }, []);

    if (loading) return <p className="text-center">Loading...</p>;

    return (
        <>

            <DeliveryAgentDashboardLayout user={user}>
                <h1 className='heading'>DELIVERY DASHBOARD</h1>

                <div className="summary-container">
                    <SummaryCard title="PENDING ORDERS" count={pending.length} />
                    <SummaryCard title="TODAY'S ORDERS" count={today.length}/>
                </div>

                <h2 className='pending-heading heading'>PENDING DELIVERIES</h2>
                <PendingDeliveries/>
                <div className="summary-card">
                    
                {/* <div className="pending-deliveries"> */}
                    {/* <PendingDeliveries deliveries={pending} /> */}
                {/* </div> */}
                </div>

                <div className="today-deliveries">
                    <TodayDeliveries deliveries={today} />
                </div>

                {/* <DeliverySummary summary={summary} /> */}
                {/* <PendingDeliveries deliveries={pending} /> */}
                {/* <TodayDeliveries deliveries={today} /> */}
            </DeliveryAgentDashboardLayout>
    
        </>
    );
};

export default DeliveryPage;
