// Author: Hitiksha Patel
// Description: Sidebar for Delivery Agent Dashboard

import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import revenue from '../../assets/chart-arrow-up-icon.png';
import report from '../../assets/reports-icon.png';
import manage from '../../assets/edit-document-icon.svg';
import users from '../../assets/business-team-icon.png';
import request from '../../assets/registration-icon.png';
import message from '../../assets/envelope-icon.png';
import ticket from '../../assets/speech-bubble-line-icon.png';
import promotion from '../../assets/new-product-icon.png';
import profile from '../../assets/avatar-icon.png';
import configuration from '../../assets/working-time-icon.png';
import ConfigurationsMenu, { configRoutes } from './ConfigurationsMenu';
import NotAvailablePage from '../../pages/NotAvailablePage';
import '../../styles/Admin/AdminSidebar.css';
import '../../styles/Admin/AdminCommon.css';
import '../../styles/Admin/ConfigurationMenu.css';  

const AdminSidebar = ({ admin }) => {
    const location = useLocation();
    const isConfigActive = configRoutes.some(path => location.pathname.startsWith(path));

    const [isConfigMenuOpen, setConfigMenuOpen] = useState(false);
    
    const handleConfigClick = () => {
        setConfigMenuOpen(prev => !prev);
    };

    const handleMouseEnter = () => setConfigMenuOpen(true);
    const handleMouseLeave = () => setConfigMenuOpen(false);

    return (
        <div className="admin-sidebar">
        
            <div className="admin-sidebar-top">
            
                <ul className="admin-sidebar-links">
                    <li>
                        <NavLink to="/revenue/summary" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={revenue} alt="" />
                            <span>Revenue</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/reports/order/trend" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={report} alt="" />
                            <span>Reports</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/provider-requests" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={request} alt="" />
                            <span>Requests</span>
                        </NavLink>
                    </li>
                    <li> 
                        <NavLink to="/users/customer/graphs" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={users} alt="" />
                            <span>Users</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/not-available" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={message} alt="" />
                            <span>Send Message</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/not-available" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={ticket} alt="" />
                            <span>Complaints</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/service/summary" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={manage} alt="" />
                            <span>Manage Service Listing</span>
                        </NavLink>
                    </li> 
                    <li>
                        <NavLink to="/not-available" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={promotion} alt="" />
                            <span>Promotion</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/configurations/providers" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={configuration} alt="" />
                            <span>Configurations</span>
                        </NavLink>
                    </li>
                    <li>
                        <NavLink to="/admin-profile" className="admin-sidebar-link">
                            <img className="admin-sidebar-icon" src={profile} alt="" />
                            <span>My Profile</span>
                        </NavLink>
                    </li>
                </ul>

            </div>

            <div className="admin-sidebar-bottom">
            
                <hr className="hr-admin"/>

                <div className="user-name">
                    <img className="admin-sidebar-icon" src={profile} alt="" />
                    <p>{admin?.firstName} {admin?.lastName}</p>
                </div>

                <hr className="hr-admin"/>

                <div className="contact-info">
                    <p>{admin?.phoneNo || 'Loading...'}</p>
                    <p>{admin?.email || 'Loading...'}</p>
                </div>

            </div>

        </div>
   
    );
};

export default AdminSidebar;