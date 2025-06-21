import React, { useEffect, useState } from 'react';
import axios from 'axios';
import PendingDeliveryCard from './PendingDeliveryCard';

const PendingDeliveries = () => {
  const [deliveries, setDeliveries] = useState([]);

  useEffect(() => {
    axios.get('/deliveries/pending')
      .then(res => setDeliveries(res.data))
      .catch(err => console.error(err));
  }, []);

  const handleAccept = (orderId) => {
    // POST to backend to accept
    console.log('Accepted:', orderId);
  };

  const handleReject = (orderId) => {
    // POST to backend to reject
    console.log('Rejected:', orderId);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      {deliveries.map((d, i) => (
        <PendingDeliveryCard
          key={i}
          delivery={d}
          onAccept={handleAccept}
          onReject={handleReject}
        />
      ))}
    </div>
  );
};

export default PendingDeliveries;
