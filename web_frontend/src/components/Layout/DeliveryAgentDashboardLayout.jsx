// Author: Hitiksha Patel
// Delivery Agent dashboard layout(template).

import React from 'react';
import { useEffect } from "react";
import '../../styles/DeliveryAgent/DeliveryAgentDashboardLayout.css';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import Header from '../DeliveryAgent/DeliveryAgentHeader';
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
    
            <Header userName={${user?.firstName || ''} ${user?.lastName || ''}} />

                {/* Page Layout: Sidebar + Main Content side-by-side */}
                <div className="inter-font">

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