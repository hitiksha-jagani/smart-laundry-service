// Author : Hitiksha Jagani
// Description : Right sidebar for manage service listing page in admin dashboard.

import { NavLink, useLocation } from 'react-router-dom';

export const serviceRoutes = [
    '/service/summary',
    '/service/add-items',
    '/service/add-services',
    '/service/add-subservices',
];

const serviceMenuOptions = [
    { label: "Summary", path: "/service/summary" },
    { label: "Services", path: "/service/add-services" },
    { label: "Sub-Services", path: "/service/add-subservices" },
    { label: "Items", path: "/service/add-items" },
];

const ServiceMenu = () => {
    const location = useLocation();
    const isServicePage = serviceRoutes.some(path => location.pathname.startsWith(path));

    // Only render if we're on a revenue page
    if (!isServicePage) return null;

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

                {serviceMenuOptions.map(link => (

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

export default ServiceMenu;
