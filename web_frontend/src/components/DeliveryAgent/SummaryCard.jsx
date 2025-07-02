// Author: Hitiksha Patel
// Description : Summary card for delivery agent dashboard.

import React from 'react';
import { Link } from 'react-router-dom';
import '../../styles/DeliveryAgent/SummaryCard.css'; 

const SummaryCard = ({ title, prefix, user, count, link, data, filterParams }) => {

    const linkState = {
        data,
        user,
        filter: filterParams?.filter,
        ...(filterParams?.filter === 'custom' && {
            startDate: filterParams.startDate,
            endDate: filterParams.endDate
        })
    };

    return (

      <div className="summary-card">
      
          <h3 className="summary-title h3-agent">{title}</h3>
 
          {link ? (

              <Link 
                  to={link} 
                  state={linkState}
                  style={{ textDecoration: 'none', color: 'inherit' }}
              >
                  <p className="summary-count">{prefix}{count}</p>
              </Link>

          ) : (
              
              <p className="summary-count">{prefix}{count}</p>

          )}

      </div>

    );
};

export default SummaryCard;