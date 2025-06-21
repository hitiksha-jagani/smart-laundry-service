// Author: Hitiksha Patel
// src/components/Shared/SummaryCard.jsx
import React from 'react';
import '../../styles/DeliveryAgent/SummaryCard.css'; // optional for styles

const SummaryCard = ({ title, count }) => {
  return (
    <div className="summary-card">
      {/* {icon && <img src={icon} alt="" className="summary-icon" />} */}
      <div className="summary-detail">
        <h2 className="summary-title">{title}</h2>
        <p className="summary-count">{count}</p>
      </div>
    </div>
  );
};

export default SummaryCard;
