// Author: Hitiksha Jagani
// Description: Pending delivery list in delivery agent dashboard.

import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import PendingDeliveryCard from './PendingDeliveryCard';
import { MdInbox } from 'react-icons/md';
import '../../styles/Toast.css';

const PendingDeliveries = () => {
  const location = useLocation();
  const state = location.state || {};
  const user = state.user;
  const data = state.data || [];

  const [orders, setOrders] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [toast, setToast] = useState({ message: '', type: '', visible: false });

  const showToast = (message, type = 'success') => {
    setToast({ message, type, visible: true });
    setTimeout(() => {
      setToast({ message: '', type: '', visible: false });
    }, 5000);
  };

  const token = localStorage.getItem('token');

  const handleAccept = async (orderId) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/deliveries/accept/${orderId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      showToast('Order accepted successfully.', 'success');
      console.log('Order Accepted:', response.data); 
      window.location.reload();

      setOrders((prevOrders) => prevOrders.filter((order) => order.orderId !== orderId));
    } catch (error) {
      const msg = error.response?.data?.message || 'Error accepting order.';
      showToast(msg, 'error');
      console.error('Error accepting order:', error.response?.data || error.message);
      alert(msg);
    }
  };

  const handleReject = async (orderId) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/deliveries/reject/${orderId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      showToast('Order rejected successfully.', 'success');
      console.log('Order Rejected:', response.data);
      window.location.reload(); 

      setOrders((prevOrders) => prevOrders.filter((order) => order.orderId !== orderId));
    } catch (error) {
      const msg = error.response?.data || 'Error rejecting order.';
      showToast(msg, 'error');
      console.error('Error rejecting order:', error.response?.data || error.message);
    }
  };

  const hasPrev = currentIndex > 0;
  const hasNext = currentIndex < data.length - 1;

  return (
    <DeliveryAgentDashboardLayout user={user}>
      <h2 className="pending-heading heading-agent h2-agent">PENDING DELIVERIES</h2>

      {data.length === 0 ? (
        <div
          style={{
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
          }}
        >
          <MdInbox size={64} color="#ccc" />
          <h2 style={{ marginTop: '1rem' }}>No Deliveries Available</h2>
          <p>Once new deliveries are assigned to you, they’ll appear here.</p>
        </div>
      ) : (
        <div>
          <PendingDeliveryCard
            data={data[currentIndex]}
            onAccept={handleAccept}
            onReject={handleReject}
          />

          <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'center', gap: '1rem' }}>
            <button
              className="nav-btn"
              onClick={() => setCurrentIndex(currentIndex - 1)}
              disabled={!hasPrev}
            >
              ⬅ Prev
            </button>
            <button
              className="nav-btn"
              onClick={() => setCurrentIndex(currentIndex + 1)}
              disabled={!hasNext}
            >
              Next ➡
            </button>
          </div>

          <p
            style={{
              marginTop: '0.5rem',
              color: '#555',
              textAlign: 'center',
              fontSize: '20px',
              fontWeight: '900',
              marginBottom: '50px'
            }}
          >
            Order {currentIndex + 1} of {data.length}
          </p>
        </div>
      )}

      {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}
    </DeliveryAgentDashboardLayout>
  );
};

export default PendingDeliveries;
