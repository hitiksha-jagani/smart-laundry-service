// Author : Hitiksha Jagani
// Description : Revenue breakdown for admin dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RevenueMenu from '../../components/Admin/RevenueMenu';
import TimeFilter from '../../components/Admin/TimeFilter.jsx';
import RevenueBreakdownDonutChart from '../../components/Admin/RevenueBreakdownDonutChart.jsx';
import RevenueBreakdownTable from '../../components/Admin/RevenueBreakdownTable.jsx';
import { BASE_URL } from '../../utils/config';

const RevenueBreakdownPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [filterParams, setFilterParams] = useState({ filter: "overall" });
    const [graph, setGraph] = useState([]);
    const [table, setTable] = useState([]);
    
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
                    const [userRes, graphRes, tableRes] = await Promise.all([
                        
                        // Fetch user data
                        axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                            console.error("User detail fetch failed", err);
                            return { data: null };
                        }),

                        // Fetch revenue breakdown graph
                        axiosInstance.get("/revenue/breakdown/graph", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            },
                            params: {
                                filter: filterParams.filter, 
                                ...(filterParams.filter === "custom" && {
                                    startDate: filterParams.startDate,
                                    endDate: filterParams.endDate
                                })
                            }
                        }).catch(err => {
                            console.error("Revenue breakdown graph data fetch failed", err);
                            return { data: null };
                        }),

                        // Fetch revenue breakdown table
                        axiosInstance.get("/revenue/breakdown/table", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            },
                            params: {
                                filter: filterParams.filter, 
                                ...(filterParams.filter === "custom" && {
                                    startDate: filterParams.startDate,
                                    endDate: filterParams.endDate
                                })
                            }
                        }).catch(err => {
                            console.error("Revenue breakdown table data fetch failed", err);
                            return { data: null };
                        }),
    
                    ]);
    
                    setUser(userRes.data);
                    console.log("User data : " ,userRes.data);
    
                    setGraph(graphRes.data);
                    console.log("Graph data : ", graphRes.data);

                    setTable(tableRes.data);
                    console.log("Table data : ", tableRes.data);
                    
    
                } catch (error) {
                    console.error("Failed to fetch one or more data:", error);
                } finally {
                    setLoading(false);
                }
            };
    
            fetchAllData();
        }, [filterParams]);
    
        if (loading) return <p className="text-center">Loading...</p>;

        return (

            <>
            
                <AdminDashboardLayout user={user}>

                    <div style={{ display: 'flex' }}>

                        <div style={{ flex: 1, paddingRight: '260px' }}>

                            <h1 className='heading-admin h1-admin'>REVENUE BREAKDOWN</h1>

                            <TimeFilter onChange={setFilterParams} /> 

                            
                            <div 
                                style={{ 
                                    marginTop: '10px',
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-around',
                                    marginTop:'150px'
                                }}
                            >
                                
                                <RevenueBreakdownDonutChart data={graph} />

                                <RevenueBreakdownTable data={table} />

                            </div> 

                        </div>

                        {/* Right Sidebar */}
                        <RevenueMenu />


                    </div>

                </AdminDashboardLayout>

            </>

        );
 
};

export default RevenueBreakdownPage;