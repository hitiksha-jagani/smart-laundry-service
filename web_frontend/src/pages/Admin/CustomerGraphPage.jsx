// Author : Hitiksha Jagani
// Description : Customer report page for admin dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import UserMenu from '../../components/Admin/UserMenu.jsx';
import RevenueTrendGraph from '../../components/Admin/RevenueTrendGraph.jsx';
import PieChartComp from '../../components/Admin/PieChartComp.jsx';
import { BASE_URL } from '../../utils/config';

const CustomerPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   
    const [graphData, setGraphData] = useState(null);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
        headers: { Authorization: `Bearer ${token}` },
    });

    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

    useEffect(() => {
                    
        const fetchAllData = async () => {
                        
            if (!token) {
                return};
            
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
                                
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch customer report 
                    axiosInstance.get("/users/customers/graphs", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Customer graph data fetch failed", err);
                        return { data: null };
                    }),
            
                ]);
            
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setGraphData(graphRes.data);
                console.log("Customer graph data : ", graphRes.data);
                    
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

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px', marginBottom: '50px' }}>

                    <h1 className='heading-admin h1-admin'>USER DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>View, manage, and analyze all platform users including customers, service providers, and delivery agents.</p>

                    <h2 className="h2-admin">CUSTOMER TRENDS</h2>

                    {/* 1. Monthly Users This Year */}
                    <RevenueTrendGraph
                        title="Users This Year (Monthly)"
                        data={graphData?.usersThisYearMonthlyTrend?.map(d => ({ label: d.month, revenue: d.count })) || []}
                    />

                    {/* 2. New Users This Month */}
                    <RevenueTrendGraph
                        title="New User This Month Trend"
                        data={graphData?.newUsersThisMonthDailyTrend?.map(d => ({ label: d.day, revenue: d.count })) || []}
                    />

                    {/* 3. Yearly Growth */}
                    <RevenueTrendGraph
                        title="User Growth Trend (Yearly)"
                        data={graphData?.userGrowthTrend?.map(d => ({ label: d.year, revenue: d.count })) || []}
                    />

                    {/* 4. Region-wise Distribution */}
                    <PieChartComp          
                        title="Region-wise User Distribution"
                        data={graphData?.regionWiseDistribution}
                        dataKey="userCount"
                        nameKey="city"
                    />

                    {/* Right Sidebar */}
                    <UserMenu />

                </div>

            </div>

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

        </AdminDashboardLayout>

    );
};

export default CustomerPage;