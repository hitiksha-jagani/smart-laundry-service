// Author : Hitiksha Jagani
// Description : Update status page in delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import { useLocation } from 'react-router-dom';
import '../../styles/Toast.css';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import '../../styles/DeliveryAgent/UpdateStatus.css';
import axios from 'axios';

const UpdateStatus = () => {

    const [toast, setToast] = useState({ message: '', type: '', visible: false });
    
    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
    
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 5000);
    };

    const [user, setUser] = useState(null);
    const { state } = useLocation();
    const delivery = state?.delivery;

    const [otp, setOtp] = useState('');
    const [loading, setLoading] = useState(false);
    const [responseMsg, setResponseMsg] = useState('');

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
                    showToast('Invalid or expired token', "error");
                    return;
                }
    
                const userId = decoded.userId || decoded.id;
    
                try {
                    // Fetch all data in parallel
                    const [userRes] = await Promise.all([
                        
                        axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                            console.error("User detail fetch failed", err);
                            return { data: null };
                        })    
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

        const handleUpdateStatus = async () => {

            setLoading(true);
            setResponseMsg('');

            const orderId = delivery.orderId;
            const status = delivery.status;

            let endpoint = '';
            const payload = {
                orderId: orderId,
                otp: otp
            };

            switch (status) {
                case 'ACCEPTED_BY_AGENT':
                    endpoint = '/emailotp/verify-pickup';
                    break;
                case 'PICKED_UP':
                    endpoint = '/emailotp/verify-handover';
                    break;
                case 'READY_FOR_DELIVERY':
                    endpoint = '/emailotp/verify-confirm-for-cloths';
                    break;
                case 'OUT_FOR_DELIVERY':
                    endpoint = '/emailotp/verify-delivery';
                    break;
                default:
                    setResponseMsg('Unsupported order status: ' + status);
                    setLoading(false);
                    return;
            }

            try {
                const res = await axiosInstance.post(endpoint, payload); // axiosInstance already has Authorization header

                showToast("Status updated successfully.", "success");
            } catch (error) {
                showToast("Failed to update status", "error");
                console.error("Failed to update status:", error);
            } finally {
                setLoading(false);
            }
        };


    if (!delivery) {
        return <div>No delivery data found.</div>;
    }

    return (

        <>           

            <DeliveryAgentDashboardLayout user={user}>

            <div className="status-wrapper">
      
                <div className="status-box">
        
                    {/* <h1 className="heading inter-font">UPDATE STATUS</h1> */}

                    <input
                        className='input-agent'
                        type="text"
                        placeholder="OTP"
                        value={otp}
                        onChange={(e) => setOtp(e.target.value)}
                        // style={{padding: '50px'}}
                    />

                    <br /><br />

                    <button
                        className="route-btn agent-btn"
                        onClick={handleUpdateStatus}
                        disabled={loading || !otp}
                        style={{width: '100%'}}
                    >
                        {loading ? 'Verifying...' : 'Verify OTP & Update'}
                    </button>

                </div>

            </div>

            </DeliveryAgentDashboardLayout>

        </>
    );
};

export default UpdateStatus;
