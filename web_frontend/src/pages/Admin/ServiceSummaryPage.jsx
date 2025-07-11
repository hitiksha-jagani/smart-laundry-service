// Author : Hitiksha Jagani
// Description : Summary page for service dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { MdInbox } from 'react-icons/md';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import ServiceMenu from '../../components/Admin/ServiceMenu.jsx';

const ServiceSummaryPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   
    const [data, setData] = useState([]);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: "http://localhost:8080",
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
                const [userRes, dataRes] = await Promise.all([
                                
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch revenue breakdown
                    axiosInstance.get("/service/summary", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Service summary data fetch failed", err);
                        return { data: null };
                    }),
            
                ]);
            
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setData(dataRes.data);
                console.log("Service summary data : ", dataRes.data);
                    
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

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className='heading-admin h1-admin'>SERVICE LISTING DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>Organize and assign clothing items under the appropriate services offered.</p>

                    <h2 className="h2-admin">SERVICES SUMMARY</h2>

                    <div className="summary-container" style={{ marginTop: '150px', marginLeft: '30px' }}>

                        {/* <AdminSummaryCard 
                                title="TOTAL REVENUE" 
                                // prefix='₹' 
                                user={user}
                                count={summary?.totalRevenue  || 0}
                                // link="/revenue/total-revenue"  
                                data={total}
                            /> */}

                    </div>

                    {/* Right Sidebar */}
                    <ServiceMenu />

                </div>

            </div>

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

        </AdminDashboardLayout>


    );

};

export default ServiceSummaryPage;