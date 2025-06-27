// Author : Hitiksha Jagani
// Description : Today's delivery list in delivery agent dashboard.

// Author : Hitiksha Jagani
// Description : Pending delivery list in delivery agent dashboard.

import React, { useState } from 'react';
import axios from 'axios';
import TodayDeliveryCard from './TodayDeliveryCard';
import { MdInbox } from 'react-icons/md';
import '../../styles/Toast.css';

const TodayDeliveries = ({ deliveries, token }) => {

    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    // const showToast = (message, type = 'success') => {
    //   setToast({ message, type, visible: true });

    //   setTimeout(() => {
    //     setToast({ message: '', type: '', visible: false });
    //   }, 5000);
    // };
    
    const [currentIndex, setCurrentIndex] = useState(0);

    const hasPrev = currentIndex > 0;
    const hasNext = currentIndex < deliveries.length - 1;

    if (!deliveries || deliveries.length === 0) {
      return (
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
          <h2 style={{ marginTop: '1rem' }}>No Today's Deliveries Available</h2>
          <p>Once new deliveries are assigned to you, they’ll appear here.</p>
        </div>
      );
    }

    return (

      <>
        <div>
          
            <TodayDeliveryCard
              delivery={deliveries[currentIndex]}
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

            <p style={{ marginTop: '0.5rem', marginBottom: '2rem', color: '#555', textAlign: 'center', fontSize: '20px', fontWeight: '900' }}>
                Order {currentIndex + 1} of {deliveries.length}
            </p>

        </div>

        {toast.visible && (
            <div className={`custom-toast ${toast.type}`}>
              { toast.message}
            </div>
        )}

      </>
    );
};

export default TodayDeliveries;
