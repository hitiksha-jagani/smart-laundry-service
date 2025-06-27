// Author: Hitiksha Patel
// Description : Summary card for admin dashboard.

import React from 'react';
import '../../styles/Admin/AdminSummaryCard.css'; 

const AdminSummaryCard = ({ title, prefix, count }) => {

    return (

      <div className="admin-summary-card">
      
          <h3 className="admin-summary-title h3-admin" style={{textAlign: 'center'}}>{title}</h3>
          <p className="admin-summary-count">{prefix}{count}</p>

      </div>

    );
};

export default AdminSummaryCard;
 