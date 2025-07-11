// Author: Hitiksha Patel
// Description: Sidebar for Delivery Agent Dashboard

import React from 'react';
import { NavLink } from 'react-router-dom';
import home from '../../assets/home-icon.svg'
import payout from '../../assets/credit-card-icon.svg'
import feedback from '../../assets/edit-document-icon.svg'
import orderHistory from '../../assets/working-time-icon.png'
import ticket from '../../assets/speech-bubble-line-icon.png'
import profile from '../../assets/avatar-icon.png';
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
                            <span>Deliveries</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/availability/manage" className="sidebar-link">
                            <img className="sidebar-icon" src={home} alt="" />
                            <span>Manage Availability</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/payouts/summary" className="sidebar-link">
                            <img className="sidebar-icon" src={payout} alt="" />
                            <span>Payout</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/not-available" className="sidebar-link">
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
                        <NavLink to="/not-available" className="sidebar-link">
                            <img className="sidebar-icon" src={ticket} alt="" />
                            <span>Raise A Ticket</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/profile/detail" className="sidebar-link">
                            <img className="sidebar-icon" src={profile} alt="" />
                            <span>My Profile</span>
                        </NavLink>
                    </li>
                </ul>

            </div>

            <div className="sidebar-bottom">
            
                <hr className="hr-agent" />

                <div className="user-name">
                    <img className="sidebar-icon" src={profile} alt="" />
                    <p>{agent?.firstName} {agent?.lastName}</p>
                </div>

                <hr className="hr-agent" />

                <div className="contact-info">
                    <p>{agent?.phoneNo || 'Loading...'}</p>
                    <p>{agent?.email || 'Loading...'}</p>
                </div>

            </div>

        </div>
   
    );
}; 

export default DeliveryAgentSidebar;