// Author : Hitiksha Jagani
// Description : Revenue breakdown setting page for admin dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { MdInbox } from 'react-icons/md';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import ConfigurationMenu from '../../components/Admin/ConfigurationMenu.jsx';

const RevenueBreakdownConfigPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   
    const [serviceProviderRevenue, setServiceProviderRevenue] = useState('');
    const [deliveryAgentRevenue, setDeliveryAgentRevenue] = useState('');
    const [currentStatus, setCurrentStatus] = useState('INACTIVE');
    const [data, setData] = useState([]);
    const [toast, setToast] = useState({ message: '', type: '', visible: false }); 

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
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
                const [userRes, dataRes] = await Promise.all([
                                
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch revenue breakdown
                    axiosInstance.get("/configurations/revenue-breakdown/history", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Revenue breakdown data fetch failed", err);
                        return { data: null };
                    }),
            
                ]);
            
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setData(dataRes.data);
                console.log("Delivery agent earning data : ", dataRes.data);
                    
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        
        };
            
        fetchAllData();
        
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!serviceProviderRevenue) {
            showToast("Service provider revenue is required.", "error");
            return;
        }

        if(!deliveryAgentRevenue){
            showToast("Delivery agent revenue is required.", "error");
            return;
        }

        if (currentStatus === 'ACTIVE') {
            const confirm = window.confirm("This will deactivate any currently active revenue breakdown settings. Are you sure?");
            if (!confirm) return;
        }

        try {
            const payload = {
                serviceProviderRevenue: Number(serviceProviderRevenue),
                deliveryAgentRevenue: Number(deliveryAgentRevenue),
                currentStatus
            };

            const response = await axiosInstance.post('/configurations/revenue-breakdown', payload);

            showToast("Breakdown set successfully.", "success");

            setServiceProviderRevenue('');
            setDeliveryAgentRevenue('');
            setCurrentStatus('INACTIVE');

        } catch (error) {
            console.error("Failed to set revenue:", error);
            showToast("Failed to set revenue.", "error");
        }
    };

    const handleReset = async () => {
        setServiceProviderRevenue('');
        setDeliveryAgentRevenue('');
        setCurrentStatus('INACTIVE');
    }

    const handleToggleStatus = async (id) => {
        const confirm = window.confirm("Are you sure you want to change the status of this entry?");
        if (!confirm) return;

        axiosInstance.put(`/configurations/revenue-breakdown/history/${id}`)
        .then((res) => {
            showToast("Status updated successfully.", "success");

            // Refetch updated data
            axiosInstance.get("/configurations/revenue-breakdown/history")
                .then((response) => {
                    setData(response.data);
                })
                .catch((error) => {
                    console.error("Failed to refresh data:", error);
                    showToast("Failed to reload updated data.", "error");
                });
        })
        .catch((error) => {
            console.error("Failed to update status:", error);
            showToast("Something went wrong while updating the status.", "error");
        });

    };
    
    
    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className='heading-admin h1-admin'>CONFIGURATION DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>Manage system-wide configuration settings such as revenue breakdown rules, delivery agent earnings, and geocoding API preferences.</p>

                    <h2 className="h2-admin">REVENUE BREAKDOWN SETTINGS</h2>

                    <div style={{ width: '500px', maxWidth: '700px' }} className="provider-box">

                        <div className="grid-row" style={{ gridTemplateColumns: '1fr' }}>

                            <div className="field">
                                <label>Service Provider Revenue</label>
                                <span>
                                    <input
                                        type="number"
                                        value={serviceProviderRevenue ?? ''}
                                        onChange={(e) => setServiceProviderRevenue(e.target.value)}
                                        className="input-field"
                                    />
                                </span>
                            </div>

                        </div>

                        <div className="grid-row" style={{ gridTemplateColumns: '1fr' }}>

                            <div className="field">
                                <label>Delivery Agent Revenue</label>
                                <span>
                                    <input
                                        type="number"
                                        value={deliveryAgentRevenue}
                                        onChange={(e) => setDeliveryAgentRevenue(e.target.value)}
                                        className="input-field"
                                    />
                                </span>
                            </div>

                        </div>

                        <div className="grid-row" style={{ gridTemplateColumns: '1fr' }}>

                            <div className="field">
                                <label>Status</label>
                                <span>
                                    <select
                                        value={currentStatus}
                                        onChange={(e) => setCurrentStatus(e.target.value)}
                                        className="input-field"
                                    >
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="INACTIVE">INACTIVE</option>
                                    </select>
                                </span>
                            </div>

                        </div>

                        <div className="button-row"> 
                            <button
                                className="admin-btn"
                                onClick={handleSubmit}
                                style={{ marginRight: '10px', width: '210px' }}
                            >
                                SAVE
                            </button>

                            <button
                                className="reset-btn"
                                onClick={handleReset}
                                style={{ width: '210px' }}
                            >
                                RESET ALL
                            </button>
                        </div>

                    </div>

                    <h2 className="h2-admin" style={{ marginTop: '50px' }}>DELIVERY AGENT EARNING HISTORY </h2>

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
                                margin: '50px auto',
                                width: '70%',
                                color: '#777',
                            }}
                        >
                                                    
                            <MdInbox size={64} color="#ccc" />
                                
                            <h2 style={{ marginTop: '1rem' }}>
                                No delivery agent earning data available for the selected period.
                            </h2>
                                
                            <p>Add data, it’ll appear here.</p>
                    
                        </div>
                                
                    ) : ( 

                        <div className="admin-total-payouts-container" style={{ marginTop: '50px', marginBottom: '50px'}}>

                            <table className="admin-total-table">

                                <thead>

                                    <tr style={{ backgroundColor: '#f1f5f9'}}>

                                        <th>No.</th>
                                        <th>Service Provider Revenue</th>
                                        <th>Delivery Agent Revenue</th>
                                        <th>Current Status</th>
                                        <th>Active At</th>
                                        <th>Deactivate At</th>
                                        <th>Action</th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {data.map((data, index) => (
                                    
                                    <tr key={data.revenueId}>

                                        <td>{index + 1}</td>
                                        <td>{data.serviceProvider}</td>
                                        <td>{data.deliveryAgent}</td>
                                        <td>{data.currentStatus}</td>
                                        <td>{data.activeAt || '-'}</td>
                                        <td>{data.deactivateAt || '-'}</td>
                                        <td>
                                            <button
                                                onClick={() => handleToggleStatus(data.revenueId)}
                                                className="admin-btn"
                                            >
                                                {data.currentStatus === 'ACTIVE' ? 'DEACTIVATE' : 'ACTIVATE'}
                                            </button>
                                        </td>

                                    </tr>

                                    ))}

                                </tbody>

                            </table>

                        </div>

                    )}

                    {/* Right Sidebar */}
                    <ConfigurationMenu />

                </div>

            </div>

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

        </AdminDashboardLayout>

    );

};

export default RevenueBreakdownConfigPage;