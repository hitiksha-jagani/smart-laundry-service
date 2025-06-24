// Author: Hitiksha Patel
// src/components/Shared/SummaryCard.jsx
import React from 'react';
import '../../styles/DeliveryAgent/SummaryCard.css'; // optional for styles

const SummaryCard = ({ title, count }) => {
    return (

      <div className="summary-card inter-font">
      
          <h3 className="summary-title">{title}</h3>
          <p className="summary-count">{count}</p>

      </div>

    );
};

export default SummaryCard;
