// Author : Hitiksha Jagani
// Description : Payout page for delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import DeliveryAgentTimeFilter from '../../components/DeliveryAgent/DeliveryAgentTimeFilter.jsx';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import PendingPayouts from '../../components/DeliveryAgent/PendingPayouts';
import PaidPayouts from '../../components/DeliveryAgent/PaidPayouts';
import AllPayouts from '../../components/DeliveryAgent/AllPayouts';
import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/SummaryCard.css';
import '../../styles/DeliveryAgent/DeliveryAgentPayout.css';
import { BASE_URL } from '../../utils/config';

const DeliveryAgentPayout = () => {
    const [filterParams, setFilterParams] = useState({ filter: "overall" });
    const [user, setUser] = useState(null);
    const [summary, setSummary] = useState([]);
    const [paid, setPaid] = useState([]);
    const [pending, setPending] = useState([]);
    const [all, setAll] = useState([]);
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
        headers: { Authorization: `Bearer ${token}` },
    });

    useEffect (() => {

        const fetchAllData = async () => {

            if(!token) return;

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
                const [userRes, summaryRes, paidRes, pendingRes, allRes] = await Promise.all([
                    
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch payout summary data
                    axiosInstance.get("/payouts/summary", {
                        header: {
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
                        console.error("Summary data fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch payout paid list
                    axiosInstance.get("/payouts/paid", {
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
                        console.error("Paid payout data fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch payout pending list
                    axiosInstance.get("/payouts/pending", {
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
                        console.error("Pending payout data fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch all payout list
                    axiosInstance.get("/payouts/all", {
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
                        console.error("All payout data fetch failed", err);
                        return { data: null };
                    }),

                ]);

                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setSummary(summaryRes.data)
                console.log("Summary data : ", summaryRes.data);

                setPaid(paidRes.data);
                console.log("Paid payouts : ", paidRes.data);

                setPending(pendingRes.data);
                console.log("Pending payouts : ", pendingRes.data);

                setAll(allRes.data);
                console.log("All payouts : ", allRes.data);

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
        
            <DeliveryAgentDashboardLayout user={user}>

                <h1 className='heading-agent h1-agent'>PAYOUT DASHBOARD</h1>
                
                    {/* Filter */}
                    <DeliveryAgentTimeFilter onChange={setFilterParams} />

                    {/* Summary Data */} 
                    <div className="summary-container" style={{ marginTop: '150px' }}>
                        
                        {/* Total Payouts */}
                        <SummaryCard 
                            title="TOTAL PAYOUTS" 
                            prefix="₹" 
                            user={user}
                            count={summary?.totalEarnings} 
                            link="/payouts/all" 
                            data={all}
                            filterParams={filterParams}
                        />

                        {/* Paid Payouts */}
                        <SummaryCard 
                            title="PAID PAYOUTS" 
                            prefix="₹" 
                            user={user}
                            count={summary?.paidPayouts} 
                            link="/payouts/paid" 
                            data={paid}
                            filterParams={filterParams} 
                        />

                        {/* Pending Payouts */}
                        <SummaryCard 
                            title="PENDING PAYOUTS" 
                            prefix="₹" 
                            user={user}
                            count={summary?.pendingPayouts} 
                            link="/payouts/pending" 
                            data={pending}
                            filterParams={filterParams}
                        />

                    </div>

            </DeliveryAgentDashboardLayout>

        </>

    );

};

export default DeliveryAgentPayout;