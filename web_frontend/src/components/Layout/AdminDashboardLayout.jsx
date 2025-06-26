// Author: Hitiksha Patel
// Admin dashboard layout(template).

import React from 'react';
import { useEffect } from "react";
import '../../styles/Admin/AdminDashboardLayout.css';
import '../../styles/Admin/AdminCommon.css';
import Header from '../Admin/AdminHeader';
import AdminSidebar from '../Admin/AdminSidebar';

const AdminDashboardLayout = ({ user, children }) => {

    useEffect(() => {
        document.body.classList.add("admin-body");

        return () => {
            document.body.classList.remove("admin-body");
        };
    }, []);
  
    return (
    
        <>
    
            <Header userName={`${user?.firstName || ''} ${user?.lastName || ''}`} />

                {/* Page Layout: Sidebar + Main Content side-by-side */}
                <div className="inter-font">

                    {/* Sidebar */} 
                    <AdminSidebar admin={user} />

                    {/* Main Dashboard Content */}
                    <main className="dashboard-content-area">
                        {children}
                    </main>

                </div>

        </>
  );
};

export default AdminDashboardLayout;
