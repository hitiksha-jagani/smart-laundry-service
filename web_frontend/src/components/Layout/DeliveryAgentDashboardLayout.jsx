// Author: Hitiksha Patel
// src/components/Layout/DashboardLayout.jsx

import React from 'react';
import { useEffect } from "react";
import '../../styles/DeliveryAgent/DeliveryAgentDashboardLayout.css'
import Header from '../DeliveryAgent/Header';
import DeliveryAgentSidebar from '../DeliveryAgent/DeliveryAgentSidebar';

const DeliveryAgentDashboardLayout = ({ user, children }) => {

    useEffect(() => {
        document.body.classList.add("delivery-agent-body");

        return () => {
            document.body.classList.remove("delivery-agent-body");
        };
    }, []);
  
    return (
    
        <>
    
            <Header userName={`${user?.firstName || ''} ${user?.lastName || ''}`} />

                {/* Page Layout: Sidebar + Main Content side-by-side */}
                <div className="delivery-dashboard flex inter-font">

                    {/* Sidebar */} 
                    <DeliveryAgentSidebar agent={user} />

                    {/* Main Dashboard Content */}
                    <main className="dashboard-content-area">
                        {children}
                    </main>

                </div>

        </>
  );
};

export default DeliveryAgentDashboardLayout;
