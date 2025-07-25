// Author: Hitiksha Patel
// Description: Delivery page of delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/SummaryCard.css';

const DeliveryPage = () => {
  const [user, setUser] = useState(null);
  const [summary, setSummary] = useState([]);
  const [pending, setPending] = useState([]);
  const [today, setToday] = useState([]);
  const [loading, setLoading] = useState(true);

  const token = localStorage.getItem("token");

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
        console.log("user Id:", decoded.id);
      } catch (err) {
        console.error("Invalid token:", err);
        return;
      }

      const userId = decoded.userId || decoded.id;

      try {
        const [userRes, summaryRes, pendingRes, todayRes] = await Promise.all([
          axiosInstance.get(`/user-detail/${userId}`).catch(err => {
            console.error("User detail fetch failed", err);
            return { data: null };
          }),
          axiosInstance.get("/deliveries/summary").catch(err => {
            console.error("Summary delivery data fetch failed", err);
            return { data: null };
          }),
          axiosInstance.get("/deliveries/pending").catch(err => {
            console.error("Pending delivery data fetch failed", err);
            return { data: null };
          }),
          axiosInstance.get("/deliveries/today").catch(err => {
            console.error("Today delivery data fetch failed", err);
            return { data: null };
          }),
        ]);

        setUser(userRes.data);
        setSummary(summaryRes.data);
        setPending(pendingRes.data);
        setToday(todayRes.data);

        console.log("User data:", userRes.data);
        console.log("Summary data:", summaryRes.data);
        console.log("Pending deliveries:", pendingRes.data);
        console.log("Today's deliveries:", todayRes.data);
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
    <DeliveryAgentDashboardLayout user={user}>
      <h1 className='heading-agent h1-agent'>DELIVERY DASHBOARD</h1>

      {/* Summary Cards */}
      <div className="summary-container" style={{ marginTop: '200px' }}>
        {/* Pending Orders */}
        <SummaryCard
          title="PENDING ORDERS"
          user={user}
          count={pending?.length || 0}
          link="/deliveries/pending"
          data={pending ?? []}
        />

        {/* Today's Orders */}
        <SummaryCard
          title="TODAY'S ORDERS"
          user={user}
          count={today?.length || 0}
          link="/deliveries/today"
          data={today ?? []}
        />
      </div>
    </DeliveryAgentDashboardLayout>
  );
};

export default DeliveryPage;
