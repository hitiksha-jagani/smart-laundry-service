// Author : Hitiksha Jagani
// Description : Service provider revenue list for admin dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { MdInbox } from 'react-icons/md';
import GraphFilter from '../../components/Admin/GraphFilter';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RevenueMenu from '../../components/Admin/RevenueMenu';
import '../../styles/Admin/TotalRevenuePage.css';

const ProviderRevenuePage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState("monthly");
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
                const [userRes, tableRes] = await Promise.all([
                                
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),
            
                    // Fetch service provider revenue 
                    axiosInstance.get("/revenue/provider-analytics-list", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        },
                        params: {
                            filter
                        }
                    }).catch(err => {
                        console.error("Service provider revenue data fetch failed", err);
                        return { data: null };
                    })

                ]);
                    
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);                           
            
                setTable(tableRes.data);
                console.log("Table data : ", tableRes.data);
                            
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
    

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>                   

                    <h2 className='heading-admin h2-admin'>SERVICE PROVIDER PAYOUTS</h2>

                    <GraphFilter onChange={setFilter} /> 
    
                    {(!table || table.length === 0) ? (
            
                        <div
                            style={{
                                    display: 'flex',
                                    flexDirection: 'column',
                                    alignItems: 'center',
                                    padding: '2rem',
                                    backgroundColor: '#fff',
                                    border: '1px dashed #ccc',
                                    borderRadius: '1rem',
                                    margin: '100px auto',
                                    width: '70%',
                                    color: '#777',
                            }}
                        >
                                
                            <MdInbox size={64} color="#ccc" />
            
                            <h2 style={{ marginTop: '1rem' }}>
                                No service provider revenue data available for the selected period.
                            </h2>
            
                            <p>Once revenue is recorded, it’ll appear here.</p>

                        </div>
            
                    ) : ( 
    
                        <div className="admin-total-payouts-container" style={{ marginTop: '100px'}}>

                            <table className="admin-total-table">

                                    <thead>

                                        <tr>

                                            <th>No.</th>
                                            <th>Provider ID</th>
                                            <th>Total Revenue</th>
                                            <th>Platform Charge</th>
                                            <th>Payout Count</th>
                                            <th>Start Date</th>
                                            <th>End Date</th>

                                        </tr>

                                    </thead>

                                    <tbody>
                                        
                                        {table.map((revenue, index) => (

                                            <tr key={revenue.providerId}>

                                                <td style={{ borderLeft: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{index + 1}</td> 
                                                <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.providerId}</td>
                                                <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.totalRevenue}</td>
                                                <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.platformCharges}</td>
                                                <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.payoutCount}</td>
                                                <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.startDate}</td>
                                                <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.endDate}</td>
                                                
                                            </tr>

                                        ))}

                                    </tbody>

                            </table>

                        </div>  

                    )}

                </div>

                {/* Right Sidebar */}
                <RevenueMenu />


            </div>


        </AdminDashboardLayout>
    
    
    );

};

export default ProviderRevenuePage;