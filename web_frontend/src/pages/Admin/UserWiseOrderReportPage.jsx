// Author: Hitiksha Jagani
// Description: User-wise order report page (admin dashboard)

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { MdInbox } from 'react-icons/md';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import ReportsMenu from './ReportsMenu';
import GraphFilter from '../../components/Admin/GraphFilter';
import UserRoleFilter from '../../components/Admin/UserRoleFilter';
import RevenueTrendGraph from '../../components/Admin/RevenueTrendGraph';

const UserWiseOrderReportPage = () => {
    const navigate = useNavigate(); 
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [role, setRole] = useState("service provider");
    const [filter, setFilter] = useState("monthly");
    const [keyword, setKeyword] = useState("");
    const [sortBy, setSortBy] = useState("order");
    const [data, setData] = useState([]);

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
                const [userRes, orderRes] = await Promise.all([
                            
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),
    
                    // Fetch revenue trend
                    axiosInstance.get("/reports/order/user-report-list", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        },
                        params: { 
                            role,
                            filter,
                            keyword,
                            sortBy 
                        },
                    }).catch(err => {
                        console.error("User wise order data fetch failed", err);
                        return { data: null };
                    }),
        
                ]);
        
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);
        
                setData(orderRes.data || []);
                console.log("User wise order data:", orderRes.data);
                console.log("Role : ", role);
        
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        };
        
        fetchAllData();
    
    }, [role, filter, keyword, sortBy]);
        
    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className="heading-admin h1-admin">User Order Report</h1>

                    <div style={{ display: 'flex', justifyContent: 'center', gap: '30px', marginTop: '-10px', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
                            
                        <UserRoleFilter onChange={setRole} />
                        <GraphFilter onChange={setFilter} />

                    </div>

                    <div style={{ display: 'flex', justifyContent: 'center', gap: '30px', marginBottom: '1.5rem', flexWrap: 'wrap' }}>

                        <input
                            type="text"
                            placeholder="Search by name..."
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                            style={{ 
                                padding: '0.5rem', 
                                border: '1px solid #0EA5E9',
                                borderRadius: '10px',
                                backgroundColor: '#ffffff',
                                width: '50%'
                            }}
                        />

                        <select value={sortBy} onChange={e => setSortBy(e.target.value)} 
                            style={{ 
                                padding: '0.5rem',
                                border: '1px solid #0EA5E9',
                                borderRadius: '10px',
                                backgroundColor: '#ffffff',
                                width: '20%'
                            }}>
                            
                            <option value="order">Sort by Orders</option>
                            <option value="rejected">Sort by Rejected Orders</option>

                        </select>
 
                    </div>

                    {(!data || data.length === 0) ? (
                                
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

                        <div className="admin-total-payouts-container" style={{ marginTop: '80px'}}>

                            <table className="admin-total-table">

                                <thead>

                                    <tr style={{ backgroundColor: '#f1f5f9'}}>

                                        <th>No.</th>
                                        <th>User Id</th>
                                        <th>Id</th>
                                        {role === 'service provider' && (
                                            <th>Business Name</th>
                                        )}

                                        <th>Total Orders</th>
                                        <th>Rejected Orders</th>
                                        <th>Action</th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {data.map((order, index) => (
                                    
                                    <tr key={order.userId}>

                                        <td>{index + 1}</td>
                                        <td>{order.userId}</td>
                                        <td>{order.id}</td>
                                        {role === 'service provider'&& (
                                            <td>{order.businessName}</td>
                                        )}
                                        <td>{order.orderCount}</td>
                                        <td>{order.rejectedOrderCount}</td>
                                        <td>
                                            <button
                                                onClick={() =>
                                                    navigate(`/reports/order/user-report-list/graph/${order.id}`, {
                                                    state: {
                                                        filter,
                                                    },
                                                    })
                                                }
                                                className="admin-btn"
                                            >
                                                GRAPH
                                            </button>
                                        </td>

                                    </tr>

                                    ))}

                                </tbody>

                            </table>

                        </div>

                    )}

                    {/* Right Sidebar */}
                    <ReportsMenu />

                </div>

            </div>

        </AdminDashboardLayout>

    );
};

export default UserWiseOrderReportPage;
