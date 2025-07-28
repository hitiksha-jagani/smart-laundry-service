// Author : Hitiksha Jagani
// Description : Sidebar menu for configuration page in admin dashboard.

import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';

export const configRoutes = [
    '/configurations/providers',
    '/configurations/revenue-breakdown',
    '/configurations/agent-earnings'
];

const configurationMenuOptions = [
    { label: "Geo-Coding", path: "/configurations/providers" },
    { label: "Revenue Breakdown", path: "/configurations/revenue-breakdown" },
    { label: "Agent Earnings", path: "/configurations/agent-earnings" }
];

const ConfigurationMenu = () => {
    const location = useLocation();
    const isConfigurationPage = configRoutes.some(path => location.pathname.startsWith(path));
    
    // Only render if we're on a configuration page
    if (!isConfigurationPage) return null;

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
    
                {configurationMenuOptions.map(link => (
    
                    <li key={link.path} style={{ marginBottom: '12px' }}>
    
                        <NavLink
    
                            to={link.path}
                            style={({ isActive }) => ({
                                    textDecoration: 'none',
                                    color: isActive ? '#fafafa' : '#333',
                                    fontWeight: isActive ? 'bold' : 'normal',
                                    display: 'block',
                                    padding: '8px 10px',
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

export default ConfigurationMenu;