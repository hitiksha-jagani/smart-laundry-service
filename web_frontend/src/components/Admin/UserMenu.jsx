// Author : Hitiksha Jagani
// Description : Right sidebar for user page in admin dashboard.

import React, { useState } from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { FiChevronDown, FiChevronRight } from 'react-icons/fi';

export const userRoutes = [
    '/users/customer/graphs',
    '/users/customers/table',
    '/users/service-providers/graphs',
    '/users/service-providers/table',
    '/users/delivery-agents/graphs',
    '/users/delivery-agents/table',
];

const userMenuOptions = [
    {
        label: "Customer",
        children: [
            { label: "Graph Report", path: "/users/customer/graphs" },
            { label: "Details", path: "/users/customers/table" }
        ]
    },
    {
        label: "Service Provider",
        children: [
            { label: "Graph Report", path: "/users/service-providers/graphs" },
            { label: "Details", path: "/users/service-providers/table" }
        ]
    },
    {
        label: "Delivery Agent",
        children: [
            { label: "Graph Report", path: "/users/delivery-agents/graphs" },
            { label: "Details", path: "/users/delivery-agents/table" }
        ]
    }
];

const UserMenu = () => {
    const location = useLocation();
    const isUserPage = userRoutes.some(path => location.pathname.startsWith(path));
    const navigate = useNavigate();

    const [openMenus, setOpenMenus] = useState({});
    
    const toggleMenu = (label) => {
        setOpenMenus(prev => ({ ...prev, [label]: !prev[label] }));
    };

    // Only render if we're on a revenue page
    if (!isUserPage) return null;

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

                {userMenuOptions.map(menu => (

                    <li key={menu.label} style={{ marginBottom: '16px' }}>

                        <button
                            onClick={() => toggleMenu(menu.label)}
                            style={{
                                cursor: 'pointer',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'space-between',
                                padding: '8px 12px',
                                borderRadius: '6px',
                                backgroundColor: openMenus[menu.label] ? '#e2e8f0' : 'transparent',
                                fontWeight: 'bold',
                                width: '100%'
                            }}
                        >
                            {menu.label}
                            {openMenus[menu.label] ? <FiChevronDown /> : <FiChevronRight />}
                        </button>

                        {openMenus[menu.label] && (

                            <ul style={{ listStyle: 'none', paddingLeft: '15px', marginTop: '6px' }}>
                                
                                {menu.children.map(child => (
                                    
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
                   

                    </li>

                ))}

            </ul>

        </div>
    );
};

export default UserMenu;
