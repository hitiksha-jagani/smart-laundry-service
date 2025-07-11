// Author : Hitiksha Jagani
// Description : Order related report page for admin dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import GraphFilter from '../../components/Admin/GraphFilter';
import RevenueTrendGraph from '../../components/Admin/RevenueTrendGraph';
import ReportsMenu from '../../pages/Admin/ReportsMenu';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';

const OrderReportPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [graph, setGraph] = useState([]);
    const [filter, setFilter] = useState("monthly");
    const [data, setData] = useState(null);  
      
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
                console.log("user Id : ", decoded.id)
            } catch (err) {
                console.error('Invalid token:', err);
                return;
            }
    
            const userId = decoded.userId || decoded.id;
    
            try {

                // Fetch all data in parallel
                const [userRes, graphRes] = await Promise.all([
                        
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch revenue trend
                    axiosInstance.get("/reports/order/trend", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        },
                        params: { filter },
                    }).catch(err => {
                        console.error("Order trend graph data fetch failed", err);
                        return { data: null };
                    }),
    
                ]);
    
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);
    
                setGraph(graphRes.data);
                console.log("Graph data:", graphRes.data);
    
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        };
    
        fetchAllData();

    }, [filter]);
    
    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <>
            
            <AdminDashboardLayout user={user}>

                <div style={{ display: 'flex' }}>

                    <div style={{ flex: 1, paddingRight: '260px' }}>

                        <h1 className='heading-admin h1-admin'>ORDER REPORT</h1>

                            <GraphFilter onChange={setFilter} />

                        <div
                            style={{
                                display: 'flex',
                                flexDirection: 'column',
                                gap: '2rem',
                                marginTop: '2rem',
                                marginBottom: '2rem',
                            }}
                        >

                            <RevenueTrendGraph
                                title="Order Volume Trend"
                                data={graph?.orderVolumeTrend?.map(d => ({ label: d.label, revenue: d.count })) || []}
                            />

                            <RevenueTrendGraph
                                title="Cancelled Order Trend"
                                data={graph?.cancelledOrderTrend?.map(d => ({ label: d.label, revenue: d.count })) || []}
                            />

                            <RevenueTrendGraph
                                title="Rejected Order Trend"
                                data={graph?.rejectedOrderTrend?.map(d => ({ label: d.label, revenue: d.count })) || []}
                            />

                        </div>

                        {/* Right Sidebar */}
                        <ReportsMenu />

                    </div>

                </div>

            </AdminDashboardLayout>

        </>

    );

};

export default OrderReportPage;