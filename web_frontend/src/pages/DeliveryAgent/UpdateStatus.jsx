// Author: Hitiksha Jagani
// Description: Update status page in delivery agent dashboard.

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
  const [user, setUser] = useState(null);
  const [delivery, setDelivery] = useState(null);
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const { state } = useLocation();
  const token = localStorage.getItem("token");

  const showToast = (message, type = 'success') => {
    setToast({ message, type, visible: true });
    setTimeout(() => {
      setToast({ message: '', type: '', visible: false });
    }, 5000);
  };

  const axiosInstance = axios.create({
    baseURL: "http://localhost:8080",
    headers: { Authorization: `Bearer ${token}` },
  });

  useEffect(() => {
    // Restore delivery object from state or localStorage
    if (state?.delivery) {
        console.log("Delivery passed to UpdateStatus:", state.delivery); 
      setDelivery(state.delivery);
      localStorage.setItem("selectedDelivery", JSON.stringify(state.delivery));
    } else {
      const stored = localStorage.getItem("selectedDelivery");
      if (stored) setDelivery(JSON.parse(stored));
    }

    // Decode JWT and fetch user info
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

    const { orderId, orderStatus } = delivery;
    const agentId = user?.userId || user?.id; // include agentId
    const payload = { orderId, otp, agentId };

    console.log("🔍 Sending OTP verification payload:", payload);

    let endpoint = '';

    // Map status to endpoint
    switch (orderStatus) {
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
        showToast(`Unsupported order status: ${orderStatus}`, "error");
        setLoading(false);
        return;
    }

    try {
      await axiosInstance.post(endpoint, payload);
      showToast("Status updated successfully!", "success");
      setOtp('');
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Update failed";

      if (msg.toLowerCase().includes("expired")) {
        showToast("OTP has expired. Request a new one.", "error");
      } else if (msg.toLowerCase().includes("invalid")) {
        showToast("Invalid OTP. Please try again.", "error");
      } else {
        showToast(msg, "error");
      }

      console.error("Error updating status:", err);
    } finally {
      setLoading(false);
    }
  };

  if (!delivery) {
    return <div>No delivery information found.</div>;
  }

  return (
    <DeliveryAgentDashboardLayout user={user}>
      <div className="status-wrapper">
        <div className="status-box">
          <input
            className="input-agent"
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
            {loading ? "Verifying..." : "Verify OTP & Update"}
          </button>
        </div>
      </div>

      {toast.visible && (
        <div className={`custom-toast ${toast.type}`}>
          {toast.message}
        </div>
      )}
    </DeliveryAgentDashboardLayout>
  );
};

export default UpdateStatus;
