// Author: Hitiksha Patel
// Delivery Agent dashboard layout (template).

import React, { useEffect, useRef, useState } from 'react';
import '../../styles/DeliveryAgent/DeliveryAgentDashboardLayout.css';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import Header from '../DeliveryAgent/DeliveryAgentHeader';
import DeliveryAgentSidebar from '../DeliveryAgent/DeliveryAgentSidebar';
import axios from 'axios';
import LocationTracker from '../DeliveryAgent/LocationTracker';

const DeliveryAgentDashboardLayout = ({ user, children }) => {
const [isAvailable, setIsAvailable] = useState(false);

  useEffect(() => {
    document.body.classList.add('delivery-agent-body');
    return () => {
      document.body.classList.remove('delivery-agent-body');
    };
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
      baseURL: `${BASE_URL}`,
      headers: { Authorization: `Bearer ${token}` },
    });

    const checkAvailability = async () => {
      try {
        const res = await axiosInstance.get("/availability/check-availability");
        console.log("API availability response:", res.data);
        setIsAvailable(res.data === true);
      } catch (err) {
        console.error("Error checking availability:", err);
        setIsAvailable(false);
      }
    };

    checkAvailability();
  }, []);

    return (
      <>
        <Header userName={`${user?.firstName || ''} ${user?.lastName || ''}`} />

        {/* Page Layout: Sidebar + Main Content side-by-side */}
        <div className="inter-font">
          {/* Sidebar */}
          <DeliveryAgentSidebar agent={user} />

          {/* Main Dashboard Content */}
          <main className="dashboard-content-area">{children}</main>

          <LocationTracker isAvailable={isAvailable} />
        </div>
      </>
    );
};

export default DeliveryAgentDashboardLayout;
