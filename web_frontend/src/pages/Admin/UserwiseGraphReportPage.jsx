// Author : Hitiksha Jagani
// User-wise graph-based order report page (admin dashboard)

// Author : Hitiksha Jagani
// Description : Order related report page for admin dashboard.

import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { MdInbox } from 'react-icons/md';
import axios from 'axios';
import GraphFilter from '../../components/Admin/GraphFilter';
import RevenueTrendGraph from '../../components/Admin/RevenueTrendGraph';
import ReportsMenu from '../../pages/Admin/ReportsMenu';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import { BASE_URL } from '../../utils/config';

const UserwiseGraphReportPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [graph, setGraph] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();
    const { id } = useParams();
    const { filter } = location.state || {};
      
    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
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

                    // Fetch graph data
                    axiosInstance.get(`/reports/order/user-report-list/graph/${id}`, {
                        headers: {
                            Authorization: `Bearer ${token}`
                        },
                        params: { filter },
                    }).catch((err) => {
                        console.error("Graph data fetch failed", err);
                        return { data: null };
                    })
    
                ]);
    
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setGraph(graphRes.data);
                console.log("Graph data : ", graphRes.data);
                console.log("Filter :", filter);
        
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

                        <div
                            style={{
                                display: 'flex',
                                flexDirection: 'column',
                                gap: '2rem',
                                marginTop: '10px',
                                marginBottom: '2rem',
                            }}
                        >

                            {graph ? (

                                <>
                                    <RevenueTrendGraph
                                        title="Order Volume Trend"
                                        data={graph.orderVolumeTrend.map(d => ({
                                            label: d.label,
                                            revenue: d.count,
                                        }))}
                                    />

                                    {/* <RevenueTrendGraph
                                        title="Cancelled Order Trend"
                                        data={graph.cancelledOrderTrend.map(d => ({
                                            label: d.label,
                                            revenue: d.count,
                                        }))}
                                    /> */}

                                    <RevenueTrendGraph
                                        title="Rejected Order Trend"
                                        data={graph.rejectedOrderTrend.map(d => ({
                                            label: d.label,
                                            revenue: d.count,
                                        }))}
                                    />
                                </>
                            ) : (

                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '2rem', backgroundColor: '#fff', border: '1px dashed #ccc', borderRadius: '1rem', margin: '2rem auto', width: '80%', color: '#777' }}>
                                
                                    <MdInbox size={64} color="#ccc" />
                                    <h2 style={{ marginTop: '1rem' }}>No Graph Data Available.</h2>
                                
                                </div>
                            )}


                        </div>

                        {/* Right Sidebar */}
                        <ReportsMenu />

                    </div>

                </div>

            </AdminDashboardLayout>

        </>

    );

};

export default UserwiseGraphReportPage;