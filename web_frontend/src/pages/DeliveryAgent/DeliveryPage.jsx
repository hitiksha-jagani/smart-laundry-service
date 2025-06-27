// Author: Hitiksha Patel
// Description: Delivery page of delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axiosInstance from "../../utils/axiosInstance";

import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import PendingDeliveries from "../../components/DeliveryAgent/PendingDeliveries";
import TodayDeliveries from "../../components/DeliveryAgent/TodayDeliveries";

import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/SummaryCard.css';

const DeliveryPage = () => {
  const [user, setUser] = useState(null);
  const [pending, setPending] = useState([]);
  const [today, setToday] = useState([]);
  const [loading, setLoading] = useState(true);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchAllData = async () => {
      if (!token) return;

      let decoded;
      try {
        decoded = jwtDecode(token);
        console.log("User ID:", decoded.id || decoded.userId);
      } catch (err) {
        console.error('Invalid token:', err);
        return;
      }

      const userId = decoded.userId || decoded.id;

      try {
        const [userRes, pendingRes, todayRes] = await Promise.all([
          axiosInstance.get(`/user-detail/${userId}`),
          axiosInstance.get("/deliveries/pending"),
          axiosInstance.get("/deliveries/today"),
        ]);

        setUser(userRes.data);
        setPending(pendingRes.data);
        setToday(todayRes.data);
      } catch (error) {
        console.error("Failed to fetch delivery dashboard data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAllData();
  }, [token]);

  if (loading) return <p className="text-center">Loading...</p>;

  return (
    <DeliveryAgentDashboardLayout user={user}>
      <h1 className='heading inter-font'>DELIVERY DASHBOARD</h1>

      <div className="summary-container inter-font">
        <SummaryCard title="TOTAL ORDERS" count={pending.length + today.length} />
        <SummaryCard title="PENDING ORDERS" count={pending.length} />
        <SummaryCard title="TODAY'S ORDERS" count={today.length} />
      </div>

      <h2 className='pending-heading heading inter-font'>PENDING DELIVERIES</h2>
      <PendingDeliveries deliveries={pending} />

      <h2 className='pending-heading heading inter-font' style={{ marginTop: '50px' }}>
        TODAY'S DELIVERIES
      </h2>
      <TodayDeliveries deliveries={today} />
    </DeliveryAgentDashboardLayout>
  );
};

export default DeliveryPage;
