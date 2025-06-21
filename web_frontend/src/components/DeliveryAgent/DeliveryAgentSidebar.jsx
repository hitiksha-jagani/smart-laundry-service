// // Author: Hitiksha Patel
// // Description: Left sidebar component for Delivery Agent Dashboard.

// import React, { useEffect, useState } from 'react';
// import jwtDecode from 'jwt-decode';
// import axios from 'axios';
// import '../styles/DeliveryAgent/DeliveryAgentSidebar.css';

// const DeliveryAgentSidebar = () => {
//     const [user, setUser] = useState(null);

//     useEffect(() => {
//         const token = localStorage.getItem('token');
//         if (!token) return;

//         let decoded;
//         try {
//             decoded = jwtDecode(token);
//         } catch (err) {
//             console.error('Invalid token:', err);
//             return;
//         }

//         const userId = decoded.userId || decoded.id;

//         // Fetch full user data from backend
//         axios.get(`/user-detail/${userId}`, {
//             headers: {
//                 Authorization: `Bearer ${token}`
//             }
//         })
//         .then(res => {
//             setUser(res.data);
//         })
//         .catch(err => {
//             console.error('Failed to fetch user details:', err);
//         });
//     }, []);

//     if (!user) {
//         return <div className="sidebar">Loading...</div>;
//     }


//     return (
//         <div className="DeliveryAgentSidebar">
//             <div className="sidebar__logo">
//                 <h2>SmartLaundry</h2>
//             </div>

//             <div className="sidebar__agent-info">
//                 <p><strong>ID:</strong> {user.userId}</p>
//                 <p><strong>Phone:</strong> {user.phone}</p>
//                 <p><strong>Email:</strong> {user.email}</p>
//             </div>

//             <hr className="sidebar__divider" />

//             <div className="sidebar__section-title">Quick Links</div>
//             <ul className="sidebar__links">
//                 <li>Home</li>
//             </ul>

//             <hr className="sidebar__divider" />

//             <div className="sidebar__customer-name">
//                 <p><strong>Name:</strong> {user.firstName} {user.lastName}</p>
//             </div>

//             <hr className="sidebar__divider" />
//         </div>
//     );
// };

// export default DeliveryAgentSidebar;

// Author: Hitiksha Patel
// Description: Sidebar for Delivery Agent Dashboard

import React from 'react';
import { NavLink } from 'react-router-dom';
import { Link } from 'react-router-dom';
import link from '../../assets/triangle-right-arrow-icon.png'
import home from '../../assets/home-icon.svg'
import payout from '../../assets/credit-card-icon.svg'
import feedback from '../../assets/edit-document-icon.svg'
import orderHistory from '../../assets/working-time-icon.png'
import ticket from '../../assets/speech-bubble-line-icon.png'
import profile from '../../assets/avatar-icon.png'
import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';

const DeliveryAgentSidebar = ({ agent }) => {

    return (
        <div className="delivery-sidebar inter-font">
        
            <div className="sidebar-top">
            
                <ul className="sidebar-links">
                    <li>
                        <NavLink to="/deliveries/summary" className="sidebar-link">
                            <img className="sidebar-icon" src={home} alt="" />
                            <span>Home</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/payouts/summary" className="sidebar-link">
                            <img className="sidebar-icon" src={payout} alt="" />
                            <span>Payout</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/feedback/summary" className="sidebar-link">
                            <img className="sidebar-icon" src={feedback} alt="" />
                            <span>Feedback</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/orders/completed" className="sidebar-link">
                            <img className="sidebar-icon" src={orderHistory} alt="" />
                            <span>Order History</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/agent-ticket/raise" className="sidebar-link">
                            <img className="sidebar-icon" src={ticket} alt="" />
                            <span>Raise A Ticket</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/profile" className="sidebar-link">
                            <img className="sidebar-icon" src={profile} alt="" />
                            <span>My Profile</span>
                        </NavLink>
                    </li>
                </ul>

            </div>

            <div className="sidebar-bottom">
            
                <hr />

                <div className="user-name">
                    <img className="sidebar-icon" src={profile} alt="" />
                    <p>{agent?.firstName} {agent?.lastName}</p>
                </div>

                <hr />

                <div className="contact-info">
                    <p>{agent?.phone || 'Loading...'}</p>
                    <p>{agent?.email || 'Loading...'}</p>
                </div>

            </div>

        </div>
   
    );
};

export default DeliveryAgentSidebar;

 // <div className="delivery-sidebar inter-font">
      
    //     <ul>
    //         <li>
    //             <img className="sidebar-icon" src={home} alt="" />
    //             <a href="/deliveries/summary">Home</a>
    //         </li>
    //         <li>
    //             <img className="sidebar-icon" src={payout} alt="" />
    //             <a href="/payouts/summary">Payout</a>
    //         </li>
    //         <li>
    //             <img className="sidebar-icon" src={feedback} alt="" />
    //             <a href="/feedback/summary">Feedback</a>
    //         </li>
    //         <li>
    //             <img className="sidebar-icon" src={orderHistory} alt="" />
    //             <a href="/orders/completed">Order History</a>
    //         </li>
    //         <li>
    //             <img className="sidebar-icon" src={ticket} alt="" />
    //             <a href="/ticket/raise">Raise A Ticket</a>
    //         </li>
    //         <li>
    //             <img className="sidebar-icon" src={profile} alt="" />
    //             <a href="/ticket/raise">My Profile</a>
    //         </li>
    //     </ul>

    //     <hr />

    //     <div className='user-name'>
    //         <img className="sidebar-icon" src={profile} alt="" />
    //         <p>{agent?.firstName} {agent?.lastName}</p>
    //     </div>
            
    //     <hr />

    //     <div>
    //         <p>{agent?.phone || 'Loading...'}</p>
    //         <p>{agent?.email || 'Loading...'}</p>
    //     </div>

    // </div>
