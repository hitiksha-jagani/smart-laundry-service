// Author : Hitiksha Jagani
// Description : Report side manu page for admin dashboard.

import React, { useState } from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { FiChevronDown, FiChevronRight } from 'react-icons/fi';

export const reportRoutes = [
    '/reports/order/trend',
    '/reports/order/user-report-list',
    '/reports/review'
];

const reportMenuOptions = [ 
    {
        label: "Order",
        children: [
            { label: "Order Trend", path: "/reports/order/trend" },
            { label: "User Wise Order", path: "/reports/order/user-report-list" }
        ]
    },
    {
        label: "Review",
        path: "/reports/review"
    }
];

const ReportsMenu = () => {
    const location = useLocation();
    const isReportPage = reportRoutes.some(path => location.pathname.startsWith(path));
    const navigate = useNavigate();

    const [openMenus, setOpenMenus] = useState({});

    const toggleMenu = (label) => {
        setOpenMenus(prev => ({ ...prev, [label]: !prev[label] }));
    };

    if (!isReportPage) return null;

    return (

        <div
            style={{
                position: 'fixed',
                top: '60px',
                right: 0,
                width: '220px',
                height: '100vh',
                backgroundColor: '#F1F5F9',
                borderLeft: '1px solid #e5e7eb',
                boxShadow: '0 0 10px rgba(0,0,0,0.1)',
                padding: '20px',
                overflowY: 'auto',
                zIndex: 999
            }}
        >

            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>

                {reportMenuOptions.map(option => (

                    <li key={option.label} style={{ marginBottom: '12px' }}>
                        
                        {/* REVIEW item - override with navigation to /not-available */}
                        {!option.children && option.label === "Review" ? (

                            <div
                                onClick={() => navigate('/not-available')}
                                style={{
                                    cursor: 'pointer',
                                    display: 'block',
                                    padding: '8px 12px',
                                    borderRadius: '6px',
                                    backgroundColor: location.pathname === '/reports/review' ? '#0EA5E9' : 'transparent',
                                    fontWeight: 'bold',
                                    color: '#333'
                                }}
                            >
                                {option.label}
                            </div>

                        ) : option.children ? (

                            <>

                                <div
                                    onClick={() => toggleMenu(option.label)}
                                    style={{
                                        cursor: 'pointer',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'space-between',
                                        padding: '8px 12px',
                                        borderRadius: '6px',
                                        backgroundColor: openMenus[option.label] ? '#e2e8f0' : 'transparent',
                                        fontWeight: 'bold',
                                        // color: '#333'
                                    }}
                                >
                                    {option.label}
                                    {openMenus[option.label] ? <FiChevronDown /> : <FiChevronRight />}
                                </div>

                                {openMenus[option.label] && (

                                    <ul style={{ listStyle: 'none', paddingLeft: '15px', marginTop: '6px' }}>
                                        
                                        {option.children.map(child => (
                                            
                                            <li key={child.path} style={{ marginBottom: '8px' }}>

                                                <NavLink
                                                    to={child.path}
                                                    style={({ isActive }) => ({
                                                        textDecoration: 'none',
                                                        color: isActive ? '#fafafa' : '#333',
                                                        fontWeight: isActive ? 'bold' : 'normal',
                                                        display: 'block',
                                                        padding: '6px 12px',
                                                        borderRadius: '6px',
                                                        backgroundColor: isActive ? '#0EA5E9' : 'transparent'
                                                    })}
                                                >
                                                    {child.label}
                                                </NavLink>

                                            </li>

                                        ))}

                                    </ul>
                                )}

                            </>

                        ) : (

                            <NavLink
                                to={option.path}
                                style={({ isActive }) => ({
                                    textDecoration: 'none',
                                    color: isActive ? '#fafafa' : '#333',
                                    fontWeight: isActive ? 'bold' : 'normal',
                                    display: 'block',
                                    padding: '8px 12px',
                                    borderRadius: '6px',
                                    backgroundColor: isActive ? '#0EA5E9' : 'transparent'
                                })}
                            >
                                {option.label}
                            </NavLink>

                        )}

                    </li>

                ))}

            </ul>

        </div>
    );
};

export default ReportsMenu;
