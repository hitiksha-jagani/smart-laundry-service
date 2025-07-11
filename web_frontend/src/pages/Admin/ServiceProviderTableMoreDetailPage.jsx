// Author : Hitiksha Jagani
// Description : Service provider profile complete detail for admin dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import { MdInbox } from 'react-icons/md'; 
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RequestsMenu from "./RequestsMenu";
import '../../styles/Admin/ServiceProviderRequests.css';

const ServiceProviderTableMoreDetailPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 5000);
    };

    const token = localStorage.getItem("token");

    const handleAccept = (userId) => {
        axiosInstance.post(`/accept-agent/${userId}`)
            .then(res => {
                showToast("Request accepted successfully", "success");
                console.log("Accepted:", res.data);

                // Move to next request
                if (currentIndex < data.length - 1) {
                    setCurrentIndex(currentIndex + 1);
                } else {
                    setData(prev => prev.filter(agent => agent.userId !== userId));
                }
            })
            .catch(err => {
                showToast("Failed to accept request", "error");
                console.error("Accept Error:", err);
            });
    };

    const handleReject = (userId) => {
        axiosInstance.post(`/reject-agent/${userId}`)
            .then(res => {
                showToast("Request rejected successfully", "success");
                console.log("Rejected:", res.data);

                // Move to next request
                if (currentIndex < data.length - 1) {
                    setCurrentIndex(currentIndex + 1);
                } else {
                    setData(prev => prev.filter(agent => agent.userId !== userId));
                }
            })
            .catch(err => {
                showToast("Failed to reject request", "error");
                console.error("Reject Error:", err);
            });
    };
   
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
                    const [userRes, dataRes] = await Promise.all([
                                
                        // Fetch user data
                        axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                            console.error("User detail fetch failed", err);
                            return { data: null };
                        }),
        
                        // Fetch delivery agent profile data
                        axiosInstance.get("/agent-requests", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            }
                        }).catch(err => {
                            console.error("Delivery agent data fetch failed", err);
                            return { data: null };
                        }),
            
                    ]);
            
                    setUser(userRes.data);
                    console.log("User data : " ,userRes.data);
            
                    setData(dataRes.data || []);
                    console.log("Delivery agent profile data:", dataRes.data);

                } catch (error) {
                    console.error("Failed to fetch one or more data:", error);
                } finally {
                    setLoading(false);
                }
            };
            
            fetchAllData();
        
        }, []);
            
        const handleFileClick = (type) => {
            const userId = currentAgent.userId;

            if (type && userId) {
                const url = `http://localhost:8080/image/agent/${type}/${userId}`;
                window.open(url, '_blank'); 
            } else {
                showToast("Invalid image type or user ID", "error");
            }
        };

                
        if (loading) return <p className="text-center">Loading...</p>;
        
        const hasPrev = currentIndex > 0;
        const hasNext = currentIndex < data.length - 1;
    
        const currentAgent = data[currentIndex];

    return (

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className="heading-admin h1-admin">REQUEST DASHBOARD</h1>

                    <p className='p-admin'>Manage requests efficiently by switching between provider and agent views.</p>

                    <h2 className="h2-admin">DELIVERY AGENT REQUESTS</h2>

                    {(!data || data.length === 0) ? (
                    
                        <div style={{
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            padding: '2rem',
                            backgroundColor: '#fff',
                            border: '1px dashed #ccc',
                            borderRadius: '1rem',
                            margin: '2rem auto',
                            width: '80%',
                            color: '#777'
                        }}>
                    
                            <MdInbox size={64} color="#ccc" />
                            <h2 style={{ marginTop: '1rem' }}>No Requests Available</h2>
                            <p>Once request arrived, they’ll appear here.</p>
                    
                        </div>
                    
                    ) : (

                        <div>

                            <div className="provider-box">

                                <div className="grid-row">
                                    <div className="field"><label>First Name</label><span>{currentAgent?.firstName}</span></div>
                                    <div className="field"><label>Last Name</label><span>{currentAgent.lastName}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Phone</label><span>{currentAgent.phoneNo}</span></div>
                                    <div className="field"><label>Email</label><span>{currentAgent.email}</span></div>
                                </div>

                                <div className="grid-row full">
                                    <div className="field full">
                                        <label>Address</label>
                                        <span>
                                        {currentAgent.addresses
                                            ? `${currentAgent.addresses.name}, ${currentAgent.addresses.areaName}, ${currentAgent.addresses.cityName} - ${currentAgent.addresses.pincode}`
                                            : 'N/A'}
                                        </span>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Date Of Birth</label><span>{currentAgent.dateOfBirth}</span></div>
                                    <div className="field"><label>Vehicle Number</label><span>{currentAgent.vehicleNumber}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Gender</label><span>{currentAgent.gender}</span></div>
                                    <div className="field"><label>Bank Account No</label><span>{currentAgent.bankAccountNumber}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Account Holder Name</label><span>{currentAgent.accountHolderName}</span></div>
                                    <div className="field"><label>Bank Name</label><span>{currentAgent.bankName}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>IFSC Code</label><span>{currentAgent.ifscCode}</span></div>
                                    <div className="field">
                                        <label>PAN Card</label>
                                        <button onClick={() => handleFileClick('pan')} className="link-btn">
                                            Click here...
                                        </button>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    <div className="field">
                                        <label>Aadhaar Card</label>
                                        <button onClick={() => handleFileClick('aadhar')} className="link-btn">
                                            Click here...
                                        </button>
                                    </div>
                                    <div className="field">
                                        <label>Driving License</label>
                                        <button onClick={() => handleFileClick('license')} className="link-btn">
                                            Click here...
                                        </button>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    
                                    <div className="field">
                                        <label>Profile</label>
                                        <button onClick={() => handleFileClick('profile')} className="link-btn">
                                            Click here...
                                        </button>
                                    </div>
                                </div>

                                <div className="button-row"> 
                                    <button
                                        className="admin-btn"
                                        onClick={() => handleAccept(currentAgent.userId)}
                                        style={{ marginRight: '10px', width: '150px' }}
                                    >
                                        Accept
                                    </button>

                                    <button
                                        className="reset-btn"
                                        onClick={() => handleReject(currentAgent.userId)}
                                        style={{ width: '150px' }}
                                    >
                                        Reject
                                    </button>
                                </div>

                            </div>

                            <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'center', gap: '1rem' }}>
                                
                                <button
                                    className="admin-nav-btn nav-btn"
                                    onClick={() => setCurrentIndex(currentIndex - 1)}
                                    disabled={!hasPrev}
                                >
                                    ⬅ Prev
                                </button>

                                <button
                                    className="admin-nav-btn nav-btn"
                                    onClick={() => setCurrentIndex(currentIndex + 1)}
                                    disabled={!hasNext}
                                >
                                    Next ➡
                                </button>

                            </div>
                     
                            <p
                                style={{
                                    marginTop: '0.3rem',
                                    color: '#555',
                                    textAlign: 'center',
                                    fontSize: '20px',
                                    fontWeight: '900',
                                    marginBottom: '50px'
                                }}
                            >
                                Request {currentIndex + 1} of {data.length}
                            </p>

                        </div>
                   
                    )}

                    {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

                    {/* Right Sidebar */}
                    <RequestsMenu />

                </div>

            </div>

        </AdminDashboardLayout>


    );

};

export default ServiceProviderTableMoreDetailPage;