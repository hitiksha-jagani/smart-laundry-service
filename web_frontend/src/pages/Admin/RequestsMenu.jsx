// Author : Hitiksha Jagani
// Description : Request side manu page for admin dashboard.

import { NavLink, useLocation } from 'react-router-dom';

export const requestRoutes = [
    '/provider-requests',
    '/agent-requests'
];

const requestMenuOptions = [
    { label: "Service Provider", path: "/provider-requests" },
    { label: "Delivery Agent", path: "/agent-requests" }
];

const RequestsMenu = () => {
    const location = useLocation();
    const isRequestPage = requestRoutes.some(path => location.pathname.startsWith(path));
        
    // Only render if we're on a request page
    if (!isRequestPage) return null;

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
            
                {requestMenuOptions.map(link => (
            
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

export default RequestsMenu;
