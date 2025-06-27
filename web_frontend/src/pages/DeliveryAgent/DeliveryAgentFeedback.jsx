// Author: Hitiksha Patel
// Description: Feedback page of delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentTimeFilter from '../../components/DeliveryAgent/DeliveryAgentTimeFilter.jsx';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/SummaryCard.css'; 

const DeliveryAgentFeedback = () => {
    const [filterParams, setFilterParams] = useState({ filter: "overall" });
    const [user, setUser] = useState(null);
    const [summary, setSummary] = useState([]);
    const [feedback, setFeedback] = useState([]);
    const [loading, setLoading] = useState(true);

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
                const [userRes, summaryRes, feedbackRes] = await Promise.all([
                        
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch feedback summary data
                    axiosInstance.get("/feedback/summary", {
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

                    // Fetch feedback list
                    axiosInstance.get("/feedback/list", {
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
                        console.error("Feedback data fetch failed", err);
                        return { data: null };
                    })
    
                ]);
    
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setSummary(summaryRes.data)
                console.log("Summary data : ", summaryRes.data);
        
                setFeedback(feedbackRes.data);
                console.log("Feedbacks list : ", feedbackRes.data);
    
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

            <h1 className='heading-agent h1-agent'>FEEDBACK DASHBOARD</h1>

            {/* Filter */}
            <DeliveryAgentTimeFilter onChange={setFilterParams} />

            {/* Summary Data */}
            <div className="summary-container" style={{ marginTop: '100px' }}>
                
                {/* Total Reviews */}
                <SummaryCard 
                    title="TOTAL REVIEWS" 
                    user={user}
                    count={summary?.totalReviews ?? 0} 
                    link="/feedback/list" 
                    data={feedback}
                    filterParams={filterParams}
                />

                {/* Average Rating */}
                <SummaryCard 
                    title="AVERAGE RATING"
                    user={user}
                    count={summary?.averageRating ?? 0} 
                />

                        
            </div>


            </DeliveryAgentDashboardLayout>

        </>

    );

};

export default DeliveryAgentFeedback;