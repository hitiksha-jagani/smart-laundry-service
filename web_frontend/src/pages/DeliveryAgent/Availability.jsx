// Author : Hitiksha Jagani
// Description : Availability Page for delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import ManageAvailability from '../../components/DeliveryAgent/ManageAvailability';
import SavedAvailability from './SavedAvailability';
import { BASE_URL } from '../../utils/config';

const Availability = () => { 
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [availabilities, setAvailabilities] = useState([]);

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
                    const [userRes, availabilitiesRes] = await Promise.all([
                        
                        // Fetch user data
                        axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                            console.error("User detail fetch failed", err);
                            return { data: null };
                        }),

                        // Fetch saved availabilities
                        axiosInstance.get("/availability/saved", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            }
                        }).catch(err => {
                            console.error("Saved availability data fetch failed", err);
                            return { data: null };
                        }),
    
                    ]);
    
                    setUser(userRes.data);
                    console.log("User data : " ,userRes.data);

                    setAvailabilities(availabilitiesRes.data);
                    console.log("Availability data : ", availabilitiesRes.data);
    
                    
    
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

            <>
            
                <DeliveryAgentDashboardLayout user={user}>

                    <h1 className='heading-agent h1-agent'>AVAILABILITY DASHBOARD</h1>

                    {/* Save New Availability */}
                    <h2 className='h2-agent' style={{ color: '#64748B', textAlign: 'center', fontSize: '20px', marginTop: '50px' }}>SAVE</h2>
                    <ManageAvailability />


                    {/* History of saved availabilities for current week */}
                    <h2 className='h2-agent' style={{ color: '#64748B', textAlign: 'center', fontSize: '20px', marginTop: '50px' }}>LIST OF SAVED AVAIALABILITY FOR THIS WEEK</h2>
                    <SavedAvailability availabilities={availabilities} />

                </DeliveryAgentDashboardLayout>

            </>

        );
 
};

export default Availability;