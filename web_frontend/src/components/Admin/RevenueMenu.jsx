// Author : Hitiksha Jagani
// Description : Right sidebar for revenue pages in admin dashboard.

import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';

export const revenueRoutes = [
    '/revenue/summary',
    '/revenue/breakdown/graph',
    '/revenue/trends',
    '/revenue/provider-analytics-list',
    '/revenue/agent-analytics-list'
];

const revenueMenuOptions = [
    { label: "Summary", path: "/revenue/summary" },
    { label: "Breakdown", path: "/revenue/breakdown/graph" },
    { label: "Trends", path: "/revenue/trends" },
    { label: "Provider Revenue", path: "/revenue/provider-analytics-list" },
    { label: "Agent Revenue", path: "/revenue/agent-analytics-list" }
];

const RevenueMenu = () => {
    const location = useLocation();
    const isRevenuePage = revenueRoutes.some(path => location.pathname.startsWith(path));

    // Only render if we're on a revenue page
    if (!isRevenuePage) return null;

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

                {revenueMenuOptions.map(link => (

                    <li key={link.path} style={{ marginBottom: '12px' }}>

                        <NavLink

                            to={link.path}
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
                            {link.label}
                        </NavLink>

                    </li>

                ))}

            </ul>

        </div>
    );
};

export default RevenueMenu;
