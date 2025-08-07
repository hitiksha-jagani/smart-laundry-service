// Author : Hitiksha Jagani
// Description : Summary of revenue for admin dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import TimeFilter from '../../components/Admin/TimeFilter.jsx';
import '../../styles/Admin/RevenuePage.css';
import RevenueMenu from '../../components/Admin/RevenueMenu';
import AdminSummaryCard from '../../components/Admin/AdminSummaryCard.jsx';

const RevenuePage = () => {
    const [filterParams, setFilterParams] = useState({ filter: "overall" });
    const [user, setUser] = useState(null);
    const [summary, setSummary] = useState(null);
    const [total, setTotal] = useState([]);
    const [loading, setLoading] = useState(true);

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
                const [userRes, summaryRes, totalRes, providerRes] = await Promise.all([
                        
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch revenue summary data
                    axiosInstance.get("/revenue/summary", {
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
                        console.error("Revenue summary data fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch total revenue data
                    axiosInstance.get("/revenue/total-revenue", {
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
                        console.error("Total revenue data fetch failed", err);
                        return { data: null };
                    }),
    
                ]);
    
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setSummary(summaryRes.data);
                console.log("User data : " ,summaryRes.data);

                setTotal(totalRes.data);
                console.log("Total revenue data : ", totalRes.data);
        
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
                        
                        <h1 className='heading-admin h1-admin'>REVENUE DASHBOARD</h1>

                        <TimeFilter onChange={setFilterParams} /> 

                        <div className="summary-container" style={{ marginTop: '150px', marginLeft: '30px' }}> 

                            <AdminSummaryCard 
                                title="TOTAL REVENUE" 
                                prefix='₹' 
                                user={user}
                                count={summary?.totalRevenue  || 0}
                                link="/revenue/total-revenue"  
                                data={total}
                            />
                            
                            <AdminSummaryCard title="TOTAL ORDERS" count={summary?.totalOrders || 0} />
                            
                            <AdminSummaryCard title="GROSS SALES" prefix='₹' count={summary?.grossSales || 0}/>
                            
                            <AdminSummaryCard title="SERVICE PROVIDERS PAYOUTS" prefix='₹' count={summary?.serviceProviderPayouts || 0}/>
                            
                            <AdminSummaryCard title="DELIVERY AGENTS PAYOUTS" prefix='₹' count={summary?.deliveryAgentPayouts || 0}/>
                            
                            <AdminSummaryCard title="AVERAGE ORDER VALUE" prefix='₹' count={summary?.avgOrderValue || 0}/>
                
                        </div>

                    </div>

                    {/* Right Sidebar */}
                    <RevenueMenu />

                </div>
                
            </AdminDashboardLayout>
    
        </>
    );

};

export default RevenuePage;