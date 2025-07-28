// Author : Hitiksha Jagani
// Description : Revenue trend page for revenue page of admin dashboard.


import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import GraphFilter from '../../components/Admin/GraphFilter';
import RevenueTypeFilter from './RevenueTypeFilter';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RevenueMenu from '../../components/Admin/RevenueMenu';
import RevenueTrendGraph from '../../components/Admin/RevenueTrendGraph';

const RevenueTrendsPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [type, setType] = useState("gross sales");
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
                const [userRes, response] = await Promise.all([
                        
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch revenue trend
                    axiosInstance.get("/revenue/trends", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        },
                        params: { type, filter },
                    }).catch(err => {
                        console.error("Revenue trend data fetch failed", err);
                        return { data: null };
                    }),
    
                ]);
    
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);
    
                setData(response.data);
                console.log("Trend data:", response.data);
    
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        };
    
        fetchAllData();

    }, [type, filter]);
    
    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <>
            
            <AdminDashboardLayout user={user}>

                <div style={{ display: 'flex' }}>

                    <div style={{ flex: 1, paddingRight: '260px' }}>

                        <h1 className='heading-admin h1-admin'>REVENUE TRENDS</h1>

                        <div style={{ display: 'flex', justifyContent: 'center', gap: '30px', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
    
                            <GraphFilter onChange={setFilter} />

                            <RevenueTypeFilter onChange={setType} />

                        </div>


                        <div
                            style={{
                                display: 'grid',
                                gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                                gap: '1rem',
                                marginTop: '2rem',
                                marginBottom: '2rem',
                            }}
                        >

                            <RevenueTrendGraph title={data?.title} data={data?.data} />

                        </div>

                        {/* Right Sidebar */}
                        <RevenueMenu />

                    </div>

                </div>

            </AdminDashboardLayout>

        </>

    );
 
};

export default RevenueTrendsPage;