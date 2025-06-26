// Author : Hitiksha Jagani
// Geocoding api setting page in admin dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import GeoCodingApiSavingForm from '../../components/Admin/GeoCodingApiSaveForm.jsx';
import GeoCodingHistory from '../../components/Admin/GeoCodingHistory.jsx';
import axios from 'axios';

const GeoCodingSetting = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   

    const token = localStorage.getItem("token");
    console.log(token);

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
                const [userRes] = await Promise.all([
                            
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),
                        // axiosInstance.get("/revenue/summary", {
                        //     headers: {
                        //         Authorization: `Bearer ${token}`
                        //     }
                        // }).catch(err => {
                        //     console.error("Revenue summary data fetch failed", err);
                        //     return { data: null };
                        // })
        
                ]);
        
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);
                
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
        
            <AdminDashboardLayout user={user}>

                <h1 className='heading-admin h1-admin'>GEO CODING SETTINGS</h1>

                <GeoCodingApiSavingForm />

                <GeoCodingHistory />

            </AdminDashboardLayout>

        </>

    );

};

export default GeoCodingSetting;