// Author : Hitiksha Jagani
// Geocoding api setting page in admin dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import { MdInbox } from 'react-icons/md';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import ConfigurationMenu from '../../components/Admin/ConfigurationMenu.jsx';
import GeoCodingApiSavingForm from '../../components/Admin/GeoCodingApiSaveForm.jsx';
import GeoCodingHistory from '../../components/Admin/GeoCodingHistory.jsx';
import axios from 'axios';

const GeoCodingSetting = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   
    const [apiKey, setApiKey] = useState("");
    const [providers, setProviders] = useState([]);
    const [selectedProvider, setSelectedProvider] = useState('');
    const [activeStatus, setActiveStatus] = useState('false');
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

                    // Fetch geo-coding api
                    axiosInstance.get("/configurations/geo-api/history", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Geo-coding api data fetch failed", err);
                        return { data: null };
                    }),
        
                ]);
        
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setData(dataRes.data);
                console.log("Geo-coding api data : ", dataRes.data);
                
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
    
        };
        
        fetchAllData();
    
    }, []);

    useEffect(() => {
        axiosInstance.get('http://localhost:8080/configurations/geo-api/providers')
            .then((res) => setProviders(res.data))
            .catch((err) => console.error("Failed to load geo-coding api providers", err));
    }, []);

    const handleSubmit = async () => {

        if (!selectedProvider) {
            showToast("Please select a provider.", "error")
            return;
        }

        if(!apiKey) {
            showToast("Api key is required.", "error")
            return;
        }

        try {

            await axiosInstance.post("/configurations/geo-api", {
                apiProvider: selectedProvider,
                apiKey: apiKey
            });

            showToast("API key saved successfully!", "success");
            setApiKey("");

        } catch (error) {
            console.error("Error saving config:", error);
            showToast("Failed to set configuration.", "error");
        }
    };

    const handleReset = async () => {
        setSelectedProvider('');
        setApiKey('');
        setActiveStatus('false');
    }

    const handleToggleStatus = async (entry) => {
        const confirm = window.confirm("Are you sure you want to change the status of this entry? Once you active this change this other active record set to deactive.");
        if (!confirm) return;

        const newStatus = !entry.activeStatus;       

        try {
            await axiosInstance.put(`/configurations/geo-api/history/${entry.id}`, {
                activeStatus: newStatus,
            });

            showToast("Status updated successfully.", "success");

            const response = await axiosInstance.get("/configurations/geo-api/history");
            setData(response.data);
        } catch (error) {
            console.error("Failed to update status:", error);
            showToast("Something went wrong while updating the status.", "error");
        }
    };

    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className='heading-admin h1-admin'>CONFIGURATION DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>Manage system-wide configuration settings such as revenue breakdown rules, delivery agent earnings, and geocoding API preferences.</p>

                    <h2 className="h2-admin">GEO CODING SETTINGS</h2>

                    <div style={{ width: '500px', maxWidth: '700px' }} className="provider-box">

                        <div className="grid-row" style={{ gridTemplateColumns: '1fr' }}>

                            <div className="field">

                                <label>Service Provider Revenue</label>

                                <select
                                    name="provider"
                                    value={selectedProvider || ''}
                                    onChange={(e) => setSelectedProvider(e.target.value)}
                                    className="input-field"
                                >
                                    <option value="">Select</option>
                                    {providers.map((provider, index) => (
                                        <option key={index} value={provider.name}>
                                            {provider.label}
                                        </option>
                                    ))}
                                </select>

                            </div>

                        </div>

                        <div className="grid-row" style={{ gridTemplateColumns: '1fr' }}>

                            <div className="field">
                                <label>API Key</label>
                                <span>
                                    <input
                                        type="text"
                                        value={apiKey}
                                        onChange={(e) => setApiKey(e.target.value)}
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
                                        value={activeStatus}
                                        onChange={(e) => setActiveStatus(e.target.value)}
                                        className="input-field"
                                    >
                                        <option value="true">ACTIVE</option>
                                        <option value="false">INACTIVE</option>
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
                                No geo-coding api data available for the selected period.
                            </h2>
                                
                            <p>Add data, it’ll appear here.</p>
                    
                        </div>
                                
                    ) : ( 

                        <div className="admin-total-payouts-container" style={{ marginTop: '50px', marginBottom: '50px'}}>

                            <table className="admin-total-table">

                                <thead>

                                    <tr style={{ backgroundColor: '#f1f5f9'}}>

                                        <th>No.</th>
                                        <th>API Provider</th>
                                        <th>API Key</th>
                                        <th>Current Status</th>
                                        <th>Created At</th>
                                        <th>Active At</th>
                                        <th>Deactivate At</th>
                                        <th>Action</th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {data.map((data, index) => (
                                    
                                    <tr key={data.id}>

                                        <td>{index + 1}</td>
                                        <td>{data.apiProvider}</td>
                                        <td >{data.apiKey}</td>
                                        <td>{data.activeStatus ? 'Active' : 'Inactive'}</td>
                                        <td>{data.createdAt}</td>
                                        <td>{data.activeAt || '-'}</td>
                                        <td>{data.deactivateAt || '-'}</td>
                                        <td>
                                            <button
                                                onClick={() => handleToggleStatus(data)}
                                                className="admin-btn"
                                            >
                                                {data.activeStatus ? 'DEACTIVATE' : 'ACTIVATE'}
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

export default GeoCodingSetting;