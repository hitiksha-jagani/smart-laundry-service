// Author: Hitiksha Patel
// Description : Summary card for admin dashboard.

import React from 'react';
import { Link } from 'react-router-dom';
import '../../styles/Admin/AdminSummaryCard.css'; 

const AdminSummaryCard = ({ title, prefix, count, user, link, data}) => {

    const linkState = {
        data,
        user
    }; 


    return (

      <div className="admin-summary-card">
      
          <h3 className="admin-summary-title h3-admin" style={{textAlign: 'center'}}>{title}</h3>
          {/* <p className="admin-summary-count">{prefix}{count}</p> */}

          {link ? (

              <Link 
                  to={link} 
                  state={linkState}
                  style={{ textDecoration: 'none', color: 'inherit' }}
              >
                  <p className="admin-summary-count">{prefix}{count}</p>
              </Link>

          ) : (
              
              <p className="admin-summary-count">{prefix}{count}</p>

          )}

      </div>

    );
};

export default AdminSummaryCard;
 