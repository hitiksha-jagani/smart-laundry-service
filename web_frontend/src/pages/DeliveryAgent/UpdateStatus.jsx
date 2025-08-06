// Author: Hitiksha Jagani
// Description: Update status page in delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import { useLocation, useNavigate } from 'react-router-dom';
import '../../styles/Toast.css';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import '../../styles/DeliveryAgent/UpdateStatus.css';
import axios from 'axios';
 import { BASE_URL } from '../../utils/config';  
const UpdateStatus = () => {
  const [toast, setToast] = useState({ message: '', type: '', visible: false });
  const [user, setUser] = useState(null);
  const [delivery, setDelivery] = useState(null);
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const { state } = useLocation();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const showToast = (message, type = 'success') => {
    setToast({ message, type, visible: true });
    setTimeout(() => {
      setToast({ message: '', type: '', visible: false });
    }, 5000);
  };

  const axiosInstance = axios.create({
    baseURL: `${BASE_URL}`,
    headers: { Authorization: `Bearer ${token}` },
  });

  useEffect(() => {
    if (state?.delivery) {
      console.log("Delivery passed to UpdateStatus:", state.delivery);
      setDelivery(state.delivery);
      localStorage.setItem("selectedDelivery", JSON.stringify(state.delivery));
    } else {
      const stored = localStorage.getItem("selectedDelivery");
      if (stored) setDelivery(JSON.parse(stored));
    }

    // Fetch user
    const fetchUser = async () => {
      if (!token) return;
      try {
        const decoded = jwtDecode(token);
        const userId = decoded.userId || decoded.id;
        const res = await axiosInstance.get(`/user-detail/${userId}`);
        setUser(res.data);
      } catch (err) {
        console.error("Error fetching user:", err);
        showToast("User info fetch failed", "error");
      }
    };

    fetchUser();
  }, [state, token]);

  const handleUpdateStatus = async () => {
    if (!delivery) return;

    setLoading(true);

    const orderId = delivery.orderId;
    const status = delivery.orderStatus;
  console.log("Order status in update:", status);
    let endpoint = '';
    const payload = { orderId, otp };

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
        showToast('Unsupported order status: ' + status, 'error');
        setLoading(false);
        return;
    }

    try {
      console.log("Update status API called");
      console.log("Order :", payload);
      await axiosInstance.post(endpoint, payload);
      showToast("Status updated successfully.", "success");
      navigate("/deliveries/summary");
    } catch (error) {
      const errorMsg =
        error?.response?.data?.message ||
        error?.response?.data ||
        error.message ||
        'Failed to update status';

      if (errorMsg.toLowerCase().includes('expired')) {
        showToast("OTP has expired. Please request a new one.", "error");
      } else if (errorMsg.toLowerCase().includes('invalid')) {
        showToast("Invalid OTP. Please try again.", "error");
      } else {
        showToast(errorMsg, "error");
      }

      console.error("Failed to update status:", error);
    } finally {
      setLoading(false);
    }
  };

  if (!delivery) {
    return <div>No delivery data found.</div>;
  }

  return (
    <DeliveryAgentDashboardLayout user={user}>
      <div className="status-wrapper">
        <div className="status-box">
          <input
            className='input-agent'
            type="text"
            placeholder="OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
          />
          <br /><br />
          <button
            className="route-btn agent-btn"
            onClick={handleUpdateStatus}
            disabled={loading || !otp}
            style={{ width: '100%' }}
          >
            {loading ? 'Verifying...' : 'Verify OTP & Update'}
          </button>
        </div>
      </div>

      {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}
    </DeliveryAgentDashboardLayout>
  );
};

export default UpdateStatus;
