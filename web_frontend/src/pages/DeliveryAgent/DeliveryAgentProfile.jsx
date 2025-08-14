// Author : Hitiksha Jagani
// Description : Delivery agent profile page for delivery agent dashboard. 

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/DeliveryAgentProfile.css';
import { BASE_URL } from '../../utils/config';

const DeliveryAgentProfile = () => {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const navigate = useNavigate();
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
        }, 5000);
    };

    useEffect(() => {
    
            const fetchAllData = async () => {
    
                if (!token) {
                    navigate('/login');
                    return;
                }

            
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
        
                        // Fetch admin profile data
                        axiosInstance.get("/profile/detail", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            }
                        }).catch(err => {
                            console.error("Delivery agent profile data fetch failed", err);
                            return { data: null };
                        }),
            
                    ]);
            
                    setUser(userRes.data);
                    console.log("User data : " ,userRes.data);
            
                    setData(dataRes.data || []);
                    console.log("Delivery agent data:", dataRes.data);

                } catch (error) {
                    console.error("Failed to fetch one or more data:", error);
                } finally {
                    setLoading(false);
                }
            };
            
            fetchAllData();
        
        }, []);
                  
        const handleFileClick = (fileUrl) => {
            if (fileUrl) {
                window.open(fileUrl, "_blank", "noopener,noreferrer");
            } else {
                showToast("Image not available", "error");
            } 
        };

        if (loading) return <p className="text-center">Loading...</p>;
        
    return  (

        <DeliveryAgentDashboardLayout user={user}>

                    <h1 className="heading-agent h1-agent">MY DETAILS</h1>

                    <div> 

                        <div style={{ margin: '50px auto 50px' }} className="agent-box" >

                            <div className="agent-grid-row">
                                    <div className="agent-field"><label>First Name</label><span>{data.firstName}</span></div>
                                    <div className="agent-field"><label>Last Name</label><span>{data.lastName}</span></div>
                            </div>

                            <div className="agent-grid-row">
                                    <div className="agent-field"><label>Phone</label><span>{data.phone}</span></div>
                                    <div className="agent-field"><label>Email</label><span>{data.email}</span></div>
                            </div>

                            <div className="agent-grid-row" style={{ gridTemplateColumns: '1fr' }}>
                                <div className="agent-field full">
                                        <label>Address</label>
                                        <span>
                                        {data.address
                                            ? `${data.address.name}, ${data.address.areaName}, ${data.address.cityName} - ${data.address.pincode}`
                                            : 'N/A'}
                                        </span>
                                </div>
                            </div>

                            <div className="agent-grid-row">
                                <div className="agent-field"><label>Date Of Birth</label><span>{data.dateOfBirth}</span></div>
                                <div className="agent-field"><label>Vehicle Number</label><span>{data.vehicleNumber}</span></div>
                            </div>

                            <div className="agent-grid-row">
                                <div className="agent-field"><label>Gender</label><span>{data.gender}</span></div>
                                <div className="agent-field"><label>Bank Account No</label><span>{data.bankAccountNumber}</span></div>
                            </div>

                            <div className="agent-grid-row">
                                <div className="agent-field"><label>Account Holder Name</label><span>{data.accountHolderName}</span></div>
                                <div className="agent-field"><label>Bank Name</label><span>{data.bankName}</span></div>
                            </div>

                                <div className="agent-grid-row">
                                    <div className="agent-field"><label>IFSC Code</label><span>{data.ifscCode}</span></div>
                                    <div className="agent-field">
                                        <label>PAN Card</label>
                                        <button onClick={() => handleFileClick(data.panCardPhoto)} className="link-btn">
                                            {data.panCardPhoto != null ? "Click here..." : "N/A"}
                                        </button>
                                    </div>
                                </div>

                                <div className="agent-grid-row">
                                    <div className="agent-field">
                                        <label>Aadhaar Card</label>
                                        <button onClick={() => handleFileClick(data.aadharCardPhoto)} className="link-btn">
                                            {data.aadharCardPhoto != null ? "Click here..." : "N/A"}
                                        </button>
                                    </div>
                                    <div className="agent-field">
                                        <label>Driving License</label>
                                        <button onClick={() => handleFileClick(data.drivingLicensePhoto)} className="link-btn">
                                            {data.drivingLicensePhoto != null ? "Click here..." : "N/A"}
                                        </button>
                                    </div>
                                </div>

                                <div className="agent-grid-row">
                                    
                                    <div className="agent-field">
                                        <label>Profile</label>
                                        <button onClick={() => handleFileClick(data.profilePhoto)} className="link-btn">
                                            {data.profilePhoto != null ? "Click here..." : "N/A"}
                                        </button>
                                    </div>
                                </div>

                            <div className="agent-button-row"> 
                                    <button
                                        className="agent-btn"
                                        onClick={() => navigate('/profile/detail/edit', { state: { data } })}
                                        style={{ marginRight: '10px', width: '210px' }}
                                    >
                                        EDIT 
                                    </button>

                                    <button
                                        className="reset-btn"
                                        onClick={() => navigate('/profile/detail/change-password')}
                                        style={{ width: '210px' }}
                                    >
                                        CHANGE PASSWORD
                                    </button>
                            </div>

                        </div>

            </div>

        </DeliveryAgentDashboardLayout>


    );

};

export default DeliveryAgentProfile;